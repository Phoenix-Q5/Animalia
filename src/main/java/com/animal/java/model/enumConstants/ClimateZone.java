package com.animal.java.model.enumConstants;

import com.animal.java.utils.EnumId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum ClimateZone implements EnumId {

   Temperate ("Temperate"),
    Cold ("Cold"),
    Polar ("Polar"),
    Tropical ("Tropical"),;

    private final String description;

    public static ClimateZone parseId(String id){
        return EnumId.parseId(ClimateZone.class, id);
    }

    public static Set<ClimateZone> getAvailableClimateZones(){
        return Set.of(ClimateZone.values());
    }

}
