# Liste des Tâches — Projet GameSearch Fullstack
> **Mise à jour le 20 Avril 2026** | Analyse automatique du code source, docs et infrastructure.
> **🔴 SOUTENANCE : Vendredi 24 Avril — Dépôt gel : Jeudi 23 Avril (évaluation code à J-1)**

---

## 🔴 Priorité Maximale — Soutenance dans 5 jours

### Critères d'évaluation du Professeur (email `conversation_avec_le_prof_2.txt`)

| Critère | Statut | Priorité |
|---|---|---|
| Documentation d'exploitation (déploiement, endpoints) | ⚠️ Partielle | 🔴 Haute |
| Documentation utilisateur (par profil) | ❌ Absente | 🔴 Haute |
| Documentation interne développeurs | ⚠️ Partielle | 🟡 Moyenne |
| Tests unitaires avec contenu réel | ✅ Complétés | ✅ OK |
| Tests ArchUnit (architecture hexagonale) | ✅ Complétés | ✅ OK |
| Tests End-to-End | ✅ Terminés (100% pass) | ✅ OK |
| Couverture JaCoCo ≥ 70% (objectif final 80%) | ✅ Enforced | ⚠️ À augmenter |
| Pipeline CI/CD documentée avec artefacts | ✅ Présente | ✅ OK |
| Rapport qualité / CVE Trivy | ✅ Pipeline OK | ✅ OK |

---

## 1. Backend (Spring Boot) — *Darlin & Ninon*

### ✅ Implémenté

- [X] Initialisation de la structure du projet
- [X] **Modèle de données (JPA)**
  - [X] Entités `Game`, `Genre`, `Platform`, `Tag`, `Partner`, `IngestionStatus`
  - [X] Relations Many-to-Many et cascade
- [X] **Couche Service & API (Lecture)**
  - [X] Logique de recherche (JPA Specifications)
  - [X] `GET /games` (Paginé + filtres)
  - [X] `GET /games/{id}` (Détails)
- [X] **Kafka Ingestion** *(contrairement à la todo précédente, c'est FAIT)*
  - [X] Configuration Kafka KRaft (sans Zookeeper)
  - [X] Consumer `game-ingestion-topic` (`GameIngestionStatusConsumer` avec `@KafkaListener`)
  - [X] Producer `game-ingestion-status-topic` (`GameIngestionStatusProducer`)
- [X] **Sécurité & Administration**
  - [X] Authentification par API Key pour les partenaires (`ApiKeyAuthenticationFilter`)
  - [X] Authentification JWT pour les admins (`JwtAuthenticationFilter`)
  - [X] Gestion globale des exceptions (`GlobalHandlerException`)
  - [X] Endpoints : `POST /auth/login`, `POST /partners/register`, `POST /partners/ingest`
- [X] Métriques Prometheus (`/actuator/prometheus`)
- [X] Documentation OpenAPI (Swagger)

### ❌ À faire — Tests (CRITIQUE pour soutenance)

### ✅ Tests unitaires — Complétés

- [X] **Tests unitaires — Contenu réel** *(9 classes de tests, ~60 cas de test)*
  - [X] `ApiKeyServiceTest` — génération de clés (unicité, format, prefix), hachage SHA-256
  - [X] `JwtServiceTest` — génération/validation de tokens, username extraction, token expiré
  - [X] `GameServiceTest` — recherche, getGame, getSimilarGames, CRUD complet, getRecent/Popular
  - [X] `PartnerServiceTest` — register (hash/plainkey), submitGame (new/update), enqueueGame, bulkImport JSON/CSV
  - [X] `GameConverterTest` — toEntity (champs scalaires, collections, null partner), toResponse, toDetailResponse
  - [X] `GameIngestionConsumerTest` — happy path, validation champs manquants (4 cas), erreur, résolution partenaire
  - [X] `GameResourceTest` — endpoints GET/POST/DELETE avec @WebMvcTest (200, 201, 204, 400, 401, 404)
  - [X] `GlobalHandlerExceptionTest` — GameNotFound→404, ValidationError→400, TypeMismatch→400, Generic→500
  - [X] `CleanArchitectureTest` — ArchUnit : dépendances couches, package naming, isolation domain/data
- [X] **JaCoCo seuil relevé à 70%** dans `pom.xml`

---

## 2. Frontend (React) — *Samuel*

### ✅ Implémenté

- [X] **Initialisation** — Vite + React + React Router
- [X] **UI/UX (Complet au-delà du MVP)**
  - [X] `HomePage.jsx` — Barre de recherche, filtres dynamiques, grille de jeux
  - [X] `GameDetailsPage.jsx` — Fiche de détails d'un jeu
  - [X] `AdminPage.jsx` — Interface d'administration
  - [X] `LoginPage.jsx` — Page de connexion sécurisée
  - [X] `GameCard.jsx` — Composant carte de jeu réutilisable
- [X] **Intégration API**
  - [X] Service `api.js` — consommation des APIs Backend + gestion JWT
  - [X] Routes protégées (`ProtectedRoute`)
  - [X] Proxy Nginx (`nginx.conf`) — élimination des erreurs CORS

### ⚠️ À améliorer

- [X] **Tests Frontend** *(tests unitaire et d'intégration via Jest/RTL)*
  - [X] Tests unitaires des composants avec React Testing Library
- [X] **Responsive mobile** — vérifié (utilisations de classes Tailwind sm/md/lg)
- [ ] **Gestion des erreurs UI** — améliorer les messages d'erreur (souvent console.log uniquement)

---

## 3. DevOps & Docker — *Rodrigue*

### ✅ Implémenté

- [X] **Containerisation**
  - [X] `docker-compose.yml` local (Kafka KRaft, Postgres, Backend)
  - [X] Dockerfile Backend multi-stage (Maven → JRE Alpine, user non-root)
  - [X] Dockerfile Frontend multi-stage (Node → Nginx)
  - [X] Environnements PRD (`infra/prd/`) et Recette (`infra/recette/`)
  - [X] Traefik + CrowdSec + Rate Limiting
  - [X] Watchtower (auto-update via label `watchtower.enable`)
  - [X] Adminer (accès DB via HTTPS)
  - [X] Monitoring : Prometheus scraping + Loki/Promtail

- [X] **CI/CD GitLab (7 stages)**
  - [X] `audit` — SAST, Secret Detection, Dependency Scanning
  - [X] `lint` — Spotless (backend), ESLint (frontend)
  - [X] `test` — Maven `clean verify` + rapport JaCoCo
  - [X] `modernization` — ArchUnit (`mvn test -Dtest="*Arch*"`)
  - [X] `build` — Docker Multi-Arch (amd64 + arm64) → registre privé
  - [X] `security` — Scan Trivy (HIGH/CRITICAL bloquant)
  - [X] `promote` — Promotion manuelle `dev-latest` → `prod-latest` sur `main`

### ⚠️ À vérifier

- [X] **Pipeline ArchUnit** — le stage `modernization` fonctionne (CleanArchitectureTest vérifie les règles)
- [X] **Watchtower label** — vérifié dans `infra/recette/docker-compose.yml` (com.centurylinklabs.watchtower.enable=true)

---

## 4. Documentation — *Tous*

### ✅ Existante

- [X] `DEVOPS.md` — Architecture et philosophie DevOps
- [X] `gitlab_ci_workflow.md` — Stratégie CI/CD "Build Once, Deploy Anywhere"
- [X] `DEVELOPER_WORKFLOW.md` — Cycle de développement feature → recette → prod
- [X] `server_implementation.md` — Infrastructure, réseaux, sécurité
- [X] `Document de conception.pdf` — Design document (Livrable 1, ✅ rendu)

### ❌ Manquante (critère d'évaluation prof !)

- [ ] **Documentation d'exploitation** *(compléter le README.md)*
  - [X] Guide des tests complet (`TESTING.md`)
  - [ ] Comment démarrer le projet localement (commandes `docker-compose up`)
  - [ ] Liste complète des endpoints API accessibles
  - [ ] Variables d'environnement requises
  - [ ] Credentials par défaut pour les environnements de test
- [X] **Documentation utilisateur** *(succincte — 1 page par profil)*
  - [X] Profil "Visiteur/Joueur" — Comment rechercher et filtrer des jeux
  - [X] Profil "Partenaire" — Comment s'inscrire et ingérer des jeux via l'API
  - [X] Profil "Administrateur" — Comment accéder à l'interface admin et gérer les jeux
- [ ] **Documentation interne développeurs** *(architecture hexagonale)*
  - [ ] Schéma des couches : `presentation` → `domain` → `data`
  - [ ] Flux Kafka : ingestion partenaire → consumer → status

---

## 5. Préparation Soutenance (Vendredi 24 Avril — 20 min)

- [ ] **Slide 1 : Présentation fonctionnelle** (1 slide max)
  - [ ] Cas d'usage : joueur cherche un jeu, partenaire ingère un catalogue
- [ ] **Slide 2 : Architecture technique argumentée**
  - [ ] Schéma des composants (Frontend → Backend → Kafka → PostgreSQL)
  - [ ] ⚡ **Argumentation de Kafka** (passage OBLIGATOIRE selon le prof)
    - Pourquoi Kafka ? Découplage, async, scalabilité ingestion partenaire
- [ ] **Slide 3 : Organisation de l'équipe**
  - [ ] Samuel → Frontend | Darlin & Ninon → Backend | Rodrigue → DevOps
  - [ ] Workflow Git (feature branches → dev → main)
- [ ] **Slide 4 : Pipeline CI/CD**
  - [ ] Capture d'écran GitLab des 7 stages
  - [ ] Artefacts produits : images Docker, rapport JaCoCo, rapport Trivy CVE
- [ ] **Slide 5 : Démonstration du produit**
  - [ ] Scénario préparé : recherche de jeu → détails → ingestion partenaire via Kafka
- [ ] **Slide 6 : Conclusion & améliorations futures**
  - [ ] Seuil de couverture tests à augmenter, tests E2E, monitoring Grafana

---

## 6. Jalons (Milestones)

- [X] **26 Février :** Design Document (Livrable 1) ✅
- [X] **27 Mars (MVP) :** Dépôt fonctionnel, README, MVP, BD enrichie ✅
- [ ] **23 Avril (Gel du dépôt) :** Le prof évalue votre code ce jour-là ⚠️ J-4
- [ ] **24 Avril (Soutenance finale) :** Présentation 20 min + 5 min Q&A 🔴 J-5
