/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import com.alibaba.fastjson.JSON;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;

public class JsonArrayBodyHandler<T> implements HttpResponse.BodyHandler<Supplier<List<T>>> {
    private final Class<T> parseType;

    public JsonArrayBodyHandler(Class<T> parseType) {
        this.parseType = parseType;
    }

    @Override
    public HttpResponse.BodySubscriber<Supplier<List<T>>> apply(HttpResponse.ResponseInfo responseInfo) {
        var upstream = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
        return HttpResponse.BodySubscribers.mapping(upstream, (String s) -> () -> JSON.parseArray(s, parseType));
    }
}
