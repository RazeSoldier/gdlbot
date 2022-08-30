/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.translation;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.throwingproviders.CheckedProvides;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import razesoldier.gdlbot.Config;
import razesoldier.gdlbot.Services;

import javax.inject.Qualifier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Objects;

/**
 * 用于{@link com.google.inject.Injector}的模块文件
 */
class TranslatorModule extends AbstractModule {
    private final Config config;

    TranslatorModule() {
        config = Services.getInstance().getConfig();
    }

    @Override
    protected void configure() {
        // create & install a module that uses the @CheckedProvides methods
        install(ThrowingProviderBinder.forModule(this));
    }

    @CheckedProvides(GlossaryProvider.class)
    @Glossary
    Map<String, String> providerGlossary() throws TranslateException {
        StringBuilder stringBuilder;
        try (InputStream inputStream = TranslatorFactory.class.getResourceAsStream("eve_glossary.json")) {
            stringBuilder = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
                String line;
                while ((line = in.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
        }  catch (IOException e) {
            throw new TranslateException(e);
        }

        return JSON.parseObject(stringBuilder.toString(), new TypeReference<>() {});
    }

    @Provides
    @TencentCredential
    Config.TencentCredential provideTencentCredential() {
        return config.tencentCred();
    }

    @Qualifier
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Glossary {}

    @Qualifier
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TencentCredential {}
}
