/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.translation;

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
        glossary.forEach((en, zh) -> ref.getAndUpdate(s -> s.replace(en, zh)));
        return ref.get();
    }
}
