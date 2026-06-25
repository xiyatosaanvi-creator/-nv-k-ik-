# PROJECT_AUDIT.md — Ciyato Android Launcher
**Date:** 2026-06-25  
**Auditor:** Superagent / automated audit pass

---

## 1. What Exists Currently

### ciyato-android/ — Native Android Project ✅
Full Kotlin + Jetpack Compose native Android launcher project.

| File | Status |
|------|--------|
| AndroidManifest.xml | ✅ Correct HOME + DEFAULT intent-filter |
| LauncherHomeActivity.kt | ✅ Real launcher shell with HOME intent |
| MainActivity.kt | ✅ Dashboard entry point with NavHost |
| CiyatoApplication.kt | ✅ Application class |
| LauncherRepository.kt | ✅ Loads real installed apps via PackageManager |
| AppCategorizer.kt | ✅ 80+ known apps + keyword fallback |
| InstalledApp.kt | ✅ Data model with all required fields |
| LauncherSettingsRepository.kt | ✅ DataStore persistence |
| LauncherViewModel.kt | ✅ Connects repo + settings to UI |
| HomeScreen.kt | ✅ Full launcher home with categories, dock, search |
| AppDrawerScreen.kt | ✅ Smart App Library with filter chips |
| OnboardingScreen.kt | ✅ 3-page onboarding + RoleManager home request |
| SettingsScreen.kt | ✅ Switch-back + uninstall guidance |
| DashboardScreen.kt | ✅ Internal control center |
| FilesScreen.kt | ✅ Mock files screen (labelled beta) |
| SearchScreen.kt | ✅ AI search with real app results |
| ThemeStudioScreen.kt | ✅ Dense/Spacious, Dark/Light, Icon style |
| AppIconView.kt | ✅ RealAppIcon + AppIconTile using real Drawable |
| SmartCategoryCard.kt | ✅ Shows real icons per category |
| BottomDock.kt | ✅ Real apps dock |
| DuplicateShortcutStrip.kt | ✅ Multi-category shortcuts with + badge |
| WeatherAgendaRow.kt | ✅ Weather + agenda cards (mock, no permissions) |
| SearchBar.kt | ✅ Ciyato search bar component |
| Color.kt | ✅ Full Ciyato dark + light palette |
| Theme.kt | ✅ MaterialTheme wrapper |
| Type.kt | ✅ Typography scale |
| build.gradle.kts | ✅ AGP 8.5, Kotlin 2.0, Compose BOM |
| gradle/libs.versions.toml | ✅ All dependencies declared |
| gradlew / gradlew.bat | ✅ Present |
| gradle-wrapper.properties | ✅ Gradle 8.7 |

### Web / Other Artifacts (kept as design reference only)
- `artifacts/ciyato/` — React/TypeScript web prototype → **KEEP as design reference**
- `artifacts/ciyato-mobile/` — Expo/React Native attempt → **KEEP for reference, not the deliverable**
- `artifacts/api-server/` — Node/TypeScript API stub → **KEEP**

---

## 2. App Type
**Native Android (Kotlin + Jetpack Compose).** Not web. Not Expo. Not React Native.

---

## 3. Can it produce a real Android APK?
**Yes** — via `./gradlew assembleDebug` in the `ciyato-android/` folder.  
Requires Android SDK (API 26+) and JDK 17.  
Can also be built via GitHub Actions (CI added in this audit).

---

## 4. Can it become a default Android Home launcher?
**Yes.** AndroidManifest.xml already has:
```xml
<category android:name="android.intent.category.HOME" />
<category android:name="android.intent.category.DEFAULT" />
```
LauncherHomeActivity handles this.

---

## 5. Does it load real installed apps?
**Yes.** LauncherRepository uses `PackageManager.queryIntentActivities(ACTION_MAIN + CATEGORY_LAUNCHER)` — the correct API.

---

## 6. Does it display real installed app icons?
**Yes.** `RealAppIcon` composable uses `ri.loadIcon(pm)` → `toBitmap()` → `asImageBitmap()`.

---

## 7. Does tapping apps launch real apps?
**Yes.** `launchApp()` uses `getLaunchIntentForPackage()` with `FLAG_ACTIVITY_NEW_TASK`.

---

## 8. Do smart categories exist?
**Yes.** 16 categories. AppCategorizer covers 80+ known apps + keyword fallback.

---

## 9. Do duplicate shortcuts exist?
**Yes.** `DuplicateShortcutStrip` shows multi-category apps with gold "+" badge.

---

## 10. Is there a switch-back/off option?
**Yes.** SettingsScreen has "Turn Off Ciyato" section that opens Home app settings + App Info.

---

## 11. Security/Privacy issues?
None found. All data is local. No network calls in launcher code.

---

## 12. Screens created
- HomeScreen ✅
- AppDrawerScreen ✅
- OnboardingScreen ✅
- SettingsScreen ✅
- DashboardScreen ✅
- FilesScreen ✅ (mock/beta)
- SearchScreen ✅
- ThemeStudioScreen ✅
- **PhotosScreen ❌ MISSING — to be created**

---

## 13. Screens missing
- `PhotosScreen.kt` — referenced in dashboard but file does not exist

---

## 14. Files to preserve
Everything in `ciyato-android/` — all files are correct and well-structured.

---

## 15. Files to replace/improve
- `ic_launcher_foreground.xml` — default Android vector, needs Ciyato "C✦" branding
- `ic_launcher_background.xml` — plain color, needs dark premium background
- **No `.github/workflows/` directory** — GitHub Actions APK CI missing

---

## 16. What must be built next (this session)
1. ✅ PhotosScreen.kt — create missing screen
2. ✅ GitHub Actions workflow — `.github/workflows/android-debug.yml`
3. ✅ SECURITY.md — required by spec
4. ✅ Improved launcher icon assets (Ciyato branded)
5. ✅ Wire PhotosScreen into MainActivity NavHost
6. ✅ Add Photos nav to DashboardScreen
