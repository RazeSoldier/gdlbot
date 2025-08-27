/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import com.github.mizosoft.methanol.Methanol;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 用于访问PH官网的访问器。可以模拟真实请求。
 */
public class PandemicHordeWebsiteAccessor {
    private final String cookie;

    /**
     * 构建一个新的{@link PandemicHordeWebsiteAccessor}
     * @param cookie PH网站的`remember_web_*`cookie，用于模拟真实用户的访问
     */
    public PandemicHordeWebsiteAccessor(String cookie) {
        this.cookie = cookie;
    }

    public HttpResponse<Supplier<UpcomingEventsDatatableModel>> getUpcomingEvents() throws IOException, InterruptedException {
        try (var client = Methanol.newBuilder().build()) {
            var request = HttpRequest
                    .newBuilder()
                    .uri(URI.create("https://www.pandemic-horde.org/events/upcoming/datatable"))
                    .header("cookie", cookie)
                    .build();
            return client.send(request, new JsonObjectBodyHandler<>(UpcomingEventsDatatableModel.class));
        }
    }

    public HttpResponse<Supplier<Map<String, BlacklistEntity>>> getBlacklist() throws IOException, InterruptedException {
        try (var client = Methanol.newBuilder().build()) {
            var request = HttpRequest
                    .newBuilder()
                    .uri(URI.create("https://www.pandemic-horde.org/blacklist"))
                    .header("cookie", cookie)
                    .build();
            return client.send(request, new PHBlacklistContentHandler());
        }
    }
}
