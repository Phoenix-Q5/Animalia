import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:8081',       
  realm: 'animalia',        
  clientId: 'animalia-dev'
});

export const hasAdminRole = (authClient) =>
  Boolean(authClient?.authenticated) &&
  (authClient.hasRealmRole('admin') ||
    authClient.hasRealmRole('ADMIN') ||
    authClient.hasResourceRole('admin', 'animalia-dev') ||
    authClient.hasResourceRole('ADMIN', 'animalia-dev'));

export default keycloak;
