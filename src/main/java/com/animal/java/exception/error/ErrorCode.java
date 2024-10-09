package com.animal.java.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter

public enum ErrorCode {

    METHOD_NOT_SUPPORTED(406, "error.methodNotSupported"),
    INTERNAL_SERVER_ERROR(500, "error.internalServerError"),
    CONSTRAINT_VIOLATION(100, "error.validation"),
    ANIMAL_NOT_FOUND(1004, "Animal %s not found"),
    LOCATION_NOT_FOUND(1004, "Location %s not found"),
    SPECIES_NOT_FOUND(1004, "Species %s not found"),
    ANIMAL_EXISTS(1004, "Animal %s exists"),
    UUID_FORMAT_ERROR(1004, "Wrong UUID format %s");

    private final int code;

    private final String message;
}
