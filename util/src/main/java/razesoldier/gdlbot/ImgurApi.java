/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import com.alibaba.fastjson2.JSON;
import reactor.netty.http.client.HttpClient;

import java.util.Optional;

public class ImgurApi {
    private final String clientId;

    public ImgurApi(String clientId) {
        this.clientId = clientId;
    }

    public String getImageLink(String imageId) throws ImgurApiException {
        HttpClient.ResponseReceiver<?> responseReceiver = RemoteFileUtil.newHttpClient()
                .headers(headers -> headers.set("Authorization", "Client-ID " + clientId))
                .get()
                .uri("https://api.imgur.com/3/image/" + imageId);
        Optional<Integer> headerOption = responseReceiver.response().map(resp -> resp.status().code())
                .blockOptional();
        if (headerOption.isEmpty()) {
            throw new ImgurApiException("[ImgurApi#getImageLink] Imgur API return empty header");
        }
        if (headerOption.get() != 200) {
            throw new ImgurApiException("[ImgurApi#getImageLink] Imgur API return status code %d".formatted(headerOption.get()));
        }
        String responseBody = responseReceiver
                .responseContent()
                .aggregate()
                .asString()
                .block();
        if (responseBody == null) {
            throw new ImgurApiException("[ImgurApi#getImageLink] Imgur API return empty response body");
        }

        ImageInformation information = JSON.parseObject(responseBody, ImageInformation.class);
        if (Boolean.FALSE.equals(information.success)) {
            throw new ImgurApiException("[ImgurApi#getImageLink(%s)] %s".formatted(imageId, information.data.error));
        }
        return information.data.link;
    }

    public record ImageInformation(Integer status, Boolean success, Data data) {
        public record Data(String link, Long size, String error) {}
    }
}
