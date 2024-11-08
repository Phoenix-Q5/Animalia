import React, {useEffect, useState} from 'react';
import logo from './assets/animalia-logo-full.png'
import AnimalCard from './AnimalCard';
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
    const [currentIndex, setCurrentIndex] = useState(0);
    const [fade, setFade] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [allAnimals, setAllAnimals] = useState([]);
    const [animals, setAnimals] = useState([]);

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
        // Fetch all animals from the backend when component mounts
        fetch('/api/v1/animals/getAllAnimals')
            .then(response => response.json())
            .then(data => {
                setAllAnimals(data); // Store all animals
                setAnimals(data); // Set initial display to all animals
                console.log('All Animals Data:', data); // Log the fetched data
            })
            .catch(error => console.error('Error fetching animals:', error));
    }, []);

    const handleSearch = () => {
        if (searchTerm.trim() === '') {
            // If the search term is empty, reset to all animals
            setAnimals(allAnimals);
        } else {
            // Filter animals based on the search term
            const filteredAnimals = allAnimals.filter(animal =>
                animal.name.toLowerCase().includes(searchTerm.toLowerCase())
            );
            setAnimals(filteredAnimals);
            console.log(`Search Results for "${searchTerm}":`, filteredAnimals); // Log the filtered results
        }
    };

    return (
        <div>
        <main className="main-content"
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
                    <img src={logo} alt="animalia"/>
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

            <div className="text-overlay">
                <h1>Explore, Learn, and Gain Insight into the Animal Kingdom!</h1>
            </div>
            <div className="search-section">
                <h2>Find your favorite animal from our amazing list!</h2>
                <div className="search-input">
                <input type="text" placeholder="Find your favorite animal" value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}/>
                <i className="fas fa-search search-icon" onClick={handleSearch} style={{cursor: 'pointer'}}></i>
            </div>
            </div>
        </main>
        <div className="animal-cards-container">
                {animals.map((animal, index) => (
                    
                    <div key={animal.id} className={index % 3 === 0 ? 'big-animal-card' : 'small-animal-card'}>
                        <AnimalCard animal={animal} />
                    </div>
                ))}
            </div>
        </div>
    );
}

export default MainContent;