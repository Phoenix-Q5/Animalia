import React, { useEffect, useMemo, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { motion, useReducedMotion } from 'framer-motion';
import { useKeycloak } from '@react-keycloak/web';
import AnimalCard, { imageSrc } from './AnimalCard';
import { hasAdminRole } from './keycloak';

const IUCN_ORDER = ['NE', 'DD', 'LC', 'NT', 'VU', 'EN', 'CR', 'EW', 'EX'];

const SCENE_IMAGES = {
  forest: 'https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=900&q=70',
  rainforest: 'https://images.unsplash.com/photo-1511497584788-876760111969?auto=format&fit=crop&w=900&q=70',
  'montane forest': 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=900&q=70',
  riparian: 'https://images.unsplash.com/photo-1439066615861-d1af74d74000?auto=format&fit=crop&w=900&q=70',
  shrubland: 'https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=900&q=70',
  grassland: 'https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=900&q=70',
  swamp: 'https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?auto=format&fit=crop&w=900&q=70',
  savanna: 'https://images.unsplash.com/photo-1516426122078-c23e76319801?auto=format&fit=crop&w=900&q=70',
  woodland: 'https://images.unsplash.com/photo-1502082553048-f009c37129b9?auto=format&fit=crop&w=900&q=70',
  tropical: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=70',
  temperate: 'https://images.unsplash.com/photo-1508193638397-1c4234db14d1?auto=format&fit=crop&w=900&q=70',
  cold: 'https://images.unsplash.com/photo-1483664852095-d6cc46771f8f?auto=format&fit=crop&w=900&q=70',
  polar: 'https://images.unsplash.com/photo-1517783999520-f068d7431a60?auto=format&fit=crop&w=900&q=70',
};

function sceneImageFor(label) {
  const key = String(label || '').trim().toLowerCase();
  return SCENE_IMAGES[key] || SCENE_IMAGES.forest;
}

const IUCN_META = {
  EX: { label: 'Extinct', tone: 'dark' },
  EW: { label: 'Extinct in the Wild', tone: 'dark' },
  CR: { label: 'Critically Endangered', tone: 'critical' },
  EN: { label: 'Endangered', tone: 'danger' },
  VU: { label: 'Vulnerable', tone: 'warn' },
  NT: { label: 'Near Threatened', tone: 'caution' },
  CD: { label: 'Conservation Dependent', tone: 'muted' },
  LC: { label: 'Least Concern', tone: 'safe' },
  DD: { label: 'Data Deficient', tone: 'muted' },
  NE: { label: 'Not Evaluated', tone: 'muted' },
};

const fadeUp = {
  hidden: { opacity: 0, y: 28 },
  show: { opacity: 1, y: 0, transition: { duration: 0.55, ease: [0.22, 1, 0.36, 1] } },
};

const stagger = {
  hidden: {},
  show: { transition: { staggerChildren: 0.07, delayChildren: 0.08 } },
};

function primaryImage(animal) {
  if (!animal) return null;
  if (animal.animalImage) return animal.animalImage;
  if (animal.animalImages?.length) return animal.animalImages[0];
  if (animal.galleryImages?.length) return animal.galleryImages[0];
  return null;
}

function locationsOf(distribution, type) {
  if (!distribution) return [];
  const byGetter = {
    CONTINENT: distribution.continents,
    SUBCONTINENT: distribution.subcontinents,
    COUNTRY: distribution.countries,
    BIOGEOGRAPHICAL_REALM: distribution.biogeographicalRealms,
    WWF_BIOME: distribution.wwfBiomes,
  };
  if (byGetter[type]?.length) return byGetter[type];
  return (distribution.locations || [])
    .filter((l) => l.locationType === type)
    .map((l) => l.value);
}

function ExpandableList({ items, limit = 6 }) {
  const [open, setOpen] = useState(false);
  if (!items?.length) return <span className="ad-muted">—</span>;
  const shown = open ? items : items.slice(0, limit);
  const hidden = items.length - shown.length;
  return (
    <span className="ad-value-list">
      {shown.join(', ')}
      {hidden > 0 && !open && (
        <button type="button" className="ad-show-more" onClick={() => setOpen(true)}>
          Show more (+{hidden})
        </button>
      )}
      {open && items.length > limit && (
        <button type="button" className="ad-show-more" onClick={() => setOpen(false)}>
          Show less
        </button>
      )}
    </span>
  );
}

function TaxonomyRow({ label, value, emphasize }) {
  if (!value) return null;
  return (
    <div className={`ad-taxo-row ${emphasize ? 'is-species' : ''}`}>
      <span className="ad-taxo-label">{label}</span>
      <span className="ad-taxo-value">{value}</span>
    </div>
  );
}

function StatTile({ label, value, unit, index }) {
  if (value == null || value === '') return null;
  return (
    <motion.div className="ad-stat" variants={fadeUp} custom={index}>
      <span className="ad-stat-label">{label}</span>
      <span className="ad-stat-value">
        {value}
        {unit ? <small>{unit}</small> : null}
      </span>
    </motion.div>
  );
}

function IucnStrip({ active }) {
  const code = (active || '').toUpperCase();
  return (
    <div className="ad-iucn" role="list" aria-label="IUCN conservation status">
      {IUCN_ORDER.map((item) => {
        const meta = IUCN_META[item];
        const isActive = item === code;
        return (
          <motion.div
            key={item}
            role="listitem"
            className={`ad-iucn-item tone-${meta.tone} ${isActive ? 'is-active' : ''}`}
            title={meta.label}
            whileHover={{ y: -3, scale: 1.06 }}
            animate={isActive ? { scale: [1, 1.12, 1] } : { scale: 1 }}
            transition={isActive ? { duration: 0.8, ease: 'easeOut' } : { duration: 0.2 }}
          >
            <span className="ad-iucn-ring">{item}</span>
            <span className="ad-iucn-caption">{meta.label.split(' ')[0]}</span>
          </motion.div>
        );
      })}
    </div>
  );
}

function SceneCard({ label, index, kind }) {
  const reduce = useReducedMotion();
  return (
    <motion.article
      className={`ad-scene-card ad-scene-${kind}`}
      style={{ backgroundImage: `url(${sceneImageFor(label)})` }}
      initial={reduce ? false : { opacity: 0, y: 18 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, amount: 0.3 }}
      transition={{ delay: index * 0.05, duration: 0.45 }}
      whileHover={reduce ? undefined : { y: -4 }}
    >
      <span className="ad-scene-overlay" />
      <strong>{label}</strong>
    </motion.article>
  );
}

function SceneGrid({ title, items, kind }) {
  if (!items?.length) return null;
  return (
    <div className={`ad-scene-block ad-scene-block-${kind}`}>
      <h3>{title}</h3>
      <div className="ad-scene-grid">
        {items.map((item, index) => (
          <SceneCard key={`${title}-${item}`} label={item} index={index} kind={kind} />
        ))}
      </div>
    </div>
  );
}

function Reveal({ children, className = '' }) {
  const reduce = useReducedMotion();
  return (
    <motion.section
      className={className}
      variants={reduce ? undefined : fadeUp}
      initial={reduce ? false : 'hidden'}
      whileInView={reduce ? undefined : 'show'}
      viewport={{ once: true, amount: 0.2 }}
    >
      {children}
    </motion.section>
  );
}

function AnimalDetails({ animal: animalProp, onBack }) {
  const { id } = useParams();
  const { keycloak, initialized } = useKeycloak();
  const [animal, setAnimal] = useState(animalProp || null);
  const [loading, setLoading] = useState(!animalProp);
  const [error, setError] = useState(null);
  const [activeGallery, setActiveGallery] = useState(0);
  const reduce = useReducedMotion();

  useEffect(() => {
    if (animalProp) {
      setAnimal(animalProp);
      setLoading(false);
      return;
    }
    if (!id) return;
    setLoading(true);
    fetch(`/api/v1/animals/getById/${id}`)
      .then((response) => {
        if (!response.ok) throw new Error('Failed to load animal');
        return response.json();
      })
      .then((data) => {
        setAnimal(data);
        setError(null);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [id, animalProp]);

  useEffect(() => {
    setActiveGallery(0);
  }, [animal?.id]);

  const hero = useMemo(() => primaryImage(animal), [animal]);
  const info = animal?.animalInfo || {};
  const extended = animal?.animalExtendedInfo || {};
  const distribution = info.distribution || {};
  const population = info.population || {};
  const appearance = info.appearance || {};
  const gallery = useMemo(() => {
    if (!animal) return [];
    if (animal.galleryImages?.length) return animal.galleryImages;
    return animal.animalImages || [];
  }, [animal]);

  const continents = locationsOf(distribution, 'CONTINENT');
  const subcontinents = locationsOf(distribution, 'SUBCONTINENT');
  const countries = locationsOf(distribution, 'COUNTRY');
  const realms = locationsOf(distribution, 'BIOGEOGRAPHICAL_REALM');
  const wwfBiomes = locationsOf(distribution, 'WWF_BIOME');
  const biomeLabels = (info.biome?.biomes || []).map((b) => b.description || b);
  const climateLabels = (info.climateZones?.zones || []).map((z) => z.description || z);
  const statusCode = (() => {
    const raw = population.conservationStatus;
    if (!raw) return '';
    if (typeof raw === 'string') return raw.toUpperCase();
    return (raw.id || raw.name || '').toUpperCase();
  })();
  const statusMeta = IUCN_META[statusCode];

  if (loading) {
    return (
      <div className="ad-page">
        <div className="ad-loading">
          <span className="ad-loading-orb" />
          <p>Gathering field notes…</p>
        </div>
      </div>
    );
  }
  if (error) {
    return <div className="ad-page"><p className="ad-error">{error}</p></div>;
  }
  if (!animal) {
    return <div className="ad-page"><p className="ad-error">Animal not found.</p></div>;
  }

  const heroSrc = gallery.length
    ? (imageSrc(gallery[Math.min(activeGallery, gallery.length - 1)]) || imageSrc(hero))
    : imageSrc(hero);

  return (
    <div className="ad-page">
      <div className="ad-atmosphere" aria-hidden="true" />

      <motion.nav
        className="ad-breadcrumb"
        initial={reduce ? false : { opacity: 0, y: -8 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <Link to="/">Home</Link>
        <span>/</span>
        <Link to="/">Animals</Link>
        {animal.animalClass && (
          <>
            <span>/</span>
            <span>{animal.animalClass}</span>
          </>
        )}
        <span>/</span>
        <strong>{animal.name}</strong>
      </motion.nav>

      {(onBack || (initialized && hasAdminRole(keycloak) && animal.id)) && (
        <div className="ad-toolbar">
          {onBack && (
            <button type="button" className="ad-back" onClick={onBack}>
              ← Back to explore
            </button>
          )}
          {initialized && hasAdminRole(keycloak) && animal.id && (
            <Link to={`/edit-animal/${animal.id}`} className="ad-edit">
              Edit animal
            </Link>
          )}
        </div>
      )}

      {/* Hero: portrait + taxonomy + vitals */}
      <motion.header
        className="ad-hero"
        variants={stagger}
        initial={reduce ? false : 'hidden'}
        animate="show"
      >
        <motion.div className="ad-hero-media" variants={fadeUp}>
          <div className="ad-hero-frame">
            {heroSrc ? (
              <motion.img
                key={heroSrc}
                src={heroSrc}
                alt={animal.name}
                initial={reduce ? false : { scale: 1.08, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                transition={{ duration: 0.8, ease: [0.22, 1, 0.36, 1] }}
              />
            ) : (
              <div className="ad-hero-empty">No portrait yet</div>
            )}
            <div className="ad-hero-glow" aria-hidden="true" />
          </div>
          {gallery.length > 1 && (
            <div className="ad-thumb-rail">
              {gallery.slice(0, 6).map((img, idx) => (
                <button
                  type="button"
                  key={img.id || img.name || idx}
                  className={`ad-thumb ${idx === activeGallery ? 'is-active' : ''}`}
                  onClick={() => setActiveGallery(idx)}
                >
                  <img src={imageSrc(img)} alt="" />
                </button>
              ))}
            </div>
          )}
        </motion.div>

        <motion.div className="ad-hero-panel" variants={fadeUp}>
          <p className="ad-kicker">Field profile</p>
          <h1 className="ad-title">{animal.name}</h1>
          {animal.scientificName && <p className="ad-latin">{animal.scientificName}</p>}
          {animal.description && <p className="ad-lede">{animal.description}</p>}

          <div className="ad-taxo">
            <TaxonomyRow label="Kingdom" value={animal.kingdom} />
            <TaxonomyRow label="Phylum" value={animal.phylum} />
            <TaxonomyRow label="Subphylum" value={animal.subphylum} />
            <TaxonomyRow label="Class" value={animal.animalClass} />
            <TaxonomyRow label="Order" value={animal.order} />
            <TaxonomyRow label="Suborder" value={animal.suborder} />
            <TaxonomyRow label="Family" value={animal.family} />
            <TaxonomyRow label="Genus" value={animal.genus} />
            <TaxonomyRow label="Species" value={animal.species || animal.scientificName} emphasize />
          </div>

          <motion.div className="ad-stats" variants={stagger}>
            <StatTile label="Population size" value={extended.populationSize} />
            <StatTile label="Life span" value={extended.lifeSpan} />
            <StatTile label="Top speed" value={extended.topSpeed} />
            <StatTile label="Weight" value={extended.weight != null ? String(extended.weight) : null} unit="kg" />
            <StatTile label="Length" value={extended.length} />
            <StatTile label="Wingspan" value={extended.wingSpan} />
          </motion.div>
        </motion.div>
      </motion.header>

      {/* Distribution */}
      <Reveal className="ad-section ad-distribution">
        <div className="ad-section-head">
          <h2>Distribution</h2>
          <p>Where this species lives across the wild map.</p>
        </div>
        <div className="ad-distribution-grid">
          <div className="ad-geo">
            <h3>Geography</h3>
            <div className="ad-geo-row"><span>Continents</span><ExpandableList items={continents} /></div>
            <div className="ad-geo-row"><span>Subcontinents</span><ExpandableList items={subcontinents} /></div>
            <div className="ad-geo-row"><span>Countries</span><ExpandableList items={countries} limit={5} /></div>
            <div className="ad-geo-row"><span>Biogeographical realms</span><ExpandableList items={realms} /></div>
            <div className="ad-geo-row"><span>WWF biomes</span><ExpandableList items={wwfBiomes} limit={4} /></div>
          </div>
          <motion.div
            className="ad-map"
            initial={reduce ? false : { opacity: 0, x: 24 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true, amount: 0.3 }}
            transition={{ duration: 0.6 }}
          >
            {distribution.distributionMap && imageSrc(distribution.distributionMap) ? (
              <img src={imageSrc(distribution.distributionMap)} alt={`${animal.name} range map`} />
            ) : (
              <div className="ad-map-placeholder">
                <span className="ad-map-pulse" />
                <strong>Range map</strong>
                <em>Upload a distribution map to pin this species on the globe.</em>
              </div>
            )}
          </motion.div>
        </div>
        {distribution.description && <p className="ad-section-copy">{distribution.description}</p>}
      </Reveal>

      {/* Habitat scenes */}
      {(biomeLabels.length > 0 || climateLabels.length > 0) && (
        <Reveal className="ad-section ad-scenes">
          <div className="ad-section-head">
            <h2>Habitat scenes</h2>
            <p>Biomes and climate bands this species calls home.</p>
          </div>
          <div className="ad-scenes-split">
            <SceneGrid title="Biome" items={biomeLabels} kind="biome" />
            <SceneGrid title="Climate zones" items={climateLabels} kind="climate" />
          </div>
        </Reveal>
      )}

      {/* Appearance — dedicated card for copy + gallery images */}
      {(appearance.description || appearance.images?.length > 0) && (
        <Reveal className="ad-section">
          <div className="ad-section-head">
            <h2>Appearance</h2>
            <p>What to look for in the field.</p>
          </div>
          <article className="ad-appearance-card">
            {appearance.description && <p className="ad-appearance-copy">{appearance.description}</p>}
            {appearance.images?.length > 0 && (
              <div className="ad-appearance-gallery">
                {appearance.images.map((img, idx) => (
                  imageSrc(img) && (
                    <motion.figure
                      key={img.id || img.name || idx}
                      className="ad-appearance-shot"
                      initial={reduce ? false : { opacity: 0, scale: 0.96 }}
                      whileInView={{ opacity: 1, scale: 1 }}
                      viewport={{ once: true }}
                      transition={{ delay: idx * 0.05 }}
                    >
                      <img src={imageSrc(img)} alt={img.name || `${animal.name} appearance`} />
                    </motion.figure>
                  )
                ))}
              </div>
            )}
            {(!appearance.images || appearance.images.length === 0) && (
              <div className="ad-appearance-empty">
                Appearance photos can be attached here as the gallery grows.
              </div>
            )}
          </article>
        </Reveal>
      )}

      {/* Ecology */}
      <Reveal className="ad-section">
        <div className="ad-section-head">
          <h2>Ecology & lifestyle</h2>
          <p>How this animal moves through its world.</p>
        </div>
        <div className="ad-ecology-grid">
          <div className="ad-ecology-blocks">
            {(info.habitsLifestyle || info.habits?.description) && (
              <article><h3>Habits & lifestyle</h3><p>{info.habitsLifestyle || info.habits?.description}</p></article>
            )}
            {info.diet && <article><h3>Diet</h3><p>{info.diet}</p></article>}
            {info.habitat && <article><h3>Habitat</h3><p>{info.habitat}</p></article>}
            {info.behavior && <article><h3>Behavior</h3><p>{info.behavior}</p></article>}
          </div>
        </div>
      </Reveal>

      {/* Population */}
      <Reveal className="ad-section ad-population">
        <div className="ad-section-head">
          <h2>Population</h2>
          <p>Numbers, pressure, and the role this species plays.</p>
        </div>
        <div className="ad-population-grid">
          <div className="ad-population-visual">
            {heroSrc && <img src={heroSrc} alt="" className="ad-population-photo" />}
            <div className="ad-population-meta">
              <div>
                <span>Population trend</span>
                <strong className="ad-accent">{population.populationTrend || 'Unknown'}</strong>
              </div>
              <div>
                <span>Population status</span>
                <strong className="ad-accent">
                  {statusMeta ? `${statusMeta.label} (${statusCode})` : (population.populationStatus || '—')}
                </strong>
              </div>
            </div>
            <IucnStrip active={statusCode} />
          </div>
          <div className="ad-population-copy">
            {population.populationThreats && (
              <article>
                <h3>Population threats</h3>
                <p>{population.populationThreats}</p>
              </article>
            )}
            {(population.populationNumber || extended.populationSize) && (
              <article>
                <h3>Population number</h3>
                <p>{population.populationNumber || extended.populationSize}</p>
              </article>
            )}
            {population.ecologicalNiche && (
              <article>
                <h3>Ecological niche</h3>
                <p>{population.ecologicalNiche}</p>
              </article>
            )}
          </div>
        </div>
      </Reveal>

      {/* Subspecies + references */}
      {animal.subspecies?.length > 0 && (
        <Reveal className="ad-section">
          <div className="ad-section-head"><h2>Subspecies</h2></div>
          <div className="ad-subspecies">
            {animal.subspecies.map((sub, i) => (
              <motion.article
                key={sub.id || sub.name}
                className="ad-subspecies-item"
                initial={reduce ? false : { opacity: 0, y: 16 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.06 }}
              >
                <h3>{sub.name}</h3>
                {sub.scientificName && <p className="ad-latin">{sub.scientificName}</p>}
                {sub.description && <p>{sub.description}</p>}
              </motion.article>
            ))}
          </div>
        </Reveal>
      )}

      {animal.references?.length > 0 && (
        <Reveal className="ad-section">
          <div className="ad-section-head"><h2>References</h2></div>
          <ol className="ad-references">
            {animal.references.map((ref) => (
              <li key={ref.id || ref.title}>
                {ref.url ? <a href={ref.url} target="_blank" rel="noreferrer">{ref.title || ref.url}</a> : ref.title}
                {ref.citation && <p>{ref.citation}</p>}
              </li>
            ))}
          </ol>
        </Reveal>
      )}

      {animal.relatedAnimals?.length > 0 && (
        <Reveal className="ad-section ad-related">
          <div className="ad-section-head">
            <h2>Related animals</h2>
            <p>Nearby branches on the family tree.</p>
          </div>
          <div className="ad-related-grid">
            {animal.relatedAnimals.map((related, i) => (
              <motion.div
                key={related.id}
                initial={reduce ? false : { opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.05 }}
                whileHover={{ y: -6 }}
              >
                <Link
                  to={`/animal-details/${related.id}`}
                  className="ad-related-link"
                  onClick={() => window.scrollTo(0, 0)}
                >
                  <AnimalCard animal={related} />
                </Link>
              </motion.div>
            ))}
          </div>
        </Reveal>
      )}
    </div>
  );
}

export default AnimalDetails;
