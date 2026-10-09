# Yalla Jamal — Arabisch lernen für Kids (Android)

A children's Arabic-learning webapp (German UI) converted into a native Android app with
[Capacitor](https://capacitorjs.com/). The app runs in a local WebView, needs **no server and
no internet connection**, and is optimized for both phones and tablets.

Jamal the camel guides children (ages 2–6) through vocabulary, colors, numbers, fruit, family,
body parts, everyday phrases and the Arabic alphabet — with cards, a balloon quiz, a memory
game and letter tracing.

## Features

- **8 topics** (Tiere, Farben, Zahlen, Obst, Familie, Körper, Jeden Tag, Buchstaben)
- **4 activities** per topic: Entdecken (cards), Ballon-Quiz, Memory, Nachmalen (tracing)
- Text-to-speech (Arabic words, German translations) + sound effects and confetti
- Sticker album and star rewards, progress saved locally
- Age toggle (2–3 vs. 4–6 years), parent area behind a multiplication gate
- Light & dark mode, pause reminder after 10 minutes
- **All numbers displayed as Western digits (0–9)**
- Fonts (Cairo, Baloo 2, Nunito) bundled locally → works 100% offline
- Phone + tablet layouts, portrait + landscape, hardware/gesture back button navigates in-app

## Project layout

```
├── www/                    # the app (single source of truth)
│   ├── index.html          # markup + CSS + JS
│   ├── fonts.css           # @font-face rules (local files)
│   └── fonts/*.woff2       # 12 bundled font files
├── android/                # generated Capacitor Android project
├── capacitor.config.json   # appId com.yallajamal.app, appName "Yalla Jamal"
├── package.json            # capacitor deps + build scripts
├── Yalla Jamal(1).html     # original untouched webapp (reference only)
└── tasks/todo.md           # plan + review of everything that was changed
```

## Requirements

- Node.js 20+ and npm
- JDK 21 (`~/.gradle/gradle.properties` already pins Gradle to it)
- Android SDK (`~/Android/Sdk`) with platform 35, build-tools 35, accepted licenses

## Build

```bash
npm install                # once: installs Capacitor
npm run sync               # copy www/ into android/app/src/main/assets/public
npm run apk                # ./gradlew assembleDebug
```

Output: `android/app/build/outputs/apk/debug/app-debug.apk`

Rebuild after every change inside `www/`: `npm run sync && npm run apk`

## Install on a device

```bash
adb install -r android/app/build/outputs/apk/debug/app-debug.apk
```

Or copy the APK to the device and open it (enable "install unknown apps").

## Project scripts

| Command | What it does |
|---|---|
| `npm run sync` | `cap sync android` — sync web assets + config |
| `npm run apk` | debug APK |
| `npm run apk:release` | release APK (needs a signing config first) |

## Native configuration

- **Package / label**: `capacitor.config.json` (`com.yallajamal.app`, "Yalla Jamal")
- **Orientation & tablets**: `android/app/src/main/AndroidManifest.xml`
  (`android:screenOrientation="fullSensor"`, `android:resizeableActivity="true"`)
- **Back button**: `MainActivity.onBackPressed()` → `window.YallaJamal.back()` in `www/index.html`
- **Status/nav bar, window colors**: `android/app/src/main/res/values/colors.xml` + `styles.xml`
- **Icons & splash**: `android/app/src/main/res/mipmap-*`, `drawable-*/splash.png`
  (regenerate from `www` artwork with Inkscape/ImageMagick)

## Making a release build

Debug APKs are debug-signed and fine for sideloading. For distribution:

1. `keytool -genkey -v -keystore yalla-jamal.keystore -alias yalla -keyalg RSA -keysize 2048 -validity 10000`
2. Add a `signingConfigs` / `buildTypes.release.signingConfig` block to `android/app/build.gradle`
   (keep the keystore and its passwords **out of version control**)
3. `npm run apk:release` → `android/app/build/outputs/apk/release/app-release.apk`

## Notes

- The Android app ships a minimal `debug` build; no analytics, ads or network calls are made.
- Arabic pronunciation needs an Arabic TTS voice on the device — the in-app parent area
  ("Für Eltern") explains how to install one.
- `tasks/todo.md` documents every change made to the original webapp, including a fix for the
  Memory game, which rendered invisible cards in the original file.
