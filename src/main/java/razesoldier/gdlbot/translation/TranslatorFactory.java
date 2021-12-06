/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.translation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import razesoldier.gdlbot.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

public class TranslatorFactory {
    /**
     * 获得默认的翻译器
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Translator make(@NotNull Config config) {
        return makeTencentTranslator(config);
    }

    @NotNull
    @Contract("_ -> new")
    public static TencentTranslator makeTencentTranslator(@NotNull Config config) {
        return new TencentTranslator(config.tencentCred().secretId(), config.tencentCred().secretKey(),
                config.tencentCred().region(), config.tencentCred().projectId());
    }

    @NotNull
    @Contract(" -> new")
    public static EVEProperNounsTranslator makeEVEProperNounsTranslator() throws TranslateException {
        return new EVEProperNounsTranslator(getEveGlossary());
    }

    private TranslatorFactory() {}

    private static Map<String, String> getEveGlossary() throws TranslateException {
        InputStream inputStream = TranslatorFactory.class.getResourceAsStream("eve_glossary.json");

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            throw new TranslateException(e);
        }

        return JSON.parseObject(stringBuilder.toString(), new TypeReference<>() {});
    }
}
