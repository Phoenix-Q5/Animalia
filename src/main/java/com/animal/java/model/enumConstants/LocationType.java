package com.animal.java.model.enumConstants;

import com.animal.java.utils.EnumId;
import lombok.Getter;

@Getter
public enum LocationType implements EnumId {
    CONTINENT("Continent"),
    SUBCONTINENT("Subcontinent"),
    COUNTRY("Country"),
    BIOGEOGRAPHICAL_REALM("Biogeographical realm"),
    WWF_BIOME("WWF biome");

    private final String description;

    LocationType(String description) {
        this.description = description;
    }
}
