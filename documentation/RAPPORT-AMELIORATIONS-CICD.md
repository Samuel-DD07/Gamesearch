# Rapport : Améliorations CI/CD Pipeline GameSearch

## Responsible for this documentation :
Rodrigue Baste - devops part

---

## Partie 1 : Ce qui a été fait

### Fichiers modifiés

| Fichier | Changements |
|---------|-------------|
| `.gitlab-ci.yml` | Réécriture complète avec les 11 améliorations listées ci-dessous |
| `gamesearch-frontend/package.json` | Ajout des `devDependencies` ESLint |
| `.gitignore` | Ajout de `infra/prd/.env` et `infra/recette/.env` pour exclure les secrets |

### Fichiers créés

| Fichier | Raison |
|---------|--------|
| `infra/prd/.env.example` | Template sans secrets, sert de modèle |
| `infra/recette/.env.example` | Template sans secrets, sert de modèle |

### Liste des 11 améliorations

**1. Cache CI intelligent**
Le cache global sans clé a été remplacé par deux caches avec clés basées sur les fichiers de dépendances (`pom.xml` pour Maven, `package-lock.json` pour npm). Le cache ne se reconstruit que lorsque les dépendances changent réellement.

**2. Frontend lint corrigé**
Les dépendances ESLint (`eslint`, `eslint-config-react-app`, `eslint-plugin-react`) étaient installées dynamiquement dans le pipeline avec `--no-save --legacy-peer-deps`, ce qui contournait le lockfile et produisait des builds non reproductibles. Elles sont maintenant déclarées dans les `devDependencies` du `package.json`. Le job CI fait simplement `npm ci && npm run lint`.

**3. Tests frontend ajoutés**
Un nouveau job `frontend-test` exécute `npm test -- --ci --coverage --watchAll=false` avec extraction du taux de couverture Jest visible dans GitLab.

**4. Rules DRY**
La condition `if: $CI_COMMIT_BRANCH == "dev" || ...` dupliquée dans chaque job a été factorisée dans trois templates YAML réutilisables : `.rules-default`, `.rules-deploy`, `.rules-promote`. Chaque job utilise `extends:` au lieu de répéter les règles.

**5. Support des Merge Requests**
Un bloc `workflow: rules` global a été ajouté pour que le pipeline se déclenche aussi sur les Merge Requests (`$CI_MERGE_REQUEST_IID`). Les développeurs voient le résultat des tests avant de merger.

**6. Stage modernization supprimé**
Le stage `modernization` exécutait les tests ArchUnit séparément, mais `mvn clean verify` dans le stage `test` les exécutait déjà. Le stage a été supprimé pour éliminer cette double exécution.

**7. Rapports Trivy natifs GitLab**
Trivy génère maintenant des rapports au format GitLab (`--format template --template "@/contrib/gitlab.tpl"`). Les rapports backend et frontend sont fusionnés via `jq` en un seul fichier `gl-container-scanning-report.json` déclaré comme artefact `container_scanning`. Les vulnérabilités sont visibles dans l'onglet Security des MR. Le pipeline ne bloque plus que sur les vulnérabilités `CRITICAL` (au lieu de `HIGH,CRITICAL`).

**8. Versioning sémantique pour la promotion**
La promotion en production n'est plus déclenchée par un push sur `main`, mais par la création d'un tag Git sémantique (`v1.0.0`, `v1.2.3`). Les images sont taguées avec le numéro de version exact en plus de `prod-latest`. Cela permet de savoir précisément quelle version tourne en production et de revenir à une version antérieure si nécessaire.

**9. Secrets hors du dépôt Git**
Les fichiers `infra/prd/.env` et `infra/recette/.env` contenaient des secrets en clair (JWT, mot de passe admin). Ils ont été ajoutés au `.gitignore` et des fichiers `.env.example` ont été créés comme templates. Les secrets doivent être migrés vers les variables CI/CD GitLab protégées.

**10. Smoke test post-déploiement**
Le déploiement en recette est assuré par Watchtower, qui détecte automatiquement le nouveau tag `dev-latest` dans le registry et redémarre les conteneurs. Un nouveau job `smoke-test-recette` attend que Watchtower ait fait la mise à jour, puis vérifie que l'API répond `200` sur `/actuator/health` (12 tentatives, 10 secondes d'intervalle). Un environnement GitLab `recette` est déclaré avec son URL.

**11. Images Docker mises à jour**
`docker:24.0.5` (2023) remplacé par `docker:27`. Trivy mis à jour de `0.49.0` à `0.62.0`.

---

## Partie 2 : Ce qu'il reste à faire

### Action 1 — Mettre à jour le lockfile frontend

Exécuter dans le dossier `gamesearch-frontend/` :

```bash
cd gamesearch-frontend
npm install
```

Cela met à jour `package-lock.json` avec les nouvelles `devDependencies` ESLint.
Commiter le lockfile mis à jour.


### Action 2 — Régénérer les secrets

Les anciens secrets (`admin123`, le JWT en dur) sont dans l'historique Git.
Même après suppression des fichiers `.env`, ils restent accessibles via `git log`.
Il faut les considérer comme compromis et en créer de nouveaux.

Générer un nouveau JWT secret :

```bash
openssl rand -base64 64
```

Copier la sortie. Mettre à jour les `.env` sur le host (là où les docker-compose de recette et production tournent) avec les nouvelles valeurs.


### Action 3 — Créer les variables CI/CD dans GitLab (optionnel)

Ces variables ne sont pas utilisées par le pipeline actuel (Watchtower gère le déploiement).
Elles sont utiles uniquement si on veut les injecter dans un futur job CI.

Aller dans le projet GitLab :

```
Settings > CI/CD > Variables > Expand > Add variable
```

| Variable | Type | Valeur | Protected | Masked |
|----------|------|--------|-----------|--------|
| `JWT_SECRET` | Variable | Sortie de `openssl rand -base64 64` | Oui | Oui |
| `JWT_EXPIRATION_MS` | Variable | `86400000` | Oui | Non |
| `ADMIN_USERNAME` | Variable | Choix libre | Oui | Oui |
| `ADMIN_PASSWORD` | Variable | Mot de passe fort | Oui | Oui |
| `POSTGRES_PASSWORD` | Variable | Mot de passe fort | Oui | Oui |
| `REGISTRY_URL` | Variable | `registry.basteproductions.fr/epita_dev_1/fullstack` | Oui | Non |


### Action 4 — Vérification

Pousser un commit sur la branche `dev`. Le pipeline doit :

1. **audit** — Scanner les secrets et le code
2. **lint** — Valider le formatage backend (Spotless) et frontend (ESLint)
3. **test** — Exécuter les tests backend (JaCoCo) et frontend (Jest)
4. **build** — Construire les images Docker multi-arch et les pousser au registry avec le tag `dev-latest`
5. **security** — Scanner les images avec Trivy, générer le rapport GitLab
6. **deploy** — Watchtower détecte le nouveau `dev-latest`, redémarre les conteneurs de recette. Le job `smoke-test-recette` attend puis vérifie le health check


### Action 5 — Déploiement en production (nouvelle procédure)

La promotion n'est plus déclenchée depuis la branche `main` mais par un tag sémantique :

```bash
git checkout main
git merge dev
git push
git tag v1.0.0
git push origin v1.0.0
```

Dans GitLab : `CI/CD > Pipelines`, trouver le pipeline du tag, cliquer **Play** sur le job `docker-promote-prod`.

Les images validées en recette (`dev-latest`) sont retaggées `v1.0.0` et `prod-latest`, puis Watchtower met à jour la production automatiquement.

---

## Annexe : Mise à jour de la documentation existante

Le fichier `gitlab_ci_workflow.md` mentionne encore l'ancienne procédure de promotion (branche `main` + `when: manual`). Il doit être mis à jour pour refléter la nouvelle procédure par tags sémantiques (action 5).
