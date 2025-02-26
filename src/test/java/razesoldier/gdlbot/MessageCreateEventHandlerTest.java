/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
    void testHandleImageLink() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        mockTestServices();
        Method method = MessageCreateEventHandler.class.getDeclaredMethod("handleImageLink", String.class, List.class);
        method.setAccessible(true);
        List<InputStream> inputStreams = new ArrayList<>();
        String res = (String) method.invoke(null, "https://media.discordapp.net/attachments/702512955400519771/1092917248064245831/Pankrab_Red_2.gif\nhttps://media.discordapp.net/attachments/702512433326981151/1102094056642850826/Pankrab_Green_5.gif", inputStreams);
        assertEquals("\n", res);
        assertEquals(2, inputStreams.size());
    }

    private static void mockTestServices() {
        Services.setup(new Config(
                null,
                null,
                null,
                null,
                new Config.Proxy("socks5", "127.0.0.1", 21881),
                null,
                null,
                null,
                null,
                null,
                null
        ), Logger.getGlobal());
    }
}
