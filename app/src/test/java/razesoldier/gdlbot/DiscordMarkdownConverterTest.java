/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai && Overflow https://github.com/mamoe/mirai && https://github.com/MrXiaoM/Overflow
 */

package razesoldier.gdlbot;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscordMarkdownConverterTest {
    @Test
    void testTransformTimeCommand2LocalTime() {
        var converter = new DiscordMarkdownConverter();
        assertEquals("2021-04-21 05:20", converter.convert("<t:1618953630:R>"));
        assertEquals("2021-04-21 05:20", converter.convert("<t:1618953630:d>"));
        assertEquals("<t:1618953630:a>", converter.convert("<t:1618953630:a>"));
    }
}
