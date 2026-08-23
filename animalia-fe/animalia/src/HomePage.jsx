import React, { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { AnimatePresence, motion } from 'framer-motion';
import { useKeycloak } from '@react-keycloak/web';
import logo from './assets/animalia-logo-full.png';
import AnimalCard from './AnimalCard';
import { hasAdminRole } from './keycloak';
import {
  ALPHABET,
  IUCN_META,
  IUCN_ORDER,
  firstLetter,
  sceneImageFor
} from './animalBrowseUtils';
import { browseFilterToSearchOptions, fetchAnimalFacets, fetchAnimalPage } from './animalApi';

const HERO_IMAGES = [
  'bluefrog.jpg',
  'chameleon.jpg',
  'clown.jpg',
  'Crocodile.jpg',
  'falcon.jpg',
  'GrizzlyBear.jpg',
  'pitviper.jpg',
  'tiger.jpg',
  'zebra.jpg'
];

const HERO_SLIDE_MS = 7000;

const heroSlideVariants = {
  enter: {
    opacity: 0,
    scale: 1.04,
    filter: 'blur(8px)'
  },
  center: {
    opacity: 1,
    scale: 1.1,
    filter: 'blur(0px)',
    transition: {
      opacity: { duration: 1.35, ease: [0.22, 1, 0.36, 1] },
      scale: { duration: HERO_SLIDE_MS / 1000, ease: 'linear' },
      filter: { duration: 1.1, ease: 'easeOut' }
    }
  },
  exit: {
    opacity: 0,
    scale: 1.06,
    filter: 'blur(4px)',
    transition: {
      duration: 1.2,
      ease: [0.4, 0, 0.2, 1]
    }
  }
};

function HeroSlideshow({ images, activeIndex }) {
  return (
    <div className="home-hero-slideshow" aria-hidden="true">
      <AnimatePresence mode="sync" initial={false}>
        <motion.div
          key={images[activeIndex]}
          className="home-hero-slide"
          style={{ backgroundImage: `url(${images[activeIndex]})` }}
          variants={heroSlideVariants}
          initial="enter"
          animate="center"
          exit="exit"
        />
      </AnimatePresence>
      <div className="home-hero-shade" />
    </div>
  );
}

function HeroDots({ images, activeIndex, onSelect }) {
  return (
    <div className="home-hero-dots" role="tablist" aria-label="Hero slideshow">
      {images.map((image, index) => (
        <button
          key={image}
          type="button"
          role="tab"
          className={`home-hero-dot ${index === activeIndex ? 'is-active' : ''}`}
          aria-selected={index === activeIndex}
          aria-label={`Show slide ${index + 1}`}
          onClick={() => onSelect(index)}
        />
      ))}
    </div>
  );
}

const AZ_ROWS = [
  ALPHABET.slice(0, 9),
  ALPHABET.slice(9, 18),
  ALPHABET.slice(18, 26)
];

const letterMotion = {
  hidden: { opacity: 0, y: 22, scale: 0.82 },
  show: (i) => ({
    opacity: 1,
    y: 0,
    scale: 1,
    transition: { delay: i * 0.03, duration: 0.4, ease: [0.22, 1, 0.36, 1] }
  })
};

function AzLetterTile({ letter, active, disabled, onClick, index }) {
  return (
    <motion.button
      type="button"
      custom={index}
      variants={letterMotion}
      initial="hidden"
      whileInView="show"
      viewport={{ once: true, amount: 0.15 }}
      whileHover={disabled ? undefined : { y: -6, scale: 1.06 }}
      whileTap={disabled ? undefined : { scale: 0.92 }}
      className={`home-az-tile ${active ? 'is-active' : ''} ${disabled ? 'is-disabled' : ''}`}
      onClick={() => !disabled && onClick(letter)}
      aria-pressed={active}
      aria-label={`Browse animals starting with ${letter}`}
      disabled={disabled}
    >
      {letter.toLowerCase()}
    </motion.button>
  );
}

function AlphabetBrowsePanel({ nameLetters, browseFilter, onSelect }) {
  let tileIndex = 0;

  return (
    <section className="home-az-panel" aria-label="Browse animals from A to Z">
      <div className="home-az-deco" aria-hidden="true">
        <span className="home-az-orbit" />
        <img src="tiger.jpg" alt="" className="home-az-deco-img" />
      </div>

      <div className="home-az-content">
        <h2 className="home-az-title">Animals from A to Z</h2>
        <p className="home-az-subtitle">Tap a letter to explore species by common name</p>

        <div className="home-az-grid">
          {AZ_ROWS.map((row, rowIndex) => (
            <div
              key={`az-row-${rowIndex}`}
              className={`home-az-row ${rowIndex === 2 ? 'is-last' : ''}`}
            >
              {row.map((letter) => {
                const currentIndex = tileIndex;
                tileIndex += 1;
                return (
                  <AzLetterTile
                    key={letter}
                    letter={letter}
                    index={currentIndex}
                    active={browseFilter?.type === 'letter' && browseFilter.value === letter}
                    disabled={!nameLetters.has(letter)}
                    onClick={onSelect}
                  />
                );
              })}
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

function AnimalGrid({ animals, compact = false }) {
  if (!animals.length) {
    return <p className="home-empty">No animals match this browse path yet.</p>;
  }

  return (
    <div className={`home-animal-grid ${compact ? 'is-compact' : ''}`}>
      {animals.map((animal, index) => (
        <motion.div
          key={animal.id}
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: index * 0.04, duration: 0.35 }}
        >
          <Link to={`/animal-details/${animal.id}`} className="home-animal-link">
            <AnimalCard animal={animal} />
          </Link>
        </motion.div>
      ))}
    </div>
  );
}

function BrowseSection({ title, subtitle, children, id }) {
  return (
    <section className="home-browse-section" id={id}>
      <div className="home-section-head">
        <h2>{title}</h2>
        {subtitle && <p>{subtitle}</p>}
      </div>
      {children}
    </section>
  );
}

function HomePage() {
  const { keycloak, initialized } = useKeycloak();
  const [heroIndex, setHeroIndex] = useState(0);
  const [animals, setAnimals] = useState([]);
  const [featuredAnimals, setFeaturedAnimals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const [browseFilter, setBrowseFilter] = useState(null);
  const [biomeOptions, setBiomeOptions] = useState([]);
  const [climateOptions, setClimateOptions] = useState([]);
  const [facets, setFacets] = useState(null);
  const [page, setPage] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 24;

  useEffect(() => {
    const interval = setInterval(() => {
      setHeroIndex((prev) => (prev + 1) % HERO_IMAGES.length);
    }, HERO_SLIDE_MS);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => setDebouncedSearch(searchTerm), 300);
    return () => clearTimeout(timer);
  }, [searchTerm]);

  useEffect(() => {
    Promise.all([
      fetchAnimalFacets(),
      fetch('/api/v1/animals/getAnimalBiomes').then((r) => r.json()),
      fetch('/api/v1/animals/getAnimalClimateZones').then((r) => r.json()),
      fetchAnimalPage({ page: 0, size: 6, sort: 'name,asc' })
    ])
      .then(([facetData, biomes, climates, featuredPage]) => {
        setFacets(facetData || null);
        setBiomeOptions(Object.entries(biomes || {}).map(([id, label]) => ({ id, label })));
        setClimateOptions(Object.entries(climates || {}).map(([id, label]) => ({ id, label })));
        setFeaturedAnimals(Array.isArray(featuredPage?.content) ? featuredPage.content : []);
      })
      .catch((error) => console.error('Error loading browse catalog:', error));
  }, []);

  useEffect(() => {
    setPage(0);
  }, [debouncedSearch, browseFilter]);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    const filterOptions = browseFilterToSearchOptions(browseFilter);
    fetchAnimalPage({
      q: debouncedSearch,
      ...filterOptions,
      page,
      size: pageSize,
      sort: 'name,asc'
    })
      .then((result) => {
        if (cancelled) return;
        setAnimals(Array.isArray(result?.content) ? result.content : []);
        setTotalElements(result?.totalElements ?? 0);
        setTotalPages(result?.totalPages ?? 0);
      })
      .catch((error) => {
        if (!cancelled) console.error('Error loading animals page:', error);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => { cancelled = true; };
  }, [debouncedSearch, browseFilter, page]);

  const nameLetters = useMemo(() => {
    const keys = new Set((facets?.letters || []).map((b) => String(b.key || '').toUpperCase()));
    return keys;
  }, [facets]);

  const speciesList = useMemo(
    () => (facets?.species || []).map((b) => b.key).filter(Boolean),
    [facets]
  );
  const habitatList = useMemo(
    () => (facets?.habitats || []).map((b) => b.key).filter(Boolean),
    [facets]
  );

  const facetCount = (type, value) => {
    const list = facets?.[type] || [];
    const match = list.find((b) => String(b.key).toLowerCase() === String(value).toLowerCase());
    return match?.count ?? 0;
  };

  const setFilter = (type, value) => {
    setBrowseFilter((prev) => (
      prev?.type === type && prev?.value === value ? null : { type, value }
    ));
  };

  const clearFilters = () => {
    setSearchTerm('');
    setBrowseFilter(null);
  };

  const hasActiveBrowse = Boolean(searchTerm.trim() || browseFilter);
  const resultsTitle = searchTerm.trim()
    ? 'Search results'
    : browseFilter
      ? 'Browse results'
      : 'Catalog';
  const filteredAnimals = animals;

  return (
    <div className="home-page">
      <main className="home-hero">
        <HeroSlideshow images={HERO_IMAGES} activeIndex={heroIndex} />

        <div className="home-hero-content">
        <header className="header home-header">
          <div className="logo">
            <img src={logo} alt="animalia" />
          </div>
          <nav>
            <ul>
              <li><a href="/">Home</a></li>
              <li><a href="#browse">Browse</a></li>
              <li><a href="#featured">Featured</a></li>
              {initialized && hasAdminRole(keycloak) && (
                <li><Link to="/add-animal">Add Animal</Link></li>
              )}
              {initialized && !keycloak.authenticated && (
                <li>
                  <a href="#login" onClick={(e) => { e.preventDefault(); keycloak.login(); }}>
                    Sign in
                  </a>
                </li>
              )}
              {initialized && keycloak.authenticated && (
                <li>
                  <a href="#logout" onClick={(e) => { e.preventDefault(); keycloak.logout({ redirectUri: window.location.origin }); }}>
                    Sign out
                  </a>
                </li>
              )}
            </ul>
          </nav>
        </header>

        <div className="home-hero-copy">
          <motion.h1
            initial={{ opacity: 0, y: 24 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
          >
            Explore, Learn, and Gain Insight into the Animal Kingdom
          </motion.h1>
          <p>Search by name, species, habitat, biome, climate, or conservation status.</p>
        </div>

        <div className="home-search-wrap">
          <div className="home-search">
            <i className="fas fa-search" aria-hidden="true" />
            <input
              type="search"
              placeholder="Search name, species, habitat, biome, climate, status..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              aria-label="Search animals"
            />
            {(searchTerm || browseFilter) && (
              <button type="button" className="home-search-clear" onClick={clearFilters}>
                Clear
              </button>
            )}
          </div>
        </div>

        <HeroDots
          images={HERO_IMAGES}
          activeIndex={heroIndex}
          onSelect={setHeroIndex}
        />
        </div>
      </main>

      <div className="home-body" id="browse">
        {loading && <p className="home-loading">Loading the catalog…</p>}

        {!loading && hasActiveBrowse && (
          <BrowseSection
            title={resultsTitle}
            subtitle={`${totalElements} animal${totalElements === 1 ? '' : 's'} found`}
          >
            <AnimalGrid animals={filteredAnimals} compact />
            {totalPages > 1 && (
              <div className="home-pagination">
                <button
                  type="button"
                  className="btn ghost"
                  disabled={page <= 0}
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                >
                  Previous
                </button>
                <span>Page {page + 1} of {totalPages}</span>
                <button
                  type="button"
                  className="btn ghost"
                  disabled={page >= totalPages - 1}
                  onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                >
                  Next
                </button>
              </div>
            )}
          </BrowseSection>
        )}

        {!loading && !hasActiveBrowse && (
          <BrowseSection id="featured" title="Featured animals" subtitle="Six spotlight species from the archive">
            <AnimalGrid animals={featuredAnimals} compact />
          </BrowseSection>
        )}

        {!loading && (
          <>
            <AlphabetBrowsePanel
              nameLetters={nameLetters}
              browseFilter={browseFilter}
              onSelect={(value) => setFilter('letter', value)}
            />

            <BrowseSection title="Browse by species" subtitle="Species recorded in the Animalia catalog">
              {speciesList.length === 0 ? (
                <p className="home-empty">Species tags will appear as animals are added to the archive.</p>
              ) : (
                <div className="home-species-grid">
                  {speciesList.map((species) => {
                    const count = facetCount('species', species);
                    return (
                      <motion.button
                        type="button"
                        key={species}
                        className={`home-species-card ${browseFilter?.type === 'species' && browseFilter.value === species ? 'is-active' : ''}`}
                        onClick={() => setFilter('species', species)}
                        whileHover={{ y: -3 }}
                      >
                        <strong>{species}</strong>
                        <em>{count} animal{count === 1 ? '' : 's'}</em>
                      </motion.button>
                    );
                  })}
                </div>
              )}
            </BrowseSection>

            <BrowseSection title="Browse by biome" subtitle="Habitat types where species live">
              <div className="home-scene-grid">
                {biomeOptions.map((option) => {
                  const count = facetCount('biomes', option.id);
                  return (
                    <motion.button
                      type="button"
                      key={option.id}
                      className={`home-scene-card ${browseFilter?.type === 'biome' && browseFilter.value === option.id ? 'is-active' : ''}`}
                      style={{ backgroundImage: `url(${sceneImageFor(option.label)})` }}
                      onClick={() => setFilter('biome', option.id)}
                      whileHover={{ y: -4 }}
                      disabled={count === 0}
                    >
                      <span className="home-scene-overlay" />
                      <strong>{option.label}</strong>
                      <em>{count} species</em>
                    </motion.button>
                  );
                })}
              </div>
            </BrowseSection>

            <BrowseSection title="Browse by climate zone" subtitle="Temperature bands across the wild map">
              <div className="home-scene-grid">
                {climateOptions.map((option) => {
                  const count = facetCount('climates', option.id);
                  return (
                    <motion.button
                      type="button"
                      key={option.id}
                      className={`home-scene-card home-scene-climate ${browseFilter?.type === 'climate' && browseFilter.value === option.id ? 'is-active' : ''}`}
                      style={{ backgroundImage: `url(${sceneImageFor(option.label)})` }}
                      onClick={() => setFilter('climate', option.id)}
                      whileHover={{ y: -4 }}
                      disabled={count === 0}
                    >
                      <span className="home-scene-overlay" />
                      <strong>{option.label}</strong>
                      <em>{count} species</em>
                    </motion.button>
                  );
                })}
              </div>
            </BrowseSection>

            <BrowseSection title="Browse by population status" subtitle="IUCN Red List conservation categories">
              <div className="home-iucn-row">
                {IUCN_ORDER.map((code) => {
                  const meta = IUCN_META[code];
                  const count = facetCount('statuses', code);
                  return (
                    <motion.button
                      type="button"
                      key={code}
                      className={`home-iucn-item tone-${meta.tone} ${browseFilter?.type === 'status' && browseFilter.value === code ? 'is-active' : ''}`}
                      onClick={() => setFilter('status', code)}
                      whileHover={{ y: -3 }}
                      disabled={count === 0}
                      title={meta.label}
                    >
                      <span className="home-iucn-ring">{code}</span>
                      <span className="home-iucn-caption">{meta.label.split(' ')[0]}</span>
                      <em>{count}</em>
                    </motion.button>
                  );
                })}
              </div>
            </BrowseSection>

            <BrowseSection title="Browse by habitat" subtitle="Ecological homes described in each profile">
              {habitatList.length === 0 ? (
                <p className="home-empty">Habitat tags will appear as more species are catalogued.</p>
              ) : (
                <div className="home-chip-row">
                  {habitatList.map((habitat) => (
                    <button
                      type="button"
                      key={habitat}
                      className={`home-chip home-chip-habitat ${browseFilter?.type === 'habitat' && browseFilter.value === habitat ? 'is-active' : ''}`}
                      onClick={() => setFilter('habitat', habitat)}
                    >
                      {habitat}
                    </button>
                  ))}
                </div>
              )}
            </BrowseSection>
          </>
        )}
      </div>
    </div>
  );
}

export default HomePage;
