package com.animal.java.model.distribution;

import com.animal.java.model.enumConstants.ClimateZone;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClimateZones {

    private String description;

    @Builder.Default
    private List<ClimateZone> zones = new ArrayList<>();
}
