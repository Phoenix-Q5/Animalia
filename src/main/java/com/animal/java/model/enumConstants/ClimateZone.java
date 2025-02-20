package com.animal.java.model.enumConstants;

import com.animal.java.utils.EnumId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClimateZone implements EnumId {

   Temperate,
    Cold,
    Polar,
    Tropical;


}
