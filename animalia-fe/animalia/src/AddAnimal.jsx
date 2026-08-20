import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { AnimatePresence, motion } from 'framer-motion';
import { useNavigate, useParams } from 'react-router-dom';
import { useKeycloak } from '@react-keycloak/web';
import logo from './assets/animalia-logo-full.png';
import { imageSrc } from './AnimalCard';

const EMPTY_ANIMAL = {
  phylum: '',
  family: '',
  name: '',
  scientificName: '',
  description: '',
  genus: '',
  species: '',
  order: '',
  kingdom: '',
  subphylum: '',
  animalClass: '',
  suborder: ''
};

const EMPTY_EXTENDED = {
  populationSize: '',
  lifeSpan: '',
  topSpeed: '',
  weight: 0,
  length: '',
  wingSpan: '',
  animalTags: [],
  population: {
    populationThreats: '',
    populationNumber: '',
    populationTrend: '',
    populationConservationStatus: ''
  },
  geography: {
    continents: [],
    countries: [],
    regions: [],
    biomes: []
  }
};

const EMPTY_ANIMAL_INFO = {
  distribution: {
    distribution: '',
    geography: {
      continents: [],
      countries: [],
      regions: [],
      biomes: []
    },
    distributionMapFile: null
  },
  appearance: {
    description: '',
    images: []
  },
  habits: {
    description: '',
    groupName: [],
    lifestyle: [],
    seasonality: ''
  },
  biome: {
    biomes: []
  },
  climateZones: {
    zones: []
  },
  diet: '',
  habitat: '',
  behavior: '',
  habitsLifestyle: ''
};

function asEnumIds(list) {
  if (!Array.isArray(list)) {
    return [];
  }
  return list
    .map((item) => {
      if (!item) return '';
      if (typeof item === 'string') return item;
      return item.id || item.name || '';
    })
    .filter(Boolean);
}

function enumId(value) {
  if (!value) return '';
  if (typeof value === 'string') return value;
  return value.id || value.name || '';
}

const IUCN_ORDER = ['NE', 'DD', 'LC', 'NT', 'VU', 'EN', 'CR', 'EW', 'EX'];

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
  NE: { label: 'Not Evaluated', tone: 'muted' }
};

const POPULATION_TREND_OPTIONS = [
  { id: 'INCREASING', label: 'Increasing' },
  { id: 'DECREASING', label: 'Decreasing' },
  { id: 'STABLE', label: 'Stable' },
  { id: 'UNKNOWN', label: 'Unknown' }
];

function AnimalForm() {
    const { keycloak } = useKeycloak();
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = Boolean(id);
    const [activeTab, setActiveTab] = useState('basic');
    const [loadingAnimal, setLoadingAnimal] = useState(Boolean(id));
    const [loadError, setLoadError] = useState('');
    const [saving, setSaving] = useState(false);
  const [animal, setAnimal] = useState(EMPTY_ANIMAL);

  const [animalExtendedInfo, setAnimalExtendedInfo] = useState(EMPTY_EXTENDED);

  const [animalInfo, setAnimalInfo] = useState(EMPTY_ANIMAL_INFO);
  const [biomeOptions, setBiomeOptions] = useState([]);
  const [climateOptions, setClimateOptions] = useState([]);

  const [animalImages, setAnimalImages] = useState([]);
  const [animalImageUrls, setAnimalImageUrls] = useState(['']);
  const [existingImages, setExistingImages] = useState([]);
  const [existingMapUrl, setExistingMapUrl] = useState('');


  const [bulkFile, setBulkFile] = useState(null);
  const [bulkResult, setBulkResult] = useState(null);
  const [bulkBusy, setBulkBusy] = useState(false);

  const api = axios.create({
    baseURL: 'http://localhost:8080'
  });

  useEffect(() => {
    Promise.all([
      api.get('/api/v1/animals/getAnimalBiomes'),
      api.get('/api/v1/animals/getAnimalClimateZones')
    ])
      .then(([biomesRes, climateRes]) => {
        setBiomeOptions(Object.entries(biomesRes.data || {}).map(([id, label]) => ({ id, label })));
        setClimateOptions(Object.entries(climateRes.data || {}).map(([id, label]) => ({ id, label })));
      })
      .catch((error) => console.error('Error loading biome/climate options:', error));
  }, []);

  useEffect(() => {
    if (!id) {
      setLoadingAnimal(false);
      setLoadError('');
      return;
    }
    setLoadingAnimal(true);
    setLoadError('');
    api.get(`/api/v1/animals/getById/${id}`)
      .then((response) => {
        const data = response.data;
        if (!data || !data.id) {
          setLoadError('Animal not found.');
          return;
        }
        const info = data.animalInfo || {};
        const extended = data.animalExtendedInfo || {};
        const population = info.population || {};
        const gallery = data.animalImages || [];
        const urls = gallery.map((image) => image.url).filter(Boolean);

        setAnimal({
          ...EMPTY_ANIMAL,
          id: data.id,
          phylum: data.phylum || '',
          family: data.family || '',
          name: data.name || '',
          scientificName: data.scientificName || '',
          description: data.description || '',
          genus: data.genus || '',
          species: data.species || '',
          order: data.order || '',
          kingdom: data.kingdom || '',
          subphylum: data.subphylum || '',
          animalClass: data.animalClass || '',
          suborder: data.suborder || ''
        });
        setAnimalExtendedInfo({
          ...EMPTY_EXTENDED,
          populationSize: extended.populationSize || '',
          lifeSpan: extended.lifeSpan || '',
          topSpeed: extended.topSpeed || '',
          weight: extended.weight ?? 0,
          length: extended.length || '',
          wingSpan: extended.wingSpan || '',
          animalTags: extended.animalTags || [],
          population: {
            populationThreats: population.populationThreats || '',
            populationNumber: population.populationNumber || '',
            populationTrend: enumId(population.populationTrend),
            populationConservationStatus: enumId(population.conservationStatus)
          }
        });
        setAnimalInfo({
          ...EMPTY_ANIMAL_INFO,
          distribution: {
            distribution: info.distribution?.description || info.distribution?.distribution || '',
            geography: {
              continents: [],
              countries: [],
              regions: [],
              biomes: []
            },
            distributionMapFile: null
          },
          appearance: {
            description: info.appearance?.description || '',
            images: []
          },
          habits: {
            description: info.habits?.description || '',
            groupName: [],
            lifestyle: [],
            seasonality: ''
          },
          biome: { biomes: asEnumIds(info.biome?.biomes) },
          climateZones: { zones: asEnumIds(info.climateZones?.zones) },
          diet: info.diet || '',
          habitat: info.habitat || '',
          behavior: info.behavior || '',
          habitsLifestyle: info.habitsLifestyle || ''
        });
        setExistingImages(gallery);
        setAnimalImageUrls(urls.length ? urls : ['']);
        setExistingMapUrl(imageSrc(info.distribution?.distributionMap) || '');
        setAnimalImages([]);
      })
      .catch((error) => {
        console.error('Error loading animal:', error);
        setLoadError('Could not load this animal for editing.');
      })
      .finally(() => setLoadingAnimal(false));
  }, [id]);

  const toggleSelection = (list, id) => (
    list.includes(id) ? list.filter((item) => item !== id) : [...list, id]
  );

  const handleBiomeToggle = (id) => {
    setAnimalInfo((prev) => ({
      ...prev,
      biome: {
        ...prev.biome,
        biomes: toggleSelection(prev.biome?.biomes || [], id)
      }
    }));
  };

  const handleClimateToggle = (id) => {
    setAnimalInfo((prev) => ({
      ...prev,
      climateZones: {
        ...prev.climateZones,
        zones: toggleSelection(prev.climateZones?.zones || [], id)
      }
    }));
  };

  const handleAnimalChange = e => {
    const { name, value } = e.target;
    setAnimal(prev => ({ ...prev, [name]: value }));
  };

  const handleExtendedInfoChange = e => {
    const { name, value } = e.target;
    setAnimalExtendedInfo(prev => ({ ...prev, [name]: value }));
  };

  const handlePopulationChange = e => {
    const { name, value } = e.target;
    setAnimalExtendedInfo(prev => ({
      ...prev,
      population: {
        ...prev.population,
        [name]: value
      }
    }));
  };

  const handleConservationStatusSelect = (code) => {
    setAnimalExtendedInfo(prev => ({
      ...prev,
      population: {
        ...prev.population,
        populationConservationStatus:
          prev.population.populationConservationStatus === code ? '' : code
      }
    }));
  };

  const handlePopulationTrendSelect = (trendId) => {
    setAnimalExtendedInfo(prev => ({
      ...prev,
      population: {
        ...prev.population,
        populationTrend: prev.population.populationTrend === trendId ? '' : trendId
      }
    }));
  };

  const handleExtendedGeographyChange = (field, newVal) => {
    // field is "continents", "countries", ...
    setAnimalExtendedInfo(prev => ({
      ...prev,
      geography: {
        ...prev.geography,
        [field]: newVal
      }
    }));
  };

  // ─────────────────────────────────────────────────────────────────────────────
  // Handlers for AnimalInfo
  // ─────────────────────────────────────────────────────────────────────────────
  const handleAnimalInfoChange = (e, section) => {
    const { name, value } = e.target;
    setAnimalInfo(prev => ({
      ...prev,
      [section]: {
        ...prev[section],
        [name]: value
      }
    }));
  };

  const handleDistributionGeographyChange = (field, newVal) => {
    setAnimalInfo(prev => ({
      ...prev,
      distribution: {
        ...prev.distribution,
        geography: {
          ...prev.distribution.geography,
          [field]: newVal
        }
      }
    }));
  };

  // For sets (groupName, lifestyle), you might parse comma-separated strings or use checkboxes
  const handleHabitsSetChange = (field, newVal) => {
    // newVal might be something like ['groupA','groupB']
    setAnimalInfo(prev => ({
      ...prev,
      habits: {
        ...prev.habits,
        [field]: newVal
      }
    }));
  };

  // ─────────────────────────────────────────────────────────────────────────────
  // Handlers for file inputs
  // ─────────────────────────────────────────────────────────────────────────────
  const handleAnimalImagesChange = e => {
    const files = Array.from(e.target.files);
    setAnimalImages(prev => [...prev, ...files]);
  };

  const handleAnimalImageUrlChange = (index, value) => {
    setAnimalImageUrls(prev => {
      const next = [...prev];
      next[index] = value;
      return next;
    });
  };

  const addAnimalImageUrlField = () => {
    setAnimalImageUrls(prev => [...prev, '']);
  };

  const removeAnimalImageUrlField = (index) => {
    setAnimalImageUrls(prev => prev.filter((_, i) => i !== index));
  };

  const handleDistributionMapChange = e => {
    const file = e.target.files[0];
    setAnimalInfo(prev => ({
      ...prev,
      distribution: {
        ...prev.distribution,
        distributionMapFile: file
      }
    }));
  };

  const handleAppearanceImagesChange = e => {
    const files = Array.from(e.target.files);
    setAnimalInfo(prev => ({
      ...prev,
      appearance: {
        ...prev.appearance,
        images: [...prev.appearance.images, ...files]
      }
    }));
  };

  // ─────────────────────────────────────────────────────────────────────────────
  // Submit
  // ─────────────────────────────────────────────────────────────────────────────
  const handleSubmit = e => {
    e.preventDefault();

    // Combine everything into a single "Animal" object (the same shape as your Java model)
    // The model has: AnimalExtendedInfo, and also AnimalInfo, so we can do:
    const imageUrlEntries = animalImageUrls
      .map(url => (url || '').trim())
      .filter(Boolean)
      .map(url => ({ url, name: url.split('/').pop() || 'cdn-image', type: 'image/jpeg' }));

    const {
      distributionMapFile,
      appearance,
      ...animalInfoRest
    } = animalInfo;

    const combinedAnimal = {
      ...animal,
      id: isEdit ? id : animal.id,
      animalExtendedInfo: { ...animalExtendedInfo },
      animalInfo: {
        ...animalInfoRest,
        distribution: {
          description: animalInfo.distribution?.distribution || '',
          locations: []
        },
        appearance: {
          description: appearance?.description || '',
          images: []
        },
        biome: {
          biomes: animalInfo.biome?.biomes || []
        },
        climateZones: {
          zones: animalInfo.climateZones?.zones || []
        },
        population: {
          populationThreats: animalExtendedInfo.population?.populationThreats || '',
          populationNumber: animalExtendedInfo.population?.populationNumber || '',
          populationTrend: animalExtendedInfo.population?.populationTrend || null,
          conservationStatus: animalExtendedInfo.population?.populationConservationStatus || null
        }
      },
      animalImages: imageUrlEntries,
    };

    // Build FormData
    const formData = new FormData();
    // The JSON portion of the Animal
    formData.append(
      'animal',
      new Blob([JSON.stringify(combinedAnimal)], { type: 'application/json' })
    );

    // Attach top-level Animal images
    animalImages.forEach(file => {
      formData.append('animalImages', file);
    });

    // Attach distributionMap if present
    if (animalInfo.distribution.distributionMapFile) {
      formData.append('distributionMap', animalInfo.distribution.distributionMapFile);
    }

    // Attach appearance images if present
    if (animalInfo.appearance.images && animalInfo.appearance.images.length > 0) {
      animalInfo.appearance.images.forEach(file => {
        formData.append('appearanceImages', file);
      });
    }

    const request = isEdit
      ? api.put(`/api/v1/animals/admin/updateCompleteAnimal/${id}`, formData, {
          headers: {
            Authorization: keycloak.token ? `Bearer ${keycloak.token}` : undefined
          }
        })
      : api.post('/api/v1/animals/admin/addCompleteAnimal', formData, {
          headers: {
            Authorization: keycloak.token ? `Bearer ${keycloak.token}` : undefined
          }
        });

    setSaving(true);
    request
      .then(response => {
        console.log(isEdit ? 'Animal updated:' : 'Animal added:', response.data);
        if (isEdit) {
          navigate(`/animal-details/${id}`);
          return;
        }
        setAnimal({ ...EMPTY_ANIMAL });
        setAnimalExtendedInfo({
          ...EMPTY_EXTENDED,
          population: { ...EMPTY_EXTENDED.population },
          geography: { ...EMPTY_EXTENDED.geography }
        });
        setAnimalInfo({
          ...EMPTY_ANIMAL_INFO,
          distribution: { ...EMPTY_ANIMAL_INFO.distribution, geography: { ...EMPTY_ANIMAL_INFO.distribution.geography } },
          appearance: { ...EMPTY_ANIMAL_INFO.appearance, images: [] },
          habits: { ...EMPTY_ANIMAL_INFO.habits, groupName: [], lifestyle: [] },
          biome: { biomes: [] },
          climateZones: { zones: [] }
        });
        setAnimalImages([]);
        setAnimalImageUrls(['']);
        setExistingImages([]);
        setExistingMapUrl('');
      })
      .catch(error => console.error('Error:', error))
      .finally(() => setSaving(false));
  };

  const handleBulkFileChange = e => {
    const file = e.target.files[0];
    setBulkFile(file || null);
    setBulkResult(null);
  };

  const downloadTemplate = () => {
    api.get('/api/v1/animals/admin/bulkTemplate', {
      responseType: 'blob',
      headers: {
        Authorization: keycloak.token ? `Bearer ${keycloak.token}` : undefined
      }
    })
      .then(response => {
        const url = window.URL.createObjectURL(response.data);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'animalia-bulk-template.csv';
        link.click();
        window.URL.revokeObjectURL(url);
      })
      .catch(error => console.error('Error:', error));
  };

  const handleBulkImport = () => {
    if (!bulkFile) {
      return;
    }
    const formData = new FormData();
    formData.append('file', bulkFile);
    setBulkBusy(true);
    api.post('/api/v1/animals/admin/bulkImport', formData, {
      headers: {
        Authorization: keycloak.token ? `Bearer ${keycloak.token}` : undefined
      }
    })
      .then(response => setBulkResult(response.data))
      .catch(error => {
        console.error('Error:', error);
        setBulkResult({
          totalRows: 0,
          imported: 0,
          failed: 0,
          fileError: 'Bulk import failed. Check the file and try again.',
          rows: []
        });
      })
      .finally(() => setBulkBusy(false));
  };

  const tabs = isEdit
    ? [
        { id: 'basic', label: 'Identity', hint: 'Name and taxonomy', icon: 'fa-paw' },
        { id: 'info', label: 'Life story', hint: 'Range, look, habits', icon: 'fa-globe' },
        { id: 'extended', label: 'Field notes', hint: 'Stats and photos', icon: 'fa-camera' }
      ]
    : [
        { id: 'basic', label: 'Identity', hint: 'Name and taxonomy', icon: 'fa-paw' },
        { id: 'info', label: 'Life story', hint: 'Range, look, habits', icon: 'fa-globe' },
        { id: 'extended', label: 'Field notes', hint: 'Stats and photos', icon: 'fa-camera' },
        { id: 'bulk', label: 'Bulk import', hint: 'CSV, Excel, or JSON', icon: 'fa-file-upload' }
      ];
  const activeIndex = tabs.findIndex(tab => tab.id === activeTab);

  const renderPreviews = (files) => (
    files && files.length > 0 ? (
      <div className="upload-previews">
        {files.map((file, index) => (
          <div className="upload-preview" key={`${file.name}-${index}`}>
            <img src={URL.createObjectURL(file)} alt={file.name} />
            <span>{file.name}</span>
          </div>
        ))}
      </div>
    ) : null
  );

  const renderBasicInfoTab = () => (
    <div className="field-grid">
      <label className="field">
        <span>Phylum</span>
        <input type="text" name="phylum" placeholder="Chordata" value={animal.phylum} onChange={handleAnimalChange} />
      </label>
      <label className="field">
        <span>Family</span>
        <input type="text" name="family" placeholder="Felidae" value={animal.family} onChange={handleAnimalChange} />
      </label>
      <label className="field">
        <span>Name</span>
        <input type="text" name="name" placeholder="Bengal tiger" value={animal.name} onChange={handleAnimalChange} />
      </label>
      <label className="field">
        <span>Scientific Name</span>
        <input type="text" name="scientificName" placeholder="Panthera tigris tigris" value={animal.scientificName} onChange={handleAnimalChange} />
      </label>
      <label className="field">
        <span>Order</span>
        <input type="text" name="order" placeholder="Carnivora" value={animal.order} onChange={handleAnimalChange} />
      </label>
      <label className="field">
        <span>Kingdom</span>
        <input type="text" name="kingdom" placeholder="Animalia" value={animal.kingdom} onChange={handleAnimalChange} />
      </label>
      <label className="field">
        <span>Genus</span>
        <input type="text" name="genus" placeholder="Panthera" value={animal.genus} onChange={handleAnimalChange} />
      </label>
      <label className="field">
        <span>Species</span>
        <input type="text" name="species" placeholder="tigris" value={animal.species} onChange={handleAnimalChange} />
      </label>
      <label className="field">
        <span>Class</span>
        <input type="text" name="animalClass" placeholder="Mammalia" value={animal.animalClass} onChange={handleAnimalChange} />
      </label>
      <label className="field">
        <span>Subphylum</span>
        <input type="text" name="subphylum" placeholder="Vertebrata" value={animal.subphylum} onChange={handleAnimalChange} />
      </label>
      <label className="field">
        <span>Suborder</span>
        <input type="text" name="suborder" placeholder="Feliformia" value={animal.suborder} onChange={handleAnimalChange} />
      </label>
      <label className="field field-wide">
        <span>Description</span>
        <textarea name="description" rows={4} placeholder="A short field note about this animal..." value={animal.description} onChange={handleAnimalChange} />
      </label>
    </div>
  );

  const renderAnimalInfoTab = () => (
    <div className="panel-stack">
      <section className="panel-card">
        <div className="panel-card-title">
          <i className="fas fa-map-marked-alt"></i>
          <h3>Distribution</h3>
        </div>
        <label className="field field-wide">
          <span>Range notes</span>
          <textarea
            name="distribution"
            rows={3}
            placeholder="Where this species lives and migrates..."
            value={animalInfo.distribution.distribution}
            onChange={(e) => handleAnimalInfoChange(e, 'distribution')}
          />
        </label>
        <label className="upload-dropzone">
          <input type="file" accept="image/*" onChange={handleDistributionMapChange} />
          <i className="fas fa-cloud-upload-alt"></i>
          <strong>Distribution map</strong>
          <em>{animalInfo.distribution.distributionMapFile ? animalInfo.distribution.distributionMapFile.name : 'Drop an image or browse'}</em>
        </label>
        {animalInfo.distribution.distributionMapFile && renderPreviews([animalInfo.distribution.distributionMapFile])}
        {!animalInfo.distribution.distributionMapFile && existingMapUrl && (
          <div className="upload-previews">
            <div className="upload-preview">
              <img src={existingMapUrl} alt="Current distribution map" />
              <span>Current map</span>
            </div>
          </div>
        )}
      </section>

      <section className="panel-card">
        <div className="panel-card-title">
          <i className="fas fa-tree"></i>
          <h3>Biome & climate</h3>
        </div>
        <p className="bulk-copy">Select every biome and climate zone this species occupies. These power the habitat scene cards on the profile page.</p>

        <div className="choice-block">
          <h4>Biome</h4>
          <div className="choice-grid">
            {biomeOptions.map((option) => {
              const selected = (animalInfo.biome?.biomes || []).includes(option.id);
              return (
                <button
                  type="button"
                  key={option.id}
                  className={`choice-chip choice-chip-biome ${selected ? 'is-selected' : ''}`}
                  onClick={() => handleBiomeToggle(option.id)}
                  aria-pressed={selected}
                >
                  {option.label}
                </button>
              );
            })}
            {!biomeOptions.length && <em className="choice-empty">Loading biomes…</em>}
          </div>
        </div>

        <div className="choice-block">
          <h4>Climate zones</h4>
          <div className="choice-grid">
            {climateOptions.map((option) => {
              const selected = (animalInfo.climateZones?.zones || []).includes(option.id);
              return (
                <button
                  type="button"
                  key={option.id}
                  className={`choice-chip choice-chip-climate ${selected ? 'is-selected' : ''}`}
                  onClick={() => handleClimateToggle(option.id)}
                  aria-pressed={selected}
                >
                  {option.label}
                </button>
              );
            })}
            {!climateOptions.length && <em className="choice-empty">Loading climate zones…</em>}
          </div>
        </div>
      </section>

      <section className="panel-card">
        <div className="panel-card-title">
          <i className="fas fa-eye"></i>
          <h3>Appearance</h3>
        </div>
        <label className="field field-wide">
          <span>Look and markings</span>
          <textarea
            name="description"
            rows={3}
            placeholder="Coat, plumage, distinctive features..."
            value={animalInfo.appearance.description}
            onChange={(e) => handleAnimalInfoChange(e, 'appearance')}
          />
        </label>
        <label className="upload-dropzone">
          <input type="file" accept="image/*" multiple onChange={handleAppearanceImagesChange} />
          <i className="fas fa-images"></i>
          <strong>Appearance images</strong>
          <em>{animalInfo.appearance.images.length ? `${animalInfo.appearance.images.length} file(s) selected` : 'Add one or more photos'}</em>
        </label>
        {renderPreviews(animalInfo.appearance.images)}
      </section>

      <section className="panel-card">
        <div className="panel-card-title">
          <i className="fas fa-moon"></i>
          <h3>Habits</h3>
        </div>
        <label className="field field-wide">
          <span>Behavior</span>
          <textarea
            name="description"
            rows={3}
            placeholder="Diet, social life, activity pattern..."
            value={animalInfo.habits.description}
            onChange={(e) => handleAnimalInfoChange(e, 'habits')}
          />
        </label>
      </section>
    </div>
  );

  const renderExtendedInfoTab = () => (
    <div className="panel-stack">
      <section className="panel-card">
        <div className="panel-card-title">
          <i className="fas fa-chart-line"></i>
          <h3>Vital stats</h3>
        </div>
        <div className="field-grid">
          <label className="field">
            <span>Population Size</span>
            <input type="text" name="populationSize" placeholder="e.g. 3,900" value={animalExtendedInfo.populationSize} onChange={handleExtendedInfoChange} />
          </label>
          <label className="field">
            <span>Life Span</span>
            <input type="text" name="lifeSpan" placeholder="e.g. 10–15 years" value={animalExtendedInfo.lifeSpan} onChange={handleExtendedInfoChange} />
          </label>
          <label className="field">
            <span>Top Speed</span>
            <input type="text" name="topSpeed" placeholder="e.g. 65 km/h" value={animalExtendedInfo.topSpeed} onChange={handleExtendedInfoChange} />
          </label>
          <label className="field">
            <span>Weight</span>
            <input type="number" name="weight" placeholder="kg" value={animalExtendedInfo.weight} onChange={handleExtendedInfoChange} />
          </label>
          <label className="field">
            <span>Length</span>
            <input type="text" name="length" placeholder="e.g. 2.8 m" value={animalExtendedInfo.length} onChange={handleExtendedInfoChange} />
          </label>
          <label className="field">
            <span>Wing Span</span>
            <input type="text" name="wingSpan" placeholder="If applicable" value={animalExtendedInfo.wingSpan} onChange={handleExtendedInfoChange} />
          </label>
        </div>
      </section>

      <section className="panel-card">
        <div className="panel-card-title">
          <i className="fas fa-leaf"></i>
          <h3>Population &amp; conservation</h3>
        </div>
        <p className="bulk-copy">
          Set population notes and IUCN Red List status. The selected status ring is highlighted on the profile page.
        </p>

        <div className="field-grid">
          <label className="field">
            <span>Estimated population</span>
            <input
              type="text"
              name="populationNumber"
              placeholder="e.g. 3,900 mature individuals"
              value={animalExtendedInfo.population.populationNumber}
              onChange={handlePopulationChange}
            />
          </label>
          <label className="field field-wide">
            <span>Population threats</span>
            <textarea
              name="populationThreats"
              rows={3}
              placeholder="Habitat loss, poaching, climate change..."
              value={animalExtendedInfo.population.populationThreats}
              onChange={handlePopulationChange}
            />
          </label>
        </div>

        <div className="choice-block">
          <h4>Population trend</h4>
          <div className="choice-grid choice-grid-trend">
            {POPULATION_TREND_OPTIONS.map((option) => {
              const selected = animalExtendedInfo.population.populationTrend === option.id;
              return (
                <button
                  type="button"
                  key={option.id}
                  className={`choice-chip choice-chip-trend ${selected ? 'is-selected' : ''}`}
                  onClick={() => handlePopulationTrendSelect(option.id)}
                  aria-pressed={selected}
                >
                  {option.label}
                </button>
              );
            })}
          </div>
        </div>

        <div className="choice-block">
          <h4>IUCN conservation status</h4>
          <div className="form-iucn" role="list" aria-label="IUCN conservation status">
            {IUCN_ORDER.map((code) => {
              const meta = IUCN_META[code];
              const selected = animalExtendedInfo.population.populationConservationStatus === code;
              return (
                <button
                  type="button"
                  key={code}
                  role="listitem"
                  className={`form-iucn-item tone-${meta.tone} ${selected ? 'is-selected' : ''}`}
                  onClick={() => handleConservationStatusSelect(code)}
                  aria-pressed={selected}
                  title={meta.label}
                >
                  <span className="form-iucn-ring">{code}</span>
                  <span className="form-iucn-caption">{meta.label.split(' ')[0]}</span>
                </button>
              );
            })}
          </div>
        </div>
      </section>

      <section className="panel-card">
        <div className="panel-card-title">
          <i className="fas fa-camera-retro"></i>
          <h3>Gallery images</h3>
        </div>
        <p className="bulk-copy">
          Upload files (stored on Cloudinary) and/or paste existing CDN image URLs.
        </p>
        {existingImages.length > 0 && (
          <div className="upload-previews">
            {existingImages.map((image, index) => (
              imageSrc(image) ? (
                <div className="upload-preview" key={image.id || `existing-${index}`}>
                  <img src={imageSrc(image)} alt={image.name || 'Current gallery image'} />
                  <span>{image.name || 'Current image'}</span>
                </div>
              ) : null
            ))}
          </div>
        )}
        <label className="upload-dropzone">
          <input type="file" accept="image/*" multiple onChange={handleAnimalImagesChange} />
          <i className="fas fa-cloud-upload-alt"></i>
          <strong>Upload to CDN</strong>
          <em>{animalImages.length ? `${animalImages.length} file(s) selected` : 'Images are uploaded to Cloudinary'}</em>
        </label>
        {renderPreviews(animalImages)}

        <div className="cdn-url-block">
          <div className="panel-card-title" style={{ marginTop: '1.25rem' }}>
            <i className="fas fa-link"></i>
            <h3>CDN image URLs</h3>
          </div>
          {animalImageUrls.map((url, index) => (
            <div className="cdn-url-row" key={`img-url-${index}`}>
              <label className="field" style={{ flex: 1 }}>
                <span>Image URL {index + 1}</span>
                <input
                  type="url"
                  placeholder="https://res.cloudinary.com/..."
                  value={url}
                  onChange={(e) => handleAnimalImageUrlChange(index, e.target.value)}
                />
              </label>
              {animalImageUrls.length > 1 && (
                <button type="button" className="btn ghost" onClick={() => removeAnimalImageUrlField(index)}>
                  Remove
                </button>
              )}
            </div>
          ))}
          <button type="button" className="btn ghost" onClick={addAnimalImageUrlField}>
            Add another URL
          </button>
        </div>
      </section>
    </div>
  );

  const renderBulkTab = () => (
    <div className="panel-stack">
      <section className="panel-card">
        <div className="panel-card-title">
          <i className="fas fa-layer-group"></i>
          <h3>Import from any template</h3>
        </div>
        <p className="bulk-copy">
          Upload CSV, TSV, Excel, or JSON. Column names can vary — <em>common name</em>,
          <em> latin name</em>, <em>scientific_name</em>, and similar aliases are mapped automatically.
          Required fields: name, scientific name, phylum, family, and species.
        </p>
        <div className="bulk-actions">
          <button type="button" className="btn ghost" onClick={downloadTemplate}>
            Download CSV template
          </button>
        </div>
        <label className="upload-dropzone">
          <input
            type="file"
            accept=".csv,.tsv,.txt,.xlsx,.xls,.json"
            onChange={handleBulkFileChange}
          />
          <i className="fas fa-file-import"></i>
          <strong>Drop a catalog file</strong>
          <em>{bulkFile ? bulkFile.name : 'CSV, Excel, or JSON'}</em>
        </label>
      </section>

      {bulkResult && (
        <section className="panel-card">
          <div className="panel-card-title">
            <i className="fas fa-clipboard-check"></i>
            <h3>Import results</h3>
          </div>
          {bulkResult.fileError && <p className="bulk-error">{bulkResult.fileError}</p>}
          <div className="bulk-summary">
            <span>{bulkResult.imported} imported</span>
            <span>{bulkResult.failed} failed</span>
            <span>{bulkResult.totalRows} rows</span>
          </div>
          {bulkResult.rows && bulkResult.rows.length > 0 && (
            <div className="bulk-table-wrap">
              <table className="bulk-table">
                <thead>
                  <tr>
                    <th>Row</th>
                    <th>Name</th>
                    <th>Status</th>
                    <th>Validation</th>
                  </tr>
                </thead>
                <tbody>
                  {bulkResult.rows.map(row => (
                    <tr key={row.rowNumber} className={row.success ? 'ok' : 'fail'}>
                      <td>{row.rowNumber}</td>
                      <td>{row.name || '—'}</td>
                      <td>{row.success ? 'Imported' : 'Rejected'}</td>
                      <td>{row.errors && row.errors.length ? row.errors.join(' · ') : '—'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      )}
    </div>
  );

  return (
    <motion.div
      className="add-animal-page"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
    >
      <div className="add-animal-bg" aria-hidden="true">
        <span className="orb orb-one"></span>
        <span className="orb orb-two"></span>
        <span className="orb orb-three"></span>
      </div>

      <header className="add-animal-topbar">
        <a href="/" className="add-animal-brand">
          <img src={logo} alt="animalia" />
        </a>
        <span className="add-animal-badge">Admin catalog</span>
      </header>

      {loadingAnimal && (
        <div className="add-animal-card">
          <p className="hero-copy">Loading animal for editing…</p>
        </div>
      )}

      {loadError && !loadingAnimal && (
        <div className="add-animal-card">
          <div className="add-animal-hero">
            <p className="eyebrow">Edit species</p>
            <h1>Animal not found</h1>
            <p className="hero-copy">{loadError}</p>
          </div>
        </div>
      )}

      {!loadingAnimal && !loadError && (
      <form onSubmit={handleSubmit} className="add-animal-card">
        <div className="add-animal-hero">
          <p className="eyebrow">{isEdit ? 'Edit species entry' : 'New species entry'}</p>
          <h1>{animal.name || (isEdit ? 'Edit Animal' : 'Add Animal')}</h1>
          <p className="hero-copy">
            {animal.scientificName
              ? <em>{animal.scientificName}</em>
              : (isEdit
                ? 'Update this species in the Animalia archive.'
                : 'Document a new species for the Animalia archive.')}
          </p>
        </div>

        <div className="add-animal-steps">
          {tabs.map((tab, index) => (
            <button
              key={tab.id}
              type="button"
              className={`step ${activeTab === tab.id ? 'active' : ''} ${index < activeIndex ? 'complete' : ''}`}
              onClick={() => setActiveTab(tab.id)}
            >
              <span className="step-index">
                <i className={`fas ${tab.icon}`}></i>
              </span>
              <span className="step-copy">
                <strong>{tab.label}</strong>
                <small>{tab.hint}</small>
              </span>
            </button>
          ))}
        </div>

        <div className="add-animal-panel">
          <AnimatePresence mode="wait">
            <motion.div
              key={activeTab}
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -12 }}
              transition={{ duration: 0.28 }}
            >
              {activeTab === 'basic' && renderBasicInfoTab()}
              {activeTab === 'info' && renderAnimalInfoTab()}
              {activeTab === 'extended' && renderExtendedInfoTab()}
              {activeTab === 'bulk' && renderBulkTab()}
            </motion.div>
          </AnimatePresence>
        </div>

        <div className="add-animal-actions">
          {activeIndex > 0 && (
            <button type="button" className="btn ghost" onClick={() => setActiveTab(tabs[activeIndex - 1].id)}>
              Back
            </button>
          )}
          {activeIndex < tabs.length - 1 && (
            <button type="button" className="btn secondary" onClick={() => setActiveTab(tabs[activeIndex + 1].id)}>
              Continue
            </button>
          )}
          {activeTab === 'bulk' ? (
            <button type="button" className="btn primary" onClick={handleBulkImport} disabled={!bulkFile || bulkBusy}>
              {bulkBusy ? 'Importing...' : 'Import catalog'}
            </button>
          ) : (
            <button type="submit" className="btn primary" disabled={saving}>
              {saving
                ? (isEdit ? 'Saving...' : 'Publishing...')
                : (isEdit ? 'Save changes' : 'Publish animal')}
            </button>
          )}
          <button
            type="button"
            className="btn text"
            onClick={() => (window.location.href = isEdit ? `/animal-details/${id}` : '/')}
          >
            Exit
          </button>
        </div>
      </form>
      )}
    </motion.div>
  );
}

export default AnimalForm;