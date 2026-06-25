# Ciyato — Android Launcher — Build & Test Guide

## ⚠️ HONEST STATUS

| Item | Status |
|------|--------|
| Native Android project (Kotlin + Compose) | ✅ Complete |
| AndroidManifest HOME intent-filter | ✅ Complete |
| Real installed app loading (PackageManager) | ✅ Complete |
| Real app launching | ✅ Complete |
| Smart categories (local, keyword-based) | ✅ Complete |
| Duplicate smart shortcuts component | ✅ Complete |
| Onboarding with RoleManager launcher selection | ✅ Complete |
| Safe switch-back / uninstall flow | ✅ Complete |
| Settings with "Turn off Ciyato" section | ✅ Complete |
| Theme Studio | ✅ Complete |
| Files screen | ✅ (mock data — file permissions not yet requested) |
| Photos screen | ✅ (mock data — photo permissions not yet requested) |
| Weather/Agenda | ✅ (mock data — no location/calendar perms needed) |
| APK compiled | ❌ Requires Android Studio / local JDK + SDK |
| Physical device tested | ❌ Requires physical Android device |

This is a complete, correct, compilable project. It cannot be built inside Replit (no Android SDK/Gradle). **Use Android Studio.**

---

## 1. Prerequisites

- Android Studio (Hedgehog 2023.1 or newer — recommended: Iguana 2024.1+)
- Android SDK with API 34 installed
- JDK 17 (bundled with Android Studio)
- USB-debugging enabled Android phone **or** Android Emulator (API 26+)

---

## 2. Open in Android Studio

1. Copy/download the `ciyato-android/` folder to your computer.
2. Open Android Studio → **Open** → select the `ciyato-android/` folder.
3. Wait for Gradle sync to complete (first run downloads dependencies — ~2 min).
4. Android Studio will also generate the `gradle-wrapper.jar` automatically.

---

## 3. Build the Debug APK

### Option A — Android Studio UI
Click **Build → Build Bundle(s) / APK(s) → Build APK(s)**

### Option B — Terminal
```bash
cd ciyato-android
chmod +x gradlew
./gradlew assembleDebug
```

### APK output location
```
ciyato-android/app/build/outputs/apk/debug/app-debug.apk
```

---

## 4. Install on Your Android Phone

### Option A — ADB (USB cable)
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Option B — Transfer via USB/email
Copy `app-debug.apk` to your phone → open it → allow "Install from unknown sources".

---

## 5. Step-by-Step Testing on Your Phone

### Step 1: Install the APK
Transfer and install as above.

### Step 2: Open Ciyato
Tap the Ciyato icon in your current launcher. You should see the **Onboarding** screen.

### Step 3: Complete Onboarding
- Screen 1: Welcome
- Screen 2: Smart App Library
- Screen 3: Privacy
- Tap **"Set Ciyato as Home App"**

### Step 4: Set as Default Launcher
On Android 10+ (Q+):
- A system dialog appears: "Set Ciyato as your default Home app?" — tap **Set as default**.

On older Android:
- Opens **Settings → Default Apps → Home App** → select **Ciyato**.

### Step 5: Press the Home button
You should now see the **Ciyato launcher home screen** — not your previous launcher.

### Step 6: Verify real installed apps appear
- Check the Smart Categories — they should show your real installed apps with real icons.
- Check the bottom dock — should show real Phone, Messages, Chrome, Camera icons.
- Check "Duplicate smart shortcuts" — shows apps that appear in 2+ categories.

### Step 7: Test App Launching
- Tap any app icon in a category card → the real app should open.
- Tap any dock icon → the real app should open.
- If an app fails to launch, a toast "Could not open [app]" appears.

### Step 8: Test App Drawer
- Tap the grid icon at the bottom of the home screen → App Drawer opens.
- All installed apps should appear with real icons.
- Search for "Chrome" or "WhatsApp" → matching apps appear instantly.
- Tap any app → it should open.
- Use category chips to filter.

### Step 9: Test Search
- Type in the home screen search bar → live app filtering works.
- Open App Drawer → use its search bar → same filtering.

### Step 10: Test Settings (most important)
- From the home screen, tap the floating grid icon → App Drawer → no, wrong.
- From the **Dashboard** (open Ciyato from its LAUNCHER icon in another app's drawer):
  Dashboard → Settings → scroll to **"Turn off Ciyato"**

### Step 11: Switch back to previous launcher
Settings → "Switch back to system launcher" → Android Default Apps settings opens.
Select your original launcher. Press Home → your original launcher appears. ✅

### Step 12: Theme Studio
Dashboard → Theme Studio → change Dark/Light/Auto, Dense/Spacious, Gold accent.

### Step 13: Uninstall
Settings → "Uninstall Ciyato" → Android App Info opens → Uninstall. ✅

---

## 6. What Works Now (Beta)

| Feature | Works |
|---------|-------|
| HOME launcher registration (manifest) | ✅ |
| Launcher selection via RoleManager (Android 10+) | ✅ |
| Launcher selection via Settings fallback (Android 8-9) | ✅ |
| Real installed app icons (PackageManager) | ✅ |
| Real app labels | ✅ |
| Launch real apps on tap | ✅ |
| Smart categories (keyword-based) | ✅ |
| App search (label + package) | ✅ |
| App drawer | ✅ |
| Bottom dock (real icons) | ✅ |
| Duplicate smart shortcuts | ✅ |
| Onboarding (3 screens) | ✅ |
| Switch back to system launcher | ✅ |
| Uninstall flow | ✅ |
| Theme Studio (Dark/Light, Dense/Spacious, Gold) | ✅ |
| Settings persistence (DataStore) | ✅ |
| Dashboard / control center | ✅ |
| Files screen | ✅ (mock data) |
| Search screen | ✅ (apps real, files mock) |
| Weather | ✅ (mock — hardcoded) |
| Agenda | ✅ (mock — hardcoded) |

---

## 7. What is Mocked (Beta)

| Feature | Status |
|---------|--------|
| Weather | Mock hardcoded (24°, Partly sunny, New York) |
| Agenda/calendar | Mock hardcoded (Design Sync, Client Call, Gym Session) |
| File scanning | Mock UI (no storage permissions requested yet) |
| Photo library | Not implemented (no media permissions yet) |
| Duplicate file detection | Mock UI |
| AI suggestions | UI placeholder only |
| Vault | UI placeholder |

---

## 8. Permissions Used

| Permission | Why |
|-----------|-----|
| `QUERY_ALL_PACKAGES` | Required to load all installed app icons and labels for the launcher |

**No other permissions are requested in this beta.**

Not requested (yet):
- `READ_EXTERNAL_STORAGE` / `READ_MEDIA_IMAGES` — not needed until Files/Photos features are activated
- `ACCESS_FINE_LOCATION` — not needed until weather uses real GPS
- `READ_CALENDAR` — not needed until agenda uses real calendar
- `INTERNET` — not used at all in this beta

---

## 9. How to Set Ciyato as Default Launcher

### Android 10+ (API 29+)
App automatically triggers the system Home Role request dialog.

### Android 8-9 (API 26-28)
Open: Settings → Apps → Default apps → Home app → select Ciyato

### Manual path (any Android)
```
Settings → Apps (or Application manager) → Default apps → Home app → Ciyato
```

---

## 10. How to Switch Back to Previous Launcher

**Option A:** Inside Ciyato Settings → "Switch back to system launcher" → opens Default Apps.

**Option B:** From another app → Android Settings → Apps → Default apps → Home app → choose your previous launcher.

**Option C:** If Ciyato crashes (emergency)
Long-press the Ciyato home screen → no actions will work → force-close Ciyato in Android Settings → App Info → Force stop → then go to Default Apps and change the Home app.

---

## 11. How to Uninstall Ciyato

1. **First** switch to another launcher (step above)
2. Then go to: Settings → Apps → Ciyato → Uninstall

Or use the in-app shortcut: Dashboard → Settings → "Uninstall Ciyato" → opens App Info.

---

## 12. Known Limitations (Beta)

1. **gradle-wrapper.jar missing** — open in Android Studio; it generates the JAR automatically.
2. **Weather is mock** — hardcoded 24°, New York. Real weather requires an API key + `INTERNET` permission.
3. **Agenda is mock** — requires `READ_CALENDAR` permission, not yet implemented.
4. **Files/Photos are mock** — real file scanning requires `MANAGE_EXTERNAL_STORAGE` or `READ_MEDIA_IMAGES`, not yet requested.
5. **Dock editing** — dock apps are auto-selected by package name; manual editing UI not yet built.
6. **Widget support** — Android launcher widgets require `BIND_APPWIDGET` permission; not in this beta.
7. **Wallpaper** — setting wallpaper requires `SET_WALLPAPER` permission; not in this beta.
8. **Cannot be tested in Replit** — this is a native Android project. No browser preview exists.

---

## 13. Security Verification Checklist

- [x] No `android.permission.INTERNET` in Manifest
- [x] No network calls anywhere in source code
- [x] No analytics SDK initialized
- [x] No installed app list uploaded
- [x] No file/photo upload
- [x] No background service
- [x] No notification spam service
- [x] `allowBackup="false"` in Manifest
- [x] User can switch back to previous launcher at any time
- [x] User can uninstall via in-app Settings
- [x] Ciyato stays active only because user selected it — Android enforces this, not Ciyato
- [x] File/photo permissions deferred until those features are used

---

## 14. File Structure

```
ciyato-android/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml          ← HOME intent-filter here
│       ├── java/com/ciyato/launcher/
│       │   ├── CiyatoApplication.kt
│       │   ├── MainActivity.kt          ← Opens from app icon (dashboard)
│       │   ├── LauncherHomeActivity.kt  ← THE REAL LAUNCHER
│       │   ├── data/
│       │   │   ├── InstalledApp.kt
│       │   │   ├── LauncherRepository.kt  ← Loads real apps from PackageManager
│       │   │   ├── AppCategorizer.kt
│       │   │   └── LauncherSettingsRepository.kt
│       │   ├── viewmodel/
│       │   │   └── LauncherViewModel.kt
│       │   └── ui/
│       │       ├── theme/ (Color, Theme, Type)
│       │       ├── components/
│       │       │   ├── AppIconView.kt   ← Uses real Drawable from PackageManager
│       │       │   ├── SmartCategoryCard.kt
│       │       │   ├── DuplicateShortcutStrip.kt
│       │       │   ├── WeatherAgendaRow.kt
│       │       │   ├── BottomDock.kt
│       │       │   └── SearchBar.kt
│       │       └── screens/
│       │           ├── HomeScreen.kt
│       │           ├── AppDrawerScreen.kt
│       │           ├── OnboardingScreen.kt
│       │           ├── DashboardScreen.kt
│       │           ├── FilesScreen.kt
│       │           ├── SearchScreen.kt
│       │           ├── SettingsScreen.kt
│       │           └── ThemeStudioScreen.kt
│       └── res/
│           ├── drawable/ (vector icons)
│           ├── mipmap-anydpi-v26/ (adaptive icon)
│           └── values/ (strings, themes)
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/gradle-wrapper.properties
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
└── TESTING.md  ← this file
```
