/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import java.util.logging.Logger;

/**
 * 服务定位器。单例，可以使用{@link Services#getInstance()}获得此单例
 */
public final class Services {
    private static Services instance;
    private final Logger logger;
    private final Config config;

    public Config getConfig() {
        return config;
    }

    /**
     * 获得全局的{@link Logger}
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * 初始化{@link Services}，必须在程序的入口调用
     */
    public static void setup(Config config, Logger logger) {
        instance = new Services(config, logger);
    }

    public static Services getInstance() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    private Services(Config config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }
}
