package com.animal.java.exception.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.Optional;

@Getter
public class ErrorResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer code;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

    public ErrorResponse(ErrorCode errorCode, String message, Object data){
        this.code = errorCode.getCode();
        this.message = Optional.ofNullable(message).orElse(errorCode.getMessage());
        this.data = data;
    }

    public ErrorResponse(ErrorCode errorCode, String message){
        this(errorCode, message, null);
    }

    public ErrorResponse(ErrorCode errorCode, Object data){
        this(errorCode, null, data);
    }

    public ErrorResponse (ErrorCode errorCode){
        this(errorCode, null, null);
    }

    public ErrorResponse(String message, Object data){
        this.message = message;
        this.data = data;
    }
}
