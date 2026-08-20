package com.animal.java.model.info;

import com.animal.java.model.animal.Image;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Appearance {

    private UUID id;
    private UUID animalId;
    private String description;

    @Builder.Default
    private List<Image> images = new ArrayList<>();
}
