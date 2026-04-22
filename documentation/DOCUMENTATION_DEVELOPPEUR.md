# Documentation interne developpeur - GameSearch

## 1. Objectif

Ce document est destine aux developpeurs qui doivent comprendre, maintenir ou faire evoluer GameSearch.

Il decrit :
- l'organisation du depot ;
- l'architecture backend ;
- l'architecture frontend ;
- les flux principaux ;
- les conventions de developpement ;
- les commandes utiles ;
- les points d'attention techniques.

## 2. Organisation du depot

```text
.
├── gamesearch/              # Backend Spring Boot
├── gamesearch-frontend/     # Frontend React
├── infra/                   # Compose recette/prod et scripts d'exploitation
├── documentation/           # Documentation projet
├── .gitlab-ci.yml           # Pipeline CI/CD principal
└── README.md
```

## 3. Backend

Le backend est une application Spring Boot 3.3 en Java 21.

Emplacement :

```text
gamesearch/
```

Technologies principales :
- Spring Web ;
- Spring Data JPA ;
- Spring Security ;
- Spring Kafka ;
- Spring Validation ;
- Spring Actuator ;
- PostgreSQL en environnement serveur ;
- H2 en execution locale par defaut ;
- JWT pour l'administration ;
- cle API pour l'authentification partenaire prevue ;
- Lombok pour reduire le code repetitif.

## 4. Structure backend

```text
gamesearch/src/main/java/fr/epita/apping/fullstack/gamesearch/
├── config/                  # Security, Kafka, OpenAPI
├── converter/               # Conversion model/domain/response
├── data/
│   ├── loader/              # Donnees initiales
│   ├── model/               # Entites JPA
│   └── repository/          # Repositories Spring Data
├── domain/
│   ├── entity/              # Objets metier internes
│   └── service/             # Logique metier
├── exception/               # Exceptions et handler global
├── kafka/
│   ├── consumer/            # Consumers Kafka
│   ├── dto/                 # Messages Kafka
│   └── producer/            # Producers Kafka
└── presentation/
    ├── api/request/         # DTO d'entree
    ├── api/response/        # DTO de sortie
    └── rest/                # Controllers REST
```

## 5. Couches backend

### Presentation

La couche `presentation/rest` expose les endpoints HTTP.

Controllers principaux :
- `GameResource` : catalogue de jeux ;
- `AuthResource` : connexion administrateur ;
- `PartnerResource` : ingestion partenaire.

Les controllers doivent rester fins :
- lire les parametres ;
- valider les DTO ;
- appeler un service ;
- convertir la reponse si necessaire.

### Domain

La couche `domain/service` contient la logique metier.

Services principaux :
- `GameService` : recherche, detail, creation, modification, suppression, similarite ;
- `PartnerService` : enregistrement partenaire, ingestion, bulk import ;
- `JwtService` : generation et validation de JWT ;
- `ApiKeyService` : generation et hash de cle API.

### Data

La couche `data/model` contient les entites JPA.

Entites principales :
- `GameModel` ;
- `GenreModel` ;
- `PlatformModel` ;
- `TagModel` ;
- `PartnerModel` ;
- `IngestionStatusModel`.

La couche `data/repository` contient les interfaces Spring Data JPA.

### Converter

`GameConverter` convertit :
- `GameModel` vers `GameEntity` ;
- `GameEntity` vers `GameResponse` ;
- `GameEntity` vers `GameDetailResponse`.

Cela evite d'exposer directement les entites JPA dans l'API.

## 6. Modele de donnees

Un jeu possede :
- un identifiant interne UUID ;
- un identifiant externe optionnel fourni par un partenaire ;
- un titre ;
- une annee de sortie ;
- un editeur ;
- une description ;
- une URL de couverture ;
- une note ;
- des genres ;
- des plateformes ;
- des tags ;
- un partenaire associe.

Relations :
- `GameModel` many-to-many `GenreModel` ;
- `GameModel` many-to-many `PlatformModel` ;
- `GameModel` many-to-many `TagModel` ;
- `GameModel` many-to-one `PartnerModel`.

## 7. Flux catalogue

### Liste des jeux

1. Le frontend appelle `GET /games`.
2. `GameResource` construit un `Pageable`.
3. `GameService.searchGames` construit une `Specification`.
4. `GameRepository` execute la requete.
5. Les resultats sont convertis en `GameResponse`.

Filtres supportes :
- titre via `q` ;
- genre ;
- plateforme ;
- annee.

### Detail d'un jeu

1. Le frontend appelle `GET /games/{id}`.
2. `GameService.getGame` charge le jeu.
3. `GameService.getSimilarGames` calcule les jeux similaires.
4. `GameConverter.toDetailResponse` produit la reponse.

La similarite est basee sur :
- genres communs ;
- plateformes communes ;
- meme editeur ;
- annee de sortie proche.

## 8. Flux d'authentification admin

1. Le frontend appelle `POST /auth/login`.
2. Spring Security authentifie l'utilisateur admin en memoire.
3. `JwtService` genere un JWT.
4. Le frontend stocke le token dans `localStorage`.
5. Les appels suivants ajoutent :

```http
Authorization: Bearer <token>
```

Les routes admin utilisent le role `ADMIN`.

## 9. Flux d'ingestion Kafka

### Envoi d'un jeu

1. Le client appelle `POST /partner/games`.
2. `PartnerService.enqueueGame` cree un statut `PENDING`.
3. Le service publie un `GameIngestionMessage` dans Kafka.
4. Le endpoint repond avec le statut initial.

### Traitement Kafka

1. `GameIngestionStatusConsumer` consomme `game-ingestion-topic`.
2. Il valide les champs obligatoires.
3. Il appelle `PartnerService.submitGame`.
4. Le jeu est cree ou mis a jour selon son `externalId`.
5. Le statut d'ingestion passe en `SUCCESS` ou `ERROR`.
6. Un message est publie dans `game-ingestion-status-topic`.

### Suivi du statut

Le frontend ou un partenaire appelle :

```http
GET /partner/ingestions/{gameId}
```

Les statuts possibles sont :
- `PENDING` ;
- `SUCCESS` ;
- `ERROR`.

## 10. Frontend

Le frontend est une application React 18.

Emplacement :

```text
gamesearch-frontend/
```

Technologies principales :
- React ;
- React Router ;
- Axios ;
- Framer Motion ;
- Lucide React ;
- Nginx en production.

## 11. Structure frontend

```text
gamesearch-frontend/src/
├── App.js                   # Router et layout principal
├── index.js                 # Point d'entree React
├── index.css                # Styles globaux
├── setupProxy.js            # Proxy local /api vers backend
├── components/
│   └── GameCard.jsx         # Carte jeu
├── pages/
│   ├── HomePage.jsx         # Catalogue et filtres
│   ├── GameDetailsPage.jsx  # Detail jeu
│   ├── LoginPage.jsx        # Connexion admin
│   └── AdminPage.jsx        # Dashboard admin
└── services/
    └── api.js               # Client Axios
```

## 12. Routes frontend

| Route | Composant | Description |
|---|---|---|
| `/` | `HomePage` | Catalogue public |
| `/games/:id` | `GameDetailsPage` | Detail d'un jeu |
| `/login` | `LoginPage` | Connexion admin |
| `/admin` | `AdminPage` | Dashboard admin protege |

La protection frontend de `/admin` verifie uniquement la presence d'un token local. La vraie securite doit toujours rester cote backend.

## 13. Client API frontend

Le fichier `services/api.js` centralise les appels HTTP.

Services exposes :
- `authService` ;
- `gameService` ;
- `partnerService`.

Le client Axios utilise :

```javascript
process.env.REACT_APP_API_URL || '/api'
```

Un interceptor ajoute automatiquement le JWT si present :

```http
Authorization: Bearer <token>
```

## 14. Ajouter un endpoint backend

Procedure recommandee :

1. Creer ou modifier un DTO dans `presentation/api/request` si l'endpoint recoit un corps JSON.
2. Creer ou modifier un DTO dans `presentation/api/response` si la reponse est nouvelle.
3. Ajouter la methode metier dans un service de `domain/service`.
4. Ajouter la methode REST dans le controller adapte.
5. Ajouter les regles de securite dans `SecurityConfig` si necessaire.
6. Ajouter ou adapter les tests.
7. Documenter l'endpoint dans la documentation d'exploitation.

## 15. Ajouter une fonctionnalite frontend

Procedure recommandee :

1. Ajouter la methode HTTP dans `services/api.js`.
2. Creer ou modifier la page dans `pages/`.
3. Extraire un composant dans `components/` si l'interface devient reutilisable.
4. Ajouter une route dans `App.js` si une nouvelle page est necessaire.
5. Gerer les etats `loading`, `error` et `empty`.
6. Verifier le comportement sur mobile et desktop.

## 16. Commandes developpeur

### Backend

Lancer les tests :

```bash
cd gamesearch
./mvnw test
```

Verifier le format :

```bash
cd gamesearch
./mvnw spotless:check
```

Corriger le format :

```bash
cd gamesearch
./mvnw spotless:apply
```

Build complet :

```bash
cd gamesearch
./mvnw clean verify
```

Lancer le backend :

```bash
cd gamesearch
./mvnw spring-boot:run
```

### Frontend

Installer les dependances :

```bash
cd gamesearch-frontend
npm install
```

Lancer le frontend :

```bash
cd gamesearch-frontend
npm start
```

Build production :

```bash
cd gamesearch-frontend
npm run build
```

Lint :

```bash
cd gamesearch-frontend
npm run lint
```

## 17. Tests

Le projet contient actuellement une structure de tests backend, mais les tests metier sont tres limites.

Fichiers existants :
- `GamesearchApplicationTests` ;
- `GameServiceTest` ;
- `GameResourceTest` ;
- `GameIngestionConsumerTest`.

Priorites conseillees :
- tester `GameService.searchGames` ;
- tester `GameService.getSimilarGames` ;
- tester `PartnerService.enqueueGame` ;
- tester `PartnerService.bulkImport` ;
- tester les droits d'acces de `SecurityConfig` ;
- tester les controllers avec Spring MVC.

## 18. CI/CD

Le pipeline principal est :

```text
.gitlab-ci.yml
```

Stages principaux :
- audit securite ;
- lint ;
- test ;
- build Docker ;
- scan de vulnerabilites ;
- promotion production.

La strategie vise a construire une image une seule fois, puis a promouvoir l'artefact valide vers la production.

## 19. Points d'attention techniques

### API Key partenaire

Le code contient un filtre `ApiKeyAuthenticationFilter`, mais il faut verifier qu'il est bien ajoute a la chaine Spring Security avant de considerer l'authentification partenaire comme operationnelle.

### Ordre des regles de securite

Les matchers Spring Security sont sensibles a l'ordre.

Les routes specifiques comme `/partner/register` doivent etre declarees avant les routes generales comme `/partner/**`.

### Recherche frontend/backend

Le frontend indique une recherche par titre, editeur ou description, mais le backend recherche actuellement uniquement par titre.

Si la recherche doit couvrir plus de champs, modifier `GameSpecification.hasTitle` ou creer une specification plus generale.

### Bulk CSV

Le parsing CSV utilise une separation simple par virgule.

Cela ne supporte pas les champs CSV complexes contenant des virgules echappees ou entre guillemets.

Pour une ingestion robuste, utiliser une bibliotheque CSV dediee.

### Tailwind / styles

Le frontend utilise des directives et classes Tailwind. Verifier que la configuration et les dependances Tailwind sont coherentes avec le mode de build vise.

### React scripts

Verifier la coherence de `react-scripts` dans `package.json` et dans le Dockerfile afin d'eviter des builds non reproductibles.

## 20. Definition of Done conseillee

Avant de merger une modification :

- le backend compile ;
- les tests pertinents passent ;
- le format Java est valide par Spotless ;
- le frontend build correctement si l'interface est touchee ;
- les endpoints nouveaux ou modifies sont documentes ;
- les variables d'environnement nouvelles sont documentees ;
- la documentation utilisateur est mise a jour si le parcours utilisateur change.
