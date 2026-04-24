# 📽️ LA BIBLE DE SOUTENANCE - GameSearch (Dossier Final J-1)

Ce guide est conçu pour être suivi à la lettre. Si vous l'utilisez tel quel, votre présentation sera visuellement professionnelle et techniquement inattaquable.

---

## 🎨 PARTIE 1 : RÉGLAGES VISUELS GLOBAUX (DESIGN SYSTEM)

*Appliquez ces réglages à votre logiciel de présentation (PowerPoint, Canva, Marp).*

- **Fond (Background)** : Couleur unie `Slate 950` (`#020617`).
- **Marges (Safety Zone)** : Laissez un espace vide de **48px** (environ 2cm) sur tous les bords de chaque slide.
- **Police Titre** : **Inter - Extra Bold** (ou **Outfit**). Taille : **48px à 64px**.
- **Police Corps** : **Inter - Regular** (ou **Medium**). Taille : **24px**.
- **Couleurs de Texte** :
  - **Titres** : Blanc pur (`#FFFFFF`).
  - **Sous-titres / Descriptions** : Slate 300 (`#CBD5E1`).
  - **Mots Clés Importants** : Cyan (`#06B6D4`).
- **Composants Visuels** : Utilisez des icônes de la librairie **Lucide** ou **Heroicons** (couleur Cyan).

---

## 📽️ PARTIE 2 : BLUEPRINT SLIDE PAR SLIDE

### 🟦 SLIDE 0 : Titre & Introduction

**[Critère Prof : Identité & Équipe]**

- **Disposition** : Texte centré au milieu du slide.
- **Contenu** :
  - **Titre (64px)** : "GameSearch"
  - **Sous-titre (32px)** : "Industrialisation de l'Ingestion & Exploration de Jeux"
  - **Bas de page** :
    - À gauche : Samuel Dorismond | Darlin | Ninon | Rodrigue
    - À droite : Soutenance de Projet S8 APC — EPITA
- **Design Tip** : Ajoutez une lueur Cyan diffuse (`#06B6D4`) derrière le mot "GameSearch".
- **Script Oral** :

> "Bonjour. Nous sommes ravis de vous présenter GameSearch. Notre projet ne s'arrête pas à un simple catalogue ; c'est une plateforme pensée comme un produit industriel, alliant robustesse, asynchronisme et automatisation totale."

---

### 📋 SLIDE 1 : Sommaire (Plan)

**[Critère Prof : Organisation du discours]**

- **Disposition** : Titre en haut à gauche. Liste numérotée centrée.
- **Espacement** : Laissez **40px** entre chaque point.
- **Contenu** :
  1. Vision & Problématique
  2. Personas & Scénarios d'Usage
  3. Architecture & Puissance de Kafka
  4. Workflow Agile & Équipe
  5. La Forteresse CI/CD (Qualité & Sécurité)
  6. L'Usine de Livraison (Build & Déploiement)
  7. Démonstration Live
  8. Bilan & Perspectives
- **Script Oral** :

> "Voici notre plan. Nous allons d'abord expliquer 'le pourquoi' du projet, avant de plonger dans le 'comment' avec notre cœur technique et notre pipeline de livraison continue."

---

### 🚀 SLIDE 2 : Vision & Problématique

**[Critère Prof : Contexte & Valeur Métier]**

- **Disposition** : Moitié gauche (Gris/Rouge) vs Moitié droite (Cyan/Blanc).
- **Contenu Gauche (Problème)** :
  - Éclatement des données (Raw Data).
  - Ingestion bloquante (Serveur qui sature lors d'imports massifs).
  - UX Lente.
- **Contenu Droite (Solution)** :
  - Centralisation unifiée.
  - **Ingestion Asynchrone** (Kafka).
  - Recherche multicritères instantanée.
- **Script Oral** :

> "Le constat initial était simple : traiter des milliers de jeux en temps réel bloque les API classiques. Notre mission était de créer une solution qui sépare le flux d'ingestion partenaire de la navigation des joueurs, sans compromis sur la vitesse."

---

### 👤 SLIDE 3 : Nos Personas & Parcours Utilisateurs

* **[Critère Prof : Besoins Utilisateurs & Scénarios]**

- **Disposition** : Trois blocs distincts avec "Besoin" et "Parcours".
- **Contenu** :
  1. **Le Joueur (Consommateur)**
     - **Besoin** : Accès instantané à l'information sans latence réseau.
     - **Parcours** : Arrivée sur la Single Page Application (React) ➔ Recherche par tags/nom ➔ Filtrage dynamique en temps réel ➔ Consultation des fiches de jeux.
  2. **L'Administrateur (Gestionnaire)**
     - **Besoin** : Piloter l'ingestion massive et assurer la cohérence du catalogue.
     - **Parcours** : Dashboard d'administration ➔ Monitoring du statut des messages **Kafka** (Pending/Success) ➔ Validation ou modération des entrées.
  3. **Le Partenaire API (Fournisseur)**
     - **Besoin** : Système d'ingestion capable d'absorber des volumes de données imprévisibles.
     - **Parcours** : Envoi de JSON via Endpoint REST ➔ Réception d'un acquittement immédiat (statut 202) ➔ Traitement asynchrone transparent par les workers Kafka.
- **Script Oral** :

> "Voici comment notre architecture sert nos utilisateurs. Le joueur parcourt une interface fluide sans jamais attendre les traitements serveurs. L'administrateur supervise le flux de données via un dashboard dédié. Enfin, notre partenaire API peut nous 'pousser' des milliers de jeux d'un coup grâce au découplage Kafka qui lui répond immédiatement, traitant l'import en tâche de fond."

---

### 🏛️ SLIDE 4 : Architecture en Couches (N-Tiers)

**[Critère Prof : Architecture Modulaire & Clean Code]**

- **Disposition** : Schéma vertical ou en blocs empilés.
- **Points Clés** :
  - **Couche Présentation (REST)** : Gère l'exposition de l'API et les DTO.
  - **Couche Métier (Domain/Service)** : Contient toute la logique décisionnelle.
  - **Couche Accès aux Données (Persistence)** : Gère SQL via JPA/Hibernate.
- **Instruction Design** : Dessinez 3 blocs empilés. Des flèches indiquent que la communication ne va que du haut vers le bas.
- **Script Oral** :

> "Côté technique, nous avons structuré notre Backend suivant une Architecture en Couches (N-Tiers) rigoureuse. Pourquoi ? Pour assurer une séparation nette des responsabilités. La présentation ne connaît pas les détails de la base de données, elle ne parle qu'à la couche métier. Cela rend le code stable et facile à tester."

---

### 🏛️ SLIDE 5 : La Puissance de Kafka (JUSTIFICATION)

**[Critère Prof : Justification technique majeure]**

- **Disposition** : Schéma horizontal Backend -> KAFKA (Grand cercle Violet) -> Consumer.
- **Arguments (En gros, au centre)** :
  - **Découplage Brut** : Le client reçoit '202 Accepted' immédiatement.
  - **Résilience** : Kafka stocke les messages même si le worker tombe.
  - **Backpressure** : Le système s'adapte à la vitesse d'écriture en base.
- **Script Oral** :

> "C'est ici qu'intervient Apache Kafka. Pourquoi Kafka plutôt qu'une simple API synchrone ? Pour l'asynchronisme. Un partenaire peut nous envoyer 50 000 jeux ; notre API accepte le flux instantanément (statut 202), Kafka le stocke, et notre worker le traite au rythme idéal. C'est l'assurance d'un service qui ne sature jamais, peu importe la charge."

---

### 👥 SLIDE 6 : Workflow Agile & Équipe

**[Critère Prof : Méthodologie & Collaboration]**

- **Disposition** : Grille 2x2.
- **Contenu** :
  - **Méthode Scrum/Agile** : Sprints de 1 semaine, Daily standups.
  - **Code Review Stricte** : Aucune fusion sans 1 approbation minimum.
  - **Outils** : GitLab Issues, Kanban, Discord.
  - **Répartition** : Backend (Samuel & Rodrigue), Frontend (Darlin & Ninon).
- **Script Oral** :

> "Une architecture robuste ne vaut rien sans une équipe organisée. Nous avons travaillé en sprints courts, avec une règle de fer : la revue de code. Chaque ligne produite a été scrutée par un autre membre pour garantir la maintenabilité."

---

### 🛡️ SLIDE 7 : La Forteresse CI/CD (Partie 1)

**[Critère Prof : Assurance Qualité & Automatisation]**

- **Disposition** : Liste de 3 blocs haut de gamme avec icônes Cyan.
- **Détails Techniques** :
  - **Audit Automatique** : SAST & Secret Detection (GitLab).
  - **Modernization (ArchUnit)** : Blocage si les règles de séparation des couches sont violées.
  - **Tests Qualitatifs** : 91 tests unitaires, JaCoCo ≥ 70%.
- **Script Oral** :

> "La qualité n'est pas une option. Notre pipeline bloque systématiquement tout code qui ne respecte pas nos standards. Nous avons même intégré ArchUnit qui 'casse' le build si un développeur tente de contourner les règles de notre architecture en couches."

---

### 🚀 SLIDE 8 : L'Usine de Livraison (Partie 2)

**[Critère Prof : Multi-arch, Sécurité Container & Promotion]**

- **Disposition** : Circuit de flux de gauche à droite.
- **Éléments visuels** : Logo Docker + Logo Trivy (🛡️).
- **Points Clés** :
  - **Docker Multi-Arch** : Build pour amd64 (Intel) et arm64 (Cloud/Mac).
  - **Scan Trivy** : Recherche de CVE (Garantie 0 Critical CVE).
  - **Promotion d'Image** : On ne re-build jamais, on retag l'image validée.
- **Script Oral** :

> "Une fois validé, Docker prend le relais. Nous construisons des images multi-arch pour être prêts pour n'importe quel cloud. Trivy scanne chaque image pour garantir 0 vulnérabilité critique avant le déploiement."

---

### 🕹️ SLIDE 9 : Démonstration Live

- **Détails Chronométrés** :
  - 0-30s : Recherche et filtres sur le frontend (Fluidité).
  - 30-60s : Connexion Admin et Ingestion Kafka (Puissance).
  - 60-90s : Preuve visuelle du statut (PENDING -> SUCCESS).
- **Script Oral** :

> "Passons à la démo. Regardez cette recherche fluide. Maintenant, je déclenche un import lourd via Kafka. Le système reste réactif, et le monitoring change de statut en temps réel."

---

### 🏁 SLIDE 10 : Bilan & Perspectives

- **Bilan** : Projet complet, pipeline industrielle, architecture en couches rigoureuse.
- **Futur** : Monitoring Grafana Avancé, Passage Cluster Kubernetes (K8s).
- **Script Oral** :

> "En conclusion, GameSearch est une plateforme robuste. Nous avons implémenté des standards industriels et une CI/CD de bout en bout. Nous sommes prêts pour vos questions."

---

## 📈 3. Schémas de Référence (À intégrer dans vos slides)

### Schéma A : Git Flow

```mermaid
graph LR
    subgraph "Développement"
        F[Feature Branch] -- "MR" --> D[dev]
    end
    subgraph "Recette"
        D -- "Auto-Deploy" --> R[Serveur Recette]
    end
    subgraph "Production"
        D -- "Manual Promote / MR" --> M[main]
        M --> P[Serveur Prod]
    end
    style R fill:#06B6D4,color:#fff
    style P fill:#22C55E,color:#fff
```

### Schéma B : La Pipeline CI/CD (7 Étapes)

```mermaid
graph TD
    A["Audit (SAST/Scanner)"] --> B["Lint (Spotless/ESlint)"]
    B --> C["Test (Maven/JaCoCo)"]
    C --> D["Modernization (ArchUnit)"]
    D --> E["Build (Docker Multi-Arch)"]
    E --> F["Security Scan (Trivy)"]
    F --> G["Promote (Deploy)"]
    style G fill:#22C55E,color:#fff
```

---

## 💡 4. Réponses aux Questions Prévues (Aide-mémoire)

1. **"Pourquoi Kafka ?"** : "Pour absorber les pics de charge et assurer le découplage asynchrone sans ralentir l'utilisateur final."
2. **"Pourquoi une architecture en couches ?"** : "Pour garantir une séparation stricte des responsabilités. Cela simplifie la maintenance et permet de tester chaque couche (Présentation, Métier, Données) de façon isolée."
3. **"Pourquoi Docker Multi-Arch ?"** : "Pour être compatible nativement avec tous les types de serveurs Cloud modernes (ARM64) et classiques (AMD64) sans perte de performance."
