-- Taxonomy expansions on animal
ALTER TABLE animal ADD COLUMN IF NOT EXISTS subphylum VARCHAR(255);
ALTER TABLE animal ADD COLUMN IF NOT EXISTS animal_class VARCHAR(255);
ALTER TABLE animal ADD COLUMN IF NOT EXISTS suborder VARCHAR(255);

-- Ecology / lifestyle fields on animalinfo (appearance + habits text retained)
ALTER TABLE animalinfo ADD COLUMN IF NOT EXISTS diet TEXT;
ALTER TABLE animalinfo ADD COLUMN IF NOT EXISTS habitat TEXT;
ALTER TABLE animalinfo ADD COLUMN IF NOT EXISTS behavior TEXT;
ALTER TABLE animalinfo ADD COLUMN IF NOT EXISTS habits_lifestyle TEXT;

-- Distribution (1:1 with animal) — geography lists live in distribution_location
CREATE TABLE IF NOT EXISTS distribution (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    animal_id UUID NOT NULL UNIQUE REFERENCES animal(id) ON DELETE CASCADE,
    description TEXT,
    map_image_id UUID REFERENCES image(id) ON DELETE SET NULL
);

-- CONTINENT | SUBCONTINENT | COUNTRY | BIOGEOGRAPHICAL_REALM | WWF_BIOME
CREATE TABLE IF NOT EXISTS distribution_location (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    distribution_id UUID NOT NULL REFERENCES distribution(id) ON DELETE CASCADE,
    location_type VARCHAR(50) NOT NULL,
    value VARCHAR(255) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_distribution_location_dist
    ON distribution_location (distribution_id);

CREATE INDEX IF NOT EXISTS idx_distribution_location_type
    ON distribution_location (distribution_id, location_type);

-- Biomes and climate zones (many values per animal)
CREATE TABLE IF NOT EXISTS animal_biome (
    animal_id UUID NOT NULL REFERENCES animal(id) ON DELETE CASCADE,
    biome VARCHAR(100) NOT NULL,
    PRIMARY KEY (animal_id, biome)
);

CREATE TABLE IF NOT EXISTS animal_climate_zone (
    animal_id UUID NOT NULL REFERENCES animal(id) ON DELETE CASCADE,
    climate_zone VARCHAR(100) NOT NULL,
    PRIMARY KEY (animal_id, climate_zone)
);

-- Population / conservation
CREATE TABLE IF NOT EXISTS population (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    animal_id UUID NOT NULL UNIQUE REFERENCES animal(id) ON DELETE CASCADE,
    threats TEXT,
    population_number VARCHAR(255),
    ecological_niche TEXT,
    population_trend VARCHAR(50),
    population_status VARCHAR(255),
    conservation_status VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS subspecies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    animal_id UUID NOT NULL REFERENCES animal(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    scientific_name VARCHAR(255),
    description TEXT
);

CREATE INDEX IF NOT EXISTS idx_subspecies_animal ON subspecies (animal_id);

CREATE TABLE IF NOT EXISTS animal_reference (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    animal_id UUID NOT NULL REFERENCES animal(id) ON DELETE CASCADE,
    title VARCHAR(500),
    url TEXT,
    citation TEXT,
    sort_order INT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_animal_reference_animal ON animal_reference (animal_id);

-- Backfill distribution rows from legacy animalinfo.distribution text when present
INSERT INTO distribution (animal_id, description)
SELECT ai.animalid, ai.distribution
FROM animalinfo ai
WHERE ai.distribution IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM distribution d WHERE d.animal_id = ai.animalid
  );
