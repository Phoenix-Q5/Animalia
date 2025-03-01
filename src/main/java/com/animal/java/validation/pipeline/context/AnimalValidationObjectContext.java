package com.animal.java.validation.pipeline.context;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AnimalValidationObjectContext {

    public static final String SPECIES = "AnimalValidationObjectContext:SPECIES";

    private final ValidationObjectContext ctx;

    public static AnimalValidationObjectContext of(ValidationObjectContext ctx) {
        return new AnimalValidationObjectContext(ctx);
    }

    public void setSpecies(String species) {
        ctx.getCache().put(SPECIES, species);
    }

    public String getSpecies() {
        return (String) ctx.getCache().getOrDefault(SPECIES, null);
    }

}
