import React from 'react';

function resolveImage(animal) {
    if (!animal) return null;
    if (animal.animalImage) return animal.animalImage;
    if (animal.animalImages?.length) return animal.animalImages[0];
    if (animal.galleryImages?.length) return animal.galleryImages[0];
    return null;
}

export function imageSrc(image) {
    if (!image) return null;
    if (image.url) return image.url;
    if (!image.imageArray) return null;
    if (typeof image.imageArray === 'string') {
        return `data:${image.type || 'image/jpeg'};base64,${image.imageArray}`;
    }
    return null;
}

const AnimalCard = ({ animal }) => {
    const image = resolveImage(animal);
    const src = imageSrc(image) || 'pitviper.jpg';

    return (
        <div className="animal-card">
            <img
                src={src}
                alt={animal?.name || 'Animal'}
                onError={(e) => {
                    e.target.onerror = null;
                    e.target.src = 'pitviper.jpg';
                }}
            />
            <div className="animal-overlay">
                <h3 className="animal-name">{animal.name}</h3>
                <p className="animal-scientific-name">{animal.scientificName || animal.family}</p>
            </div>
        </div>
    );
};

export default AnimalCard;
