package com.animal.java.utils.validation;

import com.animal.java.utils.validation.NoNull;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoNullValidator implements ConstraintValidator<NoNull, Object> {

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context){
        return object != null;
    }
}
