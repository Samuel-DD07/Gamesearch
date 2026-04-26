# Feedback Evaluation - MasterGame

Ce document synthétise les retours d'évaluation pour le groupe **MasterGame** (Équipe 17) basés sur la grille d'évaluation du projet Gamesearch.

## 📊 Résultat Global (Code)
**Note de Code : 54 / 60** (basée sur un score brut de 63 / 70)
**Note de Code (sur 40) : 36 / 40**

---

## 🧮 Simulateur de Note Finale
Selon la formule officielle : **(Note de Conception + Note de Code + 20) / 5**

| Note de Conception (sur 40) | Note de Code (sur 40) | Bonus | Note Finale / 20 |
| :--- | :---: | :---: | :---: |
| **32** (16/20) | 36 | 20 | **17.6** |
| **34** (17/20) | 36 | 20 | **18.0** |
| **36** (18/20) | 36 | 20 | **18.4** |
| **38** (19/20) | 36 | 20 | **18.8** |
| **40** (20/20) | 36 | 20 | **19.2** |

*Note : Additionnez votre score de dossier (sur 40) à 36, ajoutez 20, puis divisez par 5.*

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
> **Note du correcteur** : "Stratégie pyramide complète assumée. Sécurité massive en CI. L'usage d'ArchUnit est un vrai plus différenciateur."
