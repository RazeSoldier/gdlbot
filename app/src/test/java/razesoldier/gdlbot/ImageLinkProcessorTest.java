/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai && Overflow https://github.com/mamoe/mirai && https://github.com/MrXiaoM/Overflow
 */

package razesoldier.gdlbot;

import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageLinkProcessorTest {
    @Test
    @EnabledIfEnvironmentVariable(named = "IMGUR_CLIENT_ID", matches = ".*")
    void testHandleImgurLink() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Services.setup(new Config(mockConfig()), null);
        Method method = ImageLinkProcessor.class.getDeclaredMethod("handleImgurLink", List.class);
        method.setAccessible(true);
        List<InputStream> inputStreams = new ArrayList<>();
        var mockObject1 = new ImageLinkProcessor("https://imgur.com/CVRqEcb", null);
        String res = (String) method.invoke(mockObject1, inputStreams);
        assertEquals("", res);
        assertEquals(1, inputStreams.size());
        var mockObject2 = new ImageLinkProcessor("https://imgur.com/CVRqEcb test", null);
        res = (String) method.invoke(mockObject2, inputStreams);
        assertEquals(" test", res);
        assertEquals(2, inputStreams.size());
    }

    private com.typesafe.config.Config mockConfig() {
        return ConfigFactory.parseMap(Map.of("discordBotToken", "test", "ph.site.cookie", "test", "adminContact", 123L));
    }
}
