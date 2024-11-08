import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

function AllAnimals({ setSelectedAnimal }) {
    const [animals, setAnimals] = useState([]);

    useEffect(() => {
        // Fetch all animals from the backend when component mounts
        fetch('/api/v1/animals/getAllAnimals')
            .then(response => response.json())
            .then(data => setAnimals(data))
            .catch(error => console.error('Error fetching animals:', error));
    }, []);

    return (
        <div>
            <h2>All Animals</h2>
            <ul>
                {animals.map(animal => (
                    <li key={animal.id}>
                        <Link
                            to={`/animal-details/${animal.id}`}
                            onClick={() => setSelectedAnimal(animal)}>
                            {animal.name}
                        </Link>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default AllAnimals;
