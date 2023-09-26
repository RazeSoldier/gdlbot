/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.translation;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import razesoldier.gdlbot.Services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EVEProperNounsTranslatorTests {
    @Test
    void testBasic() {
        Services.setup(null, null);
        var translator = getInstance();
        assertEquals("重导", translator.translate("HML"));
        assertEquals("重导 小希", translator.translate("HML Cerberus"));
        assertEquals("优先级: 重导 小希/曲剑 > 隐秘行动T3 加成 > 指挥驱逐 加成 (鹳鸟/Biforsts)  > 麒麟/手术刀 > 轻拦 > NBI 守夜/克勒斯",
                translator.translate("Doctrine: HML Cerberus/Scimitar > Ninja Links > Cmd Destroyer Links (Storks/Biforsts)  > Kirin/Scalpels > Dictors > NBI Vigil/Keres"));
        assertEquals("鹳鸟/天梯 加成 > 曲剑 > 轻拦 > 侦查舰 > 小希 (重导 配置) > 斯威普 > NBI 守夜",
                translator.translate("Stork/Bifrost links > Scimitars > Interdictors > Recons > Cerberuses (HML FITS) > Svipuls > NBI Vigils"));
        assertEquals("Give me 轻拦 and 狞獾", translator.translate("Give me dictors and caracals"));
        assertEquals("轻拦", translator.translate("dictor"));
        assertEquals("聚焦涅槃隐轰", translator.translate("FVB Bombers"));
        assertEquals("Newbean PVP and Fleet Basics", translator.translate("Newbean PVP and Fleet Basics"));
    }

    @Test
    void testCapitalizeFirstLetter() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getCapitalizeFirstLetterMethod();
        assertEquals("Dictor", method.invoke(null, "dictor"));
    }

    @Test
    void testCapitalizeFirstLetterAndPlural() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method capitalizeFirstLetterMethod = getCapitalizeFirstLetterMethod();
        var s = capitalizeFirstLetterMethod.invoke(null, "dictor");
        assertEquals("Dictors", getGetPluralMethod().invoke(null, s));
    }

    @Test
    void testGetPlural() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assertEquals("dictors", getGetPluralMethod().invoke(null, "dictor"));
    }

    @Test
    void testCapitalizeAllLetter() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assertEquals("DICTOR", getCapitalizeAllLetterMethod().invoke(null, "dictor"));
    }

    @Test
    void testCapitalizeAllLetterAndPlural() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method pluralMethod = getGetPluralMethod();
        var s = pluralMethod.invoke(null, "dictor");
        assertEquals("DICTORS", getCapitalizeAllLetterMethod().invoke(null, s));
    }

    @NotNull
    private Method getCapitalizeFirstLetterMethod() throws NoSuchMethodException {
        Method method = EVEProperNounsTranslator.class.getDeclaredMethod("capitalizeFirstLetter", String.class);
        method.setAccessible(true);
        return method;
    }

    @NotNull
    private Method getGetPluralMethod() throws NoSuchMethodException {
        Method method = EVEProperNounsTranslator.class.getDeclaredMethod("getPlural", String.class);
        method.setAccessible(true);
        return method;
    }

    @NotNull
    private Method getCapitalizeAllLetterMethod() throws NoSuchMethodException {
        Method method = EVEProperNounsTranslator.class.getDeclaredMethod("capitalizeAllLetter", String.class);
        method.setAccessible(true);
        return method;
    }

    @NotNull
    @Contract(" -> new")
    private EVEProperNounsTranslator getInstance() {
        return TranslatorFactory.makeEVEProperNounsTranslator();
    }
}
