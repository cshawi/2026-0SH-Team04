<p align="Center"><img src="./_bin/logo.svg" alt="drawing" width="100"/></p>
<h4 align="Center">0SH - Gestion de projet (2026)</h4>

<h2 align="center">SoundOfSoul</h2>

# Comment partir le projet

Prérequis
- Android Studio (version récente)
- Android SDK Platform 34 et Android Emulator
- Image système x86_64 pour API 34 (préférer Google Play / Google APIs)

Rendre le serveur accessible publiquement (ngrok)
- Téléchargez et installez ngrok depuis https://ngrok.com/. Choisissez la version Windows.
- Ajoutez le dossier contenant `ngrok.exe` à votre variable d'environnement PATH (pour pouvoir l'exécuter depuis n'importe quel terminal).
- Configurez ngrok avec votre clé (doit être exécuté dans Powershell en mode administrateur) :

```powershell
# Ouvrir PowerShell en tant qu'administrateur puis exécuter (remplacez <YOUR_AUTH_TOKEN>) :
ngrok authtoken <YOUR_AUTH_TOKEN>
```

- Pour exposer le serveur local (port 3000) publiquement :

```powershell
# Depuis un terminal non-administrateur (ou PowerShell normal) dans le dossier où ngrok est disponible :
ngrok http 3000
```

Étapes minimales
1. Ouvrir le projet et se placer sur la branche `main` :
    - ouvrir le terminal
	- git clone `https://github.com/cshawi/2026-0SH-Team04.git`
	- git checkout main

2. Ouvrir le dossier ***Android-app*** dans Android Studio. Laisser Gradle synchroniser et installer les SDK demandés.

3. Créer un AVD : **Device** = Pixel 7, **API Level** = 34, **ABI** = x86_64 (Google Play recommandé). Démarrer l'émulateur.


- Copiez l'adresse publique fournie par ngrok (par ex. `https://abcd-12-34-56-78.ngrok.io`).
- Dans le fichier d'environnement du backend (`2026-0SH-Team04-Server/.env`), modifiez la valeur `BaseUrl` pour utiliser l'URL publique ngrok (inclure `https://`).
- Lancez ensuite le serveur backend local `npm run dev`. Vérifiez que le serveur écoute et que l'URL ngrok proxy fonctionne (la page d'état de ngrok et la sortie console montrent les requêtes entrantes).

- Une fois le serveur accessible via l'URL publique ngrok, démarrez l'application Android (émulateur ou appareil). Le serveur utilise `BaseUrl` pour communiquer avec l'API publique.

4. Vérifications rapides
   - L'application démarre sur l'émulateur.
   - Vérifier Home / Search / Library / Profile et que l'utilisateur connecté est affiché.
   - utiliser l'utilisateur de test **email = john.doe@example.com** , **password = abcdef**
    - Sur la fenêtre ngrok : vérifier les requêtes entrantes et les réponses (ngrok fournit un tableau de requêtes tunnellées à http://127.0.0.1:4040).


Si problème : consulter Logcat dans Android Studio pour les erreurs et installer les SDK manquants via le SDK Manager.


### Description du produit final
Fatigué d’écouter des musiques qui ne correspondent pas à votre humeur ?
SoundOfSoul vous permet de générer une musique personnalisée adaptée à votre état d’esprit et à vos besoins émotionnels du moment.

<hr><p align="Center"><img src="./_bin/end.png" alt="drawing" width="100"/></p>