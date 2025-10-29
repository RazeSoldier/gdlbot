/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai && Overflow https://github.com/mamoe/mirai && https://github.com/MrXiaoM/Overflow
 */

package razesoldier.gdlbot;

import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.MessageData;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 本类用于处理消息文本中的图片链接，将其转换成图片
 */
public class ImageLinkProcessor {
    private String content;
    private final Message message;

    public ImageLinkProcessor(String content, Message message) {
        this.content = content;
        this.message = message;
    }

    public Result process() {
        List<InputStream> inputStreams = new ArrayList<>();
        handleEmbed(inputStreams);
        try {
            handleImgurLink(inputStreams);
        } catch (ImgurApiException e) {
            Services.getInstance().getLogger().warning(e.getMessage());
        }
        return new Result(inputStreams, content);
    }

    private void handleEmbed(List<InputStream> inputStreams) {
        // 这里请求HTTP API获取消息数据而不是直接使用Gateway传递过来的消息数据是因为Gateway传递过来的消息数据中有可能不包含Embed
        MessageData messageData = message.getRestMessage().getData().block();
        if (messageData == null) {
            return;
        }
        List<EmbedData> embedData = messageData.embeds();
        if (embedData.isEmpty()) {
            return;
        }
        Map<String, String> imageURLs = DiscordUtil.getEmbedImageURLs(messageData);

        AtomicReference<String> ref = new AtomicReference<>(content);
        imageURLs.forEach((originUrl, thumbUrl) -> {
            inputStreams.add(RemoteFileUtil.getInputStream(thumbUrl));
            ref.set(content.replace(originUrl, "\uD83D\uDCCE" + getFilenameFromUri(originUrl))); // 📎+文件名
        });
        content = ref.get();
    }

    private void handleImgurLink(List<InputStream> inputStreams) throws ImgurApiException {
        Pattern pattern = Pattern.compile("https://imgur.com/(\\w*)");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String link = new ImgurApi(Services.getInstance().getConfig().getImgurClientId()).getImageLink(matcher.group(1));
            inputStreams.add(RemoteFileUtil.getInputStream(link));
        }
        content = matcher.replaceAll("");
    }

    @NotNull
    private String getFilenameFromUri(@NotNull String uri) {
        return Path.of(URI.create(uri).getPath()).getFileName().toString();
    }

    public record Result(List<InputStream> imageInputStreams, String processedContent) {
    }
}
