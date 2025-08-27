/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.command;

import net.mamoe.mirai.contact.Contact;
import razesoldier.gdlbot.DOMException;
import razesoldier.gdlbot.PandemicHordeWebsiteAccessor;
import razesoldier.gdlbot.BlacklistEntity;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 处理`.bl <character_name>`命令
 */
class QueryBlacklistCommand implements Command {
    private final String characterName;
    private Contact contact;
    private final PandemicHordeWebsiteAccessor phWebsiteAccessor;
    private final Logger logger;

    QueryBlacklistCommand(String characterName, String cookie, Logger logger) {
        this.characterName = characterName;
        phWebsiteAccessor = new PandemicHordeWebsiteAccessor(cookie);
        this.logger = logger;
    }

    @Override
    public void execute() {
        Map<String, BlacklistEntity> entities;
        try {
            logger.info("查询黑名单");
            entities = phWebsiteAccessor.getBlacklist().body().get();
            logger.info("查询完毕");
        } catch (IOException e) {
            logException(e);
            return;
        } catch (InterruptedException e) {
            logException(e);
            Thread.currentThread().interrupt();
            return;
        } catch (DOMException e) {
            logException(e);
            contact.sendMessage("HTML解析失败，是不是登录状态失效了??");
            return;
        }
        if (entities.containsKey(characterName)) {
            var entity = entities.get(characterName);
            contact.sendMessage(String.format("%s%n操作人: %s%n时间: %s%n原因: %s", entity.characterName(), entity.operator(), entity.time(), entity.reason()));
        } else {
            contact.sendMessage(String.format("%s 不在黑名单", characterName));
        }
    }

    @Override
    public void setRecipient(Contact contact) {
        this.contact = contact;
    }

    private void logException(Exception e) {
        logger.warning(() -> (String.format("Can't access PH site, reason: %s", e.getMessage())));
    }
}
