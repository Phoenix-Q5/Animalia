package com.animal.java.service;

import com.animal.java.dao.AnimalRepository;
import com.animal.java.model.Animal;
import com.animal.java.model.Image;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.animal.java.utils.ImageUtils.decompressImage;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnimalService {

    private final AnimalRepository animalRepository;

    public List<Animal> getAllAnimals() {
        List<Animal> rawAnimalList = animalRepository.findAll();

        rawAnimalList.stream().filter(animal -> Objects.nonNull(animal.getAnimalImage()))
                .forEach(animal -> {
                    animal.setAnimalImage(Image.builder().id(animal.getAnimalImage().getId())
                            .name(animal.getAnimalImage().getName())
                            .type(animal.getAnimalImage().getType())
                            .image(decompressImage(animal.getAnimalImage().getImage())).build());
                });

        return rawAnimalList;
    }

    public Animal addAnimal(Animal animal) {
        animal.setId(UUID.randomUUID().toString().substring(1,6));
        return animalRepository.save(animal);
    }

    public List<Animal> smartAnimalSearch(String keyword){
        return animalRepository.findAll(AnimalSmartSearch.smartSearch(keyword));
    }
}
