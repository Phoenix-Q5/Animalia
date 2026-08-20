package com.animal.java.model.distribution;

import com.animal.java.model.enumConstants.BiomeId;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Biome {

    private String description;

    @Builder.Default
    private List<BiomeId> biomes = new ArrayList<>();
}
