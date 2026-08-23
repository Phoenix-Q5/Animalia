/**
 * Paginated animal catalog API helpers (Redis + Elasticsearch backed).
 */

export function buildAnimalSearchParams({
  q = '',
  letter,
  species,
  habitat,
  biome,
  climate,
  status,
  page = 0,
  size = 24,
  sort = 'name,asc'
} = {}) {
  const params = new URLSearchParams();
  if (q && q.trim()) params.set('q', q.trim());
  if (letter) params.set('letter', letter);
  if (species) params.set('species', species);
  if (habitat) params.set('habitat', habitat);
  if (biome) params.set('biome', biome);
  if (climate) params.set('climate', climate);
  if (status) params.set('status', status);
  params.set('page', String(page));
  params.set('size', String(size));
  params.set('sort', sort);
  return params;
}

export async function fetchAnimalPage(options = {}) {
  const params = buildAnimalSearchParams(options);
  const response = await fetch(`/api/v1/animals?${params.toString()}`);
  if (!response.ok) {
    throw new Error(`Animal search failed (${response.status})`);
  }
  return response.json();
}

export async function fetchAnimalFacets() {
  const response = await fetch('/api/v1/animals/facets');
  if (!response.ok) {
    throw new Error(`Animal facets failed (${response.status})`);
  }
  return response.json();
}

export function browseFilterToSearchOptions(browseFilter) {
  if (!browseFilter?.type) return {};
  switch (browseFilter.type) {
    case 'letter':
      return { letter: browseFilter.value };
    case 'species':
      return { species: browseFilter.value };
    case 'habitat':
      return { habitat: browseFilter.value };
    case 'biome':
      return { biome: browseFilter.value };
    case 'climate':
      return { climate: browseFilter.value };
    case 'status':
      return { status: browseFilter.value };
    default:
      return {};
  }
}
