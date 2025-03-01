package com.animal.java.model.animal;

import com.animal.java.model.distribution.Biome;
import com.animal.java.model.distribution.ClimateZones;
import com.animal.java.model.distribution.Geography;
import com.animal.java.model.distribution.Population;
import com.animal.java.model.enumConstants.Classification;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnimalExtendedInfo {

    private UUID id;
    private UUID animalId;
    private String populationSize;
    private String lifeSpan;
    private String topSpeed;
    private double weight;
    private String length;
    private String wingSpan;
    private List<Map<String, Classification>> animalTags;
    private Population population;
    private Geography geography;
    private ClimateZones climateZones;
    private Biome biome;

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(!(o instanceof AnimalExtendedInfo)){
            return false;
        }
        AnimalExtendedInfo that = (AnimalExtendedInfo) o;
        return new EqualsBuilder().append(populationSize, that.populationSize)
                .append(lifeSpan, that.lifeSpan)
                .append(topSpeed, that.topSpeed)
                .append(weight, that.weight)
                .append(length, that.length)
                .append(wingSpan, that.wingSpan)
                .append(animalTags, that.animalTags)
                .append(population, that.population)
                .append(geography, that.geography)
                .append(climateZones, that.climateZones)
                .append(biome, that.biome)
                .isEquals();
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder().append(populationSize)
                .append(lifeSpan)
                .append(topSpeed)
                .append(weight)
                .append(length)
                .append(wingSpan)
                .append(animalTags)
                .append(population)
                .append(geography)
                .append(climateZones)
                .append(biome)
                .toHashCode();
    }
}
