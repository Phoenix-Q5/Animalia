import './App.css';
import React from 'react';
import AnimalList from './AnimalList';
import AddAnimal from "./AddAnimal";
import { BrowserRouter as Router, Link, Route, Routes, NavLink } from 'react-router-dom';

function App() {

  return (
    <Router>
      <div className="App">
        <header className="App-header">
          <h1>Animalia</h1>
        </header>
        <main>
          <NavLink
            to="/add-animal"
            className={({ isActive }) => (isActive ? "hidden" : "add-animal")}
          >
            Add Animal
          </NavLink>
          <Routes>
            <Route path="/add-animal" element={<AddAnimal />} />
            <Route path="/" element={<AnimalList />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}
// Do something - removed

export default App;
