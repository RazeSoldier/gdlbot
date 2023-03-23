/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
}
