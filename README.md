# 🎮 GameSearch — Moteur de Recherche de Jeux Vidéo

[![CI/CD Pipeline](https://img.shields.io/badge/CI%2FCD-GitLab-orange.svg)](https://gitlab.basteproductions.fr/epita_dev_1/fullstack)
[![Backend Tests](https://img.shields.io/badge/Backend%20Tests-91%20Passed-green.svg)]()
[![Code Coverage](https://img.shields.io/badge/Coverage-70%25-brightgreen.svg)]()
[![Frontend](https://img.shields.io/badge/Frontend-React%2018-blue.svg)]()
[![Kafka](https://img.shields.io/badge/Messaging-Kafka%20KRaft-black.svg)]()

> **GameSearch** est une plateforme fullstack de référencement de jeux vidéo conçue pour une haute performance et une scalabilité horizontale. Ce projet met en œuvre une **Architecture En Couches (N-Tiers) (Clean Architecture)** sur le backend et une gestion asynchrone des flux partenaires via **Apache Kafka**.

---

## 🏗️ Architecture Technique

Le projet repose sur une pile technologique moderne et robuste :
- **Backend** : Java 21, Spring Boot 3.3.x, JPA / PostgreSQL.
- **Messaging** : Cluster Apache Kafka en mode **KRaft** (sans Zookeeper).
- **Frontend** : React 18, React Router, TailwindCSS.
- **Industrialisation** : Docker, Traefik (Edge Router), CrowdSec (Sécurité), GitLab CI/CD.

Pour plus de détails sur les choix de conception, consultez la [Documentation Architecturale](ARCHITECTURE.md).

---

## 🚀 Guide de Démarrage (Déploiement Local)

Le déploiement est entièrement conteneurisé. Assurez-vous d'avoir Docker et Docker Compose installés.

### 1. Clonage et Préparation
```bash
git clone https://gitlab.basteproductions.fr/epita_dev_1/fullstack.git
cd fullstack
```

### 2. Lancement de la Stack
Vous pouvez lancer l'intégralité de la solution (Base de données, Kafka, Backend, Frontend) avec une seule commande :
```bash
cd gamesearch
docker-compose up --build -d
```
*Note : Le backend attend que la base de données soit saine (healthcheck) avant de démarrer.*

### 3. Accès aux Services
Une fois la stack levée, les services sont accessibles aux adresses suivantes :

| Service | URL / Port | Description |
|---|---|---|
| **Frontend** | [http://localhost:3000](http://localhost:3000) | Interface utilisateur |
| **Backend API** | [http://localhost:8081](http://localhost:8081) | Point d'entrée REST |
| **Swagger UI** | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | Documentation interactive |
| **Kafka UI** | [http://localhost:8090](http://localhost:8090) | Supervision des topics |
| **PostgreSQL** | `localhost:5433` | Stockage persistant |

---

## 🔑 Identifiants & Configuration

### Authentification par défaut
| Profil | Utilisateur | Mot de passe |
|---|---|---|
| **Administrateur** | `admin` | `admin123` |
| **Base de données** | `gamesearch_user` | `ChangeMe123!` |

### Variables d'environnement prioritaires
Ces variables sont configurées dans le `docker-compose.yml` mais peuvent être surchargées dans un fichier `.env` :
- `SPRING_PROFILES_ACTIVE=prod` : Active les optimisations de production.
- `JWT_SECRET` : Clé de signature des tokens d'administration.
- `ADMIN_USERNAME` / `ADMIN_PASSWORD` : Identifiants du compte maître.

---

## 📖 Utilisation de l'API Partner (Ingestion)

Les partenaires peuvent soumettre des catalogues de jeux via deux méthodes :

1.  **REST API** : `POST /partner/games` avec un header `X-API-Key`.
2.  **Kafka** : Publication dans le topic `game-ingestion-topic`.

Le statut de l'ingestion est publié de manière asynchrone dans `game-ingestion-status-topic`.

---

## 🛠️ Développement & Tests

### Backend
Exécuter les tests unitaires, d'intégration et ArchUnit :
```bash
cd gamesearch
./mvnw clean verify
```

### Frontend
Linter et tester les composants :
```bash
cd gamesearch-frontend
npm run lint
npm test
```

Pour plus de détails sur les protocoles de tests, voir [TESTING.md](TESTING.md).

---

## 👥 Équipe Projet
- **Samuel Dorismond** : Frontend & UI Engine.
- **Rodrigue Baste** : Infrastructure, CI/CD & DevOps.
- **Darlin** : Backend, Core Architecture & Business Logic.
- **Ninon** : Coordination, Documentation & Transversalité.

---
*Projet réalisé dans le cadre du S8 APC — EPITA 2026.*
