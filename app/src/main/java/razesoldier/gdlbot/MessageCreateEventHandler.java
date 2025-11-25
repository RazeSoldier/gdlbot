/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.GuildChannel;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.Image;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 处理{@link MessageCreateEvent}
 */
public class MessageCreateEventHandler implements Runnable {
    private final MessageCreateEvent event;
    private final GDLBot gdlBot;
    private final Map<Snowflake, List<MessageReceipt<Group>>> discordMsgMapQQMSg;
    private final Map<Snowflake, Member> memberCache;
    private final List<Config.DiscordRelaySetting> discordRelayConfig;
    private final List<Long> channelWhitelist = new ArrayList<>();
    private final String imgurClientId;

    public MessageCreateEventHandler(MessageCreateEvent event,
                                     GDLBot gdlBot,
                                     Map<Snowflake, List<MessageReceipt<Group>>> discordMsgMapQQMSg,
                                     Map<Snowflake, Member> memberCache) {
        this.event = event;
        this.gdlBot = gdlBot;
        this.discordMsgMapQQMSg = discordMsgMapQQMSg;
        this.memberCache = memberCache;
        Config config = Services.getInstance().getConfig();
        discordRelayConfig = config.getDiscordRelaySettings();
        discordRelayConfig.forEach(discordRelay -> channelWhitelist.addAll(discordRelay.getDiscordChannels()));
        if (config.hasImgurClientId()) {
            imgurClientId = config.getImgurClientId();
        } else {
            imgurClientId = null;
        }
    }

    @Override
    public void run() {
        Message message = event.getMessage();
        List<MessageReceipt<Group>> messageReceipts = Collections.synchronizedList(new ArrayList<>());
        Flux.zip(message.getGuild(), message.getChannel().ofType(GuildChannel.class), getMemberFromMessage(message))
                .log()
                .retry(2)
                .doOnNext(tuple3 -> {
                    // 记录日志
                    String guildName = tuple3.getT1().getName();
                    String channelName = tuple3.getT2().getName();
                    Services.getInstance().getLogger().info(() -> String.format("Received %s#%s: %s", guildName, channelName, message.getContent()));
                    memberCache.put(tuple3.getT3().getId(), tuple3.getT3());
                })
                .filter(tuple3 -> {
                    // 过滤请求。仅接受来自白名单频道的消息
                    long channelId = tuple3.getT2().getId().asLong();
                    return channelWhitelist.contains(channelId);
                })
                .doOnError(error ->
                    gdlBot.sendMessage(getAdminContact(), "[MessageCreateEventHandler] " + error.getMessage()) // 给管理员用户发送错误消息
                )
                .doOnComplete(() -> discordMsgMapQQMSg.put(message.getId(), messageReceipts))
                .subscribe(tuple3 -> {
                    String guildName = tuple3.getT1().getName();
                    String channelName = tuple3.getT2().getName();
                    Member sender = tuple3.getT3();
                    List<InputStream> inputStreams; // 最终要传给sendMessageToDownstream()方法的文件流列表
                    inputStreams = DiscordUtil.attachment2InputStream(message.getAttachments());
                    String content = normalizedMessageContent(message.getContent());

                    ImageLinkProcessor imageLinkProcessor = new ImageLinkProcessor(content, message);
                    if (imgurClientId != null) {
                        imageLinkProcessor.setImgurClientId(imgurClientId);
                    }
                    ImageLinkProcessor.Result result = imageLinkProcessor.process();
                    content = result.processedContent();
                    inputStreams.addAll(result.imageInputStreams());

                    var pendingMessage = new PingNotification(guildName,
                            channelName,
                            sender.getNickname().orElse(sender.getUsername()),
                            content
                    ).toString();
                    for (Config.DiscordRelaySetting relay : discordRelayConfig) {
                        if (relay.getDiscordChannels().contains(tuple3.getT2().getId().asLong())) {
                            sendMessageToDownstream(messageReceipts, pendingMessage, inputStreams, relay.getDownstreamGroups());
                        }
                    }
                    try {
                        RemoteFileUtil.closeInputStreams(inputStreams);
                    } catch (IOException e) {
                        throw new CloseInputStreamException(e);
                    }
                });
    }

    private void sendMessageToDownstream(List<MessageReceipt<Group>> messageReceipts, String pendingMessage, List<InputStream> inputStreams, @NotNull List<Long> downstream) {
        for (Long groupId : downstream) {
            Group group = gdlBot.findGroup(groupId);
            var images = uploadImages(inputStreams, group);
            var receipt = gdlBot.sendMessageToGroup(group, pendingMessage, images);
            // 每当发送QQ群消息时记录消息回执，用于撤回消息
            messageReceipts.add(receipt);
        }
    }

    /**
     * 将提供的{@link InputStream}作为图片上传到指定{@link Group 群组}
     */
    @NotNull
    private List<Image> uploadImages(@NotNull List<InputStream> inputStreams, @NotNull Group group) {
        List<Image> images = new ArrayList<>();
        for (InputStream inputStream : inputStreams) {
            try {
                inputStream.reset(); // 重置输入流
                images.add(gdlBot.uploadImage(group, inputStream));
            } catch (IOException e) {
                Services.getInstance().getLogger().severe(e.getMessage());
            }
        }
        return images;
    }

    /**
     * 尝试从{@link Message}或者从{@link MessageCreateEventHandler#memberCache}中获得{@code Mono<Member>}
     */
    @NotNull
    private Mono<Member> getMemberFromMessage(@NotNull Message message) {
        Optional<Snowflake> memberId = event.getMember().map(User::getId); // 从这次事件里获得发送者的ID
        Mono<Member> member;
        if (memberId.isPresent() && memberCache.containsKey(memberId.get())) {
            member = Mono.just(memberCache.get(memberId.get()));
        } else {
            member = message.getAuthorAsMember(); // 如果缓存没有则从Message实例里获取
        }
        return member;
    }

    private Long getAdminContact() {
        return Services.getInstance().getConfig().getAdminContact();
    }

    /**
     * 规范化消息内容（去掉内容里的特殊字符）
     */
    @NotNull
    private String normalizedMessageContent(@NotNull String content) {
        return new DiscordMarkdownConverter(event.getGuild().block()).convert(content);
    }
}
