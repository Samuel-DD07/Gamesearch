# Guide Utilisateur — GameSearch

Ce document présente les fonctionnalités essentielles du projet GameSearch pour chaque profil utilisateur.

---

## 1. Visiteur (Joueur)
L'accès public permet de consulter le catalogue sans authentification.
- **Recherche** : Saisissez le titre d'un jeu dans la barre de recherche pour un filtrage instantané.
- **Filtres** : Utilisez les menus de sélection par Genre, Plateforme ou Année pour affiner les résultats.
- **Détails** : Cliquez sur une fiche de jeu pour consulter sa fiche complète (description, tags, notes).

---

## 2. Partenaire (Éditeur)
L'intégration de nouveaux jeux s'effectue via deux canaux :
- **API REST** : Envoyez une requête `POST /partners/ingest` avec votre clé API dans le header `X-API-Key`.
- **Kafka** : Publiez directement les données de vos jeux dans le topic `game-ingestion-topic`.
- **Vérification** : Consultez le tableau de bord pour confirmer le statut de l'ingestion.

---

## 3. Administrateur
Gestion et supervision de la plateforme :
- **Connexion** : Authentification via `/login` avec les identifiants d'administration.
- **Monitoring** : Suivi en temps réel des flux Kafka et de l'état des services via le Dashboard.
- **Maintenance** : Gestion du catalogue et mise à jour des métadonnées si nécessaire.

---

### Informations Techniques
En cas de dysfonctionnement, vérifiez la santé des services via `/actuator/health` ou consultez les logs des conteneurs Docker respectifs.
