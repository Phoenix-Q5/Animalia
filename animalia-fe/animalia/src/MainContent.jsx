import React, {useEffect, useState} from 'react';
import logo from './assets/animalia-logo-full.png'
import AnimalCard from './AnimalCard';
import AnimalDetails from './AnimalDetails';
import { useKeycloak } from '@react-keycloak/web';
import { hasAdminRole } from './keycloak';

const images = [
    'bluefrog.jpg',
    'chameleon.jpg',
    'clown.jpg',
    'Crocodile.jpg',
    'falcon.jpg',
    'GrizzlyBear.jpg',
    'pitviper.jpg',
    'tiger.jpg',
    'zebra.jpg',
];

function MainContent() {
    const { keycloak, initialized } = useKeycloak();
    const [currentIndex, setCurrentIndex] = useState(0);
    const [fade, setFade] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [allAnimals, setAllAnimals] = useState([]);
    const [animals, setAnimals] = useState([]);
    const [selectedAnimal, setSelectedAnimal] = useState(null);

    useEffect(() => {
        const interval = setInterval(() => {
            setFade(false);
            setTimeout(() => {
                setCurrentIndex((prevIndex) => (prevIndex + 1) % images.length);
                setFade(true);
            }, 1000);
        }, 7000);

        return () => clearInterval(interval);
    }, []);

    useEffect(() => {
        fetch('/api/v1/animals?page=0&size=48&sort=name,asc')
            .then(response => response.json())
            .then(data => {
                const content = Array.isArray(data?.content) ? data.content : (Array.isArray(data) ? data : []);
                setAllAnimals(content);
                setAnimals(content);
            })
            .catch(error => console.error('Error fetching animals:', error));
    }, []);

    const handleSearch = () => {
        const params = new URLSearchParams({
            page: '0',
            size: '48',
            sort: 'name,asc'
        });
        if (searchTerm.trim()) {
            params.set('q', searchTerm.trim());
        }
        fetch(`/api/v1/animals?${params}`)
            .then(response => response.json())
            .then(data => {
                const content = Array.isArray(data?.content) ? data.content : [];
                setAnimals(content);
            })
            .catch(error => console.error('Error searching animals:', error));
    };

    const handleAnimalClick = (animalId) => {
        fetch(`/api/v1/animals/getById/${animalId}`)
            .then(response => response.json())
            .then(data => setSelectedAnimal(data))
            .catch(error => console.error('Error fetching animal details:', error));
    };

    const handleBack = () => {
        setSelectedAnimal(null); // Go back to the list view
    };

    return (
        <div>
            {!selectedAnimal ? (
                <>
                    <main
                        className="main-content"
                        style={{
                            backgroundImage: `linear-gradient(to bottom, rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.1)), url(${images[currentIndex]})`,
                            backgroundSize: 'cover',
                            backgroundPosition: 'center',
                            backgroundRepeat: 'no-repeat',
                            width: '100%',
                            minHeight: '70vh',
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
                                    {initialized && hasAdminRole(keycloak) && (
                                        <li><a href="/add-animal">Add Animal</a></li>
                                    )}
                                    {initialized && !keycloak.authenticated && (
                                        <li>
                                            <a href="#login" onClick={(e) => { e.preventDefault(); keycloak.login(); }}>
                                                Sign in
                                            </a>
                                        </li>
                                    )}
                                    {initialized && keycloak.authenticated && (
                                        <li>
                                            <a href="#logout" onClick={(e) => { e.preventDefault(); keycloak.logout({ redirectUri: window.location.origin }); }}>
                                                Sign out
                                            </a>
                                        </li>
                                    )}
                                </ul>
                            </nav>
                        </header>

                        <div className="text-overlay">
                            <h1>Explore, Learn, and Gain Insight into the Animal Kingdom!</h1>
                        </div>
                        <div className="search-section">
                            <h2>Find your favorite animal from our amazing list!</h2>
                            <div className="search-input">
                                <input
                                    type="text"
                                    placeholder="Find your favorite animal"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                                <i className="fas fa-search search-icon" onClick={handleSearch} style={{ cursor: 'pointer' }}></i>
                            </div>
                        </div>
                    </main>
                    <div className="animal-cards-container">
                        {animals.map((animal, index) => (
                            <div
                                key={animal.id}
                                className={index % 3 === 0 ? 'big-animal-card' : 'small-animal-card'}
                                onClick={() => handleAnimalClick(animal.id)}
                            >
                                <AnimalCard animal={animal} />
                            </div>
                        ))}
                    </div>
                </>
            ) : (
                <AnimalDetails animal={selectedAnimal} onBack={handleBack} />
            )}
        </div>
    );
}

export default MainContent;