import React, { useState } from 'react';

function AnimalForm() {
    const [animal, setAnimal] = useState({
        breed: '',
        earTag: '',
        name: '',
        description: '',
        age: 0,
        weight: 0,
        image: null
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
        console.log(file);
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

        fetch('http://localhost:8080/api/v1/animals/addCompleteAnimal', {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                console.log('Animal added:', data);
                setAnimal({
                    breed: '',
                    earTag: '',
                    name: '',
                    description: '',
                    age: 0,
                    weight: 0,
                    image: null
                });
            })
            .catch(error => console.error('Error:', error));
    };

    return (
        <form onSubmit={handleSubmit}>
            <div>
                <label htmlFor="breed">Breed:</label>
                <input type="text" id="breed" name="breed" value={animal.breed} onChange={handleChange} />
            </div>
            <div>
                <label htmlFor="earTag">Ear Tag:</label>
                <input type="text" id="earTag" name="earTag" value={animal.earTag} onChange={handleChange} />
            </div>
            <div>
                <label htmlFor="name">Name:</label>
                <input type="text" id="name" name="name" value={animal.name} onChange={handleChange} />
            </div>
            <div>
                <label htmlFor="description">Description:</label>
                <textarea id="description" name="description" value={animal.description} onChange={handleChange} />
            </div>
            <div>
                <label htmlFor="age">Age:</label>
                <input type="number" id="age" name="age" value={animal.age} onChange={handleChange} />
            </div>
            <div>
                <label htmlFor="weight">Weight:</label>
                <input type="number" id="weight" name="weight" value={animal.weight} onChange={handleChange} />
            </div>
            <div>
                <label htmlFor="image">Image:</label>
                <input type="file" id="image" name="image" onChange={handleImageChange} />
            </div>
            <button type="submit">Submit</button>
        </form>
    );
}

export default AnimalForm;