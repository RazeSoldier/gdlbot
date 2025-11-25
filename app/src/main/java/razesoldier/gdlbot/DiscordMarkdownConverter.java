/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai && Overflow https://github.com/mamoe/mirai && https://github.com/MrXiaoM/Overflow
 */

package razesoldier.gdlbot;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.PartialMember;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordMarkdownConverter {
    public static final String DISCORD_TIME_PATTERN = "<t:(\\d*)(:[tTdDfFR])?>";

    @Nullable
    private Guild guild;

    public DiscordMarkdownConverter() {}

    public DiscordMarkdownConverter(@Nullable Guild guild) {
        this.guild = guild;
    }

    public String convert(String content) {
        content = transformTimeCommand2LocalTime(content);
        if (guild !=  null) {
            content = transformMemberId2Name(content);
        }
        return content;
    }

    public String transformTimeCommand2LocalTime(String content) {
        Matcher matcher = Pattern.compile(DISCORD_TIME_PATTERN).matcher(content);
        return matcher.replaceAll(matchResult -> {
            Instant instant = Instant.ofEpochSecond(Long.parseLong(matchResult.group(1)));
            return LocalDateTime.ofInstant(instant, ZoneId.of("+8")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        });
    }

    public String transformMemberId2Name(String content) {
        Matcher matcher = Pattern.compile("<@(\\d*)>").matcher(content);
        return matcher.replaceAll(matchResult -> "@" + getMemberNameById(Long.valueOf(matchResult.group(1))));
    }

    @Nullable
    @Contract(pure = true)
    private String getMemberNameById(Long memberId) {
        if (guild == null) {
            return String.valueOf(memberId);
        }
        return guild.getMemberById(Snowflake.of(memberId))
                .map(PartialMember::getDisplayName)
                .blockOptional()
                .orElseGet(() -> String.valueOf(memberId));
    }
}
