package com.animal.java.model.animal;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Subspecies {

    private UUID id;
    private UUID animalId;
    private String name;
    private String scientificName;
    private String description;
}
