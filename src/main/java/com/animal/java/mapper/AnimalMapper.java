package com.animal.java.mapper;

import com.animal.java.model.animal.Animal;
import com.animal.java.model.animal.Image;
import com.animal.java.utils.CommonConvertor;
import com.animal.java.utils.TimeUtils;
import com.animal.jooq.generated.tables.pojos.JAnimal;
import com.animal.jooq.generated.tables.pojos.JImage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AnimalMapper {

    public Animal fromJooq(JAnimal jAnimal) {
        if (Objects.isNull(jAnimal)) {
            return null;
        }

        return new Animal().builder()
                .id(jAnimal.getId())
                .name(jAnimal.getName())
                .genus(jAnimal.getGenus())
                .description(jAnimal.getDescription())
                .family(jAnimal.getFamily())
                .phylum(jAnimal.getPhylum())
                .scientificName(jAnimal.getScientificname())
                .species(jAnimal.getSpecies())
                .build();
    }

    public List<Animal> fromJooqAnimals(List<JAnimal> animals) {
        return animals.stream().map(this::fromJooq).collect(Collectors.toList());
    }

    public JAnimal toJooq(Animal animal) {
        JAnimal jAnimal = new JAnimal();
        jAnimal.setDescription(animal.getDescription());
        jAnimal.setId(animal.getId());
        jAnimal.setFamily(animal.getFamily());
        jAnimal.setName(animal.getName());
        jAnimal.setGenus(animal.getGenus());
        jAnimal.setPhylum(animal.getPhylum());
        jAnimal.setSpecies(animal.getSpecies());
        jAnimal.setScientificname(animal.getScientificName());
        jAnimal.setKingdom(animal.getKingdom());
        jAnimal.setOrder(animal.getOrder());
        return jAnimal;
    }

    public Animal fullAnimal(JAnimal jAnimal, JImage jImage) {
        if (Objects.isNull(jAnimal) && Objects.isNull(jImage)) {
            return null;
        }
        Image i = new Image().builder()
                .id(jImage.getId())
                .name(jImage.getName())
                .type(jImage.getType())
                .imageArray(jImage.getImagearray())
                .build();

        Animal a = new Animal().builder()
                .id(jAnimal.getId())
                .name(jAnimal.getName())
                .genus(jAnimal.getGenus())
                .description(jAnimal.getDescription())
                .family(jAnimal.getFamily())
                .phylum(jAnimal.getPhylum())
                .scientificName(jAnimal.getScientificname())
                .species(jAnimal.getSpecies())
                .animalImages(List.of(i))
                .build();
        return a;
    }

    public List<Animal> fullAnimal(List<JAnimal> jAnimals, List<JImage> jImages) {
        if (Objects.isNull(jAnimals) || Objects.isNull(jImages)) {
            return Collections.emptyList();
        }
        List<Animal> animals = new ArrayList<>();

        for (int i = 0; i < jAnimals.size(); i++) {
            JAnimal jAnimal = jAnimals.get(i);
            JImage jImage = i < jImages.size() ? jImages.get(i) : null;

            animals.add(fullAnimal(jAnimal, jImage));
        }

        return animals;
    }

    public Image fromJooqImage(JImage jImage) {
        Image image = new Image();
        image.setImageArray(jImage.getImagearray());
        image.setType(jImage.getType());
        image.setId(jImage.getId());
        image.setName(jImage.getName());
        image.setUploadedAt(CommonConvertor.ZDT_CONVERTOR.from(jImage.getUploadedat()));
        return image;
    }

    public List<Image> fromJooqImages(List<JImage> jImages) {
        return jImages.stream().map(this::fromJooqImage).collect(Collectors.toList());
    }

    public JImage toJooqImage(Image image){
        JImage jImage = new JImage();
        jImage.setAnimalId(image.getAnimalId());
        jImage.setImagearray(image.getImageArray());
        jImage.setName(image.getName());
        jImage.setType(image.getType());
        jImage.setId(image.getId());
        jImage.setUploadedat(CommonConvertor.ZDT_CONVERTOR.to(image.getUploadedAt()));
        return jImage;
    }

}
