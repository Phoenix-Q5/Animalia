package com.animal.java.model.enumConstants;

import com.animal.java.utils.EnumId;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Phylum implements EnumId {
    PORIFERA ("Porifera"),
    CNIDARIA ("Cnidaria"),
    CTENOPHORA("Ctenophora"),
    ASCHELMINTHES ("Aschelminthes"),
    ARTHROPODA ("Arthropoda"),
    NEMATODA ("Nematoda"),
    ANNELIDA ("Annelida"),
    MOLLUSCA ("Mollusca"),
    PLATYHELMINTHES ("Platyhelminthes"),
    ECHINODERMATA ("Echinodermata"),
    CHORDATA ("Chordata");

    private final String description;

    @JsonCreator
    public static Phylum parseId(String id){
        return EnumId.parseId(Phylum.class, id);
    }

    public static Phylum parseDescription(String description){
        for(Phylum p: values()){
            if(p.getDescription().equalsIgnoreCase(description)){
                return p;
            }
        }
        return null;
    }

    @Override
    public String toString(){
        return description;
    }
}
