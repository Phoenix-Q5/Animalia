package com.animal.java.model.enumConstants;

import com.animal.java.utils.EnumId;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AnimalClass implements EnumId {

    AGNATHA ("Jaw-less fish"),
    CHRONDRICHTYES ("Cartilaginous Fish"),
    OSTEICHTHYES ("Bony Fish"),
    AMPHIBIA ("Amphibians"),
    REPTILIA ("Reptiles"),
    AVES ("Birds"),
    MAMMALIA ("Mammals");

    private final String description;

    @JsonCreator
    public static AnimalClass parseId(String id){
        return EnumId.parseId(AnimalClass.class, id);
    }

    public static AnimalClass parseDescription(String description){
        for(AnimalClass a: values()){
            if(a.getDescription().equalsIgnoreCase(description))
                return a;
        }
        return null;
    }
}
