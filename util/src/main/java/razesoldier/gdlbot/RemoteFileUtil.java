/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import org.jetbrains.annotations.NotNull;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * 互联网访问的工具类
 */
public class RemoteFileUtil {
    private static HttpClient httpClient;

    /**
     * 提供url返回该资源的InputStream
     * @apiNote 调用者需要手动关闭InputStream
     */
    @NotNull
    public static InputStream getInputStream(String url) {
        return Objects.requireNonNull(httpClient().get()
                .uri(url)
                .responseContent()
                .aggregate()
                .asInputStream()
                .block());
    }

    /**
     * 批量关闭{@link InputStream}
     */
    public static void closeInputStreams(@NotNull List<InputStream> inputStreams) throws IOException {
        for (InputStream inputStream : inputStreams) {
            inputStream.close();
        }
    }

    /**
     * 获得{@link HttpClient Reactor Netty的HttpClient}单例，此HttpClient已经使用了机器人配置文件所设置的代理
     */
    public static HttpClient httpClient() {
        if (httpClient == null) {
            httpClient = newHttpClient();
        }

        return httpClient;
    }

    /**
     * 获得一个新的{@link HttpClient HttpClient}，此HttpClient已经使用了机器人配置文件所设置的代理
     */
    @NotNull
    public static HttpClient newHttpClient() {
        HttpClient client = HttpClient.create();
        return setProxy(client);
    }

    private static HttpClient setProxy(HttpClient client) {
        String proxyType = System.getenv("PROXY_TYPE");
        String proxyHost;
        Integer proxyPort;
        // 首先尝试从环境变量中获取代理配置，如果获取不到，则尝试从配置文件中获取代理配置
        if (proxyType == null) {
            Config.ProxySetting proxy = Services.getInstance().getConfig().getProxySetting();
            if (proxy == null) {
                // 没有配置代理，则使用无代理的HttpClient
                return client;
            } else {
                proxyType = proxy.type();
                proxyHost = proxy.host();
                proxyPort = proxy.port();
            }
        } else {
            proxyHost = System.getenv("PROXY_HOST");
            proxyPort = Integer.valueOf(System.getenv("PROXY_PORT"));
        }
        String finalProxyType = proxyType;
        return client.proxy(typeSpec -> {
            ProxyProvider.Proxy type;
            if (finalProxyType.equals("socks5")) {
                type = ProxyProvider.Proxy.SOCKS5;
                Services.getInstance().getLogger().info("Using socks5 proxy");
            } else {
                type = ProxyProvider.Proxy.HTTP;
                Services.getInstance().getLogger().info("Using http proxy");
            }
            typeSpec.type(type).host(proxyHost).port(proxyPort).build();
        });
    }

    private RemoteFileUtil() {}
}
