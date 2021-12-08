/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

    public String getUpcomingEvents() throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest
                .newBuilder()
                .uri(URI.create("https://www.pandemic-horde.org/events/upcoming/datatable"))
                .header("cookie", cookie)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}
