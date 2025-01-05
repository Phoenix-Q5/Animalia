import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

function AllAnimals({ setSelectedAnimal }) {
    const [animals, setAnimals] = useState([]);

    useEffect(() => {
        fetch('/api/v1/animals/getAllAnimals')
            .then(response => response.json())
            .then(data => setAnimals(data))
            .catch(error => console.error('Error fetching animals:', error));
    }, []);

    return (
        <div>
          {/* Empty Div */}
        </div>
    );
}

export default AllAnimals;
