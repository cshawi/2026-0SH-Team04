# Liste détaillée des tâches (à partir du 05-03-2026)

Ce plan est construit à partir du calendrier dans `readme.md` et des étapes dans `project.md`.

## Semaine du 05-03 → 20-03 (Livrable #2 – Interface mobile fonctionnelle)
- Valider l’architecture Android (Kotlin/Jetpack Compose) et la structure du projet.
- Mettre en place le projet Android Studio (modules, packages, thème).
- Créer les écrans principaux :
  - Accueil
  - Génération
  - Connexion / Inscription
- Mettre en place la navigation Jetpack Compose.
- Ajouter des données mockées pour simuler la génération.
- Intégrer un lecteur audio local (ExoPlayer) avec un fichier test.
- Préparer les interfaces réseau (Retrofit/OkHttp) sans appel réel.
- Vérifier la cohérence UI/UX Android (standards Material).

## Semaine du 20-03 → 27-03 (Backend + API)
- Créer le serveur Node.js/Express.
- Mettre en place la base de données MySQL (tables utilisateurs + générations).
- Implémenter l’authentification (login / register).
- Créer les routes API :
  - POST /auth/register
  - POST /auth/login
  - POST /generate
  - GET /generations
- Ajouter la logique d’appel à l’API Suno.
- Tester les routes avec Thunder Client / Postman.

## Semaine du 27-03 → 10-04 (Livrable #3 – Génération musicale via API)
- Connecter l’app Android au backend via Retrofit.
- Implémenter l’écran de génération avec appel API réel.
- Gérer les états de chargement et erreurs réseau.
- Sauvegarder l’historique localement + côté serveur.
- Valider l’écoute du fichier audio retourné.

## Semaine du 10-04 → 17-04 (Sécurité mobile)
- Sécuriser le stockage des tokens (EncryptedSharedPreferences).
- Ajouter vérification d’accès aux routes protégées.
- Gérer la déconnexion et expiration de session.

## Semaine du 17-04 → 24-04 (Tests finaux + optimisation)
- Tests fonctionnels (génération, lecture, téléchargement).
- Tests API et erreurs (API indisponible, timeout, audio corrompu).
- Optimisation UX (chargement, messages d’erreur, feedback utilisateur).
- Corrections de bugs.

## Semaine du 24-04 → 01-05 (Livrable #4 – Version finale)
- Finaliser la démo et le script de présentation.
- Rédiger/mettre à jour `readme.md` (guide utilisateur).
- Préparer la présentation finale.
- Vérifier toutes les fonctionnalités en conditions réelles.
