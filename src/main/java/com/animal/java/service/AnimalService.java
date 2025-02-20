package com.animal.java.service;

import com.animal.java.model.animal.Animal;

import java.util.List;

public interface AnimalService {

    List<Animal> getAllAnimals();

    Animal addAnimal(Animal animal);

//    List<Animal> smartAnimalSearch(String search);

    Animal getAnimalById(String name);

    List<Animal> getAnimalByName(String name);
}
