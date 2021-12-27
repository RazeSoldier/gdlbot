/*
  Author: RazeSoldier (razesoldier@outlook.com)
  License: AGPLv3
  Use Mirai https://github.com/mamoe/mirai
 */

package razesoldier.gdlbot.command;

import com.alibaba.fastjson.JSON;
import net.mamoe.mirai.contact.Contact;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import razesoldier.gdlbot.PandemicHordeWebsiteAccessor;
import razesoldier.gdlbot.Services;
import razesoldier.gdlbot.UpcomingEventsDatatableModel;
import razesoldier.gdlbot.translation.TranslateException;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.logging.Logger;

/**
 * 处理`.event`命令
 */
class GetUpcomingEventCommand implements Command {
    private static final String SPLIT_LINE = "----------------\n";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC);

    private final Logger logger;
    private final String cookie;
    private Contact contact;

    GetUpcomingEventCommand(Logger logger, String cookie) {
        this.logger = logger;
        this.cookie = cookie;
    }

    @Override
    public void execute() {
        String upcomingEvents;
        try {
            upcomingEvents = new PandemicHordeWebsiteAccessor(cookie).getUpcomingEvents();
        } catch (IOException e) {
            logger.warning(String.format("Can't access PH site, reason: %s", e.getMessage()));
            return;
        } catch (InterruptedException e) {
            logger.warning(String.format("Can't access PH site, reason: %s", e.getMessage()));
            Thread.currentThread().interrupt();
            return;
        }

        StringBuilder enMessage = new StringBuilder();
        StringBuilder zhMessage = new StringBuilder();
        enMessage.append("即将到来的PH活动").append("\n").append(SPLIT_LINE);
        JSON.parseObject(upcomingEvents, UpcomingEventsDatatableModel.class)
                .data()
                .stream()
                .map(data -> {
                    var time = ZonedDateTime.parse(html2text(data.start_time()), DATE_TIME_FORMATTER)
                            .withZoneSameInstant(ZoneId.of("+8"))
                            .toLocalDateTime()
                            .format(DATE_TIME_FORMATTER);
                    return new UpcomingEvent(html2text(data.name()), html2text(data.op_type()), time, html2text(data.fleet_priority()));
                })
                .sorted(Comparator.comparing(o -> o.time))
                .forEach(event -> {
                            enMessage.append("名称：").append(event.name).append("\n")
                                    .append("类型：").append(event.type).append("\n")
                                    .append("优先级：").append(event.fleetPriority).append("\n")
                                    .append("时间：").append(event.time).append("\n")
                                    .append(SPLIT_LINE);
                            try {
                                zhMessage.append("名称：").append(translate(event.name)).append("\n")
                                        .append("类型：").append(translate(event.type)).append("\n")
                                        .append("优先级：").append(translatePriority(event.fleetPriority)).append("\n")
                                        .append("时间：").append(event.time).append("\n")
                                        .append(SPLIT_LINE);
                            } catch (TranslateException e) {
                                logger.warning(e.getMessage());
                            }
                        }
                );

        contact.sendMessage(enMessage.append("腾讯云翻译：\n-----------------------\n").append(zhMessage).toString());
    }

    /**
     * 将HTML转换为纯文本
     */
    @NotNull
    private String html2text(@NotNull String html) {
        return Jsoup.parse(html).text();
    }

    private String translate(String source) throws TranslateException {
        return Services.getInstance().getTranslator().translate(source);
    }

    /**
     * 翻译优先级
     */
    private String translatePriority(String source) {
        source = source.toLowerCase();
        switch (source) {
            case "normal":
                return "普通";
            case "krab-pen":
                return "绿色";
            case "hype-pen":
                return "金色";
            case "red-pen":
                return "红色";
            case "pink-pen":
                return "粉色";
            default:
                var tempSource = source;
                logger.warning(() -> String.format("GetUpcomingEventCommand#translatePriority doesn't handle '%s'", tempSource));
                return source;
        }
    }

    @Override
    public void setRecipient(Contact contact) {
        this.contact = contact;
    }

    private record UpcomingEvent(String name, String type, String time, String fleetPriority) {
    }
}
