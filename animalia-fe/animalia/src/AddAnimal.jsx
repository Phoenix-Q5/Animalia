import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useKeycloak } from '@react-keycloak/web';
import axios from 'axios';

function AnimalForm() {
    const { keycloak, initialized } = useKeycloak();
    const [animal, setAnimal] = useState({
        phylum: '',
        family: '',
        name: '',
        description: '',
        genus: '',
        species: '',
        scientificName: '',
        image: null
    });

    if (!initialized) {
        return <div>Loading...</div>;
    }

    if (!keycloak.authenticated) {
        return <div>Please log in to access this page.</div>;
    }

    const api = axios.create({
        baseURL: 'http://localhost:8080',
    });

    api.interceptors.request.use(config => {
        if (keycloak.token) {
            config.headers.Authorization = `Bearer ${keycloak.token}`;
        }
        return config;
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setAnimal(prevState => ({
            ...prevState,
            [name]: value
        }));
    };

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        setAnimal(prevState => ({
            ...prevState,
            image: file
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append('animal', new Blob([JSON.stringify(animal)], { type: "application/json" }));
        formData.append('image', animal.image);

        api.post('/api/v1/animals/admin/addCompleteAnimal', formData)
            .then(response => {
                console.log('Animal added:', response.data);
                setAnimal({
                    phylum: '',
                    family: '',
                    name: '',
                    description: '',
                    genus: '',
                    species: '',
                    scientificName: '',
                    image: null
                });
            })
            .catch(error => console.error('Error:', error));
    };

    return (
        <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="animated-background"
        >
            <form onSubmit={handleSubmit}>
                <div className="add-items">
                    <div className="input-row">
                        <div className="input-text">
                            <input type="text" id="phylum" name="phylum" placeholder="Phylum" value={animal.phylum} onChange={handleChange} />
                        </div>
                        <div className="input-text">
                            <input type="text" id="family" name="family" placeholder="Family" value={animal.family} onChange={handleChange} />
                        </div>
                        <div className="input-text">
                            <input type="text" id="name" name="name" placeholder="Name" value={animal.name} onChange={handleChange} />
                        </div>
                        <div className="input-text">
                            <input type="text" id="scientificName" name="scientificName" placeholder="Scientific Name" value={animal.scientificName} onChange={handleChange} />
                        </div>
                    </div>
                    <div className="input-row">
                        <div className="input-text">
                            <textarea id="description" name="description" placeholder="Description" value={animal.description} onChange={handleChange}></textarea>
                        </div>
                        <div className="input-text">
                            <input type="text" id="genus" name="genus" placeholder="Genus" value={animal.genus} onChange={handleChange} />
                        </div>
                        <div className="input-text">
                            <input type="text" id="species" name="species" placeholder="Species" value={animal.species} onChange={handleChange} />
                        </div>
                    </div>
                    <div className="input-row">
                        <div className="file-input">
                            <label htmlFor="image">Animal Image:</label>
                            <input type="file" id="image" name="image" onChange={handleImageChange} />
                        </div>
                    </div>
                    <button className="button" type="submit">Submit</button>
                    <button className="button" type="button" onClick={() => window.location.href = '/'}>Exit</button>
                </div>
            </form>
        </motion.div>
    );
}

export default AnimalForm;
