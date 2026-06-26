# Security and Privacy Policy — Ciyato

Ciyato is designed with a **Privacy-First** and **Local-First** philosophy. All processing happens on your device. No data is uploaded, shared, or sent to any server.

## Core Security Principles

1. **Local Processing:** All app categorization, file indexing, and smart organization happen entirely on your device.
2. **Permission Minimization:** Permissions are requested only when you enter a specific feature and accept a clear explanation. Nothing is requested at onboarding.
3. **No Hidden Tracking:** Ciyato contains no background analytics, tracking SDKs, or "phone booster" scams.
4. **User Control:** You can switch back to your previous launcher or uninstall at any time.

---

## Permission Details (Functional Wiring Phase)

### Location (`ACCESS_COARSE_LOCATION`)
- **When requested:** Only when you open the Weather card and tap "Enable Local Weather"
- **Never requested:** At onboarding, app launch, or automatically in the background
- **How used:** Approximate location only — to fetch local weather conditions
- **What is NOT done:** No background location, no precise GPS, no location logging, no upload

### File Access (Storage Access Framework)
- **When requested:** Only when you open a Files collection tile and tap "Choose Folder"
- **How used:** You select a specific folder via Android's built-in folder picker (SAF); Ciyato reads only that folder's listing
- **What is NOT done:** No `READ_EXTERNAL_STORAGE`, no `MANAGE_EXTERNAL_STORAGE`, no broad storage access in beta
- **Data stays local:** File names, sizes, and MIME types are displayed on-device only

### Photo Access
- **When requested:** Only when you open Photos and choose to enable organization
- **How used:** Android Photo Picker only in beta — no full gallery access required
- **What is NOT done:** No cloud upload, no off-device AI analysis, no background scanning

### App List (`QUERY_ALL_PACKAGES`)
- **Always granted at install:** Required for launcher function
- **How used:** Loads installed app names, icons, and categories on-device
- **Data stays local:** The app list is never transmitted

### Calendar
- **Not requested in beta:** Sample agenda items shown only
- **Future:** Will be requested only if the user explicitly enables real calendar integration

### Microphone
- **Not requested in beta:** Voice search is not implemented

---

## What Ciyato Does NOT Do

- Does not upload app data, file data, photos, or location to any server
- Does not delete or modify files automatically (cleanup requires explicit user confirmation)
- Does not clone or duplicate APK files ("Duplicate Smart Shortcuts" is visual multi-placement only)
- Does not request background location
- Does not use `MANAGE_EXTERNAL_STORAGE` in beta
- Does not share data with any third party

---

## Data Retention

- All preferences are stored locally using Android DataStore (on-device only)
- File/folder URI permissions are managed by Android SAF and can be revoked at any time in Android Settings
- Uninstalling Ciyato removes all local data

---

## Threat Model

| Threat | Mitigation |
|--------|-----------|
| Location misuse | Foreground-only, never background, explicitly user-triggered |
| File data exfiltration | SAF only — no broad storage access; no network calls from repository |
| Photo leakage | Photo Picker only in beta; no cloud AI; no upload path exists |
| App list exposure | Local use only; no analytics SDK present |
| Accidental file deletion | No automatic deletion; user must confirm every destructive action |
| Launcher lock-in | Switch-back via Settings → Home app always preserved and tested |

---

*Ciyato — Organize Smarter. Live Better.*
