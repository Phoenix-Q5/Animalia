package com.animal.java.validation.pipeline;

import com.animal.java.utils.EnumId;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum ValidationCategory implements EnumId {

    SPECIALIZATION,PROFILE, TAGS, TARGET;

    @JsonCreator
    public static ValidationCategory parseId(String id){
        return EnumId.parseId(ValidationCategory.class, id);
    }
}
