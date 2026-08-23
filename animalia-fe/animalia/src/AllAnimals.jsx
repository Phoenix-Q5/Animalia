import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

function AllAnimals({ setSelectedAnimal }) {
    const [animals, setAnimals] = useState([]);

    useEffect(() => {
        fetch('/api/v1/animals?page=0&size=48&sort=name,asc')
            .then(response => response.json())
            .then(data => {
                const content = Array.isArray(data?.content) ? data.content : (Array.isArray(data) ? data : []);
                setAnimals(content);
            })
            .catch(error => console.error('Error fetching animals:', error));
    }, []);

    return (
        <div>
          {/* Empty Div */}
        </div>
    );
}

export default AllAnimals;
