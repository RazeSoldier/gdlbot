/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.command;

import net.mamoe.mirai.message.data.Message;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import razesoldier.gdlbot.Config;

import java.util.logging.Logger;

public class CommandFactory {
    private final Logger logger;
    private final Config config;

    public CommandFactory(Logger logger, Config config) {
        this.logger = logger;
        this.config = config;
    }

    @NotNull
    @Contract("_ -> new")
    public Command newFromMessage(@NotNull Message message) {
        var msg = message.contentToString();
        if (!msg.startsWith(".")) {
            return new EmptyCommand();
        }
        if (msg.equals(".event")) {
            return new GetUpcomingEventCommand(logger, config.phSiteCookie());
        }
        return new EmptyCommand();
    }
}
