package com.animal.java.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Animal {

    @Id
    private String id;

    private String breed;

    private String earTag;

    private String name;

    private String description;

    private int age;

    private double weight;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image animalImage;

}
