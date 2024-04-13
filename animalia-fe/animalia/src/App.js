import './App.css';
import React from 'react';
import AnimalList from './AnimalList';
import AddAnimal from "./AddAnimal";
import { BrowserRouter as Router, Link, Route, Routes } from 'react-router-dom';

function App() {
  return (
    <Router>
      <div className="App">
          <header className="App-header">
              <h1>Animalia</h1>
          </header>
          <main>
                  <Link className='add-animal' to="/add-animal">Add Animal</Link>
                  <Routes>
                      <Route path="/add-animal" element={<AddAnimal/>} />
                      <Route path="/" element={<AnimalList />} />
                  </Routes>
              </main>
      </div>
      </Router>
  );
}

export default App;
