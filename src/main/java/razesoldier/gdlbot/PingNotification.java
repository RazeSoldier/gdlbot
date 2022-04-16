/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import org.jetbrains.annotations.NotNull;
import razesoldier.gdlbot.translation.TranslatorFactory;

/**
 * Ping信息的构建器
 */
public class PingNotification {
    private final String channel;
    private final String sender;
    private final String text;

    public PingNotification(String channel, String sender, String text) {
        this.channel = channel;
        this.sender = sender;
        this.text = text;
    }

    public String toString() {
        /*
            # pings
            FC: Formup in R1O
            Fleetname: xxx
         */
        return "# " + channel + ":\n" + sender + ": " + text + "\n" +
                "---------------------" + "\n" + translate(text);
    }

    @NotNull
    private String translate(@NotNull String source) {
        return Services.getInstance()
                .getTranslationPipeline()
                .addTranslator(TranslatorFactory.makeEVEProperNounsTranslator())
                .addTranslator(TranslatorFactory.makeTencentTranslator())
                .translate(source);
    }
}
