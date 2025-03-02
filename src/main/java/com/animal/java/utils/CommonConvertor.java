package com.animal.java.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jooq.Converter;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonConvertor {

    public static final ZdtConvertor ZDT_CONVERTOR = new ZdtConvertor();

    public static class ZdtConvertor implements Converter<Timestamp, ZonedDateTime> {

        public ZonedDateTime from(Date date) {
            return TimeUtils.zonedDateTime(date);
        }

        @Override
        public ZonedDateTime from(Timestamp timestamp) {
            return TimeUtils.zonedDateTime(timestamp);
        }

        @Override
        public Timestamp to(ZonedDateTime userObject) {
            return Optional.ofNullable((userObject))
                    .map(o -> Timestamp.from(o.toInstant()))
                    .orElse(null);
        }

        @Override
        public Class<Timestamp> fromType() {
            return Timestamp.class;
        }

        @Override
        public Class<ZonedDateTime> toType() {
            return ZonedDateTime.class;
        }
    }
}
