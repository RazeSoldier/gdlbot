/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.translation;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EVEProperNounsTranslatorTests {
    @Test
    void testBasic() throws TranslateException {
        var translator = getInstance();
        assertEquals("重导", translator.translate("HML"));
        assertEquals("重导 小希", translator.translate("HML Cerberus"));
        assertEquals("优先级: 重导 小希/曲剑 > 隐秘加成T3 > 指挥驱逐舰 (鹳鸟s/Biforsts)  > 麒麟/手术刀 > 轻拦 > NBI 守夜/克勒斯",
                translator.translate("Doctrine: HML Cerberus/Scimitar > Ninja Links > Cmd Destroyer Links (Storks/Biforsts)  > Kirin/Scalpels > Dictors > NBI Vigil/Keres"));
        assertEquals("鹳鸟/天梯 links > 曲剑s > 轻拦 > 侦查舰 > 小希es (重导 FITS) > 斯威普s > NBI 守夜",
                translator.translate("Stork/Bifrost links > Scimitars > Interdictors > Recons > Cerberuses (HML FITS) > Svipuls > NBI Vigils"));
    }

    @NotNull
    @Contract(" -> new")
    private EVEProperNounsTranslator getInstance() throws TranslateException {
        return TranslatorFactory.makeEVEProperNounsTranslator();
    }
}
