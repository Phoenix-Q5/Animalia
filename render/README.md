# Animalia on Render — starter infrastructure

Sources: supplied docker-compose.yml and publicly accessible raw files from Phoenix-Q5/Animalia (main). **Not deploy-validated**: Docker image builds and Render Blueprint sync have not been executed.

## Verify paths before syncing
- Backend Dockerfile **verified** at `docker/backend.Dockerfile` and Maven `pom.xml` **verified** at repo root. Java 17 and Spring Boot 3.1.7.
- Dockerfile copies `/build/target/animalia.jar`; confirm Maven build produces this exact filename. If not, change Dockerfile or set `finalName` in pom.xml.
- Keycloak realm JSON **verified** at `docker/keycloak/animalia-realm.json` in public repo. The included Keycloak Dockerfile copies this file from your repo; keep it at that location.
- Frontend is NOT in the active Blueprint: Compose references `../animalia-fe/animalia`, which may be outside the referenced repository. Confirm the frontend folder is committed into the monorepo and add it (example below).

## Important integration changes
1. Spring Boot JDBC URLs require **JDBC syntax**. Render `fromDatabase.connectionString` is `postgresql://...`, **not** `jdbc:postgresql://...`. Change Spring configuration to convert `DATABASE_URL` into a JDBC URL, or set `SPRING_DATASOURCE_URL` manually to `jdbc:postgresql://<internal host>:5432/animal`. Set this required value when creating the Blueprint.
2. Redis: repository `application.properties` explicitly reads `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`. These are represented in the Blueprint; confirm Render Key Value requires a password and supply its actual password securely. Remove any default password from properties.
3. Elasticsearch: use its private URL `http://<Render private hostname>:9200` for `ELASTICSEARCH_URIS`; provide the actual hostname via Render dashboard after service creation. Its security is disabled: keep it private.
4. Keycloak: `KC_DB_URL` may need a `jdbc:postgresql://...` URL, not the generated PostgreSQL URL. Set JDBC URL explicitly and supply `KC_DB_USERNAME`, `KC_DB_PASSWORD` based on the Keycloak database. Set this required value when creating the Blueprint.
5. For JWT validation, use the public HTTPS issuer e.g. `https://animalia-keycloak.onrender.com/realms/animalia` and public HTTPS JWK set `https://animalia-keycloak.onrender.com/realms/animalia/protocol/openid-connect/certs`. Set Keycloak's `KC_HOSTNAME` to its exact public HTTPS URL. Actual hostname can vary.
6. Frontend API base URL, CORS origins, OAuth client redirect URLs, and logout URLs need to reflect Render public domains. Frontend SPA may require `/*` rewrite to `/index.html`.
7. **Critical exposed secrets:** `src/main/resources/application.properties` contains hardcoded SMTP, Cloudinary, Redis and database credentials. Rotate them first, remove hardcoded defaults and commit clean configuration. Scrub Git history where feasible; avoid public reuse. Set `CLOUDINARY_URL`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`, and any other credentials through Render environment settings. Do not expose full Actuator endpoints publicly.
8. Jenkins is intentionally excluded: no privileged Docker socket setup on Render.
9. PostgreSQL 14 availability and plan identifiers must be checked against the Render account and region. Upgrade to a supported PostgreSQL version if version 14 cannot be provisioned.
10. Render infrastructure is not equivalent to Docker Compose startup ordering. Services may deploy independently; add retries to DB/Redis/Elasticsearch connections and appropriate health checks.

## Optional frontend entry
Once the frontend path and Dockerfile are confirmed, add this service to the `services` list:

```yaml
  - type: web
    name: animalia-frontend
    runtime: docker
    region: ohio
    plan: 0.5c-512mb
    dockerfilePath: ./<frontend-path>/Dockerfile
    dockerContext: ./<frontend-path>
    envVars:
      - key: PORT
        value: '10000'
```

The frontend Dockerfile must listen on `0.0.0.0:$PORT` (or set `PORT` to the actual listener's fixed port, e.g. `80` for Nginx). For Vite/React, build-time API URL must be provided during image build or injected via runtime config.

## How to deploy
1. Copy `../render.yaml` to repository root and the included Keycloak Dockerfile to `docker/keycloak/Dockerfile`. The realm JSON already exists in the GitHub repository.
2. Fix both JDBC URL mappings and provide Keycloak DB credentials.
3. Commit changes and authorize the GitHub repo in Render.
4. In Render: New > Blueprint > select repo + branch > review resources, costs, and prompted variables > apply.
5. Supply issuer/JWKS and Elasticsearch URL after the domains and internal hostname are provisioned; redeploy services.
6. Validate Blueprint syntax with `render blueprints validate render.yaml` (Render CLI) before applying.

This is a structural conversion, not a verified working deployment. Costs will include several paid always-on services and databases.

## Verified public repository files
- https://raw.githubusercontent.com/Phoenix-Q5/Animalia/main/pom.xml
- https://raw.githubusercontent.com/Phoenix-Q5/Animalia/main/docker/backend.Dockerfile
- https://raw.githubusercontent.com/Phoenix-Q5/Animalia/main/docker/keycloak/animalia-realm.json
- https://raw.githubusercontent.com/Phoenix-Q5/Animalia/main/src/main/resources/application.properties

The current `application.properties` explicitly defines the localhost JWT issuer, SMTP credentials, and a static server port. Remove inline secrets, put `spring.mail.*` settings behind environment placeholders, configure production issuer and CORS, and restrict actuator exposure before deployment.
