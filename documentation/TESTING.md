# Documentation des Tests — GameSearch

Ce document détaille les protocoles de tests mis en œuvre pour assurer la fiabilité des composants Backend et Frontend du projet.

---

## Stratégie de Validation

| Catégorie | Domaine | Technologies |
|---|---|---|
| **Unitaires (Back)** | Logique métier | JUnit 5, Mockito |
| **Intégration (Back)** | Persistance & Messaging | Testcontainers (Postgres, Kafka) |
| **Architecture (Back)** | Structure hexagonale | ArchUnit |
| **Couverture (Back)** | Taux de couverture | JaCoCo (Objectif : 70%) |
| **Composants (Front)** | Interface utilisateur | Vitest, React Testing Library |
| **End-to-End (E2E)** | Parcours complets | Playwright |

---

## Backend (Java / Spring Boot)

### Exécution globale
Pour exécuter l'intégralité de la suite de tests (Unitaires, Intégration, Architecture) :
```bash
cd gamesearch
./mvnw clean test
```

### Rapport de couverture
Le projet impose un seuil de couverture de **70%**. Si ce seuil n'est pas atteint, le build est interrompu par JaCoCo. Pour générer un rapport HTML :
```bash
./mvnw jacoco:report
# Fichier généré : target/site/jacoco/index.html
```

### Conformité Architecturale
L'intégrité de la Clean Architecture est vérifiée par **ArchUnit**. Ces tests s'assurent notamment que les couches `domain` et `presentation` sont isolées des spécificités de la couche `data`.

---

## Frontend (React)

### Tests Unitaires
Validation des composants React et de la logique de service :
```bash
cd gamesearch-frontend
npm test
```

### Tests End-to-End (E2E)
Ces tests valident les scénarios utilisateurs réels et nécessitent que l'ensemble des services soient opérationnels.

**Lancement de la suite E2E :**
```bash
cd gamesearch-frontend
npx playwright test
```
*Le rapport détaillé est consultable via `npx playwright show-report`.*

---

## Prérequis d'Infrastructure
Certains tests (Intégration et E2E) requièrent le déploiement des conteneurs via Docker Compose :
- **PostgreSQL** : Port 5433
- **Kafka / UI** : Ports 29092 / 8090
- **Backend / Frontend** : Ports 8080 / 3000
