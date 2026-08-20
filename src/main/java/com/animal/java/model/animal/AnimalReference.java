package com.animal.java.model.animal;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnimalReference {

    private UUID id;
    private UUID animalId;
    private String title;
    private String url;
    private String citation;
    private Integer sortOrder;
}
