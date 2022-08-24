/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import discord4j.common.ReactorResources;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.object.entity.Member;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.MessageReceipt;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负责与Discord通讯的机器人
 */
public class DiscordBot {
    private final DiscordClient client;
    private GDLBot gdlBot;

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
    }

    public void run() {
        Map<Snowflake, List<MessageReceipt<Group>>> discordMsgMapQQMSg = new HashMap<>();
        Map<Snowflake, Member> memberCache = new ConcurrentHashMap<>();
        Mono<Void> login = client.withGateway(gateway -> {
                    Mono<Void> handleMessageCreate = gateway.on(MessageCreateEvent.class, event -> Mono.fromRunnable(new MessageCreateEventHandler(event, gdlBot, discordMsgMapQQMSg, memberCache))).then();
                    Mono<Void> handleMessageDelete = gateway.on(MessageDeleteEvent.class, event -> Mono.fromRunnable(new MessageDeleteEventHandler(event, discordMsgMapQQMSg))).then();
                    return handleMessageCreate.and(handleMessageDelete);
                }
        );

        login.block();
    }

    public void setGDLBot(GDLBot gdlBot) {
        this.gdlBot = gdlBot;
    }
}
