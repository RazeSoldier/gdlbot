/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.translation;

import org.atteo.evo.inflector.English;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 用于翻译EVE专有名词
 */
class EVEProperNounsTranslator implements Translator {
    private final Map<String, String> glossary;

    EVEProperNounsTranslator(Map<String, String> glossary) {
        this.glossary = glossary;
    }

    @Override
    public String translate(String source) {
        var ref = new AtomicReference<>(source);
        glossary.forEach((en, zh) -> {
            // en为小写。需要计算出en的首字母大写、首字母大写复数形式、复数形式、全部字母大写、全部字母大写复数形式
            String[] targets = {getPlural(capitalizeFirstLetter(en)), getPlural(en), capitalizeAllLetter(getPlural(en)),
                    capitalizeFirstLetter(en), capitalizeAllLetter(en), en};
            for (String target : targets) {
                var before = ref.get();
                var after = before.replace(target, zh);
                // 如果替换成功则直接跳过剩下的target
                if (!before.equals(after)) {
                    ref.set(after);
                    break;
                }
            }
        });
        return ref.get();
    }

    /**
     * 首字母大写
     */
    @NotNull
    private static String capitalizeFirstLetter(@NotNull String source) {
        var firstLetter = source.substring(0, 1);
        return firstLetter.toUpperCase() + source.substring(1);
    }

    /**
     * 获得source的复数形式
     */
    private static String getPlural(@NotNull String source) {
        return English.plural(source);
    }

    /**
     * 字母全部大写
     */
    @NotNull
    private static String capitalizeAllLetter(@NotNull String source) {
        return source.toUpperCase();
    }
}
