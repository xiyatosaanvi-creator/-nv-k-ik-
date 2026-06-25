# Testing and Build Guide — Ciyato

This document provides instructions for building and testing the Ciyato Android Launcher.

## 1. Prerequisites
- **Android Studio Koala (2024.1.1)** or newer.
- **JDK 17** (standard with Android Studio).
- **Android Device/Emulator** running API 26 (Android 8.0) or higher.

## 2. Building the APK

### Option A: Android Studio (Recommended)
1.  Open Android Studio.
2.  Select **Open** and choose the `ciyato-android/` directory.
3.  Wait for Gradle to sync. Android Studio will automatically download the required Gradle wrapper JAR and dependencies.
4.  Go to **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5.  The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`.

### Option B: Command Line (Linux/macOS/Windows)
If you have Android SDK and JDK 17 installed:
```bash
./gradlew assembleDebug
```

### Option C: GitHub Actions (Cloud Build)
1.  Push the project to a GitHub repository.
2.  Go to the **Actions** tab.
3.  The **Android Build Debug APK** workflow will run automatically.
4.  Download the `app-debug` artifact from the successful run.

## 3. Testing on a Real Device

### Installation
1.  Transfer `app-debug.apk` to your phone.
2.  Open the APK and tap **Install**. (You may need to allow "Install from unknown sources").

### Launcher Setup
1.  Open the **Ciyato** app.
2.  Complete the **Onboarding** flow.
3.  On the final page, tap **Set Ciyato as Home**.
4.  Select **Ciyato** from the Android "Home app" list.
5.  Press your phone's **Home button**.

### Functional Checklist
- [ ] **Launcher Experience:** Does the home screen match the premium dark design?
- [ ] **App Loading:** Do your real installed apps appear in the smart categories?
- [ ] **App Launching:** Does tapping an app icon open the real application?
- [ ] **App Drawer:** Swipe/tap to open the App Library. Is it organized correctly?
- [ ] **Search:** Use the search bar to find an app. Does it work instantly?
- [ ] **Dashboard:** Open the Ciyato app itself to see the internal control center.
- [ ] **Switch Back:** Go to **Settings > Turn Off Ciyato** and confirm you can easily switch back to your old launcher.

---
*Ciyato — Your phone, organized beautifully.*
