<p align="center"><img src="./_bin/logo.svg" alt="drawing" width="100"/></p>
<h4 align="center">0SH - Gestion de projet (2026)</h4>

# Calendrier du projet


|  Date |                   Matière en classe                    | Projet                                              |
| ----: | :----------------------------------------------------: | :-------------------------------------------------- |
| 02-20 |              Création d'un projet GitHub               | Planification + choix Android (Kotlin / Jetpack Compose)         |
| 02-27 |                 Documentation d'un Bug                 | Livrable #1 – Architecture  + maquettes UI       |
| 03-06 |                        Relâche                         |                                                     |
| 03-13 |                  Introduction Jetpack Compose               | Création interface Android (écrans principaux)       |
| 03-20 |                  Mise en place du projet            | Livrable #2 – Interface mobile fonctionnelle        |
| 03-27 |               Développement Backend et API             | Implémentation appel backend (OkHttp / Retrofit)           |
| 04-03 |                    Congé de Pâques                     |                                                     |
| 04-10 |                                | Livrable #3 – Génération musicale fonctionnelle via API |
| 04-17 |                    Sécurité mobile                     | Authentification utilisateur + stockage sécurisé    |
| 04-24 |          Tests & optimisations finaux           | Tests finaux + corrections + préparation démo       |
| 05-01 | Présentation des projets en classe (Épreuve terminale) | Livrable #4 – Version finale + démonstration |

---

# 2. Mise en situation (client)

**Un atelier de production sonore** reçoit souvent des demandes rapides de musique originale pour des projets (vidéos, jeux, contenu web). Les clients veulent des morceaux personnalisés (style, ambiance, durée), mais la création manuelle prend trop de temps. L’équipe passe beaucoup d’heures à composer, ajuster et livrer des versions, ce qui ralentit les projets et limite le nombre de demandes qu’elle peut accepter.

On confie donc à votre équipe la création d’une application Android simple : elle devra générer de la musique par IA, permettre l’écoute instantanée et offrir un accès vers des suggestions similaires. Le but est d’obtenir une première version rapidement exploitable, claire et facile à utiliser.

---

# 3. Preuve de faisabilité technique

### 3.1 Faisabilité technique

Le projet SoundOfSoul est techniquement réalisable sous forme d’application mobile native pour Android.

Le développement sera effectué en :

- Langage : Kotlin
- Environnement : Android Studio
- Framework UI : Jetpack Compose
- Lecteur audio : ExoPlayer (framework natif Android)

L’application permettra :

- Interface mobile optimisée
- Formulaire de génération (style, description, paroles)
- Lecteur audio intégré
- Affichage de l’historique
- Indicateur de progression pendant la génération

---

### 3.2 Backend (Serveur API)

L’application Android communiquera avec un serveur backend via API REST.

Technologies :

- JavaScrpit (Node.js)
- Express

Responsabilités du backend :

- Authentification des utilisateurs
- Gestion des quotas de génération
- Validation des paramètres
- Appel sécurisé à l’API externe de génération musicale
- Sauvegarde des données en base

---

### 3.3 Base de données

- MongoDB
- Stockage des utilisateurs et générations

---

### 3.4 Intégration API de génération musicale

Le système utilisera l’API Suno de génération musicale basée sur l’intelligence artificielle.

**API proposée (voix + paroles + instrumental)** :

- **Suno** : génération complète (paroles, voix et instrumental) à partir du style, de l’humeur et de la durée.

Processus :

1. L’utilisateur sélectionne les paramètres dans l’application.
2. L’application envoie une requête HTTPS au backend.
3. Le backend recherche dans la base de données une musique correspondante et enregistre l’URL du fichier.
4. Si aucune correspondance, le backend appelle l’API externe.
5. L’API retourne un fichier audio (MP3 ou WAV).
6. Le backend enregistre l’URL du fichier.
7. L’application récupère l’URL.
8. Lecture du fichier via ExoPlayer.

---

### 3.5 Sécurité

- Les clés API seront stockées uniquement côté serveur.
- Communication sécurisée via HTTPS.
- Authentification par session sécurisée.
- Mots de passe chiffrés (bcrypt).
- Validation des données côté application et côté serveur.

---

### 3.6 Performance

- Temps cible de génération : moins de 30 secondes.
- Requêtes réseau asynchrones (OkHttp/Retrofit).
- Indicateur de chargement pendant la génération.
- Possibilité de mise en cache locale des fichiers récents.

---

### 3.7 Contraintes techniques

- Dépendance à une API externe.
- Nécessite une connexion Internet stable.
- Dépendance aux mises à jour Android.

---

### 3.8 Conclusion

Grâce à Kotlin, Jetpack Compose et aux frameworks natifs Android (ExoPlayer, OkHttp/Retrofit), le projet SoundOfSoul est techniquement réalisable sous forme d’application Android dans un délai de 6 à 8 semaines pour une version minimale fonctionnelle.

---

# 4. Conception

## 4.1 Introduction

Le projet consiste à développer une application web/mobile nommée **SoundOfSoul**, permettant aux utilisateurs de générer de la musique automatiquement grâce à une API d’intelligence artificielle.

Contrairement aux plateformes de streaming traditionnelles, SoundOfSoul génère des morceaux uniques selon les préférences de l’utilisateur (style, titre, description, paroles).

**Objectif principal :** permettre de générer une musique personnalisée en moins de 30 secondes.

## 4.2 Besoin

### Besoin principal

Permettre à un utilisateur de :

- Générer une musique personnalisée
- Écouter le morceau instantanément
- Sauvegarder ou télécharger la musique générée

### Problème résolu

- Difficulté à créer de la musique sans compétences techniques
- Manque de musique personnalisée adaptée à une ambiance précise
- Besoin de contenu original pour créateurs (vidéos, jeux, projets)

---


## 4.3 Utisateurs et rôles

### Utilisateur standard

**Objectif :** générer et écouter de la musique personnalisée.

**Actions :**

- Créer un compte
- Se connecter
- Choisir un style musical (pop, afrobeat, instrumental, etc.)
- Donner les paroles ou une simple description
- Générer une musique via l’API
- Écouter le fichier généré
- Télécharger la musique
- Sauvegarder dans sa bibliothèque

**Restrictions :**

- Durée maximale limitée à 5 minutes.
- N'a pas accès aux informations des autres utilisateurs
- Nombre de musiques générées Limitées

---

### Administrateur

**Objectif :** superviser l’utilisation de l’API et gérer les utilisateurs.

**Actions :**

- Voir le nombre de générations par utilisateur
- Suspendre ou supprimer un compte
- Consulter les statistiques d’utilisation

---

## 4.4 Objectifs

1. Générer une musique en moins de 30 secondes.
2. Assurer une disponibilité du service 24/7.
3. Permettre le téléchargement en format MP3 ou WAV.
4. Offrir une interface simple et intuitive.

---

## 4.5 Périmètre (Inclus / Exclus)

### Inclus

- Authentification utilisateur
- Formulaire de génération (style, humeur, durée)
- Recherche dans la base de données
- Appel à l’API de génération musicale
- Lecteur audio intégré
- Sauvegarde des morceaux générés
- Téléchargement des fichiers

### Exclus

- Réseau social musical
- Éditeur musical avancé
- Marketplace de musique

---

## 4.6 Livrables attendus

###  Produit

- Application mobile Android SoundOfSoul.
- Backend Node.js/Express connecté à l’API de génération musicale.
- Base de données (utilisateurs, générations).

### Assurance qualité

- Démo.
- Vérifications manuelles des règles principales.

### Support

- Guide utilisateur(readme.md).
- Présentation de démonstration.

---

## 4.7 Règles du domaine et validations

### Règles imposées

- L’utilisateur doit être connecté pour générer une musique
- Les fichiers générés seront enrégistrés dans la base de données et désormais disponibles pour tous les utilisateurs
- Les paramètres doivent être valides (durée positive, style sélectionné)

### Messages d’erreur

- « Erreur lors de la génération, veuillez réessayer. »
- « Connexion requise. »
- « Paramètres invalides. »

### Cas limites

- L’API externe est temporairement indisponible
- Temps de génération trop long
- Fichier audio corrompu
- Perte de connexion pendant la génération

---

## 4.8 Hypothèses

- Tous les utilisateurs disposent d’un téléphone Android compatible avec une version récente d’Android.
  - **Si faux** : l’application ne pourra pas être installée depuis play Store.

- Les utilisateurs disposent d’une connexion Internet stable pendant l’utilisation.
  - **Si faux** : afficher une erreur réseau et permettre de relancer la génération plus tard.

- L’API externe de génération musicale est disponible et fonctionnelle.
  - **Si faux** : afficher un message indiquant que le service est temporairement indisponible et désactiver la génération.

- Les clés API et le serveur backend sont opérationnels en tout temps.
  - **Si faux** : prévoir un système de journalisation des erreurs et une notification administrateur.

- Les utilisateurs acceptent de créer un compte pour sauvegarder leurs musiques.
  - **Si faux** : prévoir un mode invité avec fonctionnalités limitées.

---

## 4.9 Exigences non fonctionnelles

### Performance

- Le temps de génération musicale doit être inférieur à 30 secondes.
- L’application doit répondre aux actions utilisateur en peu de temps.

### Sécurité

- Les mots de passe doivent être chiffrés.
- Les communications doivent être sécurisées via HTTPS.
- Les clés API doivent être stockées uniquement côté serveur.

### Disponibilité

- L’application doit être accessible 99 % du temps (hors maintenance).
- Sauvegarde régulière des données utilisateurs.

### Expérience utilisateur (UX)

- Interface simple, intuitive et adaptée aux standards Android.
- Indicateur de chargement visible lors de la génération.
- Navigation fluide entre les écrans.

### Compatibilité

- Compatible avec les appareils Android supportant une version récente d’Android.
- Optimisée pour différentes tailles d’écran.

### Testabilité

- Tests unitaires pour les appels API.
- Tests fonctionnels pour la génération et la lecture audio.

## 4.10 Risques majeurs et mitigation

### 1. Dépendance à l’API externe

- Probabilité : Moyenne
- Impact : Élevé
- Mitigation : gestion d’erreurs robuste et système de notification utilisateur

### 2. Coût élevé des appels API

- Probabilité : Élevée
- Impact : Moyen à élevé
- Mitigation : limiter le nombre de générations par utilisateur et optimiser les appels à l’API

### 3. Temps de génération trop long

- Probabilité : Moyenne
- Impact : Moyen
- Mitigation : affichage d’un indicateur de progression

---

## 4.11 Cas d’utilisation (happy path)

### 1 – Générer une musique (Utilisateur standard)

- **Acteur principal :** Utilisateur standard
- **Préconditions :** connecté, paramètres valides (style, humeur, durée).
- **Déclencheur :** l’utilisateur veut créer une musique personnalisée.
- **Scénario principal :**
  1. L’utilisateur ouvre l’écran de génération.
  2. Il choisit le style, l’humeur et la durée.
  3. Le système envoie la requête à l’API.
  4. Le système reçoit le fichier audio.
- **Postconditions :** musique générée et disponible pour écoute.

### 2 – Écouter et télécharger une musique (Utilisateur standard)

- **Acteur principal :** Utilisateur standard
- **Préconditions :** musique générée disponible.
- **Déclencheur :** l’utilisateur veut écouter ou télécharger le morceau.
- **Scénario principal :**
  1. L’utilisateur ouvre sa musique générée.
  2. Le système lance la lecture audio.
  3. L’utilisateur télécharge le fichier.
- **Postconditions :** musique écoutée et fichier téléchargé.

### 3 – Sauvegarder une musique (Utilisateur standard)

- **Acteur principal :** Utilisateur standard
- **Préconditions :** musique générée disponible.
- **Déclencheur :** l’utilisateur veut garder le morceau dans sa bibliothèque.
- **Scénario principal :**
  1. L’utilisateur sélectionne « Sauvegarder ».
  2. Le système ajoute la musique à la bibliothèque.
- **Postconditions :** musique enregistrée dans la bibliothèque.

---

## 4.12 Liste et modélisation des entités

- **User**
  - id
  - name
  - email
  - password
  - avatarUrl


<!-- En réflexion -->
  <!-- - quota
  - role -->

- **Track**
  - id
  - title
  - styleName
  - duration
  - createdAt
  - audioUrl
  - coverUrl
  - description
  - lyrics

- **Playlist**
  - id
  - name
  - createdAt
  - userId
  - generation_ids[]

- **Play**
  - id
  - playedAt
  - progression
  - user_id
  - track_id
  
  

---

# 5. Planification

Cette section présente les étapes de réalisation du projet SoundOfSoul, incluant les livrables ainsi que les paramètres de suivi.

---

## 5.1 Étapes de réalisation

### Étape 1 – Analyse et conception
- Définition des besoins fonctionnels et non fonctionnels
- Rédaction des hypothèses
- Conception de l’architecture (Android + Backend)

Livrable #1 :
- Document de conception complet

---

### Étape 2 – Mise en place du projet Android
- Création du projet Android Studio
- Configuration Jetpack Compose
- Mise en place de la navigation
- Création des écrans principaux (Accueil, Génération, Connexion)

Livrable #2 :
- Interface mobile fonctionnelle (sans API)

---

### Étape 3 – Développement Backend et API
- Création des routes API (authentification, génération)
- Connexion à la base de données
- Intégration de l’API externe de génération musicale
- Connexion Android ↔ Backend (OkHttp/Retrofit)
- Gestion des erreurs

Livrable #3 :
- Génération musicale fonctionnelle via API

---

### Étape 4 

- Intégration complète et sécurité
  - Gestion Session sécurisée
  - Sécurisation des requêtes HTTPS
  - Lecture audio avec ExoPlayer
  - Gestion de l’historique
  
- Tests et optimisation
  - Tests fonctionnels
  - Tests de performance
  - Correction des bogues
  - Amélioration UX

Livrable #4 :
- Application complète (MVP fonctionnel)
- Application stable prête pour démonstration
- Guide utilisateur
- Présentation finale

---

## 5.2 Paramètres de suivi

- Respect des délais du calendrier académique
- Avancement hebdomadaire vérifié sur GitHub
- Tests réalisés à chaque nouvelle fonctionnalité
- Validation progressive des livrables
- Documentation mise à jour en continu

<hr>
<p align="center"><img src="./_bin/end.png" alt="drawing" width="150"/></p>
