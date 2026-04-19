# Implémentation Serveur et Intégration Infrastructure - Projet GameSearch

## Responsible for this documentation :
Rodrigue Baste - devops part

Ce document détaille l'implémentation technique du projet GameSearch au sein de l'infrastructure globale `~/infra` et explique les interconnexions entre les services locaux du projet et les services transverses.

## 1. Architecture Réseau et Exposition

Le projet GameSearch est intégré à l'infrastructure via un modèle de réseau en couches, utilisant le réseau externe `edge` comme point d'entrée unique.

### Le Réseau `edge`
- **Définition** : Réseau Docker global géré par le service Traefik dans `~/infra/traefik`.
- **Rôle** : Permet la communication entre le reverse proxy central et les conteneurs applicatifs (frontend et backend) sans exposer directement les ports sur l'hôte.
- **Configuration** : Déclaré comme `external: true` dans les fichiers `docker-compose.yml` de GameSearch.

### Reverse Proxy (Traefik)
L'exposition des services `gamesearch.basteproductions.fr` et `api.gamesearch.basteproductions.fr` repose sur la configuration dynamique de Traefik :
- **SSL/TLS** : Géré par le `certresolver` `letsencrypt` configuré globalement.
- **Routage** : Les labels Docker sur les services `frontend` et `backend` instruisent Traefik sur les règles de Host et les ports de destination (80 pour le front, 8080 pour le back).

## 2. Sécurité Transversale

Le projet bénéficie de la pile de sécurité centralisée située dans `~/infra/traefik`.

### Intégration CrowdSec
- **Middleware** : Utilisation du middleware `crowdsec-bouncer@file` défini dans `~/infra/traefik/dynamic/security.yml`.
- **Protection** : Filtrage automatique des IPs malveillantes détectées par le service `crowdsec` global.
- **Application** : Appliqué via les labels Traefik sur les deux services exposés (API et Web).

### Rate Limiting
- **Standard** : Utilisation du middleware `rate@file` pour la protection générale contre les abus.
- **Spécifique (Partner API)** : Implémentation d'un middleware local `partner-ratelimit` pour l'ingestion de données partenaires, limitant les flux à 10 requêtes/s en moyenne.

## 3. Observabilité et Monitoring

L'intégration avec la pile de monitoring (`~/infra/monitoring`) est native via la découverte de services Docker.

### Prometheus
- **Scraping** : Le backend GameSearch expose ses métriques au format Prometheus via Spring Boot Actuator (`/actuator/prometheus`).
- **Découverte** : Le label `prometheus.scrape=true` permet au serveur Prometheus central de détecter et de collecter automatiquement les métriques du backend.
- **Réseau** : La communication se fait via le réseau `edge` ou `monitoring_internal`.

### Centralisation des Logs (Loki)
- **Collecte** : Le service `promtail` global (`~/infra/monitoring/promtail`) monte le socket Docker et `/var/lib/docker/containers` pour ingérer les logs de tous les conteneurs GameSearch.
- **Visualisation** : Les logs sont consultables dans Grafana via la source de données Loki.

## 4. Cycle de Vie et CI/CD

### Registre d'Images
- **Source** : Les images sont construites par GitLab CI et stockées dans le registre privé `registry.basteproductions.fr` géré dans `~/infra/gitlab`.
- **Authentification** : Les nœuds de déploiement doivent être authentifiés auprès de ce registre pour tirer les images `dev-latest` (Recette) ou `prod-latest` (Production).

### Mises à jour Automatiques (Watchtower)
- **Mécanisme** : Le service `watchtower` global (`~/infra/watchtower`) est configuré pour surveiller les changements sur le registre.
- **Activation** : Bien que prêt, l'auto-update nécessite le label `com.centurylinklabs.watchtower.enable=true` sur les services cibles.
- **Stratégie** : Utilisation du mode `ROLLING_RESTART` pour minimiser l'indisponibilité lors des mises à jour.

## 5. Dépendances Locales vs Globales

Contrairement à d'autres services qui pourraient partager une base de données globale, GameSearch maintient sa propre isolation pour ses composants de données :
- **PostgreSQL 16** : Instance dédiée par environnement (PRD/RECETTE) pour garantir l'étanchéité des données.
- **Kafka (KRaft)** : Cluster local dédié au projet pour la gestion des événements asynchrones et l'ingestion partenaire.

Cette approche permet au projet d'être portable tout en profitant de la robustesse de l'infrastructure d'accueil pour tout ce qui concerne l'exposition et la surveillance.

## 6. Administration des Bases de Données (Adminer)

Pour pallier certaines limitations réseau inhérentes aux environnements virtuels (comme la non-exposition fiable des ports `localhost` sous Docker Desktop pour macOS) et pour des raisons de sécurité, les ports natifs de PostgreSQL (`5432`) ne sont **pas** exposés sur la machine hôte.

L'administration s'effectue via une interface Web dédiée (Adminer) conteneurisée :
- **Déploiement** : Un service `adminer` est embarqué dans les `docker-compose.yml` (ex: Recette). Il partage le réseau interne `gamesearch_data` avec PostgreSQL.
- **Exposition** : L'accès est pris en charge par Traefik en HTTPS. Exemple pour la Recette : `https://db-recette-gs.basteproductions.fr`
- **Méthode de connexion** :
  - **Serveur** : `postgres` (résolution DNS interne Docker)
  - **Utilisateur / Base** : `gamesearch_user` / `gamesearch`
  - **Mot de passe** : Celui défini dans le fichier `.env` ou `ChangeMe123!` par défaut.
