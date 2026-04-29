# Feedback Evaluation - MasterGame

Ce document synthétise les retours d'évaluation pour le groupe **MasterGame** (Équipe 17) basés sur la grille d'évaluation du projet Gamesearch.

## 📊 Résultats Détaillés (MasterGame - Équipe 17)

### 📄 Document de Conception
**Note : 15 / 25** (équivalent à **12 / 20**)
*   **Motivation & Business** : 3 / 4
*   **Backlog & US** : 5 / 8
*   **Stratégie Sprints** : **0 / 4** ⚠️ (Section entièrement manquante)
*   **Architecture Logicielle** : 3 / 4
*   **Plateforme DevOps** : 4 / 5

### 💻 Développement (Code)
**Note brute : 63 / 70**
**Note de Code (sur 40) : 36 / 40** (Palier 4 atteint)

---

## 🧮 Calcul de la Note Finale (Scolarité)
Selon la formule : **(Note de Conception + Note de Code + 20) / 5**

| Élément | Score (sur 40) | Contribution |
| :--- | :---: | :---: |
| Note de Conception | **24** | 24 / 40 |
| Note de Code | **36** | 36 / 40 |
| Bonus Fixe | **20** | 20 / 40 |
| **TOTAL** | **80 / 100** | **16.0 / 20** |

---

## ⚖️ Nouvelle Pondération (Phase Finale)
L'enseignant a communiqué une nouvelle répartition des points :
*   **Conception (20%)** : 12/20 → **2.4 pts**
*   **Projet (60%)** : 18/20 → **10.8 pts**
*   **Présentation (20%)** : À venir... (Ex: 18/20 → 3.6 pts)
*   **Note Estimée** : **16.8 / 20** (basé sur une présentation à 18/20)

---

## 🛠️ Analyse Technique (SonarQube)

### Backend (gamesearch)
- **Quality Gate** : ❌ **ERROR**
- **Fiabilité (Reliability)** : **D** (2 bugs critiques détectés)
- **Sécurité** : **A** (0 vulnérabilité)
- **Maintenabilité** : **A**
- **Duplication** : 4.9% (juste sous le seuil de 5%)
- **Points critiques** :
    - **Self-invocation @Transactional** (`PartnerService:154`) : Un bug fonctionnel où la transaction n'est pas démarrée car la méthode est appelée via `this`.
    - **Injection de champ (Field Injection)** (`SecurityConfig`) : Préférer l'injection par constructeur.
    - **Random à réutiliser** (`ApiKeyService`) : Utiliser une instance partagée de `SecureRandom`.

### Frontend (gamesearch-frontend)
- **Quality Gate** : ✅ **OK**
- **Fiabilité / Sécurité / Maintenabilité** : **A / A / A**
- **Bugs / Vulnérabilités** : 0
- **Duplication** : 0%

---

## 🚀 CI/CD & Déploiement
**Note : 4 / 4** (Palier 4 atteint)

### Points Forts :
- **Docker Buildx Multi-Arch** : Support amd64 et arm64 (unique parmi les équipes).
- **Promotion d'image via Crane** : Passage de `dev-latest` à `prod-latest` sans rebuild (bit-for-bit identique).
- **Environnements GitLab** : Recette et Production bien configurés avec URLs cliquables.
- **Validation manuelle** : Bouton `when: manual` pour le déploiement en production.

### Points Faibles :
- Absence de tags Git SemVer (versioning par SHA et latest uniquement).

---

## 🧪 Qualité & Variété des Tests
**Note Pertinence : 5 / 5** | **Note Variété : 5 / 5**

### Points Forts :
- **ArchUnit (UNIQUE)** : 10 tests d'architecture pour garantir la séparation des couches.
- **Tests E2E Playwright** : Suite complète intégrée dans le pipeline CI via Docker.
- **Sécurité Multi-couches** : SAST, Secret-Detection, Dependency-Scanning et Trivy (bloquant).

---

## 📈 Couverture de Code
**Note : 3 / 5** (Palier 3 atteint)

### Analyse :
- **Seuil Bloquant Désactivé** : La configuration JaCoCo affiche un seuil de **0.00** avec un commentaire `TODO : a remettre a 0.80`.
- **Estimation** : ~50-70% sur le backend, ~20-40% sur le frontend.

---

## ❓ Questions de Soutenance Préparées

1. **Architecture** : Pourquoi bloquer la dépendance du Domaine vers JPA ? (Garantie d'indépendance de la logique métier).
2. **Kafka/Transaction** : Pourquoi `this.method()` sur du `@Transactional` est un bug ? (Court-circuit du proxy Spring).
3. **Sécurité** : Risque des secrets par défaut dans `application.yaml` ? (Accès admin trivial si variables d'env non configurées).
4. **Tests** : Pourquoi avoir laissé le seuil JaCoCo à 0 ? (Dette technique assumée pour laisser passer la CI).
5. **Algorithme** : Choix du `HashSet` dans `getSimilarGames` ? (Dédoublonnage des jeux multi-critères).

---

> [!TIP]
> **Commentaires de l'enseignant** : 
> "Usage de Kafka solide — le livrer tôt pour lever le risque technique. ALERTE : aucune stratégie de sprints dans le document ! L'équipe a la technique mais pas la planification. Risque de travailler sans jalons et de livrer en retard."

## 📢 Recommandations Prioritaires
1.  **URGENCE** : Définir un sprint planning (découpage en 3-4 sprints avec objectifs).
2.  **Backlog** : Ajouter les US du profil Admin (manquantes).
3.  **Qualité** : Ajouter des critères d'acceptation aux User Stories (US).
4.  **Kafka** : Maintenir l'implémentation actuelle qui est jugée comme la plus aboutie techniquement.
