package com.animal.java.model.distribution;

import com.animal.java.model.enumConstants.PopulationTrend;
import com.animal.java.model.enumConstants.VulnerabilityStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Population {

    private UUID id;
    private UUID animalId;
    private String populationThreats;
    private String populationNumber;
    private String ecologicalNiche;
    private PopulationTrend populationTrend;
    private String populationStatus;
    private VulnerabilityStatus conservationStatus;
}
