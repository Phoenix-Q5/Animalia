package com.animal.java.model.distribution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience view of distribution geography lists.
 * Persisted via {@link DistributionLocation} rows.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Geography {

    @Builder.Default
    private List<String> continents = new ArrayList<>();

    @Builder.Default
    private List<String> subcontinents = new ArrayList<>();

    @Builder.Default
    private List<String> countries = new ArrayList<>();

    @Builder.Default
    private List<String> biogeographicalRealms = new ArrayList<>();

    @Builder.Default
    private List<String> wwfBiomes = new ArrayList<>();

    @Builder.Default
    private List<String> regions = new ArrayList<>();

    @Builder.Default
    private List<String> biomes = new ArrayList<>();
}
