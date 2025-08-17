package com.devteria.post.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component

public class DateTimeFormatter {
    public DateTimeFormatter() {
        strategyMap.put(60L, this::formatInSecound);
        strategyMap.put(3600L, this::formatInMinutes);
        strategyMap.put(86400L, this::formatInHors);
        strategyMap.put(Long.MAX_VALUE,this::formatInDay);
    }
    Map<Long, Function<Instant, String>> strategyMap = new HashMap<>();

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
    public String formatInDay (Instant instant) {
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter.ISO_DATE;
        return localDateTime.format(dateTimeFormatter) + " trước";
    }

    public String format (Instant instant) {
        long elapseSecound = ChronoUnit.SECONDS.between(instant, Instant.now());

        var strategy = strategyMap.entrySet()
                .stream()
                .filter(longFunctionEntry ->elapseSecound < longFunctionEntry.getKey())
                .findFirst().get();
        return strategy.getValue().apply(instant);
    }
}

