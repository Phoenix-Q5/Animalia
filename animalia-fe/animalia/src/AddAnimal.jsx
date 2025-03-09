import React, { useState } from 'react';
import axios from 'axios';
import { motion } from 'framer-motion';

function AnimalForm() {
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
    api.post('/api/v1/animals/admin/addCompleteAnimal', formData)
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

  // ─────────────────────────────────────────────────────────────────────────────
  // JSX for the form
  // ─────────────────────────────────────────────────────────────────────────────
  const renderBasicInfoTab = () => (
    <table className="info-table">
      <tbody>
        <tr>
          <td><label>Phylum</label></td>
          <td>
            <input
              type="text"
              name="phylum"
              value={animal.phylum}
              onChange={handleAnimalChange}
            />
          </td>
        </tr>
        <tr>
          <td><label>Family</label></td>
          <td>
            <input
              type="text"
              name="family"
              value={animal.family}
              onChange={handleAnimalChange}
            />
          </td>
        </tr>
        <tr>
          <td><label>Name</label></td>
          <td>
            <input
              type="text"
              name="name"
              value={animal.name}
              onChange={handleAnimalChange}
            />
          </td>
        </tr>
        <tr>
          <td><label>Scientific Name</label></td>
          <td>
            <input
              type="text"
              name="scientificName"
              value={animal.scientificName}
              onChange={handleAnimalChange}
            />
          </td>
        </tr>
        <tr>
          <td><label>Order</label></td>
          <td>
            <input
              type="text"
              name="order"
              value={animal.order}
              onChange={handleAnimalChange}
            />
          </td>
        </tr>
        <tr>
          <td><label>Kingdom</label></td>
          <td>
            <input
              type="text"
              name="kingdom"
              value={animal.kingdom}
              onChange={handleAnimalChange}
            />
          </td>
        </tr>
        <tr>
          <td><label>Description</label></td>
          <td>
            <textarea
              name="description"
              value={animal.description}
              onChange={handleAnimalChange}
              rows={3}
            />
          </td>
        </tr>
      </tbody>
    </table>
  );

  const renderAnimalInfoTab = () => (
    <div>
      <h3>Distribution</h3>
      <table className="info-table">
        <tbody>
          <tr>
            <td><label>Distribution Description</label></td>
            <td>
              <textarea
                name="distribution"
                rows={3}
                value={animalInfo.distribution.distribution}
                onChange={(e) => handleAnimalInfoChange(e, 'distribution')}
              />
            </td>
          </tr>
          <tr>
            <td><label>Distribution Map</label></td>
            <td>
              <input type="file" onChange={handleDistributionMapChange} />
            </td>
          </tr>
        </tbody>
      </table>

      <h3>Appearance</h3>
      <table className="info-table">
        <tbody>
          <tr>
            <td><label>Description</label></td>
            <td>
              <textarea
                name="description"
                rows={3}
                value={animalInfo.appearance.description}
                onChange={(e) => handleAnimalInfoChange(e, 'appearance')}
              />
            </td>
          </tr>
          <tr>
            <td><label>Images</label></td>
            <td>
              <input type="file" multiple onChange={handleAppearanceImagesChange} />
            </td>
          </tr>
        </tbody>
      </table>

      <h3>Habits</h3>
      <table className="info-table">
        <tbody>
          <tr>
            <td><label>Description</label></td>
            <td>
              <textarea
                name="description"
                rows={3}
                value={animalInfo.habits.description}
                onChange={(e) => handleAnimalInfoChange(e, 'habits')}
              />
            </td>
          </tr>
          {/* You can add groupName, lifestyle, etc. if needed. */}
        </tbody>
      </table>
    </div>
  );

  const renderExtendedInfoTab = () => (
    <div>
      <table className="info-table">
        <tbody>
          <tr>
            <td><label>Population Size</label></td>
            <td>
              <input
                type="text"
                name="populationSize"
                value={animalExtendedInfo.populationSize}
                onChange={handleExtendedInfoChange}
              />
            </td>
          </tr>
          <tr>
            <td><label>Life Span</label></td>
            <td>
              <input
                type="text"
                name="lifeSpan"
                value={animalExtendedInfo.lifeSpan}
                onChange={handleExtendedInfoChange}
              />
            </td>
          </tr>
          <tr>
            <td><label>Top Speed</label></td>
            <td>
              <input
                type="text"
                name="topSpeed"
                value={animalExtendedInfo.topSpeed}
                onChange={handleExtendedInfoChange}
              />
            </td>
          </tr>
          <tr>
            <td><label>Weight</label></td>
            <td>
              <input
                type="number"
                name="weight"
                value={animalExtendedInfo.weight}
                onChange={handleExtendedInfoChange}
              />
            </td>
          </tr>
          <tr>
            <td><label>Length</label></td>
            <td>
              <input
                type="text"
                name="length"
                value={animalExtendedInfo.length}
                onChange={handleExtendedInfoChange}
              />
            </td>
          </tr>
          <tr>
            <td><label>Wing Span</label></td>
            <td>
              <input
                type="text"
                name="wingSpan"
                value={animalExtendedInfo.wingSpan}
                onChange={handleExtendedInfoChange}
              />
            </td>
          </tr>
        </tbody>
      </table>

      <h3>Top-Level Images</h3>
      <table className="info-table">
        <tbody>
          <tr>
            <td><label>Animal Images</label></td>
            <td>
              <input
                type="file"
                multiple
                onChange={handleAnimalImagesChange}
              />
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  );

  return (
    <motion.div
      className="tabs-page-container"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
    >
      <form onSubmit={handleSubmit} className="tabs-form">
        <h1>Add Animal</h1>

        {/* TAB NAVIGATION */}
        <div className="tab-nav">
          <button
            type="button"
            className={`tab-link ${activeTab === 'basic' ? 'active' : ''}`}
            onClick={() => setActiveTab('basic')}
          >
            Basic Info
          </button>
          <button
            type="button"
            className={`tab-link ${activeTab === 'info' ? 'active' : ''}`}
            onClick={() => setActiveTab('info')}
          >
            Animal Info
          </button>
          <button
            type="button"
            className={`tab-link ${activeTab === 'extended' ? 'active' : ''}`}
            onClick={() => setActiveTab('extended')}
          >
            Extended Info & Images
          </button>
        </div>

        <div className="tab-content">
          {activeTab === 'basic' && renderBasicInfoTab()}
          {activeTab === 'info' && renderAnimalInfoTab()}
          {activeTab === 'extended' && renderExtendedInfoTab()}
        </div>

        <div className="tab-button-group">
          <button type="submit" className="btn primary">Submit</button>
          <button
            type="button"
            className="btn secondary"
            onClick={() => (window.location.href = '/')}
          >
            Exit
          </button>
        </div>
      </form>
    </motion.div>
  );
}

export default AnimalForm;