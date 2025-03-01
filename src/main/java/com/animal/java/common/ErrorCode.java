package com.animal.java.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    UNKNOWN_ERROR(1000,"Unknown error"),
    INVALID_INPUT(1000,"Invalid input"),
    INVALID_REQUEST(1000,"Invalid request"),
    INVALID_RESPONSE(1000,"Invalid response"),
    INVALID_OPERATION(1000,"Invalid operation"),
    INVALID_STATE(1000,"Invalid state"),
    INVALID_CONFIGURATION(1000,"Invalid configuration"),
    INVALID_DATA(1000,"Invalid data"),
    INVALID_PARAMETER(1000,"Invalid parameter"),
    INVALID_ARGUMENT(1000,"Invalid argument"),
    INVALID_FORMAT(1000,"Invalid format"),
    INVALID_TYPE(1000,"Invalid type"),
    INVALID_VALUE(1000,"Invalid value"),
    INVALID_LENGTH(1000,"Invalid length"),
    INVALID_SIZE(1000,"Invalid size"),
    INVALID_RANGE(1000,"Invalid range"),
    INVALID_NUMBER(1000,"Invalid number"),
    INVALID_DATE(1000,"Invalid date"),
    INVALID_TIME(1000,"Invalid time"),
    INVALID_TIMESTAMP(1000,"Invalid timestamp"),
    INVALID_DURATION(1000,"Invalid duration"),
    INVALID_PERIOD(1000,"Invalid period"),
    INVALID_INTERVAL(1000,"Invalid interval"),
    INVALID_FREQUENCY(1000,"Invalid frequency"),
    INVALID_PATTERN(1000,"Invalid pattern"),
    INVALID_EXPRESSION(1000,"Invalid expression"),
    INVALID_URL(1000,"Invalid URL"),
    INVALID_EMAIL(1000,"Invalid email"),
    INVALID_PHONE(1000,"Invalid phone"),
    INVALID_ADDRESS(1000,"Invalid address"),
    INVALID_CURRENCY(1000,"Invalid currency"),
    INVALID_LOCALE(1000,"Invalid locale"),
    INVALID_TIMEZONE(1000,"Invalid timezone"),
    INVALID_ENCODING(1000,"Invalid encoding"),
    INVALID_ENCRYPTION(1000,"Invalid encryption"),
    INVALID_HASHING(1000,"Invalid hashing"),
    INVALID_COMPRESSION(1000,"Invalid compression"),
    INVALID_SERIALIZATION(1000,"Invalid serialization"),
    INVALID_DESERIALIZATION(1000,"Invalid deserialization"),
    INVALID_ENVELOPE(1000,"Invalid envelope"),
    INVALID_HEADER(1000,"Invalid header"),
    INVALID_FOOTER(1000,"Invalid footer"),
    INVALID_PAYLOAD(1000,"Invalid payload"),
    INVALID_SIGNATURE(1000,"Invalid signature"),
    INVALID_CERTIFICATE(1000,"Invalid certificate"),
    INVALID_KEY(1000,"Invalid key"),
    INVALID_TOKEN(1000,"Invalid token"),
    INVALID_SESSION(1000,"Invalid session"),
    INVALID_COOKIE(1000,"Invalid cookie"),
    INVALID_ATTRIBUTE(1000,"Invalid attribute"),
    INVALID_PROPERTY(1000,"Invalid property"),
    INVALID_FIELD(1000,"Invalid field"),
    INVALID_COLUMN(1000,"Invalid column"),
    INVALID_ROW(1000,"Invalid row"),
    INVALID_CELL(1000,"Invalid cell"),
    INVALID_INDEX(1000,"Invalid index"),
    INVALID_REFERENCE(1000,"Invalid reference"),
    INVALID_POINTER(1000,"Invalid pointer"),
    INVALID_CURSOR(1000,"Invalid cursor"),
    INVALID_POSITION(1000,"Invalid position"),
    INVALID_OFFSET(1000,"Invalid offset"),
    INVALID_LIMIT(1000,"Invalid limit"),
    INVALID_COUNT(1000,"Invalid count"),

    VALIDATION_FAILURE(1001,"Validation failure");

    private final int code;
    private final String description;
}
