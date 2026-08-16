import './App.css';
import React, {useState} from 'react';
import AddAnimal from "./AddAnimal";
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from "./Header";
import MainContent from "./MainContent";
import AllAnimals from "./AllAnimals";
import { ReactKeycloakProvider } from '@react-keycloak/web';
import keycloak from './keycloak';
import AnimalDetails from './AnimalDetails';
import AdminRoute from './AdminRoute';

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
          <Header />
            <Routes>
              <Route path="/" element={<Layout><AllAnimals setSelectedAnimal={setSelectedAnimal} /></Layout>} />
            <Route path="/animal-details/:id" element={<Layout><AnimalDetails selectedAnimal={selectedAnimal} /></Layout>} />
            <Route path="/add-animal" element={<AdminRoute><AddAnimal /></AdminRoute>} />
            </Routes>
        </div>
      </Router>
    </ReactKeycloakProvider>
  );
}

export default App;
