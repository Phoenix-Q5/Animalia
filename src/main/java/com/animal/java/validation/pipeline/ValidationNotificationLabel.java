package com.animal.java.validation.pipeline;

import com.animal.java.utils.EnumId;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum ValidationNotificationLabel implements EnumId {

    INVALID,

    WRONG_FORMAT,

    AMIMAL_IN_EDIT,

    INVALID_ANIMAL,

    DRAFT_ANIMAL,

    NO_DISCREPANCY;

    @JsonCreator
    public static ValidationNotificationLabel parseId(String id){
        return EnumId.parseId(ValidationNotificationLabel.class, id);
    }
}
