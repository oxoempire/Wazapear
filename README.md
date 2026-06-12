# Wazapear 🐸💬

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg?style=flat-square)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple.svg?style=flat-square)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue.svg?style=flat-square)](https://developer.android.com/jetpack/compose)

**Wazapear** is a utility Android application that allows you to send WhatsApp messages and media files to any phone number without needing to add it to your device's contacts list. 

This project is a modern native rewrite (using **Kotlin** and **Jetpack Compose / Material 3**) of a legacy MIT App Inventor application, introducing a premium UI/UX design, custom dark mode, multi-language support, and advanced sharing mechanics.

---

## 🚀 Key Features

* **Direct Messages**: Send text messages directly to any WhatsApp number by selecting its country dial code and typing the number.
* **Country Selector**: Interactive country selector bottom-sheet containing flag icons, country names, search filtering, and automatic input limits (`maxDigits`) matching each country's formatting rules.
* **Settings & Preferences**:
  - **Language Selection**: Toggle application language dynamically between English and Spanish.
  - **Default Country**: Save your most used country so that the app starts pre-configured.
  - **Theme Support**: Custom Light, Dark (matching WhatsApp's official dark palette), and System-default themes.
* **Universal Share Receiver**: 
  - The app registers in the Android system sharing sheet (`ACTION_SEND`).
  - Supports receiving **texts**, **images**, **videos**, **audio**, and **generic documents** shared from other applications (e.g., Google Photos, file explorers, browsers).
  - Shows a dedicated preview banner depending on the file type (rendering image thumbnails, video icons, etc.).
  - Clicking the main button packages the media and forwards it directly to the chat of the selected number on WhatsApp or WhatsApp Business.
* **Clear Inputs**: A one-tap **Borrar** (Clear) button to wipe all fields, clear any active attachment stream, and restore the selected country to your defined default country.
* **About Dialog**: Clean modal screen presenting author information, application version, and a direct link to the developer's GitHub profile.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose with Material Design 3
- **Configuration Persistence**: Android SharedPreferences
- **Dependency Management**: Gradle Version Catalogs (`libs.versions.toml`)
- **Intent Integration**: Android Sharesheet integration (`Intent.ACTION_SEND` with `*/*` mimeType) and targeted WhatsApp packages forwarding (`com.whatsapp` & `com.whatsapp.w4b`).

---

## 📋 Requirements

To build and run this project, you will need:
- **JDK 17** or higher
- **Android SDK** (API 34/compileSdk 34)
- **Gradle 8.5** (included via wrapper)
- An Android Device or Emulator running Android 7.0 (API 24) or higher

---

## 🔨 How to Build

Follow these steps to compile the application from the command line:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/oxoempire/wazapear.git
   cd wazapear
   ```

2. **Configure environment variables (if not set globally)**:
   - On Windows (PowerShell):
     ```powershell
     $env:JAVA_HOME="C:\Path\To\Your\JDK-17"
     $env:ANDROID_HOME="C:\Path\To\Your\Android\Sdk"
     ```
   - On Linux/macOS:
     ```bash
     export JAVA_HOME="/path/to/your/jdk-17"
     export ANDROID_HOME="/path/to/your/android/sdk"
     ```

3. **Build the Debug APK**:
   ```bash
   # On Windows
   ./gradlew.bat assembleDebug

   # On Linux/macOS
   ./gradlew assembleDebug
   ```

The compiled APK will be available in the output directory:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License

This project is open-source and available under the MIT License.

## 👤 Author

Developed with ❤️ by **Manu Cabello** ([oxoempire](https://github.com/oxoempire)).
