package com.animal.java.model;

import com.animal.java.utils.EnumId;
import com.fasterxml.jackson.annotation.JsonCreator;
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
