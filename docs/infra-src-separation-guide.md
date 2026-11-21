# Guide: Separating Infra and Source into Distinct Directories

Reference layout: Phoenix-Q5/Animalia. Give this file to the Copilot agent in the target project and say:
"Follow docs/infra-src-separation-guide.md to restructure this repo. Show the plan first, then apply it."

## Target layout

```
<repo>/
├── src/                      # backend source (Maven/Gradle standard layout)
├── <frontend>/               # frontend app, self-contained (own Dockerfile, package.json)
├── docker/                   # local/dev infra
│   ├── docker-compose.yml
│   ├── .env
│   ├── backend.Dockerfile (+ .dockerignore)
│   ├── init-*.sql
│   └── keycloak/             # realm exports, other service config
├── render/                   # deployment-platform specific infra (Render, etc.)
│   ├── README.md
│   └── docker/<service>/
├── render.yaml               # kept at repo ROOT (platform requires it there)
├── Dockerfile                # root Dockerfile only if a platform needs it
├── Jenkinsfile               # CI definition stays at root
├── pom.xml / mvnw / .mvn     # build files stay at root
└── docs/
```

Rule of thumb: application code lives in `src/` and the frontend dir; anything that
provisions, builds images for, or runs services lives in `docker/` or `render/`.
Files that tools discover by fixed location (`render.yaml`, `Jenkinsfile`, `pom.xml`,
`.github/`) stay at the root.

## Steps

1. **Inventory.** List infra files: Dockerfiles, compose files, `.env`, SQL init scripts,
   IdP/realm exports, nginx configs, CI/deploy configs, K8s manifests.
2. **Create target dirs** and move with `git mv` so history is preserved
   (`git mv Dockerfile docker/backend.Dockerfile`).
3. **Fix path references** (this is where breakage happens):
   - `docker-compose.yml`: `build.context` and `dockerfile` (context usually becomes `..`,
     dockerfile `docker/backend.Dockerfile`), volume mounts (`./init.sql`, `./keycloak`),
     `env_file`.
   - Dockerfiles: `COPY` paths are relative to the build context, not the Dockerfile.
   - `.dockerignore`: with a Dockerfile in a subdir, name it `<Dockerfile>.dockerignore`
     next to it (BuildKit) or keep `.dockerignore` at the context root.
   - CI (`Jenkinsfile`, GitHub Actions): `docker build -f`, `docker compose -f docker/docker-compose.yml`.
   - Deploy config (`render.yaml`): `dockerfilePath`, `dockerContext`, `rootDir`.
   - Docs/README commands, `.gitignore` entries.
4. **Externalize environment-specific values** (URLs, ports, hostnames, secrets) into
   `.env` / env vars rather than code. In Animalia, the last infra commit touched
   `SecurityConfig.java`, `keycloak.js` and the realm JSON so issuer/redirect URLs
   come from config, and `KC_HTTP_ENABLED`-style booleans are quoted strings in YAML.
5. **Keep the frontend self-contained**: it has its own `Dockerfile` so it can build
   independently of the backend.
6. **Validate**:
   - `docker compose -f docker/docker-compose.yml config` (catches bad paths)
   - `docker compose -f docker/docker-compose.yml up --build`
   - Backend build (`./mvnw -q package`) and frontend build
   - Pipeline lint / dry run; confirm deploy blueprint still resolves paths
7. **Commit in small steps** (moves first, then path fixes) so reviews show pure renames.

## Gotchas learned in Animalia

- `render.yaml` was first placed in `render/` and later moved back to root
  (commit da83ac8): the platform expects it at the repo root.
- Don't commit generated data dirs (e.g. `docker/mysql_keycloak_data` was deleted); add to `.gitignore`.
- `.env` in `docker/` is read relative to the compose file, not your cwd.
- Realm/IdP exports hold environment URLs; review them after moving to a new host.
- Check IDE run configs (`.idea/`) for stale paths.

## Per-project checklist for the agent

- [ ] Propose the before/after tree and wait for approval
- [ ] Move files with `git mv`
- [ ] Update compose/Dockerfile/CI/deploy/README references
- [ ] Run the validation commands above and report results
- [ ] Do not change application logic beyond configuration externalization
