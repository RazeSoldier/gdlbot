/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai && Overflow https://github.com/mamoe/mirai && https://github.com/MrXiaoM/Overflow
 */

package razesoldier.gdlbot;

import com.typesafe.config.ConfigFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ConfigLoader {
    /**
     * 从当前工作目录下尝试加载`application.conf`文件
     */
    @Contract(" -> new")
    public static @NotNull Config load() {
        return new ConfigLoader("application.conf").realLoadConfig();
    }

    public ConfigLoader(String configPath) {
        System.setProperty("config.file", configPath);
    }

    @Contract(" -> new")
    private @NotNull Config realLoadConfig() {
        return Config.initFromConfig(ConfigFactory.load());
    }
}
