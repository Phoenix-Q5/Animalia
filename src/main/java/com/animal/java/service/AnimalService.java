package com.animal.java.service;

import com.animal.java.model.animal.Animal;
import com.animal.java.model.info.Appearance;
import com.animal.java.model.info.Habits;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AnimalService {

    List<Animal> getAllAnimals();

    Animal insertAnimal(Animal animal);

    Animal insertAnimal(Animal animal, MultipartFile file);

    List<Animal> insertAnimals(List<Animal> animals);

    List<Animal> updateAnimals(List<Animal> animals);

    Animal updateAnimal(UUID id, Animal animal);

    Animal getAnimalById(UUID id);

    List<Animal> getAnimalByName(String name);

    List<Animal> getAnimalByType(String type);

    List<Animal> getAnimalByHabit(Habits habit);

    List<Animal> getAnimalByAppearance(Appearance appearance);

    List<Animal> getAnimalByDiet(String diet);

    List<Animal> getAnimalByFamily(String family);

    List<Animal> getAnimalByKingdom (String kingdom);

    void deleteAnimal(UUID id);

    void deleteAnimals(List<UUID> ids);

}
