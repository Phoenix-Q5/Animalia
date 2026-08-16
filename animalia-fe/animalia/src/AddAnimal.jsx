import React, { useState } from 'react';
import axios from 'axios';
import { AnimatePresence, motion } from 'framer-motion';
import { useKeycloak } from '@react-keycloak/web';
import logo from './assets/animalia-logo-full.png';

function AnimalForm() {
    const { keycloak } = useKeycloak();
    const [activeTab, setActiveTab] = useState('basic');
  const [animal, setAnimal] = useState({
    phylum: '',
    family: '',
    name: '',
    scientificName: '',
    description: '',
    genus: '',
    species: '',
    order: '',
    kingdom: ''
  });

  const [animalExtendedInfo, setAnimalExtendedInfo] = useState({
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
      // vulnerabilityStatus is an enum on the backend, so a user must select from a dropdown:
      populationConservationStatus: ''
    },

    geography: {
      continents: [],
      countries: [],
      regions: [],
      biomes: []
    },

    climateZones: {},
    biome: {}
  });

  const [animalInfo, setAnimalInfo] = useState({
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
    }
  });

  const [animalImages, setAnimalImages] = useState([]);


  const [bulkFile, setBulkFile] = useState(null);
  const [bulkResult, setBulkResult] = useState(null);
  const [bulkBusy, setBulkBusy] = useState(false);

  const api = axios.create({
    baseURL: 'http://localhost:8080'
  });

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
    const combinedAnimal = {
      ...animal,
      animalExtendedInfo: { ...animalExtendedInfo },
      // animalInfo might not exist in your model’s Animal, so if you actually store
      // it in Animal as well, you can add it. But from your definitions, Animal has
      // AnimalExtendedInfo plus a separate "AnimalInfo" if you want that. 
      animalInfo: { ...animalInfo },
      // images for the top-level: "animalImages"
      // We'll just attach them as a separate property or keep them as "animalImages"
      // which the server can parse.  
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

    // Post to your endpoint
    api.post('/api/v1/animals/admin/addCompleteAnimal', formData, {
      headers: {
        Authorization: keycloak.token ? `Bearer ${keycloak.token}` : undefined
      }
    })
      .then(response => {
        console.log('Animal added:', response.data);
        // Reset the form
        setAnimal({
          phylum: '',
          family: '',
          name: '',
          scientificName: '',
          description: '',
          genus: '',
          species: '',
          order: '',
          kingdom: ''
        });
        setAnimalExtendedInfo({
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
          },
          climateZones: {},
          biome: {}
        });
        setAnimalInfo({
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
          }
        });
        setAnimalImages([]);
      })
      .catch(error => console.error('Error:', error));
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

  const tabs = [
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
          <i className="fas fa-camera-retro"></i>
          <h3>Gallery images</h3>
        </div>
        <label className="upload-dropzone">
          <input type="file" accept="image/*" multiple onChange={handleAnimalImagesChange} />
          <i className="fas fa-plus-circle"></i>
          <strong>Animal images</strong>
          <em>{animalImages.length ? `${animalImages.length} file(s) selected` : 'Build the public gallery'}</em>
        </label>
        {renderPreviews(animalImages)}
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

      <form onSubmit={handleSubmit} className="add-animal-card">
        <div className="add-animal-hero">
          <p className="eyebrow">New species entry</p>
          <h1>{animal.name || 'Add Animal'}</h1>
          <p className="hero-copy">
            {animal.scientificName
              ? <em>{animal.scientificName}</em>
              : 'Document a new species for the Animalia archive.'}
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
            <button type="submit" className="btn primary">Publish animal</button>
          )}
          <button type="button" className="btn text" onClick={() => (window.location.href = '/')}>
            Exit
          </button>
        </div>
      </form>
    </motion.div>
  );
}

export default AnimalForm;