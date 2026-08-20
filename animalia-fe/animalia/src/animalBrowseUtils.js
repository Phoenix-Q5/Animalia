const ALPHABET = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');

export { ALPHABET };

export function enumLabel(value) {
  if (!value) return '';
  if (typeof value === 'string') return value;
  return value.description || value.id || value.name || '';
}

export function enumId(value) {
  if (!value) return '';
  if (typeof value === 'string') return value;
  return value.id || value.name || '';
}

export function conservationCode(animal) {
  const raw = animal?.animalInfo?.population?.conservationStatus;
  if (!raw) return '';
  if (typeof raw === 'string') return raw.toUpperCase();
  return (raw.id || raw.name || '').toUpperCase();
}

export function biomeIds(animal) {
  return (animal?.animalInfo?.biome?.biomes || []).map((b) => enumId(b)).filter(Boolean);
}

export function climateIds(animal) {
  return (animal?.animalInfo?.climateZones?.zones || []).map((z) => enumId(z)).filter(Boolean);
}

export function habitatText(animal) {
  return (animal?.animalInfo?.habitat || '').trim();
}

export function speciesKey(animal) {
  return (animal?.species || animal?.scientificName || '').trim();
}

export function firstLetter(text) {
  const match = String(text || '').trim().match(/[A-Za-z]/);
  return match ? match[0].toUpperCase() : null;
}

export function matchesSearch(animal, query) {
  const q = query.trim().toLowerCase();
  if (!q) return true;

  const biomes = (animal?.animalInfo?.biome?.biomes || []).map(enumLabel).join(' ');
  const climates = (animal?.animalInfo?.climateZones?.zones || []).map(enumLabel).join(' ');
  const status = conservationCode(animal);

  const haystack = [
    animal?.name,
    animal?.species,
    animal?.scientificName,
    animal?.genus,
    animal?.family,
    animal?.phylum,
    animal?.description,
    habitatText(animal),
    biomes,
    climates,
    status
  ]
    .filter(Boolean)
    .join(' ')
    .toLowerCase();

  return haystack.includes(q);
}

export function applyBrowseFilter(animals, filter) {
  if (!filter?.type || filter.value == null || filter.value === '') {
    return animals;
  }

  switch (filter.type) {
    case 'letter':
      return animals.filter((a) => firstLetter(a?.name) === filter.value);
    case 'speciesLetter':
      return animals.filter((a) => firstLetter(speciesKey(a)) === filter.value);
    case 'species':
      return animals.filter((a) => speciesKey(a).toLowerCase() === String(filter.value).toLowerCase());
    case 'biome':
      return animals.filter((a) => biomeIds(a).includes(filter.value));
    case 'climate':
      return animals.filter((a) => climateIds(a).includes(filter.value));
    case 'status':
      return animals.filter((a) => conservationCode(a) === filter.value);
    case 'habitat':
      return animals.filter((a) => habitatText(a).toLowerCase() === String(filter.value).toLowerCase());
    default:
      return animals;
  }
}

export function filterAnimals(animals, searchTerm, browseFilter) {
  return applyBrowseFilter(
    animals.filter((a) => matchesSearch(a, searchTerm)),
    browseFilter
  );
}

export function uniqueSpecies(animals) {
  const seen = new Map();
  animals.forEach((animal) => {
    const key = speciesKey(animal);
    if (key && !seen.has(key.toLowerCase())) {
      seen.set(key.toLowerCase(), key);
    }
  });
  return Array.from(seen.values()).sort((a, b) => a.localeCompare(b));
}

export function uniqueHabitats(animals) {
  const seen = new Set();
  animals.forEach((animal) => {
    const habitat = habitatText(animal);
    if (habitat) seen.add(habitat);
  });
  return Array.from(seen).sort((a, b) => a.localeCompare(b));
}

export function lettersWithMatches(animals, getLetter) {
  const letters = new Set();
  animals.forEach((animal) => {
    const letter = getLetter(animal);
    if (letter) letters.add(letter);
  });
  return letters;
}

export const SCENE_IMAGES = {
  forest: 'https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=900&q=70',
  grassland: 'https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=900&q=70',
  savanna: 'https://images.unsplash.com/photo-1516426122078-c23e76319801?auto=format&fit=crop&w=900&q=70',
  swamp: 'https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?auto=format&fit=crop&w=900&q=70',
  riparian: 'https://images.unsplash.com/photo-1439066615861-d1af74d74000?auto=format&fit=crop&w=900&q=70',
  woodland: 'https://images.unsplash.com/photo-1502082553048-f009c37129b9?auto=format&fit=crop&w=900&q=70',
  tropical: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=70',
  temperate: 'https://images.unsplash.com/photo-1508193638397-1c4234db14d1?auto=format&fit=crop&w=900&q=70',
  cold: 'https://images.unsplash.com/photo-1483664852095-d6cc46771f8f?auto=format&fit=crop&w=900&q=70',
  polar: 'https://images.unsplash.com/photo-1517783999520-f068d7431a60?auto=format&fit=crop&w=900&q=70'
};

export function sceneImageFor(label) {
  const key = String(label || '').trim().toLowerCase();
  return SCENE_IMAGES[key] || SCENE_IMAGES.forest;
}

export const IUCN_ORDER = ['NE', 'DD', 'LC', 'NT', 'VU', 'EN', 'CR', 'EW', 'EX'];

export const IUCN_META = {
  EX: { label: 'Extinct', tone: 'dark' },
  EW: { label: 'Extinct in the Wild', tone: 'dark' },
  CR: { label: 'Critically Endangered', tone: 'critical' },
  EN: { label: 'Endangered', tone: 'danger' },
  VU: { label: 'Vulnerable', tone: 'warn' },
  NT: { label: 'Near Threatened', tone: 'caution' },
  LC: { label: 'Least Concern', tone: 'safe' },
  DD: { label: 'Data Deficient', tone: 'muted' },
  NE: { label: 'Not Evaluated', tone: 'muted' }
};
