import React from 'react';

const AnimalCard = ({ animal }) => {

    return (
        <div className="animal-card" >
            <img src={`data:${animal.animalImage.type};base64,${animal.animalImage.imageArray}`} 
            onError={(e) => {
                e.target.onerror = null;
                e.target.src = 'pitviper.jpg';
            }}  />
            <div className="animal-overlay">
                <h3 className="animal-name">{animal.name}</h3>
                <p className="animal-scientific-name">{animal.family}</p>
            </div>
        </div>
    );
};

export default AnimalCard;