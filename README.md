# MediVault

[![Build Status](https://img.shields.io/badge/Android-Java-orange?logo=android&logoColor=white)](https://github.com/Omkesh-Bash/MediVault-Release-ByteMe)
[![Firebase](https://img.shields.io/badge/Firebase-Configured-blue?logo=firebase)](https://console.firebase.google.com/)
[![Download APK (Drive)](https://img.shields.io/badge/Download%20APK-Google%20Drive-brightgreen?logo=google-drive)](https://drive.google.com/file/d/1EF3Thr7HHAErk8J6h6U0oYeXf6rr7MP-/view?usp=sharing)
[![Download APK (raw)](https://img.shields.io/badge/Download%20APK-Raw%20GitHub-brightgreen?logo=github)](https://github.com/Omkesh-Bash/MediVault-Release-ByteMe/tree/main/app/build/outputs/apk/debug)

A secure Android app to store, manage and share personal medical reports. MediVault lets users upload and view medical reports, interact with doctors, and access emergency features — built with Java and Firebase.

---

## Quick links
- Download (recommended): Google Drive — https://drive.google.com/file/d/1EF3Thr7HHAErk8J6h6U0oYeXf6rr7MP-/view?usp=sharing  
- Raw APK (repo branch `files`): https://github.com/Omkesh-Bash/MediVault-Release-ByteMe/tree/main/app/build/outputs/apk/debug  
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

| Home — Reports list | Upload report | Saved reports |
|:---:|:---:|:---:|
| <img src="assets/screenshots/home.png" width="250"> | <img src="assets/screenshots/doc_uploader.png" width="250"> | <img src="assets/screenshots/saved_reports.png" width="250"> |

| Profile | Shared notification | AI Chat |
|:---:|:---:|:---:|
| <img src="assets/screenshots/profile.png" width="250"> | <img src="assets/screenshots/shared_notification.png" width="250"> | <img src="assets/screenshots/Ai.png" width="250"> |

---

## Download & install

Important:
- APKs installed from outside the Play Store require the user to enable "Install unknown apps" for the installing app (browser or file manager).  
- Make sure you trust the source before installing. Review the code or build yourself if you need maximum assurance.

Download options (choose one):
- **Google Drive** (recommended for easy public sharing)  
  [Download Final MediVault.apk — Google Drive](https://drive.google.com/file/d/1EF3Thr7HHAErk8J6h6U0oYeXf6rr7MP-/view?usp=sharing)

- **Raw file from this repository** (branch `files`)  
  [Download Final MediVault.apk — Raw GitHub](https://github.com/Omkesh-Bash/MediVault-Release-ByteMe/tree/main/app/build/outputs/apk/debug)

How to install:
1. Download the APK.
2. On your Android device, enable installation from unknown sources for the app you use to open the APK (Settings → Apps → Browser/File Manager → Install unknown apps).
3. Open the APK file and follow the installer prompts.

Optional verification:
- We recommend verifying the APK checksum after download. Example (on Linux/macOS):
  ```bash
  shasum -a 256 Final\ MediVault.apk
