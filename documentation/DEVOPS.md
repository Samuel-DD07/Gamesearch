# Architecture et Philosophie DevOps - Projet GameSearch

## Responsible for this documentation :
Rodrigue Baste - devops part

## 1. Vision Globale et Stratégie

Le processus d'intégration et de déploiement continu repose sur le principe cardinal **Build Once, Deploy Anywhere**. L'objectif est de garantir une immutabilité stricte des livrables : l'image Docker compilée, testée et certifiée par la CI est exactement celle employée en environnement de Recette, puis promue en Production.

L'infrastructure s'articule autour des technologies suivantes :
- **Orchestration locale et serveurs** : Docker & Docker Compose
- **Reverse Proxy & Sécurité réseau** : Traefik & CrowdSec
- **Intégration Continue (CI)** : GitLab CI
- **Automatisation du Déploiement (CD)** : Watchtower
- **Services Applicatifs** : Spring Boot (Java 21), React.js (Nginx), PostgreSQL 16, Kafka (KRaft)

## 2. Intégration Continue (CI) - GitLab

Le pipeline défini dans `.gitlab-ci.yml` sécurise et valide l'intégrité du code source avant la génération de tout artefact. Il exécute les phases critiques suivantes :

1. **Audit Statistique (SAST & Secrets)** : Détection proactive des vulnérabilités de code et des éventuelles fuites de clés de sécurité.
2. **Lint & Formatting** : Application automatique du code style et de la qualité syntaxique sur le Backend (Spotless/Maven) et le Frontend (ESLint).
3. **Tests & Couverture** : Exécution des tests unitaires backend avec stricte validation de la "Quality Gate" via JaCoCo (seuil de couverture fixé à 80%).
4. **Modernisation Architecture (ArchUnit)** : Validation par des tests automatisés du respect de la Clean Architecture et des normes du langage (ex: recours aux Records Java 21).
5. **Build Multi-Arch** : Emballage des applications en architecture `linux/amd64` et `linux/arm64` via Docker Buildx, avec publication vers un Container Registry privé. 
6. **Scanning de Sécurité (Trivy)** : Analyse statique finale (CVE) sur les binaires de l'image Docker générée. Interruption totale du pipeline en cas de menaces de type `HIGH` ou `CRITICAL`.

## 3. Conteneurisation (Docker)

Les `Dockerfiles` appliquent rigoureusement les normes de sécurité en production via l'utilisation de méthodes "Multi-stage" :

- **Applicatif Backend (Java 21)** :
  - *Stage 1 (Builder)* : S'appuie sur `maven:3.9.6-eclipse-temurin-21-alpine` pour résoudre le cache de dépendances et compiler le binaire natif.
  - *Stage 2 (Runtime)* : Basé sur une image minimaliste `eclipse-temurin:21-jre-alpine`. Le principe du moindre privilège est imposé : l'application s'exécute sous le contexte d'un utilisateur système non-root dédié (`spring:spring`).

- **Applicatif Frontend (React)** :
  - *Stage 1 (Builder)* : Environnement Node.js 20 Alpine gérant la résolution complexe des paquets (`npm install --legacy-peer-deps`) et générant la build de la Single Page Application (SPA). L'application source est codée pour pointer sur des chemins relatifs (`/api/`) afin d'être complètement agnostique à l'environnement.
  - *Stage 2 (Runtime)* : Le dossier de build est hébergé sur une image allégée `nginx:alpine`. Le runtime lourd Node.js n'atterrit jamais en production, garantissant des temps de réponse statique éclairs sur le port TCP 80. Un Reverse Proxy Nginx (`nginx.conf`) est en charge d'intercepter toutes les requêtes entrantes préfixées par `/api/` et de les router silencieusement vers le backend au sein du réseau Docker. Cela élimine définitivement les erreurs de Cross-Origin (CORS) et les problématiques d'URLs hardcodées au runtime.

## 4. Déploiement Continu et Gestion des Environnements (CD)

Le passage d'un environnement à l'autre ne repose pas sur de nouveaux builds, mais sur de la configuration découplée.

### L'approche Registry-Based
Aucun serveur de distribution cible n'exécute de directive `build`. L'infrastructure de PRD et de RECETTE démarre les conteneurs directement via un `pull` du registre CI. 
Les manifestes de composition (`docker-compose.yml`) résolvent les versions atomiques à travers de l'injection dynamique d'environnement (ex: `${BACKEND_VERSION:-prod-latest}`, ou `${FRONTEND_VERSION:-dev-latest}`). 

### Observabilité et Prérequis Hôte (Logs)
La verbosité des conteneurs est stricte. Chaque service majeur de production (Base de données, Kafka, Microservices) est verrouillé côté logging Docker par le driver `json-file` afin de protéger la saturation i-node/disque des hôtes (`max-size: 10m`, `max-file: 3`).

### Traefik, CrowdSec & Exposition
L'ensemble de la topologie locale reste confinée dans le pont réseau virtuel fermé `gamesearch_backend` ou `gamesearch_data`. 
L'exposition publique repose sur l'entrée `edge` gérée par un proxy Traefik, lequel applique plusieurs couches de protection : routage HTTPS automatisé, protection par pare-feu applicatif via l'ingestion CrowdSec (`crowdsec-bouncer@file`), et "Rate Limiting" strict sur l'API externe (partenaires) pour endiguer les dénis de service.

### L'Auto-Updater (Watchtower)
Les environnements exploitent un paradigme de mise à jour semi-délégué.
Le démon **Watchtower**, déployé localement via un socket Docker, sonde avec récurrence le registre à la recherche d'une altération de sha256 sur le tag scruté. 
La commande `WATCHTOWER_LABEL_ENABLE=true` forme un sas d'arrêt radical : un conteneur qui ne porte pas explicitement le label `com.centurylinklabs.watchtower.enable=true` sera silencieusement ignoré, empêchant Watchtower d'affecter accidentellement d'autres infrastructures portées par la même machine virtuelle.

## 5. Résolution des Problèmes d'Infrastructure et Isolation

### Isolation des Volumes de Données
Afin d'éviter les collisions entre les environnements de Production et de Recette sur un même hôte Docker, une nomenclature stricte a été adoptée pour les volumes persistants. L'utilisation de noms de volumes globaux identiques provoquait des avertissements d'appartenance de projet et empêchait la suppression propre des ressources.

Chaque environnement utilise désormais un suffixe explicite dans ses manifestes `docker-compose.yml` :
- **Production** : `gamesearch_postgres_data_prd`, `gamesearch_kafka_data_prd`
- **Recette** : `gamesearch_postgres_data_rec`, `gamesearch_kafka_data_rec`

Cette approche garantit une isolation totale des données et permet l'exécution de commandes de nettoyage (`docker compose down -v`) sans risque d'affecter l'environnement adjacent.

### Configuration du Reverse Proxy Nginx (Frontend)
Le routage des requêtes API via le conteneur Frontend a nécessité des ajustements critiques pour assurer la communication avec le Backend.

#### Routage et Préfixe /api
Le Backend Spring Boot est configuré pour répondre sur la racine `/`. Le Frontend (React) effectuant ses appels sur `/api/*`, Nginx doit impérativement retirer ce préfixe avant de transmettre la requête au service Backend.

#### Ordre des Directives et Erreurs 500
Une attention particulière doit être portée à l'ordre des instructions dans la configuration Nginx lors de l'utilisation de variables pour l'adresse de destination (`proxy_pass $upstream`).

L'instruction `rewrite ... break` interrompt immédiatement le traitement des directives de configuration pour la phase en cours. Par conséquent, toute affectation de variable (`set`) doit être déclarée **avant** la règle de réécriture. Une inversion de cet ordre entraîne une erreur 500 car Nginx tente d'utiliser une variable non initialisée au moment du passage au proxy.

Configuration cible validée :
```nginx
location /api/ {
    set $upstream http://gs-backend-prd:8080; # Initialisation en premier
    rewrite ^/api/(.*) /$1 break;            # Réécriture et arrêt
    proxy_pass $upstream;                     # Utilisation de la variable
}
```
Cette configuration permet une résolution DNS dynamique (via le `resolver` Docker) tout en assurant la transformation correcte des URLs.
