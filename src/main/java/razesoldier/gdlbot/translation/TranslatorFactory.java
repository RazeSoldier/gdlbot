/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.translation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import razesoldier.gdlbot.Services;

/**
 * {@link razesoldier.gdlbot.translation}包的API之一
 */
public class TranslatorFactory {
    private static Injector injector;

    /**
     * 获得默认的翻译器
     */
    @NotNull
    public static Translator make() {
        return makeTencentTranslator();
    }

    @NotNull
    public static TencentTranslator makeTencentTranslator() {
        TencentTranslator instance = getInjector().getInstance(TencentTranslator.class);
        var termRepoIds = Services.getInstance().getConfig().termRepoIDs();
        if (termRepoIds != null) {
            for (String id : termRepoIds) {
                instance.addTermRepo(id);
            }
        }
        var sentRepoIds = Services.getInstance().getConfig().sentRepoIDs();
        if (sentRepoIds != null) {
            for (String id : sentRepoIds) {
                instance.addSentRepo(id);
            }
        }
        return instance;
    }

    @NotNull
    @Contract(" -> new")
    public static EVEProperNounsTranslator makeEVEProperNounsTranslator() {
        return getInjector().getInstance(EVEProperNounsTranslator.class);
    }

    private static Injector getInjector() {
        if (injector == null) {
            injector = Guice.createInjector(new TranslatorModule());
        }
        return injector;
    }

    private TranslatorFactory() {
    }
}
