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


### Description du produit final
Fatigué d’écouter des musiques qui ne correspondent pas à votre humeur ?
SoundOfSoul vous permet de générer une musique personnalisée adaptée à votre état d’esprit et à vos besoins émotionnels du moment.

<hr><p align="Center"><img src="./_bin/end.png" alt="drawing" width="100"/></p>