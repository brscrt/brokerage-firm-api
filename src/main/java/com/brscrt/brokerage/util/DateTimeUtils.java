package com.brscrt.brokerage.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public interface DateTimeUtils {

    ZoneId ZONE_ID_ISTANBUL = ZoneId.of("Europe/Istanbul");

    DateTimeFormatter DATE_TIME_FORMATTER_NO_SPACE = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    DateTimeFormatter DATE_TIME_FORMATTER_WITH_SPACE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static LocalDateTime getCurrentDate() {
        return LocalDateTime.now(ZONE_ID_ISTANBUL);
    }

    static LocalDateTime getDatetime(String value) {
        return LocalDateTime.parse(value, DATE_TIME_FORMATTER_NO_SPACE);
    }

    static String formatDatetimeWithSpace(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER_WITH_SPACE);
    }

    static LocalDateTime getOrDefaultStartTime(LocalDateTime start) {
        if (Objects.isNull(start)) {
            return getCurrentDate().minusDays(7);
        }
        return start;
    }

    static LocalDateTime getOrDefaultEndTime(LocalDateTime end) {
        LocalDateTime now = getCurrentDate();
        if (Objects.isNull(end) || end.isAfter(now)) {
            return now;
        }
        return end;
    }
}