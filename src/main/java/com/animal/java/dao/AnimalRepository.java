package com.animal.java.dao;

import com.animal.java.annotations.JooqConfiguration;
import com.animal.java.annotations.TransactionRequired;
import com.animal.java.mapper.AnimalMapper;
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
import com.animal.java.utils.EnumId;
import com.animal.jooq.generated.tables.JAnimal;
import com.animal.jooq.generated.tables.JAnimalextendedinfo;
import com.animal.jooq.generated.tables.JAnimalinfo;
import com.animal.jooq.generated.tables.JGalleryimage;
import com.animal.jooq.generated.tables.daos.JAnimalDao;
import com.animal.jooq.generated.tables.daos.JAnimalextendedinfoDao;
import com.animal.jooq.generated.tables.daos.JAnimalinfoDao;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.animal.java.dao.schema.AnimalDetailTables.ANIMAL_BIOME;
import static com.animal.java.dao.schema.AnimalDetailTables.ANIMAL_CLIMATE_ZONE;
import static com.animal.java.dao.schema.AnimalDetailTables.ANIMAL_REFERENCE;
import static com.animal.java.dao.schema.AnimalDetailTables.BIOME_ANIMAL_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.BIOME_VALUE;
import static com.animal.java.dao.schema.AnimalDetailTables.CLIMATE_ANIMAL_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.CLIMATE_VALUE;
import static com.animal.java.dao.schema.AnimalDetailTables.DISTRIBUTION;
import static com.animal.java.dao.schema.AnimalDetailTables.DISTRIBUTION_LOCATION;
import static com.animal.java.dao.schema.AnimalDetailTables.DIST_ANIMAL_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.DIST_DESCRIPTION;
import static com.animal.java.dao.schema.AnimalDetailTables.DIST_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.DIST_MAP_IMAGE_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.INFO_BEHAVIOR;
import static com.animal.java.dao.schema.AnimalDetailTables.INFO_DIET;
import static com.animal.java.dao.schema.AnimalDetailTables.INFO_HABITAT;
import static com.animal.java.dao.schema.AnimalDetailTables.INFO_HABITS_LIFESTYLE;
import static com.animal.java.dao.schema.AnimalDetailTables.LOC_DISTRIBUTION_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.LOC_TYPE;
import static com.animal.java.dao.schema.AnimalDetailTables.LOC_VALUE;
import static com.animal.java.dao.schema.AnimalDetailTables.POPULATION;
import static com.animal.java.dao.schema.AnimalDetailTables.POP_ANIMAL_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.POP_CONSERVATION;
import static com.animal.java.dao.schema.AnimalDetailTables.POP_ID;
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
import static com.animal.java.dao.schema.AnimalDetailTables.SUBSPECIES;
import static com.animal.java.dao.schema.AnimalDetailTables.SUB_ANIMAL_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.SUB_DESCRIPTION;
import static com.animal.java.dao.schema.AnimalDetailTables.SUB_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.SUB_NAME;
import static com.animal.java.dao.schema.AnimalDetailTables.SUB_SCIENTIFIC_NAME;

@Component
@TransactionRequired
public class AnimalRepository {

    private final DefaultDSLContext dsl;
    private final JAnimalDao animalDao;
    private final JAnimalinfoDao animalInfoDao;
    private final JAnimalextendedinfoDao animalExtendedInfoDao;
    private final AnimalMapper mapper;
    private final ImageRepository imageRepository;

    private final JAnimal ANIMAL_ = JAnimal.ANIMAL;
    private final JAnimalinfo ANIMAL_INFO = JAnimalinfo.ANIMALINFO;
    private final JAnimalextendedinfo ANIMAL_EXTENDED = JAnimalextendedinfo.ANIMALEXTENDEDINFO;
    private final JGalleryimage GALLERY = JGalleryimage.GALLERYIMAGE;

    public AnimalRepository(
            @JooqConfiguration DefaultConfiguration create,
            ImageRepository imageRepository,
            AnimalMapper mapper
    ) {
        this.dsl = new DefaultDSLContext(create);
        this.animalDao = new JAnimalDao(create);
        this.animalInfoDao = new JAnimalinfoDao(create);
        this.animalExtendedInfoDao = new JAnimalextendedinfoDao(create);
        this.imageRepository = imageRepository;
        this.mapper = mapper;
    }

    public List<Animal> findAll() {
        return dsl.selectFrom(ANIMAL_).fetch(mapper::fromAnimalRecord);
    }

    public List<Animal> findAllForBrowse() {
        List<Animal> animals = findAll();
        for (Animal animal : animals) {
            if (animal.getId() == null) {
                continue;
            }
            animal.setAnimalImages(loadImages(animal.getId()));
            animal.setAnimalInfo(loadBrowseInfo(animal.getId()));
        }
        return animals;
    }

    private AnimalInfo loadBrowseInfo(UUID animalId) {
        Record infoRecord = dsl.select()
                .from(ANIMAL_INFO)
                .where(ANIMAL_INFO.ANIMALID.eq(animalId))
                .fetchOne();

        AnimalInfo info = AnimalInfo.builder().animalId(animalId).build();
        if (infoRecord != null) {
            info = mapper.enrichInfoFromRecord(
                    mapper.fromJooqInfo(infoRecord.into(com.animal.jooq.generated.tables.pojos.JAnimalinfo.class)),
                    infoRecord);
        }
        info.setBiome(loadBiome(animalId));
        info.setClimateZones(loadClimateZones(animalId));
        info.setPopulation(loadPopulation(animalId));
        return info;
    }

    public Animal getById(UUID id) {
        Record animalRecord = dsl.select()
                .from(ANIMAL_)
                .where(ANIMAL_.ID.eq(id))
                .fetchOne();
        if (animalRecord == null) {
            return null;
        }

        Animal animal = mapper.fromAnimalRecord(animalRecord);
        animal.setAnimalImages(loadImages(id));
        animal.setGalleryImages(loadGallery(id));
        animal.setAnimalExtendedInfo(loadExtendedInfo(id));
        animal.setAnimalInfo(loadAnimalInfo(id));
        animal.setSubspecies(loadSubspecies(id));
        animal.setReferences(loadReferences(id));
        animal.setRelatedAnimals(findRelated(animal, 8));
        return animal;
    }

    public List<Animal> findRelated(Animal animal, int limit) {
        if (animal == null || animal.getId() == null) {
            return List.of();
        }
        List<Animal> related = dsl.selectFrom(ANIMAL_)
                .where(ANIMAL_.ID.ne(animal.getId())
                        .and(ANIMAL_.FAMILY.eq(animal.getFamily())
                                .or(ANIMAL_.GENUS.eq(animal.getGenus()))))
                .limit(limit)
                .fetch(mapper::fromAnimalRecord);

        for (Animal relatedAnimal : related) {
            List<Image> images = loadImages(relatedAnimal.getId());
            relatedAnimal.setAnimalImages(images);
        }
        return related.stream().map(mapper::toRelatedCard).collect(Collectors.toList());
    }

    public Animal addAnimal(Animal animal) {
        if (animal.getId() == null) {
            animal.setId(UUID.randomUUID());
        }
        animalDao.insert(mapper.toJooq(animal));
        updateTaxonomyExtras(animal);
        saveExtendedInfo(animal);
        saveAnimalInfo(animal);
        saveDistribution(animal);
        savePopulation(animal);
        saveBiomesAndClimate(animal);
        saveSubspecies(animal);
        saveReferences(animal);
        return animal;
    }

    public boolean existsById(UUID id) {
        return id != null && dsl.fetchExists(dsl.selectFrom(ANIMAL_).where(ANIMAL_.ID.eq(id)));
    }

    public Animal updateAnimal(Animal animal) {
        if (animal == null || animal.getId() == null || !existsById(animal.getId())) {
            return null;
        }
        dsl.update(ANIMAL_)
                .set(ANIMAL_.PHYLUM, animal.getPhylum())
                .set(ANIMAL_.FAMILY, animal.getFamily())
                .set(ANIMAL_.NAME, animal.getName())
                .set(ANIMAL_.SCIENTIFICNAME, animal.getScientificName())
                .set(ANIMAL_.DESCRIPTION, animal.getDescription())
                .set(ANIMAL_.GENUS, animal.getGenus())
                .set(ANIMAL_.SPECIES, animal.getSpecies())
                .set(ANIMAL_.ORDER, animal.getOrder())
                .set(ANIMAL_.KINGDOM, animal.getKingdom())
                .where(ANIMAL_.ID.eq(animal.getId()))
                .execute();
        updateTaxonomyExtras(animal);
        upsertExtendedInfo(animal);
        upsertAnimalInfo(animal);
        upsertDistribution(animal);
        upsertPopulation(animal);
        replaceBiomesAndClimate(animal);
        if (animal.getSubspecies() != null) {
            dsl.deleteFrom(SUBSPECIES).where(SUB_ANIMAL_ID.eq(animal.getId())).execute();
            saveSubspecies(animal);
        }
        if (animal.getReferences() != null) {
            dsl.deleteFrom(ANIMAL_REFERENCE).where(REF_ANIMAL_ID.eq(animal.getId())).execute();
            saveReferences(animal);
        }
        return getById(animal.getId());
    }

    public void deleteAnimalById(UUID id) {
        dsl.deleteFrom(ANIMAL_).where(ANIMAL_.ID.eq(id)).execute();
    }

    public boolean existsByName(String name) {
        return dsl.fetchExists(dsl.selectFrom(ANIMAL_).where(ANIMAL_.NAME.equalIgnoreCase(name)));
    }

    public boolean existsByScientificName(String scientificName) {
        return dsl.fetchExists(dsl.selectFrom(ANIMAL_).where(ANIMAL_.SCIENTIFICNAME.equalIgnoreCase(scientificName)));
    }

    private void updateTaxonomyExtras(Animal animal) {
        dsl.update(ANIMAL_)
                .set(ANIMAL_.SUBPHYLUM, animal.getSubphylum())
                .set(ANIMAL_.ANIMAL_CLASS, animal.getAnimalClass())
                .set(ANIMAL_.SUBORDER, animal.getSuborder())
                .where(ANIMAL_.ID.eq(animal.getId()))
                .execute();
    }

    private AnimalExtendedInfo loadExtendedInfo(UUID animalId) {
        com.animal.jooq.generated.tables.pojos.JAnimalextendedinfo row = dsl.selectFrom(ANIMAL_EXTENDED)
                .where(ANIMAL_EXTENDED.ANIMALID.eq(animalId))
                .fetchOneInto(com.animal.jooq.generated.tables.pojos.JAnimalextendedinfo.class);
        return mapper.fromJooqExtended(row);
    }

    private AnimalInfo loadAnimalInfo(UUID animalId) {
        Record infoRecord = dsl.select()
                .from(ANIMAL_INFO)
                .where(ANIMAL_INFO.ANIMALID.eq(animalId))
                .fetchOne();

        AnimalInfo info = infoRecord == null
                ? AnimalInfo.builder().animalId(animalId).build()
                : mapper.enrichInfoFromRecord(
                        mapper.fromJooqInfo(infoRecord.into(com.animal.jooq.generated.tables.pojos.JAnimalinfo.class)),
                        infoRecord);

        info.setDistribution(loadDistribution(animalId));
        info.setPopulation(loadPopulation(animalId));
        info.setBiome(loadBiome(animalId));
        info.setClimateZones(loadClimateZones(animalId));
        return info;
    }

    private Distribution loadDistribution(UUID animalId) {
        Record distRecord = dsl.selectFrom(DISTRIBUTION)
                .where(DIST_ANIMAL_ID.eq(animalId))
                .fetchOne();
        Distribution distribution = mapper.fromDistributionRecord(distRecord);
        if (distribution == null) {
            return null;
        }

        UUID mapImageId = mapper.mapImageId(distRecord);
        if (mapImageId != null) {
            distribution.setDistributionMap(imageRepository.getById(mapImageId));
        }

        List<DistributionLocation> locations = dsl.selectFrom(DISTRIBUTION_LOCATION)
                .where(LOC_DISTRIBUTION_ID.eq(distribution.getId()))
                .fetch(mapper::fromLocationRecord)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        distribution.setLocations(locations);
        return distribution;
    }

    private Population loadPopulation(UUID animalId) {
        Record record = dsl.selectFrom(POPULATION)
                .where(POP_ANIMAL_ID.eq(animalId))
                .fetchOne();
        return mapper.fromPopulationRecord(record);
    }

    private Biome loadBiome(UUID animalId) {
        List<String> values = dsl.select(BIOME_VALUE)
                .from(ANIMAL_BIOME)
                .where(BIOME_ANIMAL_ID.eq(animalId))
                .fetch(BIOME_VALUE);
        return mapper.fromBiomeValues(values);
    }

    private ClimateZones loadClimateZones(UUID animalId) {
        List<String> values = dsl.select(CLIMATE_VALUE)
                .from(ANIMAL_CLIMATE_ZONE)
                .where(CLIMATE_ANIMAL_ID.eq(animalId))
                .fetch(CLIMATE_VALUE);
        return mapper.fromClimateValues(values);
    }

    private List<Subspecies> loadSubspecies(UUID animalId) {
        return dsl.selectFrom(SUBSPECIES)
                .where(SUB_ANIMAL_ID.eq(animalId))
                .fetch(mapper::fromSubspeciesRecord)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<AnimalReference> loadReferences(UUID animalId) {
        return dsl.selectFrom(ANIMAL_REFERENCE)
                .where(REF_ANIMAL_ID.eq(animalId))
                .orderBy(REF_SORT_ORDER.asc().nullsLast())
                .fetch(mapper::fromReferenceRecord)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Image> loadImages(UUID animalId) {
        return imageRepository.getByAnimalId(animalId);
    }

    private List<GalleryImage> loadGallery(UUID animalId) {
        List<UUID> galleryIds = dsl.select(GALLERY.ID)
                .from(GALLERY)
                .where(GALLERY.ANIMALID.eq(animalId))
                .fetch(GALLERY.ID);
        if (galleryIds.isEmpty()) {
            return List.of();
        }
        return galleryIds.stream()
                .map(imageRepository::getById)
                .filter(Objects::nonNull)
                .map(mapper::toGalleryImage)
                .collect(Collectors.toList());
    }

    private void saveExtendedInfo(Animal animal) {
        AnimalExtendedInfo extended = animal.getAnimalExtendedInfo();
        if (extended == null) {
            return;
        }
        if (extended.getId() == null) {
            extended.setId(UUID.randomUUID());
        }
        extended.setAnimalId(animal.getId());
        animalExtendedInfoDao.insert(mapper.toJooqExtended(extended));
    }

    private void upsertExtendedInfo(Animal animal) {
        AnimalExtendedInfo extended = animal.getAnimalExtendedInfo();
        if (extended == null) {
            return;
        }
        com.animal.jooq.generated.tables.pojos.JAnimalextendedinfo existing = dsl.selectFrom(ANIMAL_EXTENDED)
                .where(ANIMAL_EXTENDED.ANIMALID.eq(animal.getId()))
                .fetchOneInto(com.animal.jooq.generated.tables.pojos.JAnimalextendedinfo.class);
        if (existing == null) {
            saveExtendedInfo(animal);
            return;
        }
        extended.setId(existing.getId());
        extended.setAnimalId(animal.getId());
        animalExtendedInfoDao.update(mapper.toJooqExtended(extended));
    }

    private void saveAnimalInfo(Animal animal) {
        AnimalInfo info = animal.getAnimalInfo();
        if (info == null) {
            return;
        }
        if (info.getId() == null) {
            info.setId(UUID.randomUUID());
        }
        info.setAnimalId(animal.getId());
        animalInfoDao.insert(mapper.toJooqInfo(info));
        updateAnimalInfoExtras(info);
    }

    private void upsertAnimalInfo(Animal animal) {
        AnimalInfo info = animal.getAnimalInfo();
        if (info == null) {
            return;
        }
        com.animal.jooq.generated.tables.pojos.JAnimalinfo existing = dsl.selectFrom(ANIMAL_INFO)
                .where(ANIMAL_INFO.ANIMALID.eq(animal.getId()))
                .fetchOneInto(com.animal.jooq.generated.tables.pojos.JAnimalinfo.class);
        if (existing == null) {
            saveAnimalInfo(animal);
            return;
        }
        info.setId(existing.getId());
        info.setAnimalId(animal.getId());
        animalInfoDao.update(mapper.toJooqInfo(info));
        updateAnimalInfoExtras(info);
    }

    private void updateAnimalInfoExtras(AnimalInfo info) {
        dsl.update(ANIMAL_INFO)
                .set(INFO_DIET, info.getDiet())
                .set(INFO_HABITAT, info.getHabitat())
                .set(INFO_BEHAVIOR, info.getBehavior())
                .set(INFO_HABITS_LIFESTYLE, resolveHabitsLifestyle(info))
                .where(ANIMAL_INFO.ID.eq(info.getId()))
                .execute();
    }

    private String resolveHabitsLifestyle(AnimalInfo info) {
        if (info.getHabitsLifestyle() != null) {
            return info.getHabitsLifestyle();
        }
        if (info.getHabits() != null) {
            return info.getHabits().getDescription();
        }
        return null;
    }

    private void saveDistribution(Animal animal) {
        AnimalInfo info = animal.getAnimalInfo();
        if (info == null || info.getDistribution() == null) {
            return;
        }
        Distribution distribution = info.getDistribution();
        if (distribution.getId() == null) {
            distribution.setId(UUID.randomUUID());
        }
        distribution.setAnimalId(animal.getId());
        UUID mapImageId = distribution.getDistributionMap() != null
                ? distribution.getDistributionMap().getId()
                : null;

        dsl.insertInto(DISTRIBUTION)
                .set(DIST_ID, distribution.getId())
                .set(DIST_ANIMAL_ID, distribution.getAnimalId())
                .set(DIST_DESCRIPTION, distribution.getDescription())
                .set(DIST_MAP_IMAGE_ID, mapImageId)
                .execute();

        persistDistributionLocations(distribution);
    }

    private void upsertDistribution(Animal animal) {
        AnimalInfo info = animal.getAnimalInfo();
        if (info == null || info.getDistribution() == null) {
            return;
        }
        Record existing = dsl.selectFrom(DISTRIBUTION)
                .where(DIST_ANIMAL_ID.eq(animal.getId()))
                .fetchOne();
        if (existing == null) {
            saveDistribution(animal);
            return;
        }

        Distribution distribution = info.getDistribution();
        distribution.setId(existing.get(DIST_ID));
        distribution.setAnimalId(animal.getId());
        UUID mapImageId = distribution.getDistributionMap() != null
                ? distribution.getDistributionMap().getId()
                : existing.get(DIST_MAP_IMAGE_ID);

        dsl.update(DISTRIBUTION)
                .set(DIST_DESCRIPTION, distribution.getDescription())
                .set(DIST_MAP_IMAGE_ID, mapImageId)
                .where(DIST_ID.eq(distribution.getId()))
                .execute();

        boolean hasIncomingLocations = (distribution.getLocations() != null && !distribution.getLocations().isEmpty())
                || hasLocationLists(distribution);
        if (hasIncomingLocations) {
            dsl.deleteFrom(DISTRIBUTION_LOCATION)
                    .where(LOC_DISTRIBUTION_ID.eq(distribution.getId()))
                    .execute();
            persistDistributionLocations(distribution);
        }
    }

    private boolean hasLocationLists(Distribution distribution) {
        return (distribution.getContinents() != null && !distribution.getContinents().isEmpty())
                || (distribution.getSubcontinents() != null && !distribution.getSubcontinents().isEmpty())
                || (distribution.getCountries() != null && !distribution.getCountries().isEmpty())
                || (distribution.getBiogeographicalRealms() != null && !distribution.getBiogeographicalRealms().isEmpty())
                || (distribution.getWwfBiomes() != null && !distribution.getWwfBiomes().isEmpty());
    }

    private void persistDistributionLocations(Distribution distribution) {
        List<DistributionLocation> locations = new ArrayList<>();
        if (distribution.getLocations() != null) {
            locations.addAll(distribution.getLocations());
        }
        addLocationValues(locations, distribution.getId(), LocationType.CONTINENT, distribution.getContinents());
        addLocationValues(locations, distribution.getId(), LocationType.SUBCONTINENT, distribution.getSubcontinents());
        addLocationValues(locations, distribution.getId(), LocationType.COUNTRY, distribution.getCountries());
        addLocationValues(locations, distribution.getId(), LocationType.BIOGEOGRAPHICAL_REALM, distribution.getBiogeographicalRealms());
        addLocationValues(locations, distribution.getId(), LocationType.WWF_BIOME, distribution.getWwfBiomes());

        for (DistributionLocation location : locations) {
            if (location == null || location.getValue() == null || location.getLocationType() == null) {
                continue;
            }
            UUID locId = location.getId() != null ? location.getId() : UUID.randomUUID();
            dsl.insertInto(DISTRIBUTION_LOCATION)
                    .set(DSL.field(DSL.name("id"), UUID.class), locId)
                    .set(LOC_DISTRIBUTION_ID, distribution.getId())
                    .set(LOC_TYPE, location.getLocationType().getId())
                    .set(LOC_VALUE, location.getValue())
                    .execute();
        }
    }

    private void addLocationValues(
            List<DistributionLocation> target,
            UUID distributionId,
            LocationType type,
            List<String> values
    ) {
        if (values == null) {
            return;
        }
        boolean alreadyPresent = target.stream()
                .anyMatch(loc -> type.equals(loc.getLocationType()));
        if (alreadyPresent) {
            return;
        }
        for (String value : values) {
            target.add(DistributionLocation.builder()
                    .distributionId(distributionId)
                    .locationType(type)
                    .value(value)
                    .build());
        }
    }

    private void savePopulation(Animal animal) {
        AnimalInfo info = animal.getAnimalInfo();
        if (info == null || info.getPopulation() == null) {
            return;
        }
        Population population = info.getPopulation();
        if (population.getId() == null) {
            population.setId(UUID.randomUUID());
        }
        population.setAnimalId(animal.getId());
        persistPopulation(population);
    }

    private void upsertPopulation(Animal animal) {
        AnimalInfo info = animal.getAnimalInfo();
        if (info == null || info.getPopulation() == null) {
            return;
        }
        Population population = info.getPopulation();
        Record existing = dsl.selectFrom(POPULATION)
                .where(POP_ANIMAL_ID.eq(animal.getId()))
                .fetchOne();
        if (existing == null) {
            savePopulation(animal);
            return;
        }
        population.setId(existing.get(POP_ID));
        population.setAnimalId(animal.getId());
        dsl.update(POPULATION)
                .set(POP_THREATS, population.getPopulationThreats())
                .set(POP_NUMBER, population.getPopulationNumber())
                .set(POP_NICHE, population.getEcologicalNiche())
                .set(POP_TREND, EnumId.getIdOf(population.getPopulationTrend()))
                .set(POP_STATUS, population.getPopulationStatus())
                .set(POP_CONSERVATION, EnumId.getIdOf(population.getConservationStatus()))
                .where(POP_ID.eq(population.getId()))
                .execute();
    }

    private void persistPopulation(Population population) {
        dsl.insertInto(POPULATION)
                .set(POP_ID, population.getId())
                .set(POP_ANIMAL_ID, population.getAnimalId())
                .set(POP_THREATS, population.getPopulationThreats())
                .set(POP_NUMBER, population.getPopulationNumber())
                .set(POP_NICHE, population.getEcologicalNiche())
                .set(POP_TREND, EnumId.getIdOf(population.getPopulationTrend()))
                .set(POP_STATUS, population.getPopulationStatus())
                .set(POP_CONSERVATION, EnumId.getIdOf(population.getConservationStatus()))
                .execute();
    }

    private void replaceBiomesAndClimate(Animal animal) {
        dsl.deleteFrom(ANIMAL_BIOME).where(BIOME_ANIMAL_ID.eq(animal.getId())).execute();
        dsl.deleteFrom(ANIMAL_CLIMATE_ZONE).where(CLIMATE_ANIMAL_ID.eq(animal.getId())).execute();
        saveBiomesAndClimate(animal);
    }

    private void saveBiomesAndClimate(Animal animal) {
        AnimalInfo info = animal.getAnimalInfo();
        if (info == null) {
            return;
        }
        if (info.getBiome() != null && info.getBiome().getBiomes() != null) {
            for (BiomeId biomeId : info.getBiome().getBiomes()) {
                dsl.insertInto(ANIMAL_BIOME)
                        .set(BIOME_ANIMAL_ID, animal.getId())
                        .set(BIOME_VALUE, biomeId.getId())
                        .execute();
            }
        }
        if (info.getClimateZones() != null && info.getClimateZones().getZones() != null) {
            for (ClimateZone zone : info.getClimateZones().getZones()) {
                dsl.insertInto(ANIMAL_CLIMATE_ZONE)
                        .set(CLIMATE_ANIMAL_ID, animal.getId())
                        .set(CLIMATE_VALUE, zone.getId())
                        .execute();
            }
        }
    }

    private void saveSubspecies(Animal animal) {
        if (animal.getSubspecies() == null) {
            return;
        }
        for (Subspecies subspecies : animal.getSubspecies()) {
            UUID id = subspecies.getId() != null ? subspecies.getId() : UUID.randomUUID();
            dsl.insertInto(SUBSPECIES)
                    .set(SUB_ID, id)
                    .set(SUB_ANIMAL_ID, animal.getId())
                    .set(SUB_NAME, subspecies.getName())
                    .set(SUB_SCIENTIFIC_NAME, subspecies.getScientificName())
                    .set(SUB_DESCRIPTION, subspecies.getDescription())
                    .execute();
        }
    }

    private void saveReferences(Animal animal) {
        if (animal.getReferences() == null) {
            return;
        }
        int order = 0;
        for (AnimalReference reference : animal.getReferences()) {
            UUID id = reference.getId() != null ? reference.getId() : UUID.randomUUID();
            Integer sort = reference.getSortOrder() != null ? reference.getSortOrder() : order++;
            dsl.insertInto(ANIMAL_REFERENCE)
                    .set(REF_ID, id)
                    .set(REF_ANIMAL_ID, animal.getId())
                    .set(REF_TITLE, reference.getTitle())
                    .set(REF_URL, reference.getUrl())
                    .set(REF_CITATION, reference.getCitation())
                    .set(REF_SORT_ORDER, sort)
                    .execute();
        }
    }
}
