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
        return getInjector().getInstance(TencentTranslator.class);
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
