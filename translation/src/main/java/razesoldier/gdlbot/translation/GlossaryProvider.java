/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.translation;

import com.google.inject.throwingproviders.CheckedProvider;

import java.util.Map;

/**
 * EVE专有名词词汇表提供器的接口
 */
public interface GlossaryProvider extends CheckedProvider<Map<String, String>> {
    Map<String, String> get() throws TranslateException;
}
