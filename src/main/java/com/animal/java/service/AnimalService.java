package com.animal.java.service;

import com.animal.java.dao.AnimalRepository;
import com.animal.java.model.Animal;
import com.animal.java.model.Image;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.animal.java.utils.ImageUtils.decompressImage;

@Service
@AllArgsConstructor
public class AnimalService {

    private AnimalRepository animalRepository;

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
