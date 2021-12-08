/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import org.jetbrains.annotations.NotNull;
import razesoldier.gdlbot.translation.TranslateException;
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
        StringBuilder stringBuilder = new StringBuilder();
        /*
            # pings
            FC: Formup in R1O
            Fleetname: xxx
         */
        stringBuilder.append("# ").append(channel).append(":\n").append(sender).append(": ").append(text).append("\n");
        try {
            stringBuilder.append("---------------------").append("\n").append(translate(text));
        } catch (TranslateException e) {
            Services.getInstance().getLogger().warning(e.getMessage());
        }
        return stringBuilder.toString();
    }

    @NotNull
    private String translate(@NotNull String source) throws TranslateException {
        return Services.getInstance()
                .getTranslationPipeline()
                .addTranslator(TranslatorFactory.makeEVEProperNounsTranslator())
                .addTranslator(TranslatorFactory.makeTencentTranslator(Services.getInstance().getConfig()))
                .translate(source);
    }
}
