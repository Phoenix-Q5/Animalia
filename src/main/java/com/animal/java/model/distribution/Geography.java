package com.animal.java.model.distribution;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Geography {

    private List<String> continents;
    private List<String> countries;
    private List<String> regions;
    private List<String> biomes;
}
