create schema if not exists animal;
set search_path to animal;


CREATE TABLE animal (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        phylum VARCHAR(255) NOT NULL,
                        family VARCHAR(255) NOT NULL,
                        name VARCHAR(255) UNIQUE NOT NULL,
                        scientificName VARCHAR(255) UNIQUE NOT NULL,
                        description TEXT,
                        genus VARCHAR(255),
                        species VARCHAR(255),
                        "order" VARCHAR(255),
                        kingdom VARCHAR(255)
);

CREATE TABLE animalExtendedInfo (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    animalId UUID NOT NULL,
                                    populationSize VARCHAR(255),
                                    lifeSpan VARCHAR(255),
                                    topSpeed VARCHAR(255),
                                    weight DOUBLE PRECISION,
                                    length VARCHAR(255),
                                    wingSpan VARCHAR(255),
                                    CONSTRAINT fk_animal_extended FOREIGN KEY (animalId) REFERENCES Animal(id) ON DELETE CASCADE
);

CREATE TABLE animalInfo (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            animalId UUID NOT NULL,
                            distribution TEXT,
                            appearance TEXT,
                            habits TEXT,
                            CONSTRAINT fk_animal_info FOREIGN KEY (animalId) REFERENCES Animal(id) ON DELETE CASCADE
);

CREATE TABLE image (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(255) NOT NULL,
                       type VARCHAR(255) NOT NULL,
                       imageArray BYTEA,
                       uploadedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE galleryImage (
                              id UUID PRIMARY KEY REFERENCES Image(id) ON DELETE CASCADE,
                              animalId UUID NOT NULL,
                              CONSTRAINT fk_gallery_image FOREIGN KEY (animalId) REFERENCES Animal(id) ON DELETE CASCADE
);

CREATE TABLE indexImage (
                            id UUID PRIMARY KEY REFERENCES Image(id) ON DELETE CASCADE,
                            animalId UUID NOT NULL,
                            CONSTRAINT fk_index_image FOREIGN KEY (animalId) REFERENCES Animal(id) ON DELETE CASCADE
);

ALTER TABLE Image ADD COLUMN  animal_id UUID references  Animal(id) on delete cascade;