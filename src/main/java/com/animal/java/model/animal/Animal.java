package com.animal.java.model.animal;

import com.animal.java.validation.extension.AnimalValidationObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Animal implements AnimalValidationObject {

    private UUID id;

    private String kingdom;
    private String phylum;
    private String subphylum;
    private String animalClass;
    private String order;
    private String suborder;
    private String family;
    private String genus;
    private String species;

    private String name;
    private String scientificName;
    private String description;

    private AnimalExtendedInfo animalExtendedInfo;
    private AnimalInfo animalInfo;

    @Builder.Default
    private List<Image> animalImages = new ArrayList<>();

    @Builder.Default
    private List<GalleryImage> galleryImages = new ArrayList<>();

    @Builder.Default
    private List<Subspecies> subspecies = new ArrayList<>();

    @Builder.Default
    private List<AnimalReference> references = new ArrayList<>();

    @Builder.Default
    private List<Animal> relatedAnimals = new ArrayList<>();

    @Override
    @JsonIgnore
    public String getObjectId() {
        if (id != null) {
            return id.toString();
        }
        return name != null ? name : "animal";
    }

    @Override
    @JsonIgnore
    public List<Image> getImages() {
        return animalImages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Animal that = (Animal) o;
        return new EqualsBuilder()
                .append(getName(), that.getName())
                .append(getPhylum(), that.getPhylum())
                .append(getSpecies(), that.getSpecies())
                .append(getFamily(), that.getFamily())
                .append(getScientificName(), that.getScientificName())
                .append(getGenus(), that.getGenus())
                .append(getDescription(), that.getDescription())
                .append(getOrder(), that.getOrder())
                .append(getKingdom(), that.getKingdom())
                .append(getSubphylum(), that.getSubphylum())
                .append(getAnimalClass(), that.getAnimalClass())
                .append(getSuborder(), that.getSuborder())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(phylum)
                .append(species)
                .append(family)
                .append(scientificName)
                .append(genus)
                .append(description)
                .append(order)
                .append(kingdom)
                .append(subphylum)
                .append(animalClass)
                .append(suborder)
                .toHashCode();
    }

    @Override
    public String toString() {
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
                .append("subphylum", subphylum)
                .append("animalClass", animalClass)
                .append("suborder", suborder)
                .toString();
    }
}
