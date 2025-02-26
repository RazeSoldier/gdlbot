/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import java.util.List;

/**
 * 本机器人配置文件的数据模型
 *
 * @param account 运行QQ机器人的帐号
 * @param messageSubscribeList 订阅指定QQ群（处理命令）
 * @param phSiteCookie www.pandemic-horde.org的`remember_web_*`cookie，用于模拟真实用户的访问
 * @param tencentCred 腾讯云的认证凭据，用来访问腾讯云的机器翻译服务
 * @param proxy Discord机器人的代理配置
 * @param discordBotToken Discord机器人的令牌
 * @param relays Discord机器人的转发配置
 * @param adminContact 机器人管理员的QQ号（可以用于接受错误消息）
 * @param termRepoIDs 指定腾讯云翻译时使用词库的ID列表
 * @param sentRepoIDs 指定腾讯云翻译时使用例句库的ID列表
 */
public record Config(
        Account account,
        List<Long> messageSubscribeList,
        String phSiteCookie,
        TencentCredential tencentCred,
        Proxy proxy,
        String discordBotToken,
        List<DiscordRelay> relays,
        Long adminContact,
        String qqProtocolVersion,
        List<String> termRepoIDs,
        List<String> sentRepoIDs
) {
    public record Account(Long qq, String password) {
    }

    public record TencentCredential(String secretId, String secretKey, String region, Long projectId) {
    }

    /**
     * @param type 代理类型，可允许的值为：http和socks5，如果未指定则默认为socks5
     * @param host 代理IP
     * @param port 代理端口
     */
    public record Proxy(String type, String host, Integer port) {
        public Proxy {
            if (type == null) {
                type = "socks5";
            }
        }
    }

    /**
     * @param discordChannels 需要转发的频道列表
     * @param downstreamGroups 指定转发的目标QQ群
     */
    public record DiscordRelay(List<Long> discordChannels, List<Long> downstreamGroups) {
    }
}
