package com.animal.java.model;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

    private Image animalImage;

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }

        if(o == null || getClass() != o.getClass()){
            return false;
        }

        Animal that = (Animal) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(getName());
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
                .toString();
    }
}
