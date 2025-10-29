/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai && Overflow https://github.com/mamoe/mirai && https://github.com/MrXiaoM/Overflow
 */

package razesoldier.gdlbot;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageURLHandler {
    private final String content;
    private List<URI> imageURLs;

    public ImageURLHandler(String content) {
        this.content = content;
    }

    public List<URI> getAllImageURL() {
        if (imageURLs != null) {
            return imageURLs;
        }
        Pattern pattern = Pattern.compile("https?://([a-zA-Z0-9.-]+)(:(\\d+))?(/\\S*)?"); // 匹配URL
        Matcher matcher = pattern.matcher(content);
        List<URI> result = new ArrayList<>();
        while (matcher.find()) {
            URI uri = URI.create(matcher.group());
            // 如果URL指向一个图片，则添加到列表中
            if (Pattern.matches(".*\\.(jpg|jpeg|png|gif)$", uri.getPath())) {
                result.add(uri);
            }
        }
        imageURLs = result;
        return imageURLs;
    }

    /**
     * 获得文本中所有Discord附件的图片URL
     */
    public List<URI> getDiscordAttachmentImageURL() {
        if (imageURLs == null) {
            getAllImageURL();
        }
        return imageURLs.stream()
                .filter(uri -> (uri.getHost().equals("cdn.discordapp.com") || uri.getHost().equals("media.discordapp.net")) && uri.getPath().startsWith("/attachments/"))
                .toList();
    }

    /**
     * 获得文本中所有非Discord附件的图片URL
     */
    public List<URI> getNonDiscordAttachmentImageURL() {
        if (imageURLs == null) {
            getAllImageURL();
        }
        return imageURLs.stream()
                .filter(uri -> (!uri.getHost().equals("cdn.discordapp.com") && !uri.getHost().equals("media.discordapp.net")) || !uri.getPath().startsWith("/attachments/"))
                .toList();
    }
}
