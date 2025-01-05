import './App.css';
import React, {useState} from 'react';
import AddAnimal from "./AddAnimal";
import { BrowserRouter as Router, Link, Route, Routes, NavLink } from 'react-router-dom';
import Header from "./Header";
import MainContent from "./MainContent";
import AllAnimals from "./AllAnimals";
import { ReactKeycloakProvider } from '@react-keycloak/web';
import keycloak from './keycloak';
import AnimalDetails from './AnimalDetails';

function Layout({ children }) {
  return (
    <>
      <Header />
      <main>
        <MainContent />
        {children}
      </main>
    </>
  );
}

function App() {
  const [selectedAnimal, setSelectedAnimal] = useState(null);

  return (
    <Router>
      <div className="App">
        <Header />
          <Routes>
            <Route path="/" element={<Layout><AllAnimals setSelectedAnimal={setSelectedAnimal} /></Layout>} />
          <Route path="/animal-details/:id" element={<Layout><AnimalDetails selectedAnimal={selectedAnimal} /></Layout>} />
          <Route path="/add-animal" element={<>
            <ReactKeycloakProvider authClient={keycloak}>
              <Header /><AddAnimal />
            </ReactKeycloakProvider>
          </>} />
          </Routes>
      </div>
    </Router>
  );
}

export default App;
