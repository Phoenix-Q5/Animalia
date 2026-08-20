CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS animal (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phylum VARCHAR(255) NOT NULL,
    family VARCHAR(255) NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    scientificName VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    genus VARCHAR(255),
    species VARCHAR(255),
    "order" VARCHAR(255),
    kingdom VARCHAR(255),
    subphylum VARCHAR(255),
    animal_class VARCHAR(255),
    suborder VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS animalExtendedInfo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    animalId UUID NOT NULL,
    populationSize VARCHAR(255),
    lifeSpan VARCHAR(255),
    topSpeed VARCHAR(255),
    weight DOUBLE PRECISION,
    length VARCHAR(255),
    wingSpan VARCHAR(255),
    CONSTRAINT fk_animal_extended FOREIGN KEY (animalId) REFERENCES animal(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS animalInfo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    animalId UUID NOT NULL,
    distribution TEXT,
    appearance TEXT,
    habits TEXT,
    diet TEXT,
    habitat TEXT,
    behavior TEXT,
    habits_lifestyle TEXT,
    CONSTRAINT fk_animal_info FOREIGN KEY (animalId) REFERENCES animal(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS image (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    imageArray BYTEA,
    uploadedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    animal_id UUID REFERENCES animal(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS galleryImage (
    id UUID PRIMARY KEY REFERENCES image(id) ON DELETE CASCADE,
    animalId UUID NOT NULL,
    CONSTRAINT fk_gallery_image FOREIGN KEY (animalId) REFERENCES animal(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS indexImage (
    id UUID PRIMARY KEY REFERENCES image(id) ON DELETE CASCADE,
    animalId UUID NOT NULL,
    CONSTRAINT fk_index_image FOREIGN KEY (animalId) REFERENCES animal(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS distribution (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    animal_id UUID NOT NULL UNIQUE REFERENCES animal(id) ON DELETE CASCADE,
    description TEXT,
    map_image_id UUID REFERENCES image(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS distribution_location (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    distribution_id UUID NOT NULL REFERENCES distribution(id) ON DELETE CASCADE,
    location_type VARCHAR(50) NOT NULL,
    value VARCHAR(255) NOT NULL
);

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

CREATE TABLE IF NOT EXISTS animal_reference (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    animal_id UUID NOT NULL REFERENCES animal(id) ON DELETE CASCADE,
    title VARCHAR(500),
    url TEXT,
    citation TEXT,
    sort_order INT DEFAULT 0
);
