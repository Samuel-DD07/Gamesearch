# Project GameSearch - Road to 100% 🚀

Ce document liste toutes les étapes nécessaires pour atteindre le **Niveau 4 (Excellent)** selon les critères du sujet.

## 1. Fonctionnalités & Backend (MVP)

### Recherche & Consultation (Public)

- [X] **US-G01** : Rechercher un jeu par titre (`GET /games?q=...`)
- [X] **US-G02** : Consulter la fiche détaillée d'un jeu (`GET /games/{id}`)
- [X] **US-G03** : Filtrer les jeux par genre, plateforme, année (`GET /games?q=...&genre=...`)
- [X] **US-G04** : Voir la liste des jeux récents/populaires (Home page)
- [X] **US-G06** : Voir les jeux similaires sur la fiche d'un jeu
- [X] Implémenter la pagination sur les listes de jeux

### Administration (Admin JWT)

- [X] **US-U01** : Système d'authentification (Login/Register pour Admin)
- [X] **POST /games** : Création manuelle d'un jeu (Protégé par JWT)
- [X] **PUT /games/{id}** : Modification d'un jeu (Protégé par JWT)
- [X] **DELETE /games/{id}** : Suppression d'un jeu (Protégé par JWT)

### Ingestion Partenaires (API Key & Kafka)

- [X] **US-P04** : Système d'authentification par `X-API-Key` pour les partenaires
- [X] **POST /partner/games** : Soumission d'un jeu via REST
- [X] **POST /partner/games/bulk** : Import de masse (CSV/JSON)
- [X] **Producteur Kafka** : Envoyer un message dans `game-ingestion-topic` lors d'une soumission REST
- [X] **Consommateur Kafka** : Traiter les messages de `game-ingestion-topic` (`GameIngestionStatusConsumer`)
- [X] **Feedback Kafka** : Publier le statut de l'ingestion dans `game-ingestion-status-topic` (`GameIngestionStatusProducer`)
- [X] **US-P05** : Endpoint pour que le partenaire consulte le statut de son ingestion

---

## 2. Architecture & Qualité (Niveau 4)

- [X] **Architecture Clean** : Respecter strictement le découpage Controller -> Service -> Repository
- [X] **Modèle de données** : Entités `Genre`, `Platform`, `Tag` et relations Many-to-Many fonctionnelles
- [X] **Gestion des erreurs** : `@ControllerAdvice` global (`GlobalExceptionHandler`)
- [X] **Validation** : Contraintes Bean Validation sur les DTOs
- [X] **Documentation API** : Configurer SpringDoc / Swagger UI pour une documentation interactive complète

---

## 3. Frontend (MVP)

- [X] Initialiser le projet React dans `gamesearch-frontend`

- [X] Page d'accueil : Liste des jeux avec recherche et filtrage (`q`, `genre`, `platform`, `year`)
- [X] Page détail : Fiche complète du jeu et recommandations
- [X] Espace Admin : Interface premium pour le CRUD des jeux
- [ ] Interface simplifiée pour simulation de soumission partenaire (Bonus)

---

## 4. Tests & Sécurité

- [X] **Tests Unitaires** : JUnit & Mockito présents
- [X] **Tests d'Intégration** : Testcontainers utilisé pour PostgreSQL et Kafka
- [ ] **Tests E2E** : Créer une collection Postman ou des scénarios reproductibles
- [ ] **Couverture globale** : Atteindre >= 70% de couverture de code (actuellement non mesuré)
- [ ] **Audit Sécurité** : Préparer un rapport d'audit (Auth, Validation, X-API-Key)

---

## 5. DevOps & Industrialisation (Excellent)

- [X] **Docker-compose** : Stack incluant Backend, DB, Kafka, Zookeeper, UI
- [X] **CI Pipeline (GitLab CI)** : `.gitlab-ci.yml` configuré
- [X] **Monitoring** : Spring Actuator activé
- [ ] **README "From Scratch"** : Nettoyer le template générique par un README projet réel
- [ ] **Versioning** : Utiliser des tags Git pour les releases

---

## 💡 Quick Wins Immédiats

1. [ ] Ajouter **JaCoCo** au `pom.xml` pour mesurer la couverture
2. [ ] Remplacer le `README.md` générique par une présentation du projet
3. [X] Configurer la documentation Swagger
4. [X] Aligner le paramètre de recherche frontend (`title` -> `q`)
5. [X] Nettoyage final du code (suppression des commentaires et logs)
4. [X] S'assurer que les logs affichent clairement les flux Kafka
