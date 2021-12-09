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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

class MessageCreateEventHandlerTest {
    @ParameterizedTest
    @MethodSource("dataProvider")
    void testNormalizedMessageContent(String expected, String input) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = MessageCreateEventHandler.class.getDeclaredMethod("normalizedMessageContent", String.class);
        method.setAccessible(true);
        var res = method.invoke(null, input);
        assertEquals(expected, res);
    }

    @NotNull
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                Arguments.arguments("This is a test .", "This is a test <:hook:12412141>."),
                Arguments.arguments("This is a test .", "This is a test <a:hook:12412141>.")
        );
    }
}
