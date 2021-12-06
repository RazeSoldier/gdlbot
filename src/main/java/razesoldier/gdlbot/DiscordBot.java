/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import discord4j.common.ReactorResources;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildChannel;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.util.Objects;

/**
 * 负责与Discord通讯的机器人
 */
public class DiscordBot {
    private final DiscordClient client;
    private GDLBot gdlBot;
    private final Config.DiscordRelay discordRelayConfig;

    public DiscordBot(@NotNull Config config) {
        final var proxy = config.proxy();

        var resources = ReactorResources.builder()
                .httpClient(
                        HttpClient.create()
                                // 为DiscordClient设置代理
                                .proxy(typeSpec ->
                                        typeSpec.type(ProxyProvider.Proxy.SOCKS5).host(proxy.host()).port(proxy.port()).build()
                                )
                ).build();
        client = DiscordClientBuilder.create(config.discordBotToken()).setReactorResources(resources).build();
        discordRelayConfig = config.discordRelay();
    }

    public void run() {
        Mono<Void> login = client.withGateway(gateway ->
                gateway.on(MessageCreateEvent.class, event -> Mono.fromRunnable(() -> handleMessageCreateEvent(event)))
        );

        login.block();
    }

    private void handleMessageCreateEvent(@NotNull MessageCreateEvent event) {
        Message message = event.getMessage();
        var serverName = Objects.requireNonNull(message.getGuild().block()).getName();
        var channelName = Objects.requireNonNull(message.getChannel().ofType(GuildChannel.class).block()).getName();

        if (serverName.equals(discordRelayConfig.discordServer()) && discordRelayConfig.discordChannels().contains(channelName)) {
            gdlBot.sendMessage(discordRelayConfig.downstreamGroup(),
                    new PingNotification(channelName, getSenderName(message),message.getContent()).toString());
        }
    }

    @NotNull
    private String getSenderName(@NotNull Message message) {
        var member = message.getAuthorAsMember().block();
        return Objects.requireNonNull(member).getNickname().orElseGet(member::getUsername);
    }

    public void setGDLBot(GDLBot gdlBot) {
        this.gdlBot = gdlBot;
    }
}
