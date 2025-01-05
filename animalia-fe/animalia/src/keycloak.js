import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:8081',       
  realm: 'animalia',        
  clientId: 'animalia-client',
});

export default keycloak;
