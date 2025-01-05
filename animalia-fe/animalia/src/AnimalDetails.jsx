import React from 'react';
import logo from './assets/animalia-logo-full.png'
import { Link } from 'react-router-dom';

function AnimalDetails({ animal, onBack }) {
    return (
        <div>   
            <main
                        className="main-content"
                        style={{
                            backgroundSize: 'cover',
                            backgroundPosition: 'center',
                            backgroundRepeat: 'no-repeat',
                            width: '100%',
                            minHeight: '3vh',
                        }}
                    >
                <header className="header">
                            <div className="logo">
                                <img src={logo} alt="animalia" />
                            </div>
                            <nav>
                                <ul>
                                    <li><a href="/all-animals">All Animals</a></li>
                                    <li><a href="#">Animals</a></li>
                                    <li><a href="#">Fun Facts</a></li>
                                    <li><a href="#">Gallery</a></li>
                                    <li><a href="#">Conservation</a></li>
                                    <li><a href="#">About</a></li>
                                </ul>
                            </nav>
                </header>
                </main>
        <div>
        <nav className="breadcrumb">
                    <Link to="#" className="breadcrumb-link">Home</Link> &gt;
                    <Link to="/all-animals" className="breadcrumb-link">Animals</Link> &gt;
                    <span className="breadcrumb-current">{animal.name}</span>
                </nav>
        </div>
        <div className="animal-details-card">
            {/* <button className="back-button" onClick={onBack}>Back</button> */}
            <img src={`data:${animal.animalImage.type};base64,${animal.animalImage.imageArray}`} />
            <div className="animal-info">
                <h2>{animal.name}</h2>
                <h3><i>{animal.scientificName}</i></h3>
                <p className="animal-description">{animal.description}</p>
                <div className="animal-details">
                    <p><strong>Phylum:</strong> {animal.phylum}</p>
                    <p><strong>Family:</strong> {animal.family}</p>
                    <p><strong>Genus:</strong> {animal.genus}</p>
                    <p><strong>Species:</strong> {animal.species}</p>
                </div>
            </div>
        </div>
        </div>
    );
}

export default AnimalDetails;