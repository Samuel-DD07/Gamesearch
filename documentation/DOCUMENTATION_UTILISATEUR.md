# Documentation utilisateur - GameSearch

## 1. Presentation

GameSearch est une application de recherche et de consultation de jeux video.

Elle permet :
- aux visiteurs de parcourir un catalogue de jeux ;
- aux administrateurs de se connecter a un espace reserve ;
- aux partenaires d'envoyer des jeux au catalogue via l'API d'ingestion.

L'application web est accessible selon l'environnement :
- Recette : `https://gamesearch-rec.basteproductions.fr`
- Production : `https://gamesearch.basteproductions.fr`

L'API est accessible selon l'environnement :
- Recette : `https://api.gamesearch-rec.basteproductions.fr`
- Production : `https://api.gamesearch.basteproductions.fr`

## 2. Profil visiteur

Le visiteur est un utilisateur public. Il n'a pas besoin de compte pour utiliser le catalogue.

### Consulter le catalogue

Depuis la page d'accueil, le visiteur peut consulter la liste des jeux disponibles.

Chaque jeu est affiche sous forme de carte avec :
- son titre ;
- son annee de sortie ;
- sa note ;
- ses genres ;
- ses plateformes ;
- son image de couverture.

### Rechercher un jeu

Le champ de recherche permet de filtrer les jeux par titre.

Exemple :
- saisir `Elden` pour retrouver `Elden Ring` ;
- saisir `Cyberpunk` pour retrouver `Cyberpunk 2077`.

### Filtrer les jeux

Le bouton `Filters` permet d'afficher des filtres supplementaires.

Les filtres disponibles sont :
- genre ;
- plateforme ;
- annee de sortie.

Le catalogue se met a jour automatiquement apres modification d'un filtre.

### Changer de page

Si le catalogue contient plusieurs pages, des boutons de navigation permettent de passer a la page precedente ou suivante.

### Consulter le detail d'un jeu

En cliquant sur une carte ou sur le lien `Details`, le visiteur accede a la fiche complete du jeu.

La fiche detaillee affiche :
- l'image du jeu ;
- le titre ;
- la note ;
- l'annee de sortie ;
- l'editeur ;
- la description ;
- les plateformes ;
- les genres ;
- les tags.

Le bouton `Back to Catalog` permet de revenir au catalogue.

## 3. Profil administrateur

L'administrateur dispose d'un acces reserve a l'espace d'administration.

### Se connecter

Depuis la barre de navigation, cliquer sur `Admin`.

La page de connexion demande :
- un identifiant ;
- un mot de passe.

Apres une connexion reussie, l'utilisateur est redirige vers le tableau de bord administrateur.

En cas d'erreur, un message indique que les identifiants sont incorrects.

### Acceder au tableau de bord

Le tableau de bord administrateur permet de suivre l'etat general du systeme :
- API backend ;
- cluster Kafka ;
- consumer d'ingestion.

Il donne aussi acces a une action de synchronisation de demonstration.

### Declencher une ingestion de test

Le bouton `Sync Now` envoie un jeu de test vers le pipeline d'ingestion.

Cette action cree une demande d'ingestion asynchrone. Le jeu envoye est ensuite traite par le backend via Kafka.

### Suivre le statut d'ingestion

Apres le declenchement, un encart `Live Ingestion Monitor` apparait.

Il affiche :
- l'identifiant externe du jeu ;
- l'heure d'envoi ;
- le statut courant ;
- le message renvoye par le backend ;
- l'identifiant interne du jeu si l'ingestion reussit.

Les statuts possibles sont :
- `PENDING` : la demande est en attente de traitement ;
- `SUCCESS` : le jeu a ete integre au catalogue ;
- `ERROR` : l'ingestion a echoue.

La page interroge automatiquement le backend jusqu'a obtenir un statut final.

### Se deconnecter

Le bouton `Logout` supprime la session locale et renvoie l'utilisateur vers la page d'accueil.

## 4. Profil partenaire

Le partenaire est un systeme ou utilisateur externe autorise a envoyer des jeux dans le catalogue.

L'usage partenaire se fait principalement via l'API.

### Obtenir une cle API

Un partenaire doit etre enregistre pour recevoir une cle API.

Endpoint :

```http
POST /partner/register
```

Exemple de corps JSON :

```json
{
  "name": "Nom du partenaire"
}
```

Reponse attendue :

```json
{
  "partnerId": "uuid-du-partenaire",
  "name": "Nom du partenaire",
  "apiKey": "cle-api-generee"
}
```

La cle API doit etre conservee par le partenaire. Elle sert a authentifier les appels d'ingestion.

### Envoyer un jeu a ingerer

Endpoint :

```http
POST /partner/games
```

Exemple de corps JSON :

```json
{
  "gameId": "partner-game-001",
  "title": "Example Game",
  "releaseYear": 2024,
  "genres": ["Action", "Adventure"],
  "platforms": ["PC", "PS5"],
  "publisher": "Example Studio",
  "description": "Description du jeu.",
  "coverUrl": "https://example.com/cover.jpg",
  "rating": 8.5,
  "tags": ["Solo", "Story Rich"]
}
```

Champs obligatoires :
- `title` ;
- `releaseYear` ;
- `genres` ;
- `platforms`.

Champs optionnels :
- `gameId` ;
- `publisher` ;
- `description` ;
- `coverUrl` ;
- `rating` ;
- `tags`.

Reponse attendue :

```json
{
  "gameId": "partner-game-001",
  "status": "PENDING",
  "message": "Waiting for processing",
  "createdAt": "date-de-creation"
}
```

Le statut `PENDING` signifie que la demande a ete acceptee et placee dans le pipeline d'ingestion.

### Suivre une ingestion

Endpoint :

```http
GET /partner/ingestions/{gameId}
```

Exemple :

```http
GET /partner/ingestions/partner-game-001
```

Reponse possible :

```json
{
  "gameId": "partner-game-001",
  "status": "SUCCESS",
  "message": "Game indexed successfully",
  "internalGameId": "uuid-interne-du-jeu",
  "createdAt": "date-de-creation"
}
```

### Importer plusieurs jeux

Endpoint :

```http
POST /partner/games/bulk
```

Le fichier doit etre envoye dans un champ multipart nomme `file`.

Formats acceptes :
- `.json` ;
- `.csv`.

Le format JSON attendu est une liste de jeux :

```json
[
  {
    "gameId": "game-001",
    "title": "First Game",
    "releaseYear": 2024,
    "genres": ["Action"],
    "platforms": ["PC"]
  },
  {
    "gameId": "game-002",
    "title": "Second Game",
    "releaseYear": 2025,
    "genres": ["RPG"],
    "platforms": ["PS5"]
  }
]
```

Le format CSV attendu suit l'ordre de colonnes suivant :

```csv
title,releaseYear,genres,platforms,publisher,description,coverUrl,rating,tags
Example Game,2024,Action|Adventure,PC|PS5,Example Studio,Description,https://example.com/cover.jpg,8.5,Solo|Story Rich
```

Les valeurs multiples doivent etre separees par `|` pour :
- `genres` ;
- `platforms` ;
- `tags`.

Reponse attendue :

```json
{
  "total": 2,
  "successful": 2,
  "failed": 0,
  "errors": []
}
```

## 5. Messages d'erreur courants

### Identifiants administrateur incorrects

Si la connexion admin echoue, verifier l'identifiant et le mot de passe fournis.

### Jeu introuvable

Si une fiche jeu n'existe plus ou si l'identifiant est invalide, l'application affiche un message indiquant que le jeu n'a pas ete trouve.

### Donnees d'ingestion invalides

Une ingestion peut echouer si des champs obligatoires sont manquants.

Les champs les plus importants sont :
- `title` ;
- `releaseYear` ;
- `genres` ;
- `platforms`.

### Format de fichier non supporte

L'import bulk accepte uniquement les fichiers `.json` et `.csv`.

## 6. Bonnes pratiques

Pour les visiteurs :
- utiliser les filtres pour reduire les resultats ;
- ouvrir la fiche detaillee pour consulter toutes les informations d'un jeu.

Pour les administrateurs :
- se deconnecter apres usage sur un poste partage ;
- verifier le statut final apres une synchronisation.

Pour les partenaires :
- conserver la cle API dans un endroit securise ;
- fournir un `gameId` stable pour permettre la mise a jour d'un jeu deja envoye ;
- verifier le statut d'ingestion apres chaque envoi ;
- utiliser le JSON pour les imports complexes.