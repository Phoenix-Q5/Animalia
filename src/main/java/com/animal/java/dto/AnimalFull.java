package com.animal.java.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimalFull {
    private String animalId;
    private String phylum;
    private String family;
    private String name;
    private String scientificName;
    private String description;
    private String genus;
    private String species;
    private Long imageId;
    private String imageName;
    private String imageType;
    private byte[] imageData;
}
