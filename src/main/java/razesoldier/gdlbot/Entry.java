/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import com.alibaba.fastjson2.JSON;
import net.mamoe.mirai.Bot;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.overflow.BotBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * 本机器人的入口点
 */
public class Entry {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("GDLBot");
        logger.info("Starting GDLBot");
        Config config;
        try {
            config = getConfig();
        } catch (IOException _) {
            logger.severe("Can't read config.json");
            return;
        }
        if (config == null) {
            logger.severe("Can't parse config.json");
            return;
        }
        String onebotHost = System.getenv("ONEBOT_HOST");
        String onebotToken = System.getenv("ONEBOT_TOKEN");
        if (onebotHost == null) {
            logger.severe("ONEBOT_HOST not set");
            return;
        }

        Services.setup(config, logger);

        var gdlBot = new GDLBot(logger, config, newBot(onebotHost, onebotToken));
        gdlBot.run();
        var discordBot = new DiscordBot(config);
        discordBot.setGDLBot(gdlBot);
        discordBot.run();
    }

    @Nullable
    private static Config getConfig() throws IOException {
        var text = Files.readString(Paths.get("config.json"));
        return JSON.parseObject(text, Config.class);
    }

    @Nullable
    private static Bot newBot(String onebotHost, @Nullable String onebotToken) {
        BotBuilder builder = BotBuilder.positive(onebotHost);
        if (onebotToken != null) {
            builder.token(onebotToken);
        }
        return builder.connect();
    }
}
