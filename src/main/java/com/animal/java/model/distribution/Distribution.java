package com.animal.java.model.distribution;

import com.animal.java.model.animal.Image;
import com.animal.java.model.enumConstants.LocationType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Distribution {

    private UUID id;
    private UUID animalId;
    private String description;
    private Image distributionMap;

    @Builder.Default
    private List<DistributionLocation> locations = new ArrayList<>();

    public List<String> getContinents() {
        return valuesOf(LocationType.CONTINENT);
    }

    public List<String> getSubcontinents() {
        return valuesOf(LocationType.SUBCONTINENT);
    }

    public List<String> getCountries() {
        return valuesOf(LocationType.COUNTRY);
    }

    public List<String> getBiogeographicalRealms() {
        return valuesOf(LocationType.BIOGEOGRAPHICAL_REALM);
    }

    public List<String> getWwfBiomes() {
        return valuesOf(LocationType.WWF_BIOME);
    }

    private List<String> valuesOf(LocationType type) {
        if (locations == null) {
            return List.of();
        }
        return locations.stream()
                .filter(loc -> type.equals(loc.getLocationType()))
                .map(DistributionLocation::getValue)
                .collect(Collectors.toList());
    }
}
