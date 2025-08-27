/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import com.alibaba.fastjson2.JSON;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class JsonObjectBodyHandler <T> implements HttpResponse.BodyHandler<Supplier<T>> {
    private final Class<T> parseType;

    public JsonObjectBodyHandler(Class<T> parseType) {
        this.parseType = parseType;
    }

    @Override
    public HttpResponse.BodySubscriber<Supplier<T>> apply(HttpResponse.ResponseInfo responseInfo) {
        var upstream = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
        return HttpResponse.BodySubscribers.mapping(upstream, (String s) -> () -> JSON.parseObject(s, parseType));
    }
}
