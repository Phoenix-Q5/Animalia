package com.animal.java.service;

import com.animal.java.model.animal.Animal;

import java.util.UUID;

public interface AnimalIndexService {

    void index(Animal animal);

    void delete(UUID animalId);

    long reindexAll();
}
