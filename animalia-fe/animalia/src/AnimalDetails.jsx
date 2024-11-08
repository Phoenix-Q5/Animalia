import React from 'react';
import { useParams } from 'react-router-dom';

function AnimalDetails({ selectedAnimal }) {
    const { id } = useParams();

    // if (!selectedAnimal || selectedAnimal.id !== parseInt(id)) {
    //     return <div>No animal details available. Please select an animal from the list.</div>;
    // }

    return (
        // <div>
        //     <h2>{selectedAnimal.name}</h2>
        //     <p><strong>Scientific Name:</strong> {selectedAnimal.scientificName}</p>
        //     <p><strong>Habitat:</strong> {selectedAnimal.habitat}</p>
        //     <p><strong>Description:</strong> {selectedAnimal.description}</p>
        // </div>
    <div className="animal-card">
        <h3>{selectedAnimal.name}</h3>
        <img src={selectedAnimal.imageUrl} alt={selectedAnimal.title}/>
        <p>{selectedAnimal.description}</p>
    </div>
)
    ;
}

export default AnimalDetails;
