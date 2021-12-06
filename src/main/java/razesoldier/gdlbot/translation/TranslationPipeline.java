/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.translation;

import org.jetbrains.annotations.NotNull;
import razesoldier.gdlbot.Services;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 翻译管道。可以让一条文本按照顺序经过不同翻译器翻译。
 */
public class TranslationPipeline {
    private final List<Translator> translators = new LinkedList<>();

    public TranslationPipeline addTranslator(@NotNull Translator translator) {
        translators.add(translator);
        return this;
    }

    @NotNull
    public String translate(@NotNull String source) {
        var ref = new AtomicReference<>(source);
        translators.forEach(translator -> ref.getAndUpdate(s -> {
            try {
                return translator.translate(s);
            } catch (TranslateException e) {
                Services.getInstance().getLogger().warning(e.getMessage());
                return s;
            }
        }));
        return ref.get();
    }
}
