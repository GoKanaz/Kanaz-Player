# 🎵 Kanaz Player

![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=for-the-badge&logo=kotlin)
![Compose](https://img.shields.io/badge/Jetpack_Compose-2024.01-4285F4?style=for-the-badge&logo=jetpackcompose)
![Architecture](https://img.shields.io/badge/Architecture-Clean_Architecture-green?style=for-the-badge)
![License](https://img.shields.io/badge/License-Apache_2.0-orange?style=for-the-badge)

**Kanaz Player** is a modern, lightweight Android music player designed with a focus on performance and clean code. Built using **Jetpack Compose** for a declarative UI and **Media3 (ExoPlayer)** for a robust playback engine, it follows industry-standard best practices.

---

## ✨ Features

* **Local Playback**: High-performance streaming of audio files from local device storage.
* **Media3 Integration**: Built on Android's official Media3 framework for seamless background playback and media session management.
* **Clean Architecture**: Strict separation of concerns (Data, Domain, UI) for better maintainability and testability.
* **Modern UI**: Minimalist dark-themed interface with full edge-to-edge support and Material 3 components.
* **Smart Permissions**: Dynamic handling for Android 13+ (`READ_MEDIA_AUDIO`) and legacy storage permissions.
* **Dependency Injection**: Powered by **Dagger Hilt** for a modular and scalable codebase.

---

## 🛠 Tech Stack

| Component | Technology |
| :--- | :--- |
| **Language** | Kotlin (Coroutines & Flow) |
| **UI Framework** | Jetpack Compose |
| **Media Engine** | Android Media3 / ExoPlayer |
| **Dependency Injection** | Dagger Hilt |
| **Image Loading** | Coil |
| **Architecture** | MVVM + Clean Architecture |

---

## 📂 Project Structure

The project is organized into layers to ensure scalability and clarity:

```text
com.gokanaz.kanazplayer/
├── core/
│   ├── common/         # Utilities, Result wrappers, and Extensions
│   ├── data/           # Repository implementations, Data sources, and Mappers
│   ├── domain/         # UseCases and Repository Interfaces (Business Logic)
│   └── di/             # Dagger Hilt Modules
├── feature/
│   ├── player/         # Player UI, Animations, and ViewModels
│   └── library/        # Music browser and playlist logic
├── service/            # KanazMusicService (Media3 Session implementation)
└── ui/
    └── theme/          # Design System (Color, Typography, Theme)
    

🚀 Getting Started
Prerequisites
Android Studio Ladybug (2024.2.1) or newer.
JDK 17.
Android SDK 34 or higher.
Building from Source
Using Android Studio:
Clone the repository: git clone https://github.com/gokanaz/kanaz-player.git
Open the project and sync Gradle.
Run the app module on your device.
Using Command Line (CLI/Termux):

# Clone the repository
git clone [https://github.com/gokanaz/kanaz-player.git](https://github.com/gokanaz/kanaz-player.git)
cd kanaz-player

# Build the debug APK
./gradlew assembleDebug

The generated APK will be located at: app/build/outputs/apk/debug/app-debug.apk
🛡️ Permissions
This app strictly adheres to Android's privacy guidelines:
API 33+: Requires READ_MEDIA_AUDIO and POST_NOTIFICATIONS.
Legacy (API < 33): Requires READ_EXTERNAL_STORAGE.
Service: Uses FOREGROUND_SERVICE_MEDIA_PLAYBACK for uninterrupted music.
🤝 Contributing
Contributions are welcome! If you want to improve Kanaz Player:
Fork the repository.
Create a Feature Branch (git checkout -b feature/AmazingFeature).
Commit your changes (git commit -m 'Add some AmazingFeature').
Push to the branch (git push origin feature/AmazingFeature).
Open a Pull Request.
📄 License
Copyright © 2025 GoKanaz.
Distributed under the Apache License, Version 2.0. See the LICENSE file for more details.
Developer: GoKanaz - Modern Android Development Enthusiast



# Kanaz Player
# Kanaz Player
