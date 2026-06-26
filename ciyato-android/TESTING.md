# Testing and Build Guide — Ciyato (Functional Wiring Phase)

## 1. Prerequisites
- **Android Studio Koala (2024.1.1)** or newer
- **JDK 17** (standard with Android Studio)
- **Android Device/Emulator** running API 26 (Android 8.0) or higher

## 2. Building the APK

### Option A: Android Studio (Recommended)
1. Open Android Studio.
2. Select **Open** and choose the `ciyato-android/` directory.
3. Wait for Gradle to sync (it will download the `documentfile` dependency added in this phase).
4. Go to **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. The APK will be at: `app/build/outputs/apk/debug/Ciyato.apk`

### Option B: Command Line
```bash
cd ciyato-android
./gradlew assembleDebug
```

---

## 3. Functional Wiring Test Checklist

### Core launcher (must not break)
- [ ] Real installed app icons load on home screen
- [ ] Tapping app icon in any grid/list launches the real app
- [ ] App Drawer opens and displays categorized sections
- [ ] Search finds installed apps by label and package name
- [ ] Switch-back via Settings → Home still works
- [ ] APK builds without error

### 1. Category Cards
- [ ] Tapping a category card (Work, Social, Finance, etc.) opens `CategoryDetailScreen`
- [ ] CategoryDetailScreen shows all apps in that category with real icons
- [ ] Search inside category works (filters results live)
- [ ] Tapping app inside CategoryDetailScreen launches the real app
- [ ] Back navigation returns to Home

### 2. App Drawer Sections
- [ ] Tapping a section header expands/collapses it (Work, Social, Utilities, etc.)
- [ ] "Suggested" and "Recently Added" are expanded by default
- [ ] Tapping an app in a section launches the real app
- [ ] Duplicate Shortcuts card in Drawer is clickable → opens `DuplicateShortcutsScreen`

### 3. Duplicate Shortcuts
- [ ] DuplicateShortcutStrip on Home is fully clickable (whole card) → opens `DuplicateShortcutsScreen`
- [ ] "Manage shortcuts" link opens `DuplicateShortcutsScreen`
- [ ] DuplicateShortcutsScreen shows multi-category apps with their category chips
- [ ] Tapping app in DuplicateShortcutsScreen launches the real app
- [ ] Explainer card correctly explains no APK duplication

### 4. Weather
- [ ] Tapping Weather card opens `WeatherDetailScreen`
- [ ] WeatherDetailScreen shows permission explanation before any permission is granted
- [ ] "Enable Local Weather" button triggers the system `ACCESS_COARSE_LOCATION` permission dialog
- [ ] If permission denied: shows permission CTA again
- [ ] If permission granted: shows "Location enabled" state + "Weather API not configured yet" message
- [ ] No crash in any permission state
- [ ] Back navigation works

### 5. Agenda
- [ ] Tapping Agenda/Today card opens `AgendaScreen`
- [ ] AgendaScreen shows Today and Upcoming sample events
- [ ] Calendar permission CTA button shows "coming soon" (does not crash)
- [ ] Add item placeholder is visible
- [ ] Back navigation works

### 6. Files
- [ ] Tapping a Files category tile (Screenshots, Documents, etc.) triggers folder picker if no permission
- [ ] Folder picker uses Android SAF (`ACTION_OPEN_DOCUMENT_TREE`)
- [ ] After selecting a folder, files listed from that folder with real names, sizes, and icons
- [ ] Tapping a file opens it with `ACTION_VIEW` (system viewer)
- [ ] If folder has no files: shows "No files found" state
- [ ] Back navigation from `FileCollectionDetailScreen` returns to `FilesScreen`
- [ ] No `READ_EXTERNAL_STORAGE` is requested
- [ ] No automatic file deletion occurs

### 7. Photos
- [ ] Photos screen shows permission explanation card
- [ ] "Enable" button shows explanation (integration with Photo Picker pending in next build)
- [ ] No crash when tapping any Photos button

### 8. AI Search
- [ ] Searching installed app names returns real results
- [ ] "Work apps" chip opens Work CategoryDetailScreen (or filters Work results)
- [ ] "Recent WhatsApp files" chip opens Files permission flow
- [ ] "Find payment screenshots" chip opens Photos permission flow
- [ ] "Show PDFs from yesterday" chip opens Files permission flow
- [ ] Locked sections shown below app results with "Enable Access" buttons

### 9. Settings
- [ ] Dense / Spacious toggle changes home grid columns (3 vs 2)
- [ ] Gold Accents toggle persists via DataStore
- [ ] Smart Categories toggle persists via DataStore
- [ ] Duplicate Shortcuts toggle shows/hides the strip on Home
- [ ] Reset layout restores defaults
- [ ] Switch back → opens Android Home app settings
- [ ] App Info → opens system App Info screen

### 10. Navigation
- [ ] Home → CategoryDetail → back to Home ✓
- [ ] Home → WeatherDetail → back to Home ✓
- [ ] Home → Agenda → back to Home ✓
- [ ] Home → DuplicateShortcuts → back to Home ✓
- [ ] Home → Drawer → DuplicateShortcuts → back to Home ✓
- [ ] Home → Drawer → back to Home ✓
- [ ] Home → Settings → back to Home ✓

---

## 4. Remaining Mocked / Pending

| Feature | Status |
|---------|--------|
| Live weather (temperature) | Pending API key — shows "not configured" state |
| Real calendar events | Beta mock only — system permission not yet requested |
| Photos full gallery | Photo Picker integration pending; screen shows permission UI |
| File cleanup / delete | Disabled — UI shows suggestion only, no destructive action |
| Voice search | Not implemented in beta |
| Category editing | UI placeholder only |

---

*Ciyato — Organize Smarter. Live Better.*
