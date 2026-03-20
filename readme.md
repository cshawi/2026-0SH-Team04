<p align="Center"><img src="./_bin/logo.svg" alt="drawing" width="100"/></p>
<h4 align="Center">0SH - Gestion de projet (2026)</h4>

<h2 align="center">SoundOfSoul</h2>

# Comment partir le projet

Prérequis
- Android Studio (version récente)
- Android SDK Platform 34 et Android Emulator
- Image système x86_64 pour API 34 (préférer Google Play / Google APIs)

Étapes minimales
1. Ouvrir le projet et se placer sur la branche `develop` :
    - ouvrir le terminal
	- git clone `https://github.com/cshawi/2026-0SH-Team04.git`
	- git checkout develop

2. Ouvrir le dossier ***Android-app*** dans Android Studio. Laisser Gradle synchroniser et installer les SDK demandés.

3. Créer un AVD : **Device** = Pixel 7, **API Level** = 34, **ABI** = x86_64 (Google Play recommandé). Démarrer l'émulateur.

4. Vérifications rapides
   - L'application démarre sur l'émulateur.
   - Vérifier Home / Search / Library / Profile et que l'utilisateur connecté est affiché.
   - utiliser l'utilisateur de test **email = alice@gmail.com** , **password = alicepass**

Si problème : consulter Logcat dans Android Studio pour les erreurs et installer les SDK manquants via le SDK Manager.

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


<hr><p align="Center"><img src="./_bin/end.png" alt="drawing" width="100"/></p>