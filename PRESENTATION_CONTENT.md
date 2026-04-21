# Support de Soutenance — GameSearch (6 Slides)

Ce document contient le contenu structuré pour vos 6 slides de présentation. Les textes sont conçus pour être concis, percutants et conformes aux attentes du jury EPITA S8 APC.

---

## Slide 1 : Vision Fonctionnelle & Valeur Ajoutée
**Titre** : GameSearch — Au-delà d'un simple catalogue
- **Le Problème** : Éclatement des données de jeux vidéo et difficulté pour les partenaires d'exposer leur catalogue de manière fiable.
- **La Solution** : Une plateforme centralisée avec deux modes d'interaction :
    - **Joueurs** : Recherche multicritères fluide, filtres dynamiques et navigation réactive.
    - **Partenaires** : Ingestion asynchrone haut débit via API REST et Kafka.
- **Objectif** : Robustesse, performance et extensibilité.

---

## Slide 2 : Architecture & Kafka (Le cœur du projet)
**Titre** : Industrialisation & Messaging
- **Architecture Hexagonale (Clean Archi)** : Isolation totale du métier. Facilite les tests et le remplacement de composants infrastructure (ex: changer de DB sans toucher au code).
- **Pourquoi Kafka ?** (Argumentaire attendu par le prof) :
    - **Découplage** : L'ingestion partenaire ne bloque pas les autres services.
    - **Scalabilité** : Gestion de pics de charge lors d'imports massifs.
    - **Auditabilité** : Traçabilité complète du statut d'ingestion via le topic de feedback.
- **Mode KRaft** : Utilisation de la dernière version de Kafka sans Zookeeper (standard industriel actuel).

---

## Slide 3 : Organisation de l'Équipe & Workflow
**Titre** : Collaboration Agile & Git Flow
- **Répartition des Rôles** :
    - **Samuel** : Frontend React & E2E Testing.
    - **Darlin & Ninon** : Backend Logic, Clean Archi & Kafka Integration.
    - **Rodrigue** : CI/CD, Docker & Infrastructure.
- **Workflow Git** : Feature branching systématique, Code Reviews obligatoires, déploiement automatisé en recette via GitLab CI.

---

## Slide 4 : Pipeline CI/CD — La Garantie Qualité
**Titre** : Automatisation & Validation Continue
- **Pipeline en 7 étapes** : Audit (SAST) → Lint → Test → Modernization (ArchUnit) → Build (Multi-Arch) → Security (Trivy) → Promote.
- **Indicateurs clés** :
    - **Couverture JaCoCo ≥ 70%** (Bloquant en CI).
    - **Zéro vulnérabilité critique** (Bloquant en CI).
    - **Conformité Architecturale** vérifiée mécaniquement par ArchUnit.

---

## Slide 5 : Scénario de Démonstration (Live Demo)
**Titre** : Démonstration en conditions réelles
- **Étape 1** : Recherche du jeu "Elden Ring" par un visiteur (Performance et Filtres).
- **Étape 2** : Connexion Admin et accès au Dashboard de supervision.
- **Étape 3** : Ingestion d'un nouveau catalogue via **Kafka UI** et suivi du statut en temps réel (Démonstration de la contrainte technique majeure).
- **Étape 4** : Vérification immédiate de la mise à jour du catalogue sur le frontend.

---

## Slide 6 : Conclusion & Bilan
**Titre** : Bilan & Perspectives
- **Objectifs Atteints** : Pipeline complète, Kafka opérationnel, tests exhaustifs.
- **Ce que nous avons appris** : Gestion de cluster Kafka sans Zookeeper, isolation des couches hexagonales, sécurisation d'API REST hybride (JWT/API Key).
- **Futur** : Ajout d'un système de favoris, monitoring avancé avec Grafana, et passage à un cluster Kubernetes (K8s).

---
*Fin de présentation — Questions & Réponses (5 min)*
