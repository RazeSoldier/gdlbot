/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.MessageReceipt;

import java.util.List;
import java.util.Map;

/**
 * 处理{@link MessageDeleteEvent}
 */
public class MessageDeleteEventHandler implements Runnable {
    private final MessageDeleteEvent event;
    private final Map<Snowflake, List<MessageReceipt<Group>>> discordMsgMapQQMSg;

    public MessageDeleteEventHandler(MessageDeleteEvent event, Map<Snowflake, List<MessageReceipt<Group>>> discordMsgMapQQMSg) {
        this.event = event;
        this.discordMsgMapQQMSg = discordMsgMapQQMSg;
    }

    @Override
    public void run() {
        Snowflake messageId = event.getMessageId();
        if (discordMsgMapQQMSg.containsKey(messageId)) {
            // 撤回之前在QQ群发送的消息
            List<MessageReceipt<Group>> messageReceipts = discordMsgMapQQMSg.remove(messageId);
            synchronized (this) {
                messageReceipts.forEach(MessageReceipt::recall);
            }
        }
    }
}
