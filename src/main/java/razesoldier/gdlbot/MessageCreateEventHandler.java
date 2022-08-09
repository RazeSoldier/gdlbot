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
import discord4j.core.object.entity.channel.GuildChannel;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.MessageReceipt;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;

import java.util.*;

/**
 * 处理{@link MessageCreateEvent}
 */
public class MessageCreateEventHandler implements Runnable {
    private final MessageCreateEvent event;
    private final GDLBot gdlBot;
    private final Map<Snowflake, List<MessageReceipt<Group>>> discordMsgMapQQMSg;
    private final Config.DiscordRelay discordRelayConfig;

    public MessageCreateEventHandler(MessageCreateEvent event, GDLBot gdlBot, Map<Snowflake, List<MessageReceipt<Group>>> discordMsgMapQQMSg) {
        this.event = event;
        this.gdlBot = gdlBot;
        this.discordMsgMapQQMSg = discordMsgMapQQMSg;
        discordRelayConfig = Services.getInstance().getConfig().discordRelay();
    }

    @Override
    public void run() {
        Message message = event.getMessage();
        List<MessageReceipt<Group>> messageReceipts = Collections.synchronizedList(new ArrayList<>());
        Flux.zip(message.getGuild(), message.getChannel().ofType(GuildChannel.class), message.getAuthorAsMember())
                .log()
                .retry(2)
                .doOnNext(tuple3 -> {
                    // 记录日志
                    String guildName = tuple3.getT1().getName();
                    String channelName = tuple3.getT2().getName();
                    Services.getInstance().getLogger().info(() -> String.format("Received %s#%s: %s", guildName, channelName, message.getContent()));
                })
                .filter(tuple3 -> {
                    // 过滤请求。仅接受来自白名单服务器的消息
                    String guildName = tuple3.getT1().getName();
                    String channelName = tuple3.getT2().getName();
                    return discordRelayConfig.discordServers().contains(guildName) && discordRelayConfig.discordChannels().contains(channelName);
                })
                .doOnError(error -> gdlBot.sendMessage(getAdminContact(), "[MessageCreateEventHandler] " + error.getMessage()))
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

    private Long getAdminContact() {
        return Services.getInstance().getConfig().adminContact();
    }

    /**
     * 规范化消息内容（去掉内容里的特殊字符）
     */
    @NotNull
    private static String normalizedMessageContent(@NotNull String content) {
        return content.replaceAll("<a?:.*?:\\d*>", "");
    }
}
