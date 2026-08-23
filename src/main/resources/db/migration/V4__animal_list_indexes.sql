-- Indexes for paginated Postgres fallback / admin queries at 10k+ scale
CREATE INDEX IF NOT EXISTS idx_animal_name_lower ON animal (lower(name));
CREATE INDEX IF NOT EXISTS idx_animal_scientific_lower ON animal (lower(scientificname));
CREATE INDEX IF NOT EXISTS idx_animal_species_lower ON animal (lower(species));
CREATE INDEX IF NOT EXISTS idx_animal_family_lower ON animal (lower(family));
CREATE INDEX IF NOT EXISTS idx_animal_name ON animal (name);
CREATE INDEX IF NOT EXISTS idx_image_animal_id ON image (animal_id);
CREATE INDEX IF NOT EXISTS idx_population_animal_id ON population (animal_id);
CREATE INDEX IF NOT EXISTS idx_animal_biome_animal_id ON animal_biome (animal_id);
CREATE INDEX IF NOT EXISTS idx_animal_climate_animal_id ON animal_climate_zone (animal_id);
