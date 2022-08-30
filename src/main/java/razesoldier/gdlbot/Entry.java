/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import com.alibaba.fastjson2.JSON;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        } catch (IOException e) {
            logger.severe("Can't read config.json");
            return;
        }
        if (config == null) {
            logger.severe("Can't parse config.json");
            return;
        }

        Services.setup(config, logger);

        var gdlBot = new GDLBot(logger, config, newBot(config.account()));
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

    @NotNull
    private static Bot newBot(@NotNull Config.Account account) {
        var botConfig = new BotConfiguration();
        botConfig.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD); // 使用PAD协议，这样可以允许手机和机器人同时在线
        botConfig.fileBasedDeviceInfo("deviceinfo.json"); // 生成设备信息并在下次启动的时候自动重用
        return BotFactory.INSTANCE.newBot(account.qq(), account.password(), botConfig);
    }
}
