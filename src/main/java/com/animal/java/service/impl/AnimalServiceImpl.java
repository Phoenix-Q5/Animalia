package com.animal.java.service.impl;

import com.animal.java.annotations.TransactionRequired;
import com.animal.java.dao.AnimalRepository;
import com.animal.java.dao.ImageRepository;
import com.animal.java.model.animal.Animal;
import com.animal.java.model.animal.Image;
import com.animal.java.model.info.Appearance;
import com.animal.java.model.info.Habits;
import com.animal.java.service.AnimalService;
import com.animal.java.service.CloudinaryStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final ImageRepository imageRepository;
    private final CloudinaryStorageService cloudinaryStorageService;

    public List<Animal> getAllAnimals() {
        List<Animal> animals = animalRepository.findAll();
        for (Animal animal : animals) {
            if (animal.getId() != null) {
                animal.setAnimalImages(imageRepository.getByAnimalId(animal.getId()));
            }
        }
        return animals;
    }

    @Override
    public List<Animal> getBrowseAnimals() {
        return animalRepository.findAllForBrowse();
    }

    @Override
    @TransactionRequired
    public Animal insertAnimal(Animal animal) {
        return insertAnimal(animal, null, null, null);
    }

    @Override
    @TransactionRequired
    public Animal insertAnimal(Animal animal, MultipartFile file) {
        MultipartFile[] files = file == null || file.isEmpty() ? null : new MultipartFile[]{file};
        return insertAnimal(animal, files, null, null);
    }

    @Override
    @TransactionRequired
    public Animal insertAnimal(
            Animal animal,
            MultipartFile[] animalImageFiles,
            MultipartFile distributionMap,
            MultipartFile[] appearanceImages
    ) {
        if (animal.getId() == null) {
            animal.setId(UUID.randomUUID());
        }

        List<Image> images = collectUploadedImages(animal, animalImageFiles, distributionMap, appearanceImages);

        Animal saved = animalRepository.addAnimal(animal);
        if (!images.isEmpty()) {
            imageRepository.addImages(images);
            saved.setAnimalImages(images);
        }
        return saved;
    }

    @Override
    public List<Animal> insertAnimals(List<Animal> animals) {
        return animals.stream().map(this::insertAnimal).toList();
    }

    @Override
    public List<Animal> updateAnimals(List<Animal> animals) {
        return animals.stream()
                .map(animal -> updateAnimal(animal.getId(), animal))
                .filter(saved -> saved != null)
                .toList();
    }

    @Override
    @TransactionRequired
    public Animal updateAnimal(UUID id, Animal animal) {
        return updateAnimal(id, animal, null, null, null);
    }

    @Override
    @TransactionRequired
    public Animal updateAnimal(
            UUID id,
            Animal animal,
            MultipartFile[] animalImageFiles,
            MultipartFile distributionMap,
            MultipartFile[] appearanceImages
    ) {
        Animal existing = animalRepository.getById(id);
        if (existing == null) {
            return null;
        }
        animal.setId(id);
        preserveNestedIds(existing, animal);
        preserveExistingMap(existing, animal, distributionMap);
        dropDuplicateImageUrls(existing, animal);

        List<Image> images = collectUploadedImages(animal, animalImageFiles, distributionMap, appearanceImages);
        Animal saved = animalRepository.updateAnimal(animal);
        if (saved != null && !images.isEmpty()) {
            imageRepository.addImages(images);
        }
        return animalRepository.getById(id);
    }

    private void preserveNestedIds(Animal existing, Animal incoming) {
        if (incoming.getAnimalExtendedInfo() != null && existing.getAnimalExtendedInfo() != null) {
            incoming.getAnimalExtendedInfo().setId(existing.getAnimalExtendedInfo().getId());
            incoming.getAnimalExtendedInfo().setAnimalId(existing.getId());
        }
        if (incoming.getAnimalInfo() != null && existing.getAnimalInfo() != null) {
            incoming.getAnimalInfo().setId(existing.getAnimalInfo().getId());
            incoming.getAnimalInfo().setAnimalId(existing.getId());
            if (incoming.getAnimalInfo().getDistribution() != null
                    && existing.getAnimalInfo().getDistribution() != null) {
                incoming.getAnimalInfo().getDistribution().setId(existing.getAnimalInfo().getDistribution().getId());
            }
            if (incoming.getAnimalInfo().getPopulation() != null
                    && existing.getAnimalInfo().getPopulation() != null) {
                incoming.getAnimalInfo().getPopulation().setId(existing.getAnimalInfo().getPopulation().getId());
            }
        }
    }

    private void dropDuplicateImageUrls(Animal existing, Animal incoming) {
        if (incoming.getAnimalImages() == null || existing.getAnimalImages() == null) {
            return;
        }
        Set<String> existingUrls = existing.getAnimalImages().stream()
                .map(Image::getUrl)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(url -> !url.isBlank())
                .collect(Collectors.toSet());
        incoming.setAnimalImages(incoming.getAnimalImages().stream()
                .filter(image -> image != null && image.getUrl() != null && !existingUrls.contains(image.getUrl().trim()))
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    private void preserveExistingMap(Animal existing, Animal incoming, MultipartFile distributionMap) {
        boolean replacingMap = distributionMap != null && !distributionMap.isEmpty();
        if (replacingMap) {
            return;
        }
        if (incoming.getAnimalInfo() == null || incoming.getAnimalInfo().getDistribution() == null) {
            return;
        }
        if (existing.getAnimalInfo() != null
                && existing.getAnimalInfo().getDistribution() != null
                && existing.getAnimalInfo().getDistribution().getDistributionMap() != null) {
            incoming.getAnimalInfo().getDistribution()
                    .setDistributionMap(existing.getAnimalInfo().getDistribution().getDistributionMap());
        }
    }

    private List<Image> collectUploadedImages(
            Animal animal,
            MultipartFile[] animalImageFiles,
            MultipartFile distributionMap,
            MultipartFile[] appearanceImages
    ) {
        List<Image> images = new ArrayList<>();
        if (animal.getAnimalImages() != null) {
            for (Image existing : animal.getAnimalImages()) {
                if (existing == null) {
                    continue;
                }
                if (existing.getId() != null) {
                    continue;
                }
                if (existing.hasCdnUrl()) {
                    Image fromUrl = cloudinaryStorageService.fromUrl(existing.getUrl(), existing.getName());
                    fromUrl.setAnimalId(animal.getId());
                    images.add(fromUrl);
                }
            }
        }

        if (animalImageFiles != null) {
            for (MultipartFile file : animalImageFiles) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                Image uploaded = cloudinaryStorageService.upload(file, "animalia/animals");
                uploaded.setAnimalId(animal.getId());
                images.add(uploaded);
            }
        }

        if (appearanceImages != null) {
            for (MultipartFile file : appearanceImages) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                Image uploaded = cloudinaryStorageService.upload(file, "animalia/appearance");
                uploaded.setAnimalId(animal.getId());
                images.add(uploaded);
            }
        }

        if (distributionMap != null && !distributionMap.isEmpty()) {
            Image mapImage = cloudinaryStorageService.upload(distributionMap, "animalia/maps");
            mapImage.setAnimalId(animal.getId());
            images.add(mapImage);
            if (animal.getAnimalInfo() != null && animal.getAnimalInfo().getDistribution() != null) {
                animal.getAnimalInfo().getDistribution().setDistributionMap(mapImage);
            }
        }
        return images;
    }

    @Override
    public Animal getAnimalById(UUID id) {
        return animalRepository.getById(id);
    }

    @Override
    public List<Animal> getAnimalByName(String name) {
        return animalRepository.findAll().stream()
                .filter(animal -> animal.getName() != null
                        && animal.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    public List<Animal> getRelatedAnimals(UUID id) {
        Animal animal = animalRepository.getById(id);
        if (animal == null) {
            return List.of();
        }
        return animalRepository.findRelated(animal, 8);
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
