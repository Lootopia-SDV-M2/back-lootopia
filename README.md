# Lootopia — Backend API

API REST de la plateforme de chasse au trésor géolocalisée Lootopia. Construite avec **Spring Boot 3.4.1** et **Java 21**.

## Tech Stack

| Technologie | Version |
|-------------|---------|
| Java | 21 |
| Spring Boot | 3.4.1 |
| Spring Data JPA (Hibernate) | — |
| Spring Security | — |
| PostgreSQL (Neon.tech) | — |
| H2 (tests) | — |
| JWT (jjwt) | 0.11.5 |
| SpringDoc OpenAPI (Swagger) | 2.7.0 |
| Lombok | — |
| Maven | 3.9.9 |
| Docker | multi-stage |

## Architecture

```
┌─────────────┐     HTTP :8080     ┌──────────────────┐
│  Frontend    │ ────────────────── │  Spring Boot API  │
│  (Next.js)   │ ← ─ ─ ─ ─ ─ ─ ─ │  (back-lootopia)   │
└─────────────┘                    └────────┬─────────┘
                                            │
                                    ┌───────┴───────┐
                                    │  PostgreSQL    │
                                    │  (Neon.tech)   │
                                    └───────────────┘
```

### Couches

- **Controllers** — Endpoints REST (Auth, Hunt, Participation, Marketplace, Artefact, Notification, Voucher)
- **Services** — Logique métier avec `@Transactional`
- **Repositories** — Accès données via Spring Data JPA
- **Models** — Entités JPA (12 tables)
- **Security** — JWT stateless (HMAC-SHA256), filtre Spring Security

## Prérequis

- Java 21
- Maven 3.9+
- PostgreSQL (ou compte Neon.tech)
- Docker (optionnel)

## Démarrer en local

1. **Cloner le dépôt**

```bash
git clone https://github.com/Lootopia-SDV-M2/back-lootopia.git
cd back-lootopia
```

2. **Configurer les variables d'environnement**

Copier `.env.sample` vers `.env` et renseigner les valeurs :

```env
DB_URL=jdbc:postgresql://localhost:5432/lootopia
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=votre-cle-base64url-256bits
JWT_EXPIRATION=86400000
```

3. **Lancer l'application**

```bash
./mvnw spring-boot:run
```

L'API démarre sur `http://localhost:8080`.

## Docker

```bash
docker build -t lootopia-api .
docker run -p 8080:8080 --env-file .env lootopia-api
```

## Endpoints API

| Préfixe | Description |
|---------|-------------|
| `POST /api/auth/register` | Inscription |
| `POST /api/auth/login` | Connexion |
| `GET /api/hunts` | Chasses publiées |
| `GET /api/hunts/{id}` | Détail d'une chasse |
| `POST /api/hunts` | Créer une chasse (multipart) |
| `PUT /api/hunts/{id}/publish` | Publier une chasse |
| `POST /api/participations/{huntId}/join` | Rejoindre une chasse |
| `PUT /api/participations/{id}/validate-step` | Valider une étape |
| `PUT /api/participations/{id}/abandon` | Abandonner |
| `GET /api/artefacts/mine` | Inventaire |
| `GET /api/marketplace` | Marketplace |
| `POST /api/marketplace/list` | Mettre en vente |
| `POST /api/marketplace/{id}/buy` | Acheter |
| `GET /api/notifications` | Notifications |
| `GET /api/vouchers/mine` | Mes vouchers |
| `POST /api/vouchers/redeem` | Utiliser un voucher (orga.) |

Documentation interactive disponible sur `/swagger-ui/` et `/v3/api-docs/`.

## Structure du projet

```
src/main/java/com/supdevinci/lootopia/
├── LootopiaApplication.java
├── configuration/          # Beans, CORS, Swagger
├── security/               # JWT filter, SecurityConfig
├── controller/             # Endpoints REST
├── service/                # Logique métier
├── model/                  # Entités JPA + enums
├── repository/             # Accès données
└── dto/                    # Request/Response
```

### Base de données (12 tables)

`user`, `hunt`, `step`, `reward`, `artefact`, `participation`, `market_listing`, `bid`, `transaction`, `voucher`, `notification`

## Tests

```bash
./mvnw test
```

Profile H2 in-memory avec `create-drop`. Un seul test de contexte pour l'instant.

## CI/CD

- **Build** : Maven + tests (H2) + JaCoCo
- **Analyse** : SonarCloud avec Quality Gate
- **Déploiement** : Koyeb (backend), Neon.tech (base de données)

## Documentation

- [Documentation technique](../docs/technique/documentation-technique.md)
- [Plan sécurité & conformité](../docs/securite/plan-securite-conformite.md)
- [Guide utilisateur](../docs/guide/guide-utilisateur-partenaire.md)
- [Guide de contribution](CONTRIBUTING.md)
