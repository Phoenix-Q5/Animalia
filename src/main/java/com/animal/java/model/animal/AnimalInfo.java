package com.animal.java.model.animal;

import com.animal.java.model.distribution.Biome;
import com.animal.java.model.distribution.ClimateZones;
import com.animal.java.model.distribution.Distribution;
import com.animal.java.model.distribution.Population;
import com.animal.java.model.info.Appearance;
import com.animal.java.model.info.Habits;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnimalInfo {

    private UUID id;
    private UUID animalId;

    private Distribution distribution;
    private Appearance appearance;
    private Habits habits;

    private String diet;
    private String habitat;
    private String behavior;
    private String habitsLifestyle;

    private Biome biome;
    private ClimateZones climateZones;
    private Population population;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AnimalInfo)) {
            return false;
        }
        AnimalInfo other = (AnimalInfo) obj;
        return new EqualsBuilder()
                .append(getAnimalId(), other.getAnimalId())
                .append(getDistribution(), other.getDistribution())
                .append(getAppearance(), other.getAppearance())
                .append(getHabits(), other.getHabits())
                .append(getDiet(), other.getDiet())
                .append(getHabitat(), other.getHabitat())
                .append(getBehavior(), other.getBehavior())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getAnimalId())
                .append(getDistribution())
                .append(getAppearance())
                .append(getHabits())
                .append(getDiet())
                .append(getHabitat())
                .append(getBehavior())
                .toHashCode();
    }
}
