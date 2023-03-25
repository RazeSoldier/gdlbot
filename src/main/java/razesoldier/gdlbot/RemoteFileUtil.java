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
     * 获得{@link HttpClient Reactor Netty的HttpClient}，此HttpClient已经使用了机器人配置文件所设置的代理
     */
    public static HttpClient httpClient() {
        if (httpClient == null) {
            Config.Proxy proxy = Services.getInstance().getConfig().proxy();
            httpClient = HttpClient.create()
                    .proxy(typeSpec -> {
                        ProxyProvider.Proxy proxyType;
                        if (proxy.type().equals("socks5")) {
                            proxyType = ProxyProvider.Proxy.SOCKS5;
                            Services.getInstance().getLogger().info("Using socks5 proxy");
                        } else {
                            proxyType = ProxyProvider.Proxy.HTTP;
                            Services.getInstance().getLogger().info("Using http proxy");
                        }
                        typeSpec.type(proxyType).host(proxy.host()).port(proxy.port()).build();
                    });
        }

        return httpClient;
    }

    private RemoteFileUtil() {}
}
