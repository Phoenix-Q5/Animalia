package com.animal.java.service.impl;

import com.animal.java.annotations.TransactionRequired;
import com.animal.java.dao.AnimalRepository;
import com.animal.java.dao.ImageRepository;
import com.animal.java.model.animal.Animal;
import com.animal.java.model.info.Appearance;
import com.animal.java.model.info.Habits;
import com.animal.java.service.AnimalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final ImageRepository imageRepository;

    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    @Override
    @TransactionRequired
    public Animal insertAnimal(Animal animal) {
        animal.setId(UUID.randomUUID());
        return animalRepository.addAnimal(animal);
    }

    @Override
    @TransactionRequired
    public Animal insertAnimal(Animal animal, MultipartFile file) {
        animal.setId(UUID.randomUUID());
        imageRepository.addImages(animal.getAnimalImages());
        return animalRepository.addAnimal(animal);
    }

    @Override
    public List<Animal> insertAnimals(List<Animal> animals) {
        return List.of();
    }

    @Override
    public List<Animal> updateAnimals(List<Animal> animals) {
        return List.of();
    }

    @Override
    public Animal updateAnimal(UUID id, Animal animal) {
        return null;
    }

    @Override
    public Animal getAnimalById(UUID id) {
        return null;
    }

    @Override
    public List<Animal> getAnimalByName(String name) {
        return null;
    }

    @Override
    public List<Animal> getAnimalByType(String type) {
        return List.of();
    }

    @Override
    public List<Animal> getAnimalByHabit(Habits habit) {
        return List.of();
    }

    @Override
    public List<Animal> getAnimalByAppearance(Appearance appearance) {
        return List.of();
    }

    @Override
    public List<Animal> getAnimalByDiet(String diet) {
        return List.of();
    }

    @Override
    public List<Animal> getAnimalByFamily(String family) {
        return List.of();
    }

    @Override
    public List<Animal> getAnimalByKingdom(String kingdom) {
        return List.of();
    }

    @Override
    public void deleteAnimal(UUID id) {
        animalRepository.deleteAnimalById(id);
    }

    @Override
    public void deleteAnimals(List<UUID> ids) {

    }


}
