# Liste des Tâches — Projet GameSearch Fullstack
> **Mise à jour le 23 Avril 2026** | Audit final avant gel du dépôt (Soutenance J-1).
> **🔴 SOUTENANCE : Vendredi 24 Avril — Dépôt gel : Jeudi 23 Avril (évaluation code à J-1)**

---

## 🔴 Priorité Maximale — Soutenance dans 5 jours

### Critères d'évaluation du Professeur (email `conversation_avec_le_prof_2.txt`)

| Critère | Statut | Priorité |
|---|---|---|
| Documentation d'exploitation (déploiement, endpoints) | ✅ Complétée | ✅ OK |
| Documentation utilisateur (par profil) | ✅ Complétée | ✅ OK |
| Documentation interne développeurs | ✅ Complétée | ✅ OK |
| Tests unitaires avec contenu réel | ✅ Complétés | ✅ OK |
| Tests ArchUnit (architecture En Couches (N-Tiers)) | ✅ Complétés | ✅ OK |
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

- [X] **Documentation d'exploitation** (voir `DOCUMENTATION_EXPLOITATION.md`)
  - [X] Guide des tests complet (`TESTING.md`)
  - [X] Comment démarrer le projet localement
  - [X] Liste complète des endpoints API accessibles
  - [X] Variables d'environnement requises
  - [X] Credentials par défaut pour les environnements de test
- [X] **Documentation utilisateur** (voir `DOCUMENTATION_UTILISATEUR.md`)
  - [X] Profil "Visiteur/Joueur"
  - [X] Profil "Partenaire"
  - [X] Profil "Administrateur"
- [X] **Documentation interne développeurs** (voir `DOCUMENTATION_DEVELOPPEUR.md`)
  - [X] Schémas d'architecture et flux
  - [X] Organisation des couches et flux Kafka

---

## 5. Préparation Soutenance (Vendredi 24 Avril — 20 min)

- [X] **Slide 1 : Présentation fonctionnelle** (Content Ready)
  - [X] Cas d'usage : joueur cherche un jeu, partenaire ingère un catalogue
- [X] **Slide 2 : Architecture technique argumentée** (Content Ready)
  - [X] Schéma des composants (Frontend → Backend → Kafka → PostgreSQL)
  - [X] ⚡ **Argumentation de Kafka** (passage OBLIGATOIRE selon le prof)
- [X] **Slide 3 : Organisation de l'équipe** (Content Ready)
  - [X] Samuel → Frontend | Darlin & Ninon → Backend | Rodrigue → DevOps
- [X] **Slide 4 : Pipeline CI/CD** (Content Ready)
  - [X] Capture d'écran GitLab et Artefacts
- [X] **Slide 5 : Démonstration du produit** (Scenario Ready)
  - [X] Scénario : recherche -> détails -> ingestion Kafka
- [X] **Slide 6 : Conclusion & améliorations futures** (Content Ready)

---

## 6. Jalons (Milestones)

- [X] **26 Février :** Design Document (Livrable 1) ✅
- [X] **27 Mars (MVP) :** Dépôt fonctionnel, README, MVP, BD enrichie ✅
- [X] **23 Avril (Gel du dépôt) :** Dépôt finalisé et documenté ✅
- [ ] **24 Avril (Soutenance finale) :** Présentation 20 min + 5 min Q&A 🔴 DEMAIN
