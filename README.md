# MediVault

[![Build Status](https://img.shields.io/badge/Android-Java-orange?logo=android&logoColor=white)](https://github.com/Omkesh-Bash/MediVault-Release-ByteMe)
[![Firebase](https://img.shields.io/badge/Firebase-Configured-blue?logo=firebase)](https://console.firebase.google.com/)
[![Download APK (Drive)](https://img.shields.io/badge/Download%20APK-Google%20Drive-brightgreen?logo=google-drive)](https://drive.google.com/file/d/1EF3Thr7HHAErk8J6h6U0oYeXf6rr7MP-/view?usp=sharing)
[![Download APK (raw)](https://img.shields.io/badge/Download%20APK-Raw%20GitHub-brightgreen?logo=github)](https://raw.githubusercontent.com/Omkesh-Bash/MediVault-Release-ByteMe/files/app/build/outputs/apk/debug/Final%20MediVault.apk)

A secure Android app to store, manage and share personal medical reports. MediVault lets users upload and view medical reports, interact with doctors, and access emergency features — built with Java and Firebase.

---

## Quick links
- Download (recommended): Google Drive — https://drive.google.com/file/d/1EF3Thr7HHAErk8J6h6U0oYeXf6rr7MP-/view?usp=sharing  
- Raw APK (repo branch `files`): https://raw.githubusercontent.com/Omkesh-Bash/MediVault-Release-ByteMe/files/app/build/outputs/apk/debug/Final%20MediVault.apk  
- Project: https://github.com/Omkesh-Bash/MediVault-Release-ByteMe

---

## Table of contents
- [About](#about)
- [Key features](#key-features)
- [Screenshots](#screenshots)
- [Download & install](#download--install)
- [Tech stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting started](#getting-started)
- [Firebase setup](#firebase-setup)
- [Build & run (CLI)](#build--run-cli)
- [Project structure](#project-structure)
- [Security & notes](#security--notes)
- [Contributing](#contributing)
- [License & contact](#license--contact)

---

## About
MediVault is an Android application (Java) that acts as a personal vault for medical documents and reports. It provides user authentication, report upload/view/list functionality, doctor-side review/verification, an in-app chat/AI assistant, and emergency/lock flows for rapid access and privacy.

---

## Key features
- ✅ User authentication (signup / login)  
- ✅ Upload, view and list medical reports (with adapters & utilities)  
- ✅ Doctor workflows: verification, inbox and report review  
- ✅ In-app chat / AI assistant screen  
- ✅ Emergency mode & quick lock screen  
- ✅ Firebase integration (Auth, Storage, Database, Messaging)

---

## Screenshots

Click any image to view the full-size file in the repository.

<div style="display:flex; flex-wrap:wrap; gap:12px; align-items:flex-start;">

  <div style="flex: 1 1 320px; max-width: 320px;">
    <a href="assets/screenshots/home.png">
      <img src="assets/screenshots/home.png" alt="Home — Reports list" width="320" style="border:1px solid #e1e4e8; border-radius:6px;" />
    </a>
    <p align="center"><em>Home — Reports list</em></p>
  </div>

  <div style="flex: 1 1 320px; max-width: 320px;">
    <a href="assets/screenshots/doc_uploader.png">
      <img src="assets/screenshots/doc_uploader.png" alt="Upload report" width="320" style="border:1px solid #e1e4e8; border-radius:6px;" />
    </a>
    <p align="center"><em>Upload report</em></p>
  </div>

  <div style="flex: 1 1 320px; max-width: 320px;">
    <a href="assets/screenshots/saved_reports.png">
      <img src="assets/screenshots/saved_reports.png" alt="Saved reports" width="320" style="border:1px solid #e1e4e8; border-radius:6px;" />
    </a>
    <p align="center"><em>Saved reports</em></p>
  </div>

  <div style="flex: 1 1 320px; max-width: 320px;">
    <a href="assets/screenshots/profile.png">
      <img src="assets/screenshots/profile.png" alt="Profile" width="320" style="border:1px solid #e1e4e8; border-radius:6px;" />
    </a>
    <p align="center"><em>Profile</em></p>
  </div>

  <div style="flex: 1 1 320px; max-width: 320px;">
    <a href="assets/screenshots/shared_notification.png">
      <img src="assets/screenshots/shared_notification.png" alt="Shared notification" width="320" style="border:1px solid #e1e4e8; border-radius:6px;" />
    </a>
    <p align="center"><em>Shared notification</em></p>
  </div>

  <div style="flex: 1 1 320px; max-width: 320px;">
    <a href="assets/screenshots/Ai.png">
      <img src="assets/screenshots/Ai.png" alt="AI Chat" width="320" style="border:1px solid #e1e4e8; border-radius:6px;" />
    </a>
    <p align="center"><em>AI Chat</em></p>
  </div>

</div>

---

## Download & install

Important:
- APKs installed from outside the Play Store require the user to enable "Install unknown apps" for the installing app (browser or file manager).  
- Make sure you trust the source before installing. Review the code or build yourself if you need maximum assurance.

Download options (choose one):
- Google Drive (recommended for easy public sharing)  
  [Download Final MediVault.apk — Google Drive](https://drive.google.com/file/d/1EF3Thr7HHAErk8J6h6U0oYeXf6rr7MP-/view?usp=sharing)

- Raw file from this repository (branch `files`)  
  [Download Final MediVault.apk — Raw GitHub](https://raw.githubusercontent.com/Omkesh-Bash/MediVault-Release-ByteMe/files/app/build/outputs/apk/debug/Final%20MediVault.apk)

How to install:
1. Download the APK.
2. On your Android device, enable installation from unknown sources for the app you use to open the APK (Settings → Apps → Browser/File Manager → Install unknown apps).
3. Open the APK file and follow the installer prompts.

Optional verification:
- We recommend verifying the APK checksum after download. Example (on Linux/macOS):
  ```bash
  shasum -a 256 Final\ MediVault.apk
  ```
  (Replace with the published SHA256 fingerprint when available.)

---

## Tech stack
- Android (Java)
- Gradle (build system)
- Firebase (Auth, Storage, DB, Cloud Messaging)
- XML layouts for UI

---

## Prerequisites
- JDK 11+ (or the JDK required by your Android Gradle plugin)
- Android Studio (recommended) or Android SDK + Gradle CLI
- Android SDK platform(s) targeted by the project
- A Firebase project (if you want to run with backend services)

---

## Getting started

1. Clone:
   ```bash
   git clone https://github.com/Omkesh-Bash/MediVault-Release-ByteMe.git
   cd MediVault-Release-ByteMe
   ```
2. Open in Android Studio and let Gradle sync.
3. Configure Firebase (see below) or replace `app/google-services.json` with your file.
4. Run on emulator or device.

---

## Firebase setup
1. Create a Firebase project: https://console.firebase.google.com/
2. Add an Android app with the package name found in `AndroidManifest.xml`.
3. Download `google-services.json` and place it at:
   ```
   app/google-services.json
   ```
4. Enable the services the app uses:
   - Authentication (Email/Password)
   - Cloud Storage (for report files)
   - Firestore / Realtime Database (app data)
   - Cloud Messaging (optional)

Security: Do not commit production credentials or private keys.

---

## Build & run (CLI)
- Build debug APK:
  ```bash
  ./gradlew :app:assembleDebug
  ```
- Install on a connected device:
  ```bash
  ./gradlew :app:installDebug
  ```
- Clean:
  ```bash
  ./gradlew clean
  ```

---

## Project structure (high level)
- app/
  - src/main/java/com/example/medivault/ — main Java sources
    - Activities: Splash, Login, Signup, Home, Profile
    - Reports: UploadReportActivity, ViewReportActivity, MyReportsActivity
    - Doctor: DoctorHomeActivity, DoctorInboxActivity, DoctorVerificationActivity
    - Chat: AiChatActivity, ChatAdapter, ChatMessage
    - Models / Utils: Report, ReportUtils, DoctorUtils, etc.
  - src/main/res/ — layouts, drawables, strings
  - AndroidManifest.xml — app manifest & permissions
  - google-services.json — Firebase config (replace with your project file)

---

## Security & notes
- Do not publish APKs containing secret API keys or production credentials.
- For production, configure Firebase security rules to restrict access to authenticated users and to validate uploads.
- Prefer distributing signed release builds via Play Store or official channels for production usage.
- For repository binaries, GitHub Releases or external hosting (S3 / Firebase Storage) are preferred over committing large files in repo history.

---

## Contributing
Thanks for your interest! Suggested workflow:
1. Fork → create branch:
   ```bash
   git checkout -b feat/your-feature
   ```
2. Make changes and open a pull request with a clear description.

Please open issues for bugs or feature requests including reproduction steps.

---

## License & contact
- License: Add a LICENSE file (MIT / Apache-2.0 recommended).
- Authors / Contacts: Prafulla0001, Omkesh-Bash

---

If you like, I can:
- Add the exact APK SHA256 to the download section,
- Replace the raw GitHub badge link with a GitHub Release badge after you upload the APK as a Release asset,
- Add a GitHub Actions workflow to build and publish the debug APK automatically.
