import './App.css';
import React from 'react';
import AddAnimal from "./AddAnimal";
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import HomePage from "./HomePage";
import { ReactKeycloakProvider } from '@react-keycloak/web';
import keycloak from './keycloak';
import AnimalDetails from './AnimalDetails';
import AdminRoute from './AdminRoute';

function App() {
  return (
    <ReactKeycloakProvider
      authClient={keycloak}
      initOptions={{
        onLoad: 'check-sso',
        pkceMethod: 'S256',
        checkLoginIframe: false
      }}
    >
      <Router>
        <div className="App">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/animal-details/:id" element={<AnimalDetails />} />
            <Route path="/add-animal" element={<AdminRoute><AddAnimal /></AdminRoute>} />
            <Route path="/edit-animal/:id" element={<AdminRoute><AddAnimal /></AdminRoute>} />
          </Routes>
        </div>
      </Router>
    </ReactKeycloakProvider>
  );
}

export default App;
