package com.animal.java.mapper;

import com.animal.java.model.animal.Animal;
import com.animal.java.model.animal.AnimalExtendedInfo;
import com.animal.java.model.animal.AnimalInfo;
import com.animal.java.model.animal.AnimalReference;
import com.animal.java.model.animal.GalleryImage;
import com.animal.java.model.animal.Image;
import com.animal.java.model.animal.Subspecies;
import com.animal.java.model.distribution.Biome;
import com.animal.java.model.distribution.ClimateZones;
import com.animal.java.model.distribution.Distribution;
import com.animal.java.model.distribution.DistributionLocation;
import com.animal.java.model.distribution.Population;
import com.animal.java.model.enumConstants.BiomeId;
import com.animal.java.model.enumConstants.ClimateZone;
import com.animal.java.model.enumConstants.LocationType;
import com.animal.java.model.enumConstants.PopulationTrend;
import com.animal.java.model.enumConstants.VulnerabilityStatus;
import com.animal.java.model.info.Appearance;
import com.animal.java.model.info.Habits;
import com.animal.java.utils.CommonConvertor;
import com.animal.java.utils.EnumId;
import com.animal.jooq.generated.tables.pojos.JAnimal;
import com.animal.jooq.generated.tables.pojos.JAnimalextendedinfo;
import com.animal.jooq.generated.tables.pojos.JAnimalinfo;
import com.animal.jooq.generated.tables.pojos.JImage;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.animal.java.dao.schema.AnimalDetailTables.ANIMAL_CLASS;
import static com.animal.java.dao.schema.AnimalDetailTables.ANIMAL_SUBORDER;
import static com.animal.java.dao.schema.AnimalDetailTables.ANIMAL_SUBPHYLUM;
import static com.animal.java.dao.schema.AnimalDetailTables.BIOME_VALUE;
import static com.animal.java.dao.schema.AnimalDetailTables.CLIMATE_VALUE;
import static com.animal.java.dao.schema.AnimalDetailTables.DIST_ANIMAL_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.DIST_DESCRIPTION;
import static com.animal.java.dao.schema.AnimalDetailTables.DIST_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.DIST_MAP_IMAGE_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.INFO_BEHAVIOR;
import static com.animal.java.dao.schema.AnimalDetailTables.INFO_DIET;
import static com.animal.java.dao.schema.AnimalDetailTables.INFO_HABITAT;
import static com.animal.java.dao.schema.AnimalDetailTables.INFO_HABITS_LIFESTYLE;
import static com.animal.java.dao.schema.AnimalDetailTables.LOC_DISTRIBUTION_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.LOC_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.LOC_TYPE;
import static com.animal.java.dao.schema.AnimalDetailTables.LOC_VALUE;
import static com.animal.java.dao.schema.AnimalDetailTables.POP_CONSERVATION;
import static com.animal.java.dao.schema.AnimalDetailTables.POP_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.POP_ANIMAL_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.POP_NICHE;
import static com.animal.java.dao.schema.AnimalDetailTables.POP_NUMBER;
import static com.animal.java.dao.schema.AnimalDetailTables.POP_STATUS;
import static com.animal.java.dao.schema.AnimalDetailTables.POP_THREATS;
import static com.animal.java.dao.schema.AnimalDetailTables.POP_TREND;
import static com.animal.java.dao.schema.AnimalDetailTables.REF_ANIMAL_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.REF_CITATION;
import static com.animal.java.dao.schema.AnimalDetailTables.REF_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.REF_SORT_ORDER;
import static com.animal.java.dao.schema.AnimalDetailTables.REF_TITLE;
import static com.animal.java.dao.schema.AnimalDetailTables.REF_URL;
import static com.animal.java.dao.schema.AnimalDetailTables.SUB_ANIMAL_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.SUB_DESCRIPTION;
import static com.animal.java.dao.schema.AnimalDetailTables.SUB_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.SUB_NAME;
import static com.animal.java.dao.schema.AnimalDetailTables.SUB_SCIENTIFIC_NAME;

@Component
public class AnimalMapper {

    public Animal fromJooq(JAnimal jAnimal) {
        if (Objects.isNull(jAnimal)) {
            return null;
        }

        return Animal.builder()
                .id(jAnimal.getId())
                .name(jAnimal.getName())
                .genus(jAnimal.getGenus())
                .description(jAnimal.getDescription())
                .family(jAnimal.getFamily())
                .phylum(jAnimal.getPhylum())
                .scientificName(jAnimal.getScientificname())
                .species(jAnimal.getSpecies())
                .kingdom(jAnimal.getKingdom())
                .order(jAnimal.getOrder())
                .subphylum(jAnimal.getSubphylum())
                .animalClass(jAnimal.getAnimalClass())
                .suborder(jAnimal.getSuborder())
                .build();
    }

    public Animal fromAnimalRecord(Record record) {
        if (record == null) {
            return null;
        }
        Animal animal = Animal.builder()
                .id(record.get(com.animal.jooq.generated.tables.JAnimal.ANIMAL.ID))
                .name(record.get(com.animal.jooq.generated.tables.JAnimal.ANIMAL.NAME))
                .genus(record.get(com.animal.jooq.generated.tables.JAnimal.ANIMAL.GENUS))
                .description(record.get(com.animal.jooq.generated.tables.JAnimal.ANIMAL.DESCRIPTION))
                .family(record.get(com.animal.jooq.generated.tables.JAnimal.ANIMAL.FAMILY))
                .phylum(record.get(com.animal.jooq.generated.tables.JAnimal.ANIMAL.PHYLUM))
                .scientificName(record.get(com.animal.jooq.generated.tables.JAnimal.ANIMAL.SCIENTIFICNAME))
                .species(record.get(com.animal.jooq.generated.tables.JAnimal.ANIMAL.SPECIES))
                .kingdom(record.get(com.animal.jooq.generated.tables.JAnimal.ANIMAL.KINGDOM))
                .order(record.get(com.animal.jooq.generated.tables.JAnimal.ANIMAL.ORDER))
                .build();
        try {
            if (record.field(com.animal.jooq.generated.tables.JAnimal.ANIMAL.SUBPHYLUM) != null) {
                animal.setSubphylum(record.get(com.animal.jooq.generated.tables.JAnimal.ANIMAL.SUBPHYLUM));
            }
            if (record.field(com.animal.jooq.generated.tables.JAnimal.ANIMAL.ANIMAL_CLASS) != null) {
                animal.setAnimalClass(record.get(com.animal.jooq.generated.tables.JAnimal.ANIMAL.ANIMAL_CLASS));
            }
            if (record.field(com.animal.jooq.generated.tables.JAnimal.ANIMAL.SUBORDER) != null) {
                animal.setSuborder(record.get(com.animal.jooq.generated.tables.JAnimal.ANIMAL.SUBORDER));
            }
        } catch (Exception ignored) {
            trySet(record, ANIMAL_SUBPHYLUM, animal::setSubphylum);
            trySet(record, ANIMAL_CLASS, animal::setAnimalClass);
            trySet(record, ANIMAL_SUBORDER, animal::setSuborder);
        }
        return animal;
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
        jAnimal.setSubphylum(animal.getSubphylum());
        jAnimal.setAnimalClass(animal.getAnimalClass());
        jAnimal.setSuborder(animal.getSuborder());
        return jAnimal;
    }

    public AnimalExtendedInfo fromJooqExtended(JAnimalextendedinfo row) {
        if (row == null) {
            return null;
        }
        return AnimalExtendedInfo.builder()
                .id(row.getId())
                .animalId(row.getAnimalid())
                .populationSize(row.getPopulationsize())
                .lifeSpan(row.getLifespan())
                .topSpeed(row.getTopspeed())
                .weight(row.getWeight())
                .length(row.getLength())
                .wingSpan(row.getWingspan())
                .build();
    }

    public JAnimalextendedinfo toJooqExtended(AnimalExtendedInfo info) {
        if (info == null) {
            return null;
        }
        JAnimalextendedinfo row = new JAnimalextendedinfo();
        row.setId(info.getId());
        row.setAnimalid(info.getAnimalId());
        row.setPopulationsize(info.getPopulationSize());
        row.setLifespan(info.getLifeSpan());
        row.setTopspeed(info.getTopSpeed());
        row.setWeight(info.getWeight());
        row.setLength(info.getLength());
        row.setWingspan(info.getWingSpan());
        return row;
    }

    public AnimalInfo fromJooqInfo(JAnimalinfo row) {
        if (row == null) {
            return null;
        }
        Appearance appearance = Appearance.builder()
                .animalId(row.getAnimalid())
                .description(row.getAppearance())
                .build();
        Habits habits = Habits.builder()
                .animalId(row.getAnimalid())
                .description(row.getHabits())
                .build();
        return AnimalInfo.builder()
                .id(row.getId())
                .animalId(row.getAnimalid())
                .appearance(appearance)
                .habits(habits)
                .build();
    }

    public AnimalInfo enrichInfoFromRecord(AnimalInfo info, Record record) {
        if (info == null) {
            info = new AnimalInfo();
        }
        if (record == null) {
            return info;
        }
        trySet(record, INFO_DIET, info::setDiet);
        trySet(record, INFO_HABITAT, info::setHabitat);
        trySet(record, INFO_BEHAVIOR, info::setBehavior);
        trySet(record, INFO_HABITS_LIFESTYLE, info::setHabitsLifestyle);
        if (info.getHabitsLifestyle() != null && info.getHabits() != null
                && info.getHabits().getDescription() == null) {
            info.getHabits().setDescription(info.getHabitsLifestyle());
        }
        return info;
    }

    public JAnimalinfo toJooqInfo(AnimalInfo info) {
        if (info == null) {
            return null;
        }
        JAnimalinfo row = new JAnimalinfo();
        row.setId(info.getId());
        row.setAnimalid(info.getAnimalId());
        if (info.getDistribution() != null) {
            row.setDistribution(info.getDistribution().getDescription());
        }
        if (info.getAppearance() != null) {
            row.setAppearance(info.getAppearance().getDescription());
        }
        String habitsText = info.getHabitsLifestyle();
        if (habitsText == null && info.getHabits() != null) {
            habitsText = info.getHabits().getDescription();
        }
        row.setHabits(habitsText);
        return row;
    }

    public Distribution fromDistributionRecord(Record record) {
        if (record == null || record.get(DIST_ID) == null) {
            return null;
        }
        return Distribution.builder()
                .id(record.get(DIST_ID))
                .animalId(record.get(DIST_ANIMAL_ID))
                .description(record.get(DIST_DESCRIPTION))
                .build();
    }

    public DistributionLocation fromLocationRecord(Record record) {
        if (record == null || record.get(LOC_ID) == null) {
            return null;
        }
        String type = record.get(LOC_TYPE);
        return DistributionLocation.builder()
                .id(record.get(LOC_ID))
                .distributionId(record.get(LOC_DISTRIBUTION_ID))
                .locationType(type == null ? null : EnumId.parseId(LocationType.class, type, null))
                .value(record.get(LOC_VALUE))
                .build();
    }

    public Population fromPopulationRecord(Record record) {
        if (record == null || record.get(POP_ID) == null) {
            return null;
        }
        String trend = record.get(POP_TREND);
        String conservation = record.get(POP_CONSERVATION);
        return Population.builder()
                .id(record.get(POP_ID))
                .animalId(record.get(POP_ANIMAL_ID))
                .populationThreats(record.get(POP_THREATS))
                .populationNumber(record.get(POP_NUMBER))
                .ecologicalNiche(record.get(POP_NICHE))
                .populationTrend(trend == null ? null : EnumId.parseId(PopulationTrend.class, trend, null))
                .populationStatus(record.get(POP_STATUS))
                .conservationStatus(conservation == null ? null : EnumId.parseId(VulnerabilityStatus.class, conservation, null))
                .build();
    }

    public Subspecies fromSubspeciesRecord(Record record) {
        if (record == null || record.get(SUB_ID) == null) {
            return null;
        }
        return Subspecies.builder()
                .id(record.get(SUB_ID))
                .animalId(record.get(SUB_ANIMAL_ID))
                .name(record.get(SUB_NAME))
                .scientificName(record.get(SUB_SCIENTIFIC_NAME))
                .description(record.get(SUB_DESCRIPTION))
                .build();
    }

    public AnimalReference fromReferenceRecord(Record record) {
        if (record == null || record.get(REF_ID) == null) {
            return null;
        }
        return AnimalReference.builder()
                .id(record.get(REF_ID))
                .animalId(record.get(REF_ANIMAL_ID))
                .title(record.get(REF_TITLE))
                .url(record.get(REF_URL))
                .citation(record.get(REF_CITATION))
                .sortOrder(record.get(REF_SORT_ORDER))
                .build();
    }

    public Biome fromBiomeValues(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        List<BiomeId> biomes = values.stream()
                .map(v -> EnumId.parseId(BiomeId.class, v, null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return Biome.builder().biomes(biomes).build();
    }

    public ClimateZones fromClimateValues(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        List<ClimateZone> zones = values.stream()
                .map(v -> EnumId.parseId(ClimateZone.class, v, null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return ClimateZones.builder().zones(zones).build();
    }

    public String biomeValue(Record record) {
        return record.get(BIOME_VALUE);
    }

    public String climateValue(Record record) {
        return record.get(CLIMATE_VALUE);
    }

    public UUID mapImageId(Record record) {
        return record == null ? null : record.get(DIST_MAP_IMAGE_ID);
    }

    public Animal fullAnimal(JAnimal jAnimal, JImage jImage) {
        if (Objects.isNull(jAnimal) && Objects.isNull(jImage)) {
            return null;
        }
        Image i = Image.builder()
                .id(jImage.getId())
                .name(jImage.getName())
                .type(jImage.getType())
                .imageArray(jImage.getImagearray())
                .build();

        return Animal.builder()
                .id(jAnimal.getId())
                .name(jAnimal.getName())
                .genus(jAnimal.getGenus())
                .description(jAnimal.getDescription())
                .family(jAnimal.getFamily())
                .phylum(jAnimal.getPhylum())
                .scientificName(jAnimal.getScientificname())
                .species(jAnimal.getSpecies())
                .kingdom(jAnimal.getKingdom())
                .order(jAnimal.getOrder())
                .animalImages(List.of(i))
                .build();
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
        if (jImage == null) {
            return null;
        }
        Image image = new Image();
        image.setImageArray(jImage.getImagearray());
        image.setType(jImage.getType());
        image.setId(jImage.getId());
        image.setName(jImage.getName());
        image.setAnimalId(jImage.getAnimalId());
        image.setUploadedAt(CommonConvertor.ZDT_CONVERTOR.from(jImage.getUploadedat()));
        return image;
    }

    public Image fromImageRecord(Record record) {
        if (record == null) {
            return null;
        }
        Image image = Image.builder()
                .id(record.get(com.animal.jooq.generated.tables.JImage.IMAGE.ID))
                .name(record.get(com.animal.jooq.generated.tables.JImage.IMAGE.NAME))
                .type(record.get(com.animal.jooq.generated.tables.JImage.IMAGE.TYPE))
                .imageArray(record.get(com.animal.jooq.generated.tables.JImage.IMAGE.IMAGEARRAY))
                .animalId(record.get(com.animal.jooq.generated.tables.JImage.IMAGE.ANIMAL_ID))
                .uploadedAt(CommonConvertor.ZDT_CONVERTOR.from(
                        record.get(com.animal.jooq.generated.tables.JImage.IMAGE.UPLOADEDAT)))
                .url(readString(record, "url"))
                .cloudinaryPublicId(readString(record, "cloudinary_public_id"))
                .build();
        return image;
    }

    private static String readString(Record record, String fieldName) {
        try {
            Object value = record.get(fieldName);
            if (value == null) {
                // SELECT * / aliased fields may expose unqualified names only
                for (org.jooq.Field<?> field : record.fields()) {
                    if (fieldName.equalsIgnoreCase(field.getName())) {
                        value = record.get(field);
                        break;
                    }
                }
            }
            return value == null ? null : String.valueOf(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    public GalleryImage toGalleryImage(Image image) {
        if (image == null) {
            return null;
        }
        GalleryImage galleryImage = new GalleryImage(
                image.getId(),
                image.getName(),
                image.getType(),
                image.getImageArray(),
                image.getUploadedAt(),
                image.getAnimalId()
        );
        galleryImage.setUrl(image.getUrl());
        galleryImage.setCloudinaryPublicId(image.getCloudinaryPublicId());
        return galleryImage;
    }

    public List<Image> fromJooqImages(List<JImage> jImages) {
        return jImages.stream().map(this::fromJooqImage).collect(Collectors.toList());
    }

    public JImage toJooqImage(Image image) {
        JImage jImage = new JImage();
        jImage.setAnimalId(image.getAnimalId());
        jImage.setImagearray(image.getImageArray());
        jImage.setName(image.getName());
        jImage.setType(image.getType());
        jImage.setId(image.getId());
        jImage.setUploadedat(CommonConvertor.ZDT_CONVERTOR.to(image.getUploadedAt()));
        return jImage;
    }

    public Animal toRelatedCard(Animal source) {
        if (source == null) {
            return null;
        }
        return Animal.builder()
                .id(source.getId())
                .name(source.getName())
                .scientificName(source.getScientificName())
                .family(source.getFamily())
                .genus(source.getGenus())
                .species(source.getSpecies())
                .animalImages(source.getAnimalImages())
                .build();
    }

    private interface Setter<T> {
        void set(T value);
    }

    private <T> void trySet(Record record, org.jooq.Field<T> field, Setter<T> setter) {
        try {
            if (record.field(field) != null) {
                setter.set(record.get(field));
            }
        } catch (Exception ignored) {
            // Column may be absent before V2 migration / codegen refresh.
        }
    }
}
