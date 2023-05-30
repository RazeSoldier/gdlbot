/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.*;
import discord4j.core.object.entity.channel.GuildChannel;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.Image;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理{@link MessageCreateEvent}
 */
public class MessageCreateEventHandler implements Runnable {
    private final MessageCreateEvent event;
    private final GDLBot gdlBot;
    private final Map<Snowflake, List<MessageReceipt<Group>>> discordMsgMapQQMSg;
    private final Map<Snowflake, Member> memberCache;
    private final Config.DiscordRelay discordRelayConfig;

    public MessageCreateEventHandler(MessageCreateEvent event,
                                     GDLBot gdlBot,
                                     Map<Snowflake, List<MessageReceipt<Group>>> discordMsgMapQQMSg,
                                     Map<Snowflake, Member> memberCache) {
        this.event = event;
        this.gdlBot = gdlBot;
        this.discordMsgMapQQMSg = discordMsgMapQQMSg;
        this.memberCache = memberCache;
        discordRelayConfig = Services.getInstance().getConfig().discordRelay();
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
                    return discordRelayConfig.discordChannels().contains(channelId);
                })
                .doOnError(error -> {
                    gdlBot.sendMessage(getAdminContact(), "[MessageCreateEventHandler] " + error.getMessage()); // 给管理员用户发送错误消息
                    discordRelayConfig.downstreamGroups().forEach(group -> gdlBot.sendMessageToGroup(group, "Ops,看起来有个集结通知未能转发"));
                })
                .doOnComplete(() -> discordMsgMapQQMSg.put(message.getId(), messageReceipts))
                .subscribe(tuple3 -> {
                    String guildName = tuple3.getT1().getName();
                    String channelName = tuple3.getT2().getName();
                    Member sender = tuple3.getT3();
                    var pendingMessage = new PingNotification(guildName,
                            channelName,
                            sender.getNickname().orElse(sender.getUsername()),
                            normalizedMessageContent(message.getContent())).toString();
                    List<InputStream> inputStreams = DiscordUtil.image2InputStream(message.getAttachments());
                    Flux.fromIterable(discordRelayConfig.downstreamGroups())
                            .map(gdlBot::findGroup)
                            .publishOn(Schedulers.boundedElastic())
                            .doOnComplete(() -> {
                                try {
                                    RemoteFileUtil.closeInputStreams(inputStreams);
                                } catch (IOException e) {
                                    throw new CloseInputStreamException(e);
                                }
                            })
                            .subscribe(group -> {
                                var images = uploadImages(inputStreams, group);
                                var receipt = gdlBot.sendMessageToGroup(group, pendingMessage, images);
                                // 每当发送QQ群消息时记录消息回执，用于撤回消息
                                messageReceipts.add(receipt);
                            });
                });
    }

    /**
     * 将提供的{@link InputStream}作为图片上传到指定{@link Group 群组}
     */
    @NotNull
    private List<Image> uploadImages(@NotNull List<InputStream> inputStreams, @NotNull Group group) {
        List<Image> images = new ArrayList<>();
        for (InputStream inputStream : inputStreams) {
            try {
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
        return Services.getInstance().getConfig().adminContact();
    }

    /**
     * 规范化消息内容（去掉内容里的特殊字符）
     */
    @NotNull
    private String normalizedMessageContent(@NotNull String content) {
        content = removeUnsupportedString(content);
        content = transformMemberId2Name(content);
        return transformTimeCommand2LocalTime(content);
    }

    /**
     * 去掉内容里的不能被本程序处理的特殊字符串
     */
    @NotNull
    @Contract(pure = true)
    private static String removeUnsupportedString(@NotNull String content) {
        return content.replaceAll("<a?:.*?:\\d*>", "");
    }

    /**
     * 将内容里诸如`<@757265324688277525>`的文本转换成对应的人名
     */
    private String transformMemberId2Name(String content) {
        Matcher matcher = Pattern.compile("<@(\\d*)>").matcher(content);
        return matcher.replaceAll(matchResult -> "@" + getMemberNameById(Long.valueOf(matchResult.group(1))));
    }

    /**
     * 将内容里诸如`&#60;t:1679657400:R>`的文本转换成对应+8时区的时间
     */
    private static String transformTimeCommand2LocalTime(String content) {
        Matcher matcher = Pattern.compile("<t:(\\d*):R>").matcher(content);
        return matcher.replaceAll(matchResult -> {
            Instant instant = Instant.ofEpochSecond(Long.parseLong(matchResult.group(1)));
            return LocalDateTime.ofInstant(instant, ZoneId.of("+8")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        });
    }

    /**
     * 根据成员ID获得服务器的成员显示名
     * @param id 成员ID
     * @return 返回服务器的成员显示名，如果因为任何原因无法获得则回退成成员ID
     */
    private String getMemberNameById(Long id) {
        return event.getGuild()
                .blockOptional()
                .map(guild -> guild.getMemberById(Snowflake.of(id)))
                .map(Mono::block)
                .map(PartialMember::getDisplayName)
                .orElseGet(() -> String.valueOf(id));
    }
}
