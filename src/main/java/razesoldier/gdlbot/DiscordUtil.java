/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot;

import discord4j.core.object.entity.Attachment;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

    private DiscordUtil() {}
}
