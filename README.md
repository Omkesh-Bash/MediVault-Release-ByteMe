# MediVault

A secure Android app to store, manage and share personal medical reports. MediVault allows users to upload and view medical reports, interact with doctors, and access emergency features — built with Java and Firebase.

[![Download Debug APK](https://img.shields.io/badge/Download%20Debug%20APK-APK-brightgreen?logo=android)](https://github.com/Omkesh-Bash/MediVault-Release-ByteMe/tree/main/app/build/outputs/apk/debug)

---

## Table of contents
- [About](#about)
- [Key features](#key-features)
- [Screenshots](#screenshots)
- [Tech stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting started](#getting-started)
- [Download debug APK](#download-debug-apk)
- [Firebase setup](#firebase-setup)
- [Build & run (CLI)](#build--run-cli)
- [Project structure](#project-structure)
- [Notes & security](#notes--security)
- [Contributing](#contributing)
- [License & contact](#license--contact)

---

## About
MediVault is an Android application (Java) that acts as a personal vault for medical documents and reports. It provides user authentication, report upload/view/list functionality, doctor-side review/verification, an in-app chat/AI assistant, and emergency/lock flows for rapid access and privacy.

---

## Key features
- User authentication (signup / login)
- Upload, view and list medical reports
- Report adapters & utilities to manage file metadata
- Doctor flows: verification, inbox and report review
- In-app chat and AI-assistant screen
- Emergency modes and lock screen functionality
- Firebase integration for backend services (auth, storage, DB, messaging)

---

## Screenshots
(Add screenshots into `assets/screenshots/` or replace links below)
- Home screen — List of reports
- Upload report flow
- View report screen
- Doctor inbox & verification screens
- AI Chat screen

---

## Tech stack
- Android (Java)
- Gradle (build system)
- Firebase (Auth, Storage, Database, Cloud Messaging — configured via `google-services.json`)
- XML layouts (res/) for UI

---

## Prerequisites
- JDK 11+ (or JDK supported by your Android Gradle plugin)
- Android Studio (recommended) or command-line Android SDK + Gradle
- Android SDK platform(s) that the project targets (check `build.gradle`)
- A working Firebase project (for auth, storage, DB)

---

## Getting started

1. Clone the repository
   ```bash
   git clone https://github.com/Omkesh-Bash/MediVault-Release-ByteMe.git
   cd MediVault-Release-ByteMe
   ```

2. Open in Android Studio
   - Choose "Open an existing Android Studio project" and select the cloned folder.
   - Let Android Studio sync Gradle and download dependencies.

3. Configure Firebase (see the Firebase setup section below)
4. Run on an emulator or device:
   - From Android Studio: Run ▶ or
   - Command line:
     ```bash
     ./gradlew assembleDebug
     ./gradlew installDebug
     ```

---

## Download debug APK

You can provide a ready-to-install debug build directly from this repository using one of the recommended approaches below. The badge at the top links to an example release asset URL — replace it with your real release asset URL after uploading.

Recommended (GitHub Releases)
1. Build the debug APK locally:
   ```bash
   ./gradlew :app:assembleDebug
   ```
   The APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

2. In your repository on GitHub → Releases → Draft a new release → upload `app-debug.apk` as a release asset → publish.

3. Replace the badge and links in this README with the release asset URL. Example release asset URL format:
   ```
   https://github.com/Omkesh-Bash/MediVault-Release-ByteMe/releases/download/<RELEASE_TAG>/app-debug.apk
   ```

If you prefer automation:
- Use GitHub Actions to build the debug APK on push and either:
  - Upload the APK as a release asset automatically, or
  - Save it as a workflow artifact and link or badge to your release.

Notes & security:
- Do not publish APKs that embed sensitive keys or production credentials.
- For private repos, download access respects repo permissions — users must be signed in and have repository access.
- Avoid committing large binaries into the repo history; prefer Releases, Git LFS, or external storage.

---

## Firebase setup
1. Create a Firebase project at https://console.firebase.google.com/
2. Add an Android app to the Firebase project and register the app with your package name (check `AndroidManifest.xml` for the package).
3. Download `google-services.json` from Firebase and place it in:
   ```
   app/google-services.json
   ```
   (This repo currently contains a `google-services.json` file — ensure it matches your Firebase project or replace it with your own.)

4. Enable required Firebase features (as used in the app):
   - Authentication (Email/Password, or whichever providers the app expects)
   - Cloud Storage (for storing report files)
   - Firestore or Realtime Database (for app data)
   - Cloud Messaging (optional — for notifications)

5. If you use any API keys (third-party), store them securely (do not commit secrets).

---

## Build & run (CLI)
- Build debug APK:
  ```bash
  ./gradlew assembleDebug
  ```
- Install to connected device:
  ```bash
  ./gradlew installDebug
  ```
- Clean:
  ```bash
  ./gradlew clean
  ```

---

## Project structure (high level)
- app/
  - src/main/java/com/example/medivault/ — main Java sources
    - Activities: SplashActivity, LoginActivity, SignupActivity, HomeActivity, ProfileActivity
    - Report flows: UploadReportActivity, ViewReportActivity, MyReportsActivity
    - Doctor flows: DoctorHomeActivity, DoctorInboxActivity, DoctorVerificationActivity
    - Chat: AiChatActivity, ChatAdapter, ChatMessage
    - Utilities / models: Report, ReportUtils, DoctorUtils, etc.
  - src/main/res/ — layouts, drawables, strings, etc.
  - AndroidManifest.xml — app manifest and permissions
  - google-services.json — Firebase configuration

Tip: Start by reading `AndroidManifest.xml` to see the declared launch activity and required permissions, then open `HomeActivity`, `UploadReportActivity`, and `ViewReportActivity` to understand the core flows.

---

## Notes & security
- Do not commit private keys, credentials, or production `google-services.json` that expose sensitive data.
- Ensure Firebase Security Rules are configured for Storage and Database to prevent unauthorized reads/writes.
- Consider adding local encryption for stored report metadata or files if required by your privacy model.
- This repository is Java-based; migrating to Kotlin is optional but could improve modern Android compatibility.

---

## Contributing
Contributions are welcome. Suggested workflow:
1. Fork the repo.
2. Create a feature branch:
   ```bash
   git checkout -b feat/your-feature
   ```
3. Make changes, add tests where applicable.
4. Submit a pull request with a clear description of the change.

Please open issues for bugs or feature requests with steps to reproduce.

---

## Troubleshooting
- Gradle sync errors: check Android Studio SDK/NDK settings and Java version.
- Firebase auth/storage errors: verify `google-services.json` and Firebase console configuration.
- Runtime crashes: check Logcat for stack traces and missing permission requests (e.g. storage, camera).

---

## License & contact
- License: Add a LICENSE file to this repository to declare the license you want to use (MIT, Apache-2.0, etc.).
- Author / Contact: Prafulla0001, Omkesh-Bash — see repository owner on GitHub.

---

Thanks for checking out MediVault! If you'd like, I can:
- Add a GitHub Actions workflow to automatically build the debug APK and attach it to Releases,
- Replace the badge URL with the real release asset URL after you upload the APK,
- Generate CONTRIBUTING.md or SECURITY.md files.
