package com.animal.java.model.enumConstants;

import com.animal.java.utils.EnumId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum BiomeId implements EnumId {
    FOREST ("Forest"),
    RIPARIAN ("Riparian"),
    GRASSLAND ("Grassland"),
    SWAMP ("Swamp"),
    SAVANNA ("Savanna"),
    WOODLAND ("Woodland");

    private final String description;

    public static BiomeId parseId(String id){
        return EnumId.parseId(BiomeId.class, id);
    }

    public static Set<BiomeId> getAvailableBiomes(){
        return Set.of(BiomeId.values());
    }
}
