package com.animal.java.exception;

import com.animal.java.exception.error.ErrorCode;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends AnimaliaException{

    private final ErrorCode errorCode;

    public EntityNotFoundException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }
}
