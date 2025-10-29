/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai && Overflow https://github.com/mamoe/mirai && https://github.com/MrXiaoM/Overflow
 */

package razesoldier.gdlbot;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageURLHandlerTest {
    @Test
    void testBasic() {
        ImageURLHandler imageURLHandler = new ImageURLHandler(contentProvider());
        assertEquals(1, imageURLHandler.getDiscordAttachmentImageURL().size());
        assertEquals(3, imageURLHandler.getAllImageURL().size());
        assertEquals(2, imageURLHandler.getNonDiscordAttachmentImageURL().size());
    }

    @NotNull
    @Contract(pure = true)
    private static String contentProvider() {
        return """
                This is a test.
                https://cdn.discordapp.com/attachments/1236825864445100132/1376939093992411237/Pankrab_Blue.gif
                https://fake.discordapp.com/attachments/1236825864445100132/1376939093992411237/Pankrab_Blue.gif
                https://docs.mirai.mamoe.net/mirai.png
                Content end
                """;
    }
}
