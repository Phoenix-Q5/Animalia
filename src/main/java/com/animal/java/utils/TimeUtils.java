package com.animal.java.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeUtils {

    public static final ZoneId DEFAULT_ZONE_ID = UTC;
    public static final Locale DEFAULT_LOCALE = Locale.US;

    @NonNull
    public static ZonedDateTime now() {
        ZonedDateTime dt = ZonedDateTime.now(DEFAULT_ZONE_ID);
        return dt.withNano((dt.getNano() / 1_000) * 1_000);
    }

    public static ZonedDateTime startDay(LocalDate dt) {
        return dt.atStartOfDay(DEFAULT_ZONE_ID);
    }

    public static ZonedDateTime zonedDateTime(long epochMilli) {
        return ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(epochMilli), DEFAULT_ZONE_ID);
    }

    public static ZonedDateTime zonedDateTime(Instant instant){
        return Optional.ofNullable(instant).map(i -> ZonedDateTime.ofInstant(i, DEFAULT_ZONE_ID)).orElse(null);
    }

    public static ZonedDateTime zonedDateTime(java.util.Date date) {
        return Optional.ofNullable(date)
                .map(d -> d instanceof java.sql.Date ?
                        startDay(((java.sql.Date) d).toLocalDate()) : zonedDateTime(d.toInstant())).orElse(null);
    }
}
