package com.animal.java.search;

import com.animal.java.model.animal.Animal;
import com.animal.java.model.animal.AnimalInfo;
import com.animal.java.model.animal.Image;
import com.animal.java.model.distribution.Biome;
import com.animal.java.model.distribution.ClimateZones;
import com.animal.java.model.distribution.Population;
import com.animal.java.model.enumConstants.BiomeId;
import com.animal.java.model.enumConstants.ClimateZone;
import com.animal.java.model.enumConstants.PopulationTrend;
import com.animal.java.model.enumConstants.VulnerabilityStatus;
import com.animal.java.utils.EnumId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AnimalDocumentMapper {

    public AnimalDocument toDocument(Animal animal) {
        if (animal == null || animal.getId() == null) {
            return null;
        }
        AnimalInfo info = animal.getAnimalInfo();
        Population population = info != null ? info.getPopulation() : null;
        Image cover = firstImage(animal);

        return AnimalDocument.builder()
                .id(animal.getId().toString())
                .name(animal.getName())
                .nameKeyword(animal.getName())
                .nameLetter(firstLetter(animal.getName()))
                .scientificName(animal.getScientificName())
                .scientificNameKeyword(animal.getScientificName())
                .phylum(animal.getPhylum())
                .family(animal.getFamily())
                .genus(animal.getGenus())
                .species(animal.getSpecies())
                .speciesKeyword(StringUtils.trimToNull(animal.getSpecies()))
                .kingdom(animal.getKingdom())
                .animalClass(animal.getAnimalClass())
                .order(animal.getOrder())
                .description(animal.getDescription())
                .diet(info != null ? info.getDiet() : null)
                .habitat(info != null ? info.getHabitat() : null)
                .habitatKeyword(StringUtils.trimToNull(info != null ? info.getHabitat() : null))
                .behavior(info != null ? info.getBehavior() : null)
                .biomes(biomeIds(info))
                .climateZones(climateIds(info))
                .conservationStatus(EnumId.getIdOf(population != null ? population.getConservationStatus() : null))
                .populationTrend(EnumId.getIdOf(population != null ? population.getPopulationTrend() : null))
                .imageUrl(cover != null ? cover.getUrl() : null)
                .imageName(cover != null ? cover.getName() : null)
                .imageType(cover != null ? cover.getType() : null)
                .build();
    }

    public Animal toAnimal(AnimalDocument doc) {
        if (doc == null || StringUtils.isBlank(doc.getId())) {
            return null;
        }

        PopulationTrend trend = doc.getPopulationTrend() == null
                ? null
                : EnumId.parseId(PopulationTrend.class, doc.getPopulationTrend(), null);

        AnimalInfo info = AnimalInfo.builder()
                .diet(doc.getDiet())
                .habitat(doc.getHabitat())
                .behavior(doc.getBehavior())
                .biome(Biome.builder().biomes(parseBiomes(doc.getBiomes())).build())
                .climateZones(ClimateZones.builder().zones(parseClimates(doc.getClimateZones())).build())
                .population(Population.builder()
                        .conservationStatus(parseConservation(doc.getConservationStatus()))
                        .populationTrend(trend)
                        .build())
                .build();

        List<Image> images = new ArrayList<>();
        if (StringUtils.isNotBlank(doc.getImageUrl())) {
            images.add(Image.builder()
                    .url(doc.getImageUrl())
                    .name(StringUtils.defaultIfBlank(doc.getImageName(), "cover"))
                    .type(StringUtils.defaultIfBlank(doc.getImageType(), "image/jpeg"))
                    .animalId(UUID.fromString(doc.getId()))
                    .build());
        }

        return Animal.builder()
                .id(UUID.fromString(doc.getId()))
                .name(doc.getName())
                .scientificName(doc.getScientificName())
                .phylum(doc.getPhylum())
                .family(doc.getFamily())
                .genus(doc.getGenus())
                .species(doc.getSpecies())
                .kingdom(doc.getKingdom())
                .animalClass(doc.getAnimalClass())
                .order(doc.getOrder())
                .description(doc.getDescription())
                .animalInfo(info)
                .animalImages(images)
                .build();
    }

    private Image firstImage(Animal animal) {
        if (animal.getAnimalImages() == null || animal.getAnimalImages().isEmpty()) {
            return null;
        }
        return animal.getAnimalImages().stream()
                .filter(img -> img != null && StringUtils.isNotBlank(img.getUrl()))
                .findFirst()
                .orElse(animal.getAnimalImages().get(0));
    }

    private String firstLetter(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (Character.isLetter(ch)) {
                return String.valueOf(Character.toUpperCase(ch));
            }
        }
        return null;
    }

    private List<String> biomeIds(AnimalInfo info) {
        if (info == null || info.getBiome() == null || info.getBiome().getBiomes() == null) {
            return List.of();
        }
        return info.getBiome().getBiomes().stream()
                .map(EnumId::getIdOf)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    private List<String> climateIds(AnimalInfo info) {
        if (info == null || info.getClimateZones() == null || info.getClimateZones().getZones() == null) {
            return List.of();
        }
        return info.getClimateZones().getZones().stream()
                .map(EnumId::getIdOf)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    private List<BiomeId> parseBiomes(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .map(v -> EnumId.parseId(BiomeId.class, v, null))
                .filter(v -> v != null)
                .collect(Collectors.toList());
    }

    private List<ClimateZone> parseClimates(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .map(v -> EnumId.parseId(ClimateZone.class, v, null))
                .filter(v -> v != null)
                .collect(Collectors.toList());
    }

    private VulnerabilityStatus parseConservation(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return EnumId.parseId(VulnerabilityStatus.class, value.toUpperCase(Locale.ROOT), null);
    }
}
