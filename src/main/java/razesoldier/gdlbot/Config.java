/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import java.util.List;

public record Config(Account account, List<Long> messageSubscribeList, String phSiteCookie, TencentCredential tencentCred,
                     Proxy proxy, String discordBotToken, DiscordRelay discordRelay) {
    public record Account(Long qq, String password) {
    }

    public record TencentCredential(String secretId, String secretKey, String region, Long projectId) {
    }

    public record Proxy(String host, Integer port) {
    }

    public record DiscordRelay(String discordServer, List<String> discordChannels, List<Long> downstreamGroups) {
    }
}
