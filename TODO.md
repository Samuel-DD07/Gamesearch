# Liste des tâches - Projet GameSearch Fullstack

<<<<<<< HEAD
=======
* 

>>>>>>> 34dd8d463ca2e8a82a8990771d31e71b2064d270
## 1. Backend (Spring Boot) - *Porté par Darlin & Ninon*

- [X] Initialisation de la structure du projet
- [X] **Modèle de données (JPA)**
<<<<<<< HEAD
  - [X] Entités Game, Genre, Platform, Tag, Partner
  - [X] Relations Many-to-Many et cascade
- [X] **Couche Service & API (Lecture)**
  - [X] Logique de recherche (JPA Specifications)
  - [X] GET /games (Paginé + filtres)
  - [X] GET /games/{id} (Détails)

- [/] **Kafka Ingestion (Point Critique)**
  - [X] Configuration Kafka sans Zookeeper (KRaft mode)
  - [ ] Implémentation du Consumer game-ingestion-topic
  - [ ] Implémentation du Producer game-ingestion-status-topic
- [/] **Sécurité & Administration**
  - [X] Configuration de base (Accès public /games)
=======
  - [X] Entités `Game`, `Genre`, `Platform`, `Tag`, `Partner`
  - [X] Relations Many-to-Many et cascade
- [X] **Couche Service & API (Lecture)**
  - [X] Logique de recherche (JPA Specifications)
  - [X] `GET /games` (Paginé + filtres)
  - [X] `GET /games/{id}` (Détails)

- [/] **Kafka Ingestion (Point Critique)**
  - [X] Configuration Kafka **sans Zookeeper** (KRaft mode)
  - [ ] Implémentation du Consumer `game-ingestion-topic`
  - [ ] Implémentation du Producer `game-ingestion-status-topic`
- [/] **Sécurité & Administration**
  - [X] Configuration de base (Accès public `/games`)
>>>>>>> 34dd8d463ca2e8a82a8990771d31e71b2064d270
  - [ ] Authentification par API Key pour les partenaires
  - [ ] Gestion globale des exceptions

## 2. Frontend (React) - *Porté par Samuel*

- [ ] **Initialisation du projet**
  - [ ] Setup Vite + React
- [ ] **UI/UX (MVP)**
  - [ ] Barre de recherche et filtres dynamiques
  - [ ] Grille de jeux et fiches de détails
- [ ] **Intégration**
  - [ ] Consommation des APIs Backend

## 3. DevOps & Docker - *Porté par Trusted*

- [/] **Containerisation**
<<<<<<< HEAD
  - [X] docker-compose.yml (Kafka KRaft, Postgres, Backend)
=======
  - [X] `docker-compose.yml` (Kafka KRaft, Postgres, Backend)
>>>>>>> 34dd8d463ca2e8a82a8990771d31e71b2064d270
  - [X] Dockerfiles optimisés
  - [ ] Frontend integration

- [ ] **CI/CD (GitLab)**
  - [ ] Pipeline de build et push d'images
  - [ ] Tests et Couverture (**Cible > 70%**) - *Actuellement quelques tests existent*

## 4. Jalons (Milestones)

- [X] **26 Février :** Design Document (Livrable 1)
- [ ] **27 Mars (MVP) :** **Ce Vendredi !** (Dépôt fonctionnel, README, MVP, BD enrichie).
- [ ] **24 Avril (Final) :** Soutenance finale.

## 5. Tâches Prioritaires pour Vendredi

- [X] **Enrichissement de la Base de Données** (Ajouter un catalogue de jeux consistant)
- [X] **Fonctionnalités MVP validées** (Recherche, Liste, Détails)
