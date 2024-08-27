/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import com.google.common.collect.ImmutableMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 用来处理PH联盟黑名单内容
 */
public class PHBlacklistContentHandler implements HttpResponse.BodyHandler<Supplier<Map<String, BlacklistEntity>>> {
    @Override
    public BodySubscriber<Supplier<Map<String, BlacklistEntity>>> apply(HttpResponse.ResponseInfo responseInfo) {
        var upstream = BodySubscribers.ofString(StandardCharsets.UTF_8);
        return BodySubscribers.mapping(upstream, s -> () -> {
            Document rootDocument = Jsoup.parse(s);
            Element tableBody = rootDocument.getElementsByTag("tbody").first();// 首先获得黑名单列表的表格
            if (tableBody == null) {
                throw new DOMException("Failed to find tbody element");
            }
            Elements rows = tableBody.getElementsByTag("tr");// 然后获得包含每一行的列表
            // 最后遍历所有行将结果存入Map，key是被ban的角色名
            Map<String, BlacklistEntity> map = new HashMap<>();
            for (Element row : rows) {
                Elements columns = row.getElementsByTag("td");
                map.put(columns.get(0).text(), new BlacklistEntity(columns.get(0).text(), columns.get(1).text(), columns.get(2).text(), columns.get(3).text()));
            }
            return ImmutableMap.copyOf(map);
        });
    }
}
