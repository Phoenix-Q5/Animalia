import React, { useEffect } from 'react';
import { useKeycloak } from '@react-keycloak/web';
import { hasAdminRole } from './keycloak';

function AdminRoute({ children }) {
  const { keycloak, initialized } = useKeycloak();

  useEffect(() => {
    if (initialized && !keycloak.authenticated) {
      keycloak.login({ redirectUri: window.location.origin + '/add-animal' });
    }
  }, [initialized, keycloak]);

  if (!initialized) {
    return (
      <div className="add-animal-page">
        <div className="add-animal-bg" aria-hidden="true"></div>
        <div className="add-animal-denied">Loading catalog...</div>
      </div>
    );
  }

  if (!keycloak.authenticated) {
    return (
      <div className="add-animal-page">
        <div className="add-animal-bg" aria-hidden="true"></div>
        <div className="add-animal-denied">Redirecting to login...</div>
      </div>
    );
  }

  if (!hasAdminRole(keycloak)) {
    return (
      <div className="add-animal-page">
        <div className="add-animal-bg" aria-hidden="true"></div>
        <div className="add-animal-denied">
          <h1>Access denied</h1>
          <p>The add animal page is available to administrators only.</p>
        </div>
      </div>
    );
  }

  return children;
}

export default AdminRoute;
