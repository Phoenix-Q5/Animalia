package com.animal.java.model.animal;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Animal {

    private String id;

    private String phylum;

    private String family;

    private String name;

    private String scientificName;

    private String description;

    private String genus;

    private String species;

    private String order;

    private String kingdom;

    private AnimalExtendedInfo animalExtendedInfo;

    private List<Image> animalImages;

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }

        if(o == null || getClass() != o.getClass()){
            return false;
        }

        Animal that = (Animal) o;
        return new EqualsBuilder().append(getName(), that.getName())
                .append(getPhylum(), that.getPhylum())
                .append(getSpecies(), that.getSpecies())
                .append(getFamily(), that.getFamily())
                .append(getScientificName(), that.getScientificName())
                .append(getGenus(), that.getGenus())
                .append(getDescription(), that.getDescription())
                .append(getOrder(), that.getOrder())
                .append(getKingdom(), that.getKingdom())
                .isEquals();
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder().append(name)
                .append(phylum)
                .append(species)
                .append(family)
                .append(scientificName)
                .append(genus)
                .append(description)
                .append(order)
                .append(kingdom)
                .toHashCode();
    }

    @Override
    public String toString(){
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("phylum", phylum)
                .append("species", species)
                .append("family", family)
                .append("scientificName", scientificName)
                .append("genus", genus)
                .append("description", description)
                .append("order", order)
                .append("kingdom", kingdom)
                .toString();
    }
}
