package com.animal.java.service;

import com.animal.java.dto.AnimalFull;
import com.animal.java.model.Animal;

import java.util.List;
import java.util.Set;

public interface AnimalService {

    List<Animal> getAllAnimals();

    Animal addAnimal(Animal animal);

//    List<Animal> smartAnimalSearch(String search);

    Animal getAnimalById(String name);

    List<Animal> getAnimalByName(String name);
}
