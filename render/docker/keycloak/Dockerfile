FROM quay.io/keycloak/keycloak:26.0.5
COPY docker/keycloak/animalia-realm.json /opt/keycloak/data/import/animalia-realm.json
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start", "--import-realm", "--http-enabled=true", "--http-port=8080"]
