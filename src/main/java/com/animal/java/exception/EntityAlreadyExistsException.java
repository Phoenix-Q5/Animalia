package com.animal.java.exception;

import com.animal.java.exception.error.ErrorCode;
import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends AnimaliaException{

    private final ErrorCode errorCode;

    private final String id;

    public EntityAlreadyExistsException(ErrorCode errorCode, String message){
        this(errorCode, message, null);
    }

    public EntityAlreadyExistsException(String message, String id){
        this(null, message, id);
    }

    public EntityAlreadyExistsException(ErrorCode errorCode, String message, String id){
        super(message);
        this.errorCode = errorCode;
        this.id = id;
    }
}
