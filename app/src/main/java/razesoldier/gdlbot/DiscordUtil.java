/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import discord4j.core.object.entity.Attachment;
import discord4j.discordjson.json.MessageData;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class DiscordUtil {
    /**
     * 将Discord消息的附件转换成{@link InputStream}
     */
    @NotNull
    public static List<InputStream> attachment2InputStream(@NotNull List<Attachment> attachments) {
        List<InputStream> list = new ArrayList<>();
        for (Attachment attachment : attachments) {
            boolean isImage = attachment.getContentType().filter(s -> s.startsWith("image")).isPresent();
            if (isImage) {
                list.add(RemoteFileUtil.getInputStream(attachment.getUrl()));
            }
        }
        return list;
    }

    /**
     * 从{@link MessageData}中获取Embed中的图片URL
     * @return 返回一个映射，key是原始URL，value是缩略图URL
     */
    @NotNull
    public static Map<String, String> getEmbedImageURLs(@NotNull MessageData messageData) {
        var content = messageData.content();
        List<URI> attachmentImageURL = new ImageURLHandler(content).getAllImageURL();

        Map<String, String> embedMap = messageData
                .embeds().stream()
                .collect(Collectors.toMap(data -> data.url().get(), data -> data.thumbnail().get().url().get()));
        Map<String, String> result = new HashMap<>();
        attachmentImageURL.forEach(uri -> {
            if (embedMap.containsKey(uri.toString())) {
                result.put(uri.toString(), embedMap.get(uri.toString()));
            }
        });
        return result;
    }

    private DiscordUtil() {}
}
