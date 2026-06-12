# A.R.I.A Voice Bridge - Forge 1.18.2

Ce projet ajoute une commande serveur :

```mcfunction
/aria_voice_url <url_wav>
```

Le fichier audio doit être un WAV lisible par Java Sound. Le mod convertit en PCM mono 48kHz 16-bit puis l'envoie à tous les joueurs connectés au Simple Voice Chat.

## Build

```bash
./gradlew build
```

Le jar sera dans :

```txt
build/libs/aria-voice-bridge-1.0.0.jar
```

À mettre dans le dossier `mods/` du serveur Minecraft, avec Simple Voice Chat déjà installé.

## Test rapide

Si tu as un fichier WAV public :

```mcfunction
/aria_voice_url https://ton-site/audio/test.wav
```

## Côté Node.js

Après avoir généré le TTS en WAV et exposé l'URL, envoie via RCON :

```js
await rcon.send(`aria_voice_url ${url}`);
```

