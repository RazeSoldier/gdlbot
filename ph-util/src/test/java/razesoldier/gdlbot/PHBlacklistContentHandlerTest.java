/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai && Overflow https://github.com/mamoe/mirai && https://github.com/MrXiaoM/Overflow
 */

package razesoldier.gdlbot;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
class PHBlacklistContentHandlerTest {
    @Test
    void testParse() throws Throwable {
        Method method = PHBlacklistContentHandler.class.getDeclaredMethod("parseBlacklistFromHtml", String.class);
        method.setAccessible(true);
        Path resPath = Paths.get(Objects.requireNonNull(getClass().getResource("blacklist_test.html")).toURI());
        Supplier<Map<String, BlacklistEntity>> result = (Supplier<Map<String, BlacklistEntity>>) method.invoke(null, Files.readString(resPath));

        assertEquals(1, result.get().size());
        assertInstanceOf(BlacklistEntity.class, result.get().get("Test1"));
        assertEquals("awox", result.get().get("Test1").reason());
    }
}
