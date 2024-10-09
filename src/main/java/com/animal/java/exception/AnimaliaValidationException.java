package com.animal.java.exception;

import com.animal.java.exception.error.ErrorCode;

public class AnimaliaValidationException extends AnimaliaException{

    public AnimaliaValidationException(ErrorCode errorCode){
        super(errorCode);
    }

    public AnimaliaValidationException (ErrorCode errorCode, Object data){
        super(errorCode, data);
    }
}
