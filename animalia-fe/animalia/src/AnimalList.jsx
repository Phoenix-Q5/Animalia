import React, { useState, useEffect } from 'react';
import SearchBar from './SearchBar';
import * as PropTypes from "prop-types";

SearchBar.propTypes = {onSearch: PropTypes.func};

function AnimalList() {
    const [animals, setAnimals] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');

    useEffect(() => {
        fetch('http://localhost:8080/api/v1/animals/getAllAnimals')
            .then(response => response.json())
            .then(data => setAnimals(data))
            .catch(error => console.error('Error fetching data: ', error));
    }, []);

    function handleSearch(term) {
        setSearchTerm(term.toLowerCase());
    }

    const filteredAnimals = animals.filter(animal =>
        animal.name.toLowerCase().includes(searchTerm) ||
        animal.breed.toLowerCase().includes(searchTerm) ||
        animal.earTag?.toLowerCase().includes(searchTerm) ||
        animal.id.toString().includes(searchTerm) ||
        animal.age.toString().includes(searchTerm) ||
        animal.weight.toString().includes(searchTerm)
      );
    
      return (
        <div>
          <SearchBar onSearch={handleSearch} />
          {filteredAnimals.map(animal => (
            <div key={animal.id} className="animal">
              <div className="animal-info">
                <h3><strong>Name:</strong> {animal.name}</h3>
                <p><strong>Breed:</strong> {animal.breed}</p>
                <p><strong>Ear Tag:</strong> {animal.earTag}</p>
                <p><strong>Description:</strong> {animal.description}</p>
                <p><strong>Age:</strong> {animal.age}</p>
                <p><strong>Weight:</strong> {animal.weight} kg</p>
              </div>
              <div className="vertical-line"></div>
              <div className="animal-image">
                {/* <img src={animal.animalImage} alt={animal.name} /> */}
                {animal.animalImage && (
                  <img src={`data:${animal.animalImage.type};base64,${animal.animalImage.image}`} 
                  alt={animal.name}
                  />
                )}
              </div>
            </div>
          ))}
        </div>
      );
}

export default AnimalList;