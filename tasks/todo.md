# Plan: Yalla Jamal → Android APK

Goal: Turn the single-file webapp `Yalla Jamal(1).html` (Arabic learning app for kids) into an installable Android APK, optimized for phone **and** tablet, with visual polish where possible.

## Environment (verified)
- Android SDK: `/home/cherif/Android/Sdk` (platforms 31/34/35/36, build-tools 34/35/36, licenses accepted)
- JDK 21 (pinned for Gradle via `~/.gradle/gradle.properties`), Node 24, ImageMagick + Inkscape
- Approach: **Capacitor 7** → local WebView, no server needed, works fully offline

## Todos

### 1. Project setup
- [x] `www/index.html` (single source of truth for the app), original kept at repo root
- [x] `package.json` + Capacitor deps + `capacitor.config.json` (appId `com.yallajamal.app`, name "Yalla Jamal", webDir `www`)

### 2. Numerals (user request)
- [x] **Western digits only (0-9)**: removed the `ad()` Arabic-Indic conversion (star counter, pager, album) and replaced hardcoded ١..١٠ in the numbers topic with 1..10 — verified no ٠-٩ remain anywhere

### 3. Visual & mobile enhancements (in the HTML)
- [x] **Offline fonts**: Cairo / Baloo 2 / Nunito bundled as 12 local woff2 files (`www/fonts.css`) — Google Fonts CDN links removed, app works with zero network
- [x] **Tablet layout**: `@media (min-width:768px) and (min-height:560px)` — wider column (1120px), bigger tiles/emoji/Arabic type, larger cards, memory grid, balloons, sticker album
- [x] **Landscape phones**: compact block for short viewports so the home screen fits
- [x] **Phone polish**: `theme-color` (light+dark), gradient background, gradient tiles/buttons, no text selection, no rubber-band overscroll, `touch-action:manipulation`, viewport zoom disabled for toddler fingers, proper `<html lang>`, meta description
- [x] **Android integration**: hardware/gesture Back navigates in-app first (`window.YallaJamal.back()` + `MainActivity.onBackPressed`), TTS cancelled when app is backgrounded

### 4. App identity
- [x] Launcher icons generated (Inkscape): camel badge on purple gradient — legacy PNGs (48–192dp) + adaptive foreground PNGs (108–432dp) + gradient adaptive background drawable
- [x] Splash screens (portrait + landscape, all densities) on brand background
- [x] Theme colors: purple status bar, dark navigation bar, light window background, opted out of forced edge-to-edge so the header never sits under the status bar

### 5. Android project & build
- [x] `cap add android` + `cap sync`
- [x] Label "Yalla Jamal", `screenOrientation="fullSensor"`, `resizeableActivity="true"` (phone + tablet, portrait + landscape)
- [x] Build: `./gradlew assembleDebug` → APK

### 6. Verify
- [x] `aapt dump badging`: package `com.yallajamal.app`, label "Yalla Jamal", minSdk 23 / targetSdk 35, all densities, all 4 screen sizes
- [x] 12 font files packaged; zero `fonts.googleapis.com` references
- [x] On-device test (Android 34 emulator): home / topic / learn / memory render, status bar themed, in-app Back works (no accidental exit)
- [x] Headless Chrome screenshots for phone (405×760) and tablet (1180×820): home, topic, learn, quiz, memory, album, parent gate + dark mode
- [x] No JS/page errors in any view

## Review

**What was built**
- Capacitor 7 Android project in `android/`, web assets in `www/`, config in `capacitor.config.json`.
- Debug APK: `android/app/build/outputs/apk/debug/app-debug.apk` (~7.5 MB) — sideload-installable.
- Rebuild after any `www/` change: `npm run sync && npm run apk`.

**Changes to the webapp** (all in `www/index.html`; the original `Yalla Jamal(1).html` is untouched)
1. Numerals switched to Western 0-9 everywhere (removed `ad()` helper + Arabic-Indic digits in the numbers topic).
2. Fonts bundled locally (`fonts/` + `fonts.css`) instead of Google Fonts CDN → fully offline, no render-blocking network request.
3. Tablet/landscape media queries, gradient backgrounds, gradient tiles & buttons, selection/overscroll/zoom guards.
4. Added `window.YallaJamal` bridge API + background TTS cancel.
5. **Bug fix (pre-existing in the original file)**: `.mcard .in` was `display:inline`, collapsing every memory card to 0×0 — the Memory game was completely invisible. One-line fix (`display:block`), verified on device and in Chrome.

**Native side**
- `MainActivity.onBackPressed()` calls the web back API, only exits the app from the home screen.
- Manifest: `fullSensor` orientation + resizeable for tablets.
- Icons/splash generated with Inkscape/ImageMagick from the app's own camel artwork.

**Notes / possible follow-ups**
- Debug-signed only. For Play Store or public distribution, add a release keystore and `assembleRelease`.
- `INTERNET` permission kept (Capacitor default) though nothing loads from the network; can be dropped for a strict offline build.
- On devices without an Arabic TTS voice, words are not read aloud — the in-app parent panel already explains how to install one.
- Another app (`com.cherif.arabischkinder`) kept launching itself on the test emulator and interrupted some interaction tests; visual verification was completed with headless Chrome instead.
