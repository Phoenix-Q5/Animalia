import React, { useState, useEffect } from 'react';
import SearchBar from './SearchBar';
import * as PropTypes from "prop-types";
import { motion } from 'framer-motion';

SearchBar.propTypes = {onSearch: PropTypes.func};

function AnimalList() {
    const [animals, setAnimals] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');

    useEffect(() => {
        fetch('/api/v1/animals/getAllAnimals')
            .then(response => response.json())
            .then(data => setAnimals(data))
            .catch(error => console.error('Error fetching data: ', error));
    }, []);

    function handleSearch(term) {
        setSearchTerm(term.toLowerCase());
    }

    const filteredAnimals = animals.filter(animal =>
        animal.name.toLowerCase().includes(searchTerm) ||
        animal.phylum.toLowerCase().includes(searchTerm) ||
        animal.family?.toLowerCase().includes(searchTerm) ||
        animal.id.toString().includes(searchTerm) ||
        animal.genus.toLowerCase().includes(searchTerm) ||
        animal.species.toLowerCase().includes(searchTerm)
      );
    
      return (

        // <div className="video-background">
        // <video autoPlay="autoplay" loop="loop" muted defaultMuted playsInline className="video-element">
        //   <source src="P1470355.mp4" type="video/mp4" />
        //   Your browser does not support the video tag.
        // </video>
        <div>
          <SearchBar onSearch={handleSearch} />
          {filteredAnimals.map(animal => (
            <div key={animal.id} className="animal">
              <div className="animal-info">
                <h3><strong>Name:</strong> {animal.name}</h3>
                <p><strong>Phylum:</strong> {animal.phylum}</p>
                <p><strong>Family:</strong> {animal.family}</p>
                <p className="animal-description"><strong>Description:</strong> {animal.description}</p>
                <p><strong>Genus:</strong> {animal.genus}</p>
                <p><strong>Species:</strong> {animal.species} </p>
              </div>
              {/* <div className="vertical-line"></div> */}
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
        // </div>
      );
}

export default AnimalList;