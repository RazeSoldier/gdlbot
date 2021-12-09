/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildChannel;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;

import java.util.Objects;

/**
 * 处理{@link MessageCreateEvent}
 */
public class MessageCreateEventHandler implements Runnable {
    private final MessageCreateEvent event;
    private final GDLBot gdlBot;
    private final Config.DiscordRelay discordRelayConfig;

    public MessageCreateEventHandler(MessageCreateEvent event, GDLBot gdlBot) {
        this.event = event;
        this.gdlBot = gdlBot;
        discordRelayConfig = Services.getInstance().getConfig().discordRelay();
    }

    @Override
    public void run() {
        Message message = event.getMessage();
        var serverName = Objects.requireNonNull(message.getGuild().block()).getName();
        var channelName = Objects.requireNonNull(message.getChannel().ofType(GuildChannel.class).block()).getName();
        Services.getInstance().getLogger().info(() -> String.format("Received %s#%s: %s", serverName, channelName, message.getContent()));

        if (serverName.equals(discordRelayConfig.discordServer()) && discordRelayConfig.discordChannels().contains(channelName)) {
            var pendingMessage = new PingNotification(channelName,
                    getSenderName(message),
                    normalizedMessageContent(message.getContent())).toString();
            Flux.fromIterable(discordRelayConfig.downstreamGroups()).subscribe(group -> gdlBot.sendMessageToGroup(group, pendingMessage));
        }
    }

    /**
     * 从提供的{@link Message}获得此消息的发送人名称
     * @return 尝试返回发送人的昵称，如果没有则返回其用户名
     */
    @NotNull
    private String getSenderName(@NotNull Message message) {
        var member = message.getAuthorAsMember().block();
        return Objects.requireNonNull(member).getNickname().orElseGet(member::getUsername);
    }

    /**
     * 规范化消息内容（去掉内容里的特殊字符）
     */
    @NotNull
    private static String normalizedMessageContent(@NotNull String content) {
        return content.replaceAll("<a?:.*?:[0-9]*>", "");
    }
}
