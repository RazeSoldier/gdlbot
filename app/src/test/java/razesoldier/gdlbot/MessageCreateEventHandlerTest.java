/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import com.typesafe.config.ConfigFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageCreateEventHandlerTest {
    @ParameterizedTest
    @MethodSource("testRemoveUnsupportedStringDataProvider")
    void testRemoveUnsupportedString(String expected, String input) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = MessageCreateEventHandler.class.getDeclaredMethod("removeUnsupportedString", String.class);
        method.setAccessible(true);
        var res = method.invoke(null, input);
        assertEquals(expected, res);
    }

    @NotNull
    static Stream<Arguments> testRemoveUnsupportedStringDataProvider() {
        return Stream.of(
                Arguments.arguments("This is a test .", "This is a test <:hook:12412141>."),
                Arguments.arguments("This is a test .", "This is a test <a:hook:12412141>.")
        );
    }

    @ParameterizedTest
    @MethodSource("testTimeCommandTransformDataProvider")
    void testTimeCommandTransform(String expected, String input) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = MessageCreateEventHandler.class.getDeclaredMethod("transformTimeCommand2LocalTime", String.class);
        method.setAccessible(true);
        Object res = method.invoke(null, input);
        assertEquals(expected, res);
    }

    @NotNull
    static Stream<Arguments> testTimeCommandTransformDataProvider() {
        return Stream.of(
                Arguments.arguments("2023-03-24 19:30", "<t:1679657400:R>"),
                Arguments.arguments("Good chance of fight tomorrow in Cn timezone, hostiles are prepinging. Timer 2023-03-24 19:30",
                        "Good chance of fight tomorrow in Cn timezone, hostiles are prepinging. Timer <t:1679657400:R>")
        );
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "IMGUR_CLIENT_ID", matches = ".*")
    void testHandleImgurLink() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        mockTestServices();
        Method method = MessageCreateEventHandler.class.getDeclaredMethod("handleImgurLink", String.class, List.class);
        method.setAccessible(true);
        List<InputStream> inputStreams = new ArrayList<>();
        String res = (String) method.invoke(null, "https://imgur.com/CVRqEcb", inputStreams);
        assertEquals("", res);
        res = (String) method.invoke(null, "https://imgur.com/CVRqEcb test", inputStreams);
        assertEquals(" test", res);
    }

    private static void mockTestServices() {
        Map<String, Object> fakeConfigMap = Map.of("discordBotToken", "test", "ph.site.cookie", "test", "adminContact", 1);
        var fakeConfig = new Config(ConfigFactory.parseMap(fakeConfigMap));
        Services.setup(fakeConfig, Logger.getGlobal());
    }
}
