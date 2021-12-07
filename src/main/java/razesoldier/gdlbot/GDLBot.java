/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;
import razesoldier.gdlbot.command.Command;
import razesoldier.gdlbot.command.CommandFactory;

import java.util.logging.Logger;

/**
 * 负责与QQ通讯的机器人，包装了{@link Bot MiraiBot}
 */
class GDLBot {
    private final Logger logger;
    private final Config config;
    private final Bot bot;
    private final CommandFactory commandFactory;

    GDLBot(Logger logger, Config config, Bot bot) {
        this.logger = logger;
        this.config = config;
        this.bot = bot;
        commandFactory = new CommandFactory(logger, config);
    }

    void run() {
        logger.info("Trying login");
        bot.login();
        registerMessageSubscribe();
    }

    void sendMessage(Long id, String message) {
        bot.getFriendOrFail(id).sendMessage(message);
    }

    void sendMessageToGroup(Long id, String message) {
        bot.getGroupOrFail(id).sendMessage(message);
    }

    private void registerMessageSubscribe() {
        GlobalEventChannel.INSTANCE.filter(event -> {
            if (!(event instanceof MessageEvent)) {
                return false;
            }

            return config.messageSubscribeList().contains(getSenderFromEvent((MessageEvent) event).getId());
        }).subscribeAlways(MessageEvent.class, messageEvent -> {
            logger.info(String.format("Received a message from %s", messageEvent.getSender().getId()));
            Command command = commandFactory.newFromMessage(messageEvent.getMessage());
            logger.info(String.format("Will execute %s", command.getClass().getSimpleName()));

            command.setRecipient(getSenderFromEvent(messageEvent));
            command.execute();
        });
    }

    @NotNull
    private Contact getSenderFromEvent(@NotNull MessageEvent event) {
        if (event instanceof GroupMessageEvent groupMessageEvent) {
            return groupMessageEvent.getGroup();
        } else {
            return event.getSender();
        }
    }
}
