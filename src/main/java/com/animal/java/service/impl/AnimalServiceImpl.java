package com.animal.java.service.impl;

import com.animal.java.dao.AnimalRepository;
import com.animal.java.dto.AnimalFull;
import com.animal.java.model.Animal;
import com.animal.java.model.Image;
import com.animal.java.service.AnimalService;
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
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;

    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    public Animal addAnimal(Animal animal) {
        animal.setId(UUID.randomUUID().toString().substring(1,6));
        return animalRepository.save(animal);
    }

//    public List<Animal> smartAnimalSearch(String keyword){
//        return animalRepository.findAll(AnimalSmartSearch.smartSearch(keyword));
//    }

    public Animal getAnimalById(String id){
        return animalRepository.findById(id);
    }

    @Override
    public List<Animal> getAnimalByName(String name) {
//        return animalRepository.findByName(name);
        return null;
    }


}
