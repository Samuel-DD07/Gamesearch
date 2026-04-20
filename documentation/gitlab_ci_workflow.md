# Workflow CI/CD : Stratégie de Déploiement et Promotion d'Images

## Responsible for this documentation :
Rodrigue Baste - devops part

## Le Principe : "Build Once, Deploy Anywhere" (Promotion d'Artefacts)

En ingénierie logicielle DevOps, la **mauvaise pratique** (anti-pattern) selon moi consiste à recompiler le code (refaire un `docker build`) spécifiquement pour la production. 
Pourquoi ? Parce qu'un nouveau build sur la branche `main` peut inclure de subtiles différences (une dépendance mise à jour entre-temps, une image de base légèrement différente, etc.) qui n'ont pas été testées en environnement de `recette` (staging).

### La Bonne Pratique (Pipeline Immuable)
L'image Docker générée doit être **immuable**.
On construit l'image **une seule fois** (sur la branche `dev`), on la teste intensivement. Si elle est validée, le passage en production (`main`) consiste uniquement à prendre cette image exacte et à l'envoyer en production.

### Workflow Recommandé

#### 1. Branche `dev` (L'Atelier)
- **CI** : Exécution des tests, linters, audits de sécurité.
- **BUILD** : Création de l'image Docker (`docker build`).
- **REGISTRY** : Push de l'image avec des tags comme `dev-${CI_COMMIT_SHORT_SHA}` et `dev-latest`.
- **CD (Recette)** : Déploiement automatique de cette image sur le serveur de recette. Les QAs/Testeurs valident le comportement.

#### 2. Merge Request (`dev` vers `main`)
- Les tests unitaires/intégration sont relancés pour s'assurer qu'aucun conflit n'a cassé le code lors du merge.
- Aucun build ni push d'image.

#### 3. Branche `main` (Production)
- **DÉCLENCHEMENT MANUEL (Continuous Delivery)** : Contrairement à `dev`, le passage en production n'est pas automatique. L'action `docker-promote-prod` est en attente, exigeant qu'un responsable système clique sur *Play* manuellement dans GitLab CI.
- **PROMOTION** : Le job CI récupère l'image Docker validée en recette (ex: `dev-latest`) et lui ajoute simplement le tag `prod-latest`, puis la repousse sans recompiler.
- **CD (Production)** : Déploiement sur le serveur de production en consommant ce tag officiel finalisé.

## Implémentation GitLab CI
Dans le fichier `.gitlab-ci.yml`, cela se traduit par des règles (`rules`) strictes :
- Le job `docker-build` est restreint à `if: $CI_COMMIT_BRANCH == "dev"`.
- Le job stratégique `docker-promote-prod` est restreint à `if: $CI_COMMIT_BRANCH == "main"`. Il inclut explicitement la condition `when: manual` pour assurer ce contrôle humain final (Continuous Delivery) avant d'écraser le tag de production `prod-latest`.
