package com.animal.java.validation.commands.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationMessage {

    public static final String SPECIES_NOT_EMPTY = "Species cannot be empty";
    public static final String NAME_NOT_EMPTY = "Name cannot be empty";
    public static final String SCIENTIFIC_NAME_NOT_EMPTY = "Scientific name cannot be empty";
    public static final String PHYLUM_NOT_EMPTY = "Phylum cannot be empty";
    public static final String FAMILY_NOT_EMPTY = "Family cannot be empty";
    public static final String NAME_ALREADY_EXISTS = "An animal with this name already exists";
    public static final String SCIENTIFIC_NAME_ALREADY_EXISTS = "An animal with this scientific name already exists";
    public static final String PHYLUM_UNKNOWN = "Phylum is not recognized";
    public static final String KINGDOM_UNKNOWN = "Kingdom is not recognized";
    public static final String FIELD_TOO_LONG = "exceeds the maximum length of 255 characters";
}
