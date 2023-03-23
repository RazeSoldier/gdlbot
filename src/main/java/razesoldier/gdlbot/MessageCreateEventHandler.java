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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
                    // 过滤请求。仅接受来自白名单服务器的消息
                    String guildName = tuple3.getT1().getName();
                    String channelName = tuple3.getT2().getName();
                    return discordRelayConfig.discordServers().contains(guildName) && discordRelayConfig.discordChannels().contains(channelName);
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
                    Flux.fromIterable(discordRelayConfig.downstreamGroups()).subscribe(group -> {
                        var receipt = gdlBot.sendMessageToGroup(group, pendingMessage);
                        // 每当发送QQ群消息时记录消息回执，用于撤回消息
                        messageReceipts.add(receipt);
                    });
                });
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
        return transformMemberId2Name(content);
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
