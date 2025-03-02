package com.animal.java.common;

import com.animal.java.exception.error.ErrorResponse;
import lombok.Getter;

@Getter
public class ApiErrorResponse extends ErrorResponse {
    public ApiErrorResponse(String message) {
        super(message, null);
    }

    public ApiErrorResponse(String message, String data) {
        super(message, data);
    }
}
