package com.animal.java.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnimalSearchRequest {

    private String animalName;


}
