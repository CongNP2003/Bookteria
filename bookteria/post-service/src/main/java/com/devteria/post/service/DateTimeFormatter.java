package com.devteria.post.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Component

public class DateTimeFormatter {
    public DateTimeFormatter() {
        strategyMap.put(60L, this::formatInSecound);
        strategyMap.put(3600L, this::formatInMinutes);
        strategyMap.put(86400L, this::formatInHors);
        strategyMap.put(2592000L, this::formatInDays);
        strategyMap.put(31536000L, this::formatInMonths);
        strategyMap.put(Long.MAX_VALUE, this::formatInYears);
    }
    Map<Long, Function<Instant, String>> strategyMap = new LinkedHashMap<>();

    public String formatInSecound (Instant instant) {
        long elapseSecound = ChronoUnit.SECONDS.between(instant, Instant.now());
        return elapseSecound + " giây trước";
    }
    public String formatInMinutes (Instant instant) {
        long elapseSecound = ChronoUnit.MINUTES.between(instant, Instant.now());
        return elapseSecound + " phút trước";
    }
    public String formatInHors (Instant instant) {
        long elapseSecound = ChronoUnit.HOURS.between(instant, Instant.now());
        return elapseSecound + " giờ trước";
    }
//    public String formatInDay (Instant instant) {
//        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
//        java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter.ISO_DATE;
//        return localDateTime.format(dateTimeFormatter) + " trước";
//    }

    private String formatInDays(Instant instant) {
        long elapsed = ChronoUnit.DAYS.between(instant, Instant.now());
        return elapsed + " ngày trước";
    }

    private String formatInMonths(Instant instant) {
        long elapsedDays = ChronoUnit.DAYS.between(instant, Instant.now());
        long months = elapsedDays / 30;
        return months + " tháng trước";
    }

    private String formatInYears(Instant instant) {
        long elapsedDays = ChronoUnit.DAYS.between(instant, Instant.now());
        long years = elapsedDays / 365;
        return years + " năm trước";
    }
    public String format (Instant instant) {
        long elapseSecound = ChronoUnit.SECONDS.between(instant, Instant.now());

        var strategy = strategyMap.entrySet()
                .stream()
                .filter(longFunctionEntry -> elapseSecound < longFunctionEntry.getKey())
                .findFirst().get();
        return strategy.getValue().apply(instant);
    }
}

