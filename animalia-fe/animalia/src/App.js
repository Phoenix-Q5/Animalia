import './App.css';
import React, {useState} from 'react';
import AnimalList from './AnimalList';
import AddAnimal from "./AddAnimal";
import { BrowserRouter as Router, Link, Route, Routes, NavLink } from 'react-router-dom';
import Header from "./Header";
import MainContent from "./MainContent";
import AnimalCard from "./AnimalCard";
import AllAnimals from "./AllAnimals";
import AnimalDetails from "./AnimalDetails";

function App() {
  const [selectedAnimal, setSelectedAnimal] = useState(null);
  return (
    <Router>
      <div className="App">
        <Header />
        <main>
          {/* <MainContent /> */}
          <Routes>
            <Route
            path="/all-animals" element={<AllAnimals setSelectedAnimal={setSelectedAnimal} />}
            />
            <Route
            path="/animal-details/:id" element={<AnimalDetails selectedAnimal={selectedAnimal}/>}
            />
            <Route path="/add-animal" element={<AddAnimal />} />
            {/* <Route path="/" element={<AnimalList />} /> */}
          </Routes>

        </main>
      </div>
    </Router>
  );
}

export default App;
