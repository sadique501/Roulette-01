# Roulette Insight (Android starter project)

A native Android/Kotlin + Jetpack Compose starter for a personal roulette history and statistics app.

## Included in this starter
- Manual result entry for roulette numbers 0–36
- Local Room database storage with insertion order and timestamps
- Recent sequence and full history
- Delete individual result / clear history
- Frequency analysis and basic historical summary
- Placeholder AI Chat screen and explicit integration notes

## Not yet implemented
- Automatic capture from a third-party game's screen
- OCR/vision-based result detection and confidence review
- Floating overlay over another app
- Real AI API/backend and speech input/output
- CSV export/import and automated tests

These features require device/game-specific testing and, for screen capture, explicit Android MediaProjection permission. Android may restrict overlays and capture depending on the device and target game. The starter does not bypass a game's protections.

## Build
1. Install Android Studio with Android SDK 35 and JDK 17.
2. Open this folder in Android Studio.
3. Allow Gradle sync and install dependencies.
4. Run on an Android device or emulator.

The build needs internet access for Gradle dependency downloads. This starter has not been built or device-tested in this environment.

## Notes
- This app records results in the order they are saved. Automatic detection must be validated before committing a result to avoid duplicate or misread numbers.
- Historical frequency is not a reliable prediction for a fair, independent roulette wheel.
- Keep AI provider keys on a backend, not in the APK.


## Build from mobile using GitHub Actions
This repository includes `.github/workflows/build-apk.yml`.
1. On your phone, create a GitHub repository and upload the extracted project files (including hidden `.github` folder).
2. Open the repository's Actions tab and run **Build Android APK** (or wait for the first push build).
3. Open the completed workflow run and download the `roulette-insight-debug-apk` artifact.
4. Extract the artifact ZIP and install `app-debug.apk`. Android may ask you to allow installs from your browser/files app.

This is a debug APK for personal testing, not a Play Store release. The cloud build must complete successfully before the APK is available.
