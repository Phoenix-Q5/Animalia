package com.animal.java.model.info;

import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Habits {

    private UUID id;
    private UUID animalId;
    private String description;

    @Builder.Default
    private Set<String> groupName = new HashSet<>();

    @Builder.Default
    private Set<String> lifestyle = new HashSet<>();

    private String seasonality;
}
