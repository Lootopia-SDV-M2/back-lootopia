# Agent Context — back-lootopia

> Fichier de contexte destiné aux agents IA (Codex, Claude Code, OpenCode, etc.) travaillant sur le backend de Lootopia.
> Ce fichier est situé dans le dossier `back-lootopia/` du repo parent non versionné.

---

## 1. Vue d'ensemble

`back-lootopia` est l'**API REST** de la plateforme Lootopia (chasse au trésor géolocalisée). Elle est construite avec :

| Technologie | Version | Rôle |
|-------------|---------|------|
| Java | 21 | Langage |
| Spring Boot | 4.0.0 (pom.xml) / 3.4.1 (README) | Framework |
| Spring Data JPA | — | Accès données |
| Spring Security | — | Sécurité JWT |
| PostgreSQL | — | Base de données de production |
| H2 | — | Base de données de test |
| JJWT | 0.11.5 | Gestion des tokens JWT |
| Lombok | — | Réduction du boilerplate |
| Maven | 3.9+ | Build |
| java-dotenv | 5.2.2 | Chargement des variables d'environnement depuis `.env` |

> **Note importante** : il y a une divergence entre le `pom.xml` qui référence Spring Boot `4.0.0` et le `README.md` qui mentionne `3.4.1`. Le `pom.xml` est la source de vérité pour le build Maven.

L'API expose des endpoints sur `http://localhost:8080` et est consommée par le frontend Next.js situé dans `../front-lootopia`.

---

## 2. Architecture des dossiers

```
back-lootopia/
├── src/
│   ├── main/
│   │   ├── java/com/supdevinci/lootopia/
│   │   │   ├── LootopiaApplication.java
│   │   │   ├── configuration/          # Beans, CORS, Swagger (à compléter)
│   │   │   │   └── AppConfig.java
│   │   │   ├── security/               # Configuration sécurité + filtre JWT
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   ├── service/                # Logique métier
│   │   │   │   └── JwtService.java
│   │   │   ├── model/                  # Entités JPA + enums
│   │   │   │   ├── User.java
│   │   │   │   └── Role.java
│   │   │   └── repository/             # Accès données Spring Data JPA
│   │   │       └── UserRepository.java
│   │   └── resources/
│   │       └── application.properties  # Config Spring (DB, JWT, JPA)
│   └── test/
│       └── java/com/supdevinci/lootopia/
│           └── LootopiaApplicationTests.java
├── pom.xml
├── .env.sample
├── README.md
├── CONTRIBUTING.md
└── mvnw / mvnw.cmd
```

> **État actuel** : le backend reste embryonnaire, mais le socle auth est en place : JWT, configuration sécurité, entité `User`, rôles frontend, DTOs auth, service auth et endpoints `/api/auth/register` / `/api/auth/login`. La plupart des entités/énumérations du domaine restent à créer.

> **Mise à jour J3-03/J3-04** : le MVP inventaire possède maintenant `Artefact`, `Rarity`, `ArtefactCategory`, `ArtefactRepository`, `ArtefactDto`, `ArtefactService` et `GET /api/artefacts/mine`.
> **Mise à jour local** : `GET /api/wallet/me`, `GET /api/hunts` et `GET /api/hunts/mine` existent pour que le frontend tourne sans 403 en local. Les routes Hunt retournent une liste vide tant que les entités Hunt ne sont pas implémentées.

---

## 3. Conventions de code

### 3.1 Général

- **Langue** : code en anglais. Les messages utilisateur peuvent être en français.
- **Package racine** : `com.supdevinci.lootopia`.
- **Lombok autorisé** : utiliser `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@RequiredArgsConstructor`, `@Builder` quand pertinent.
- **Respecter SOLID** : séparer controllers, services, repositories, DTOs.
- **Services transactionnels** : utiliser `@Transactional` sur les méthodes métier qui modifient plusieurs entités.
- **Ne jamais stocker de mot de passe en clair** : utiliser `BCryptPasswordEncoder` (déjà configuré dans `AppConfig`).
- **Authentification stateless JWT** : token lu dans le header `Authorization: Bearer <token>`.

### 3.2 Entités JPA

- Utiliser `jakarta.persistence.*` (pas `javax.persistence`).
- Clés primaires auto-générées : `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)`.
- Nommer explicitement les tables avec `@Table(name = "...")`.
- Utiliser des enums Java annotées `@Enumerated(EnumType.STRING)` pour les statuts/roles.
- Entité `User` implémente déjà `UserDetails` pour Spring Security.

### 3.3 Repositories

- Étendre `JpaRepository<Entity, ID>`.
- Méthodes de recherche par convention de nommage Spring Data (`findByUsername`, `existsByEmail`, etc.).
- Pour les requêtes complexes, utiliser `@Query` JPQL.

### 3.4 Services

- Annoter les services avec `@Service`.
- Injecter les dépendances via constructeur (Lombok `@RequiredArgsConstructor`).
- Méthodes publiques documentées avec Javadoc pour la logique métier complexe.
- Garantir l'atomicité des opérations financières/transactions avec `@Transactional`.

### 3.5 Controllers

- Annoter avec `@RestController`.
- Préfixer les routes par `/api/`.
- Utiliser des DTOs pour les requêtes et réponses (ne pas exposer directement les entités).
- Retourner `ResponseEntity<T>` pour contrôler les codes HTTP.
- Gérer les exceptions via un `@ControllerAdvice` global (à créer).

### 3.6 DTOs

- Placer dans un package `dto`.
- Utiliser des records Java si possible (Java 21).
- Valider les entrées avec `jakarta.validation` (`@NotBlank`, `@Size`, etc.) + `@Valid` dans les controllers.

### 3.7 Sécurité

- `SecurityConfig` configure un filtre JWT et autorise :
  - `/api/auth/**`
  - `/swagger-ui/**`, `/v3/api-docs/**`, `/swagger-resources/**`, `/webjars/**`
  - `/actuator/**`
- Toutes les autres routes nécessitent une authentification.
- Le CORS est configuré pour autoriser toutes les origines (`*`) avec credentials. **À durcir en production.**

---

## 4. Configuration

### Variables d'environnement

Copier `.env.sample` vers `.env` à la racine de `back-lootopia/` :

```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/lootopia
DB_USERNAME=postgres
DB_PASSWORD=root

# JWT
JWT_SECRET=JWT_SECRET
JWT_EXPIRATION=86400000
```

`LootopiaApplication` charge automatiquement le fichier `.env` via `java-dotenv` au démarrage.

### `application.properties`

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}
```

### Environnements backend et base de données

| Service | URL | Usage | Compte |
|---------|-----|-------|--------|
| Swagger backend déployé | https://absolute-deny-lootopia-sdv-24cb1826.koyeb.app/swagger-ui/index.html | Vérifier les endpoints exposés et le contrat API déployé | Accès public si disponible |
| Koyeb | https://app.koyeb.com/ | Hébergement du backend Spring Boot | `lootopia@sam1.eu.org` |
| Neon | https://console.neon.tech/app/org-crimson-bread-48157192/projects | PostgreSQL cloud du projet | `lootopia@sam1.eu.org` |

Règles d'accès :

- Ne jamais écrire les mots de passe, tokens Neon, chaînes de connexion complètes ou secrets JWT dans ce fichier.
- Récupérer les secrets depuis un canal sûr fourni par le propriétaire, un gestionnaire de secrets, un MCP autorisé ou des variables d'environnement locales déjà configurées.
- Si un agent doit inspecter la base distante, privilégier l'accès Neon via MCP/console autorisé. À défaut, travailler avec une base PostgreSQL locale configurée via `.env`.
- Swagger déployé peut servir de référence rapide pour observer l'API actuellement exposée, mais le code local reste la source de vérité pour les modifications.

---

## 5. Entités du domaine (prévues)

Selon le README et le frontend, les entités suivantes sont attendues :

| Entité | Description |
|--------|-------------|
| `User` | Utilisateurs (joueurs, organisateurs, admins) |
| `Hunt` | Chasse au trésor créée par un organisateur |
| `Step` | Étape géolocalisée d'une chasse |
| `Reward` | Récompense physique/numérique liée à une chasse |
| `Artefact` | Objet collectionnable gagné par un joueur |
| `Participation` | Inscription d'un joueur à une chasse |
| `MarketListing` | Annonce de vente d'artefact |
| `Bid` | Enchère sur une annonce |
| `Transaction` | Transaction économique (vente/achat) |
| `Voucher` | Bon d'achat / code de réduction |
| `Notification` | Notification in-app |

**Note** : `User`, `Role`, `Artefact`, `Rarity` et `ArtefactCategory` existent actuellement. Les autres entités doivent être créées.

### Rôles

L'enum `Role` définit :

```java
public enum Role {
    CHERCHEUR,
    ORGANISATEUR,
    ADMIN
}
```

Ces valeurs sont alignées avec le frontend.

---

## 6. Endpoints API attendus

Ces endpoints sont documentés dans le README et/ou consommés par le frontend :

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/auth/register` | Inscription, retourne JWT + utilisateur |
| POST | `/api/auth/login` | Connexion, accepte `{ username: email, password }` |
| GET | `/api/hunts` | Liste des chasses publiées |
| GET | `/api/hunts/{id}` | Détail d'une chasse |
| POST | `/api/hunts` | Créer une chasse (multipart) |
| PUT | `/api/hunts/{id}/publish` | Publier une chasse |
| GET | `/api/hunts/mine` | Chasses de l'organisateur connecté |
| POST | `/api/participations/{huntId}/join` | Rejoindre une chasse |
| PUT | `/api/participations/{id}/validate-step` | Valider une étape |
| PUT | `/api/participations/{id}/abandon` | Abandonner |
| GET | `/api/artefacts/mine` | Inventaire du joueur connecté |
| GET | `/api/wallet/me` | Solde POL du joueur connecté |
| GET | `/api/marketplace` | Marketplace |
| POST | `/api/marketplace/list` | Mettre en vente un artefact |
| POST | `/api/marketplace/{id}/buy` | Acheter un artefact |
| GET | `/api/notifications` | Notifications |
| GET | `/api/vouchers/mine` | Vouchers du joueur |
| POST | `/api/vouchers/redeem` | Utiliser un voucher (organisateur) |

---

## 7. Commandes utiles

```bash
# Compiler et lancer l'application
./mvnw spring-boot:run

# Compiler
./mvnw clean compile

# Tests
./mvnw test

# Package
./mvnw clean package

# Sans wrapper (si Maven installé)
mvn spring-boot:run
```

---

## 8. Points d'attention pour les agents

1. **Alignement des rôles** : le backend utilise maintenant `CHERCHEUR` / `ORGANISATEUR` / `ADMIN`. Si une base existante contient `ROLE_USER` / `ROLE_ADMIN`, prévoir une migration.
2. **Spring Boot version** : le `pom.xml` pointe sur `4.0.0`. Vérifier la compatibilité des dépendances avant d'ajouter des starters.
3. **Sécurité** : ne pas désactiver CSRF / CORS sans réflexion. La config actuelle est permissive pour le développement local.
4. **JWT secret** : doit être une clé Base64 URL-safe d'au moins 256 bits en production.
5. **Tests** : utiliser le profil H2 in-memory (`src/test/resources/application-test.properties`).
6. **Swagger/OpenAPI** : le README mentionne SpringDoc 2.7.0 mais la dépendance n'est pas dans le `pom.xml`. À ajouter si la documentation interactive est requise.
7. **Multipart upload** : la création de chasse envoie un JSON `hunt` + des fichiers `images`. Le backend doit accepter `MultipartFile[]`.
8. **Géolocalisation** : les étapes contiennent latitude/longitude/radius. La validation de proximité (~20m) se fait côté front, mais peut être doublée côté back.
9. **Atomicité** : les opérations marketplace/enchères/transactions doivent être `@Transactional`.
10. **Ne pas modifier `AppConfig`, `SecurityConfig`, `JwtAuthenticationFilter` ou `JwtService` sans comprendre l'impact** sur l'authentification frontend.

---

## 9. Roadmap / fonctionnalités à implémenter

- [x] Configuration Spring Boot + sécurité JWT
- [x] Entité `User` + authentification
- [x] Alignement des rôles avec le frontend
- [x] Controllers auth + DTOs auth + validation
- [x] Profil de test H2 + tests auth
- [x] Entité `Artefact`, enums rareté/catégorie, endpoint `/api/artefacts/mine`
- [x] Endpoints locaux MVP `/api/wallet/me`, `/api/hunts`, `/api/hunts/mine`
- [ ] Entités du domaine restantes : Hunt, Step, Reward, Participation, Marketplace, Voucher, Notification, etc.
- [ ] Repositories et services métier
- [ ] Gestion des chasses (CRUD, publication)
- [ ] Gestion des participations et validation GPS des étapes
- [ ] Marketplace (listings, enchères, transactions)
- [ ] Vouchers et QR codes
- [ ] Notifications
- [ ] Tests unitaires et d'intégration
- [ ] Swagger / SpringDoc

---

## 10. Liens utiles dans le repo

- `../front-lootopia/AGENTS.md` : contexte frontend
- `../AGENTS.md` : contexte global du repo
- `README.md` : documentation humaine du backend
- `CONTRIBUTING.md` : guide de contribution (branches, qualité)
