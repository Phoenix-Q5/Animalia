package com.animal.java.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalFacets implements Serializable {

    @Builder.Default
    private List<FacetBucket> letters = new ArrayList<>();

    @Builder.Default
    private List<FacetBucket> species = new ArrayList<>();

    @Builder.Default
    private List<FacetBucket> habitats = new ArrayList<>();

    @Builder.Default
    private List<FacetBucket> biomes = new ArrayList<>();

    @Builder.Default
    private List<FacetBucket> climates = new ArrayList<>();

    @Builder.Default
    private List<FacetBucket> statuses = new ArrayList<>();

    private long totalAnimals;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FacetBucket implements Serializable {
        private String key;
        private String label;
        private long count;
    }
}
