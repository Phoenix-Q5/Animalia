package com.animal.java.model.animal;

import com.animal.java.model.distribution.Biome;
import com.animal.java.model.distribution.ClimateZones;
import com.animal.java.model.distribution.Geography;
import com.animal.java.model.distribution.Population;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnimalExtendedInfo {

    private String populationSize;
    private String lifeSpan;
    private String topSpeed;
    private float weight;
    private String length;
    private String wingSpan;
    private List<Map<String, String>> animalTags;
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
                .toHashCode();
    }
}
