package com.animal.java.dao.schema;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.UUID;

/**
 * jOOQ table/field defs for normalized animal-detail tables
 * (used until codegen is regenerated against V2).
 */
public final class AnimalDetailTables {

    private AnimalDetailTables() {
    }

    public static final Table<Record> DISTRIBUTION = DSL.table(DSL.name("distribution"));
    public static final Field<UUID> DIST_ID = DSL.field(DSL.name("distribution", "id"), UUID.class);
    public static final Field<UUID> DIST_ANIMAL_ID = DSL.field(DSL.name("distribution", "animal_id"), UUID.class);
    public static final Field<String> DIST_DESCRIPTION = DSL.field(DSL.name("distribution", "description"), String.class);
    public static final Field<UUID> DIST_MAP_IMAGE_ID = DSL.field(DSL.name("distribution", "map_image_id"), UUID.class);

    public static final Table<Record> DISTRIBUTION_LOCATION = DSL.table(DSL.name("distribution_location"));
    public static final Field<UUID> LOC_ID = DSL.field(DSL.name("distribution_location", "id"), UUID.class);
    public static final Field<UUID> LOC_DISTRIBUTION_ID = DSL.field(DSL.name("distribution_location", "distribution_id"), UUID.class);
    public static final Field<String> LOC_TYPE = DSL.field(DSL.name("distribution_location", "location_type"), String.class);
    public static final Field<String> LOC_VALUE = DSL.field(DSL.name("distribution_location", "value"), String.class);

    public static final Table<Record> ANIMAL_BIOME = DSL.table(DSL.name("animal_biome"));
    public static final Field<UUID> BIOME_ANIMAL_ID = DSL.field(DSL.name("animal_biome", "animal_id"), UUID.class);
    public static final Field<String> BIOME_VALUE = DSL.field(DSL.name("animal_biome", "biome"), String.class);

    public static final Table<Record> ANIMAL_CLIMATE_ZONE = DSL.table(DSL.name("animal_climate_zone"));
    public static final Field<UUID> CLIMATE_ANIMAL_ID = DSL.field(DSL.name("animal_climate_zone", "animal_id"), UUID.class);
    public static final Field<String> CLIMATE_VALUE = DSL.field(DSL.name("animal_climate_zone", "climate_zone"), String.class);

    public static final Table<Record> POPULATION = DSL.table(DSL.name("population"));
    public static final Field<UUID> POP_ID = DSL.field(DSL.name("population", "id"), UUID.class);
    public static final Field<UUID> POP_ANIMAL_ID = DSL.field(DSL.name("population", "animal_id"), UUID.class);
    public static final Field<String> POP_THREATS = DSL.field(DSL.name("population", "threats"), String.class);
    public static final Field<String> POP_NUMBER = DSL.field(DSL.name("population", "population_number"), String.class);
    public static final Field<String> POP_NICHE = DSL.field(DSL.name("population", "ecological_niche"), String.class);
    public static final Field<String> POP_TREND = DSL.field(DSL.name("population", "population_trend"), String.class);
    public static final Field<String> POP_STATUS = DSL.field(DSL.name("population", "population_status"), String.class);
    public static final Field<String> POP_CONSERVATION = DSL.field(DSL.name("population", "conservation_status"), String.class);

    public static final Table<Record> SUBSPECIES = DSL.table(DSL.name("subspecies"));
    public static final Field<UUID> SUB_ID = DSL.field(DSL.name("subspecies", "id"), UUID.class);
    public static final Field<UUID> SUB_ANIMAL_ID = DSL.field(DSL.name("subspecies", "animal_id"), UUID.class);
    public static final Field<String> SUB_NAME = DSL.field(DSL.name("subspecies", "name"), String.class);
    public static final Field<String> SUB_SCIENTIFIC_NAME = DSL.field(DSL.name("subspecies", "scientific_name"), String.class);
    public static final Field<String> SUB_DESCRIPTION = DSL.field(DSL.name("subspecies", "description"), String.class);

    public static final Table<Record> ANIMAL_REFERENCE = DSL.table(DSL.name("animal_reference"));
    public static final Field<UUID> REF_ID = DSL.field(DSL.name("animal_reference", "id"), UUID.class);
    public static final Field<UUID> REF_ANIMAL_ID = DSL.field(DSL.name("animal_reference", "animal_id"), UUID.class);
    public static final Field<String> REF_TITLE = DSL.field(DSL.name("animal_reference", "title"), String.class);
    public static final Field<String> REF_URL = DSL.field(DSL.name("animal_reference", "url"), String.class);
    public static final Field<String> REF_CITATION = DSL.field(DSL.name("animal_reference", "citation"), String.class);
    public static final Field<Integer> REF_SORT_ORDER = DSL.field(DSL.name("animal_reference", "sort_order"), Integer.class);

    public static final Field<String> ANIMAL_SUBPHYLUM = DSL.field(DSL.name("animal", "subphylum"), String.class);
    public static final Field<String> ANIMAL_CLASS = DSL.field(DSL.name("animal", "animal_class"), String.class);
    public static final Field<String> ANIMAL_SUBORDER = DSL.field(DSL.name("animal", "suborder"), String.class);

    public static final Field<String> INFO_DIET = DSL.field(DSL.name("animalinfo", "diet"), String.class);
    public static final Field<String> INFO_HABITAT = DSL.field(DSL.name("animalinfo", "habitat"), String.class);
    public static final Field<String> INFO_BEHAVIOR = DSL.field(DSL.name("animalinfo", "behavior"), String.class);
    public static final Field<String> INFO_HABITS_LIFESTYLE = DSL.field(DSL.name("animalinfo", "habits_lifestyle"), String.class);

    public static final Field<String> IMAGE_URL = DSL.field(DSL.name("url"), String.class);
    public static final Field<String> IMAGE_CLOUDINARY_PUBLIC_ID =
            DSL.field(DSL.name("cloudinary_public_id"), String.class);
}
