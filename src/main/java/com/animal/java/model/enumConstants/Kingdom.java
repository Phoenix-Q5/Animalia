package com.animal.java.model.enumConstants;

import com.animal.java.utils.EnumId;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Kingdom implements EnumId {
    Animal,
    Plants,
    Fungi,
    Protista,
    Eubacteria,
    Archaebacteria;

    @JsonCreator
    public static Kingdom parseId(String id){
        return EnumId.parseId(Kingdom.class, id);
    }
}
