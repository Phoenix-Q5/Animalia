package com.animal.java.exception;

import com.animal.java.exception.error.ErrorCode;
import lombok.Getter;

import javax.print.attribute.standard.JobKOctets;
import java.io.Serial;

@Getter
public class AnimaliaException extends RuntimeException{

    private ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

    private Object data;

    public AnimaliaException(String msg, Exception exception){
        super(msg, exception);
    }

    public AnimaliaException(String msg){
        super(msg);
    }

    public AnimaliaException(ErrorCode errorCode){
        this(String.valueOf(errorCode), null);
    }

    public AnimaliaException(ErrorCode errorCode, String message){
        this(errorCode, message, null);
    }
    public AnimaliaException (ErrorCode errorCode, Exception message){
        this(errorCode, null, message);
    }

    public AnimaliaException (ErrorCode errorCode, Object[] args){
        this(errorCode, String.format(errorCode.getMessage(), args));
    }

    public AnimaliaException (ErrorCode errorCode, Object[] args, Exception cause){
        this(errorCode, String.format(errorCode.getMessage(), args), cause);
    }

    protected AnimaliaException(ErrorCode errorCode, Object data){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = data;
    }

    protected AnimaliaException(ErrorCode errorCode, Object data, Exception cause){
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.data = data;
    }

    protected AnimaliaException(ErrorCode errorCode, String message, Object data, Exception cause, boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
        this.data = data;
    }

    public <T> T getTypedData(){
        return (T) data;
    }
}
