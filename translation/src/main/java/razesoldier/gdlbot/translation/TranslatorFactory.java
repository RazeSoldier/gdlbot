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
import razesoldier.gdlbot.Config;
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
    public static Translator makeTencentTranslator() {
        TencentTranslator instance = getInjector().getInstance(TencentTranslator.class);
        Config.TencentCloudTranslationSetting tencentCloudTranslationSetting = Services.getInstance().getConfig().getTencentCloudTranslationSetting();
        if (tencentCloudTranslationSetting.hasAdvancedSetting()) {
            var termRepoIds = tencentCloudTranslationSetting.termRepoIDs();
            if (termRepoIds != null) {
                for (String id : termRepoIds) {
                    instance.addTermRepo(id);
                }
            }
            var sentRepoIds = tencentCloudTranslationSetting.sentRepoIDs();
            if (sentRepoIds != null) {
                for (String id : sentRepoIds) {
                    instance.addSentRepo(id);
                }
            }
        }
        return instance;
    }

    @NotNull
    @Contract(" -> new")
    public static Translator makeEVEProperNounsTranslator() {
        return getInjector().getInstance(EVEProperNounsTranslator.class);
    }

    /**
     * 检查是否可以根据给定的配置使用翻译器。
     *
     * @param config 全局应用程序配置对象
     * @return 如果配置中存在腾讯云翻译凭据，则返回true，否则返回false
     */
    public static boolean canUseTranslator(@NotNull Config config) {
        return config.hasTencentCloudTranslationSetting();
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
