/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai && Overflow https://github.com/mamoe/mirai && https://github.com/MrXiaoM/Overflow
 */

package razesoldier.gdlbot;

import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用程序配置类<br>
 * 字段标记为{@link NotNull @NotNull}的字段必须在配置文件中存在，否则会抛出异常
 */
public class Config {
    @NotNull private final String discordBotToken;
    @NotNull private final String phSiteCookie;
    @Nullable private List<Long> commandSubscribeList;
    @Nullable private ProxySetting proxySetting;
    private final com.typesafe.config.Config innerConfig;
    @Nullable private TencentCloudTranslationSetting tencentCloudTranslationSetting;
    @Nullable private List<DiscordRelaySetting> discordRelaySettings;
    @Nullable private String imgurClientId;
    @NotNull private final Long adminContact;

    public Config(@NotNull com.typesafe.config.Config config) {
        this.discordBotToken = config.getString("discordBotToken");
        this.phSiteCookie = config.getString("ph.site.cookie");
        this.adminContact = config.getLong("adminContact");
        this.innerConfig = config;
    }

    @NotNull
    public static Config initFromConfig(@NotNull com.typesafe.config.Config config) {
        return new Config(config);
    }

    @NotNull
    public String getDiscordBotToken() {
        return discordBotToken;
    }

    @NotNull
    public String getPhSiteCookie() {
        return phSiteCookie;
    }

    public List<Long> getCommandSubscribeList() {
        if (commandSubscribeList == null && innerConfig.hasPath("messageSubscribeList")) {
            commandSubscribeList = innerConfig.getLongList("messageSubscribeList");
        }
        return commandSubscribeList;
    }

    public ProxySetting getProxySetting() {
        if (proxySetting == null && innerConfig.hasPath("proxy.host") && innerConfig.hasPath("proxy.port")) {
            proxySetting = new ProxySetting(
                    innerConfig.hasPath("proxy.type") ? innerConfig.getString("proxy.type") : "socks5",
                    innerConfig.getString("proxy.host"),
                    innerConfig.getInt("proxy.port")
            );
        }
        return proxySetting;
    }

    public TencentCloudTranslationSetting getTencentCloudTranslationSetting() {
        if (tencentCloudTranslationSetting == null) {
            TencentCloudTranslationSetting.Credential credential = new TencentCloudTranslationSetting.Credential(
                    innerConfig.getString("tencentCloudTranslation.credential.secretId"),
                    innerConfig.getString("tencentCloudTranslation.credential.secretKey"),
                    innerConfig.getString("tencentCloudTranslation.credential.region"),
                    innerConfig.getLong("tencentCloudTranslation.credential.projectId")
            );
            tencentCloudTranslationSetting = new TencentCloudTranslationSetting(
                    credential,
                    innerConfig.getStringList("tencentCloudTranslation.termRepoIDs"),
                    innerConfig.getStringList("tencentCloudTranslation.sentRepoIDs")
            );
        }
        return tencentCloudTranslationSetting;
    }

    public boolean hasTencentCloudTranslationSetting() {
        return innerConfig.hasPath("tencentCloudTranslation.credential");
    }

    public List<DiscordRelaySetting> getDiscordRelaySettings() {
        if (discordRelaySettings == null) {
            List<DiscordRelaySetting> list = new ArrayList<>();
            innerConfig.getList("relays").forEach(item -> {
                if (item instanceof ConfigObject object) {
                    DiscordRelaySetting relaySetting = ConfigBeanFactory.create(object.toConfig(), DiscordRelaySetting.class);
                    list.add(relaySetting);
                }
            });
            discordRelaySettings = list;
        }
        return discordRelaySettings;
    }

    @Nullable
    public String getImgurClientId() {
        if (imgurClientId == null) {
            imgurClientId = innerConfig.getString("imgurClientId");
        }
        return imgurClientId;
    }

    public Boolean hasImgurClientId() {
        return imgurClientId != null;
    }

    @NotNull
    public Long getAdminContact() {
        return adminContact;
    }

    public record ProxySetting(String type, String host, Integer port) {}

    public record TencentCloudTranslationSetting(Credential credential, List<String> termRepoIDs, List<String> sentRepoIDs) {
        public record Credential(String secretId, String secretKey, String region, Long projectId) {
        }

        public boolean hasAdvancedSetting() {
            return termRepoIDs != null || sentRepoIDs != null;
        }
    }

    public static class DiscordRelaySetting {
        private List<Long> discordChannels;
        private List<Long> downstreamGroups;

        public List<Long> getDiscordChannels() {
            return discordChannels;
        }

        public void setDiscordChannels(List<Long> discordChannels) {
            this.discordChannels = discordChannels;
        }

        public List<Long> getDownstreamGroups() {
            return downstreamGroups;
        }

        public void setDownstreamGroups(List<Long> downstreamGroups) {
            this.downstreamGroups = downstreamGroups;
        }

        @Override
        public String toString() {
            return "DiscordRelaySetting{" +
                    "discordChannels=" + discordChannels +
                    ", downstreamGroups=" + downstreamGroups +
                    '}';
        }
    }
}
