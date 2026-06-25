# SECURITY.md — Ciyato Android Launcher

## Privacy First. Always.

Ciyato is designed from the ground up with user privacy as a non-negotiable.

---

## What Ciyato Does

| Action | Status |
|--------|--------|
| Read installed app list (to build launcher) | ✅ Local only |
| Display real app icons | ✅ From device, never uploaded |
| Launch real apps | ✅ Standard Android launch intents |
| Store user preferences (dark mode, density, etc.) | ✅ DataStore, on-device only |
| Display weather/agenda (mock for beta) | ✅ No location permission used |

---

## What Ciyato Does NOT Do

| Action | Status |
|--------|--------|
| Upload app list to any server | ❌ Never |
| Upload icon bitmaps | ❌ Never |
| Upload file names or paths | ❌ Never |
| Upload photo names or metadata | ❌ Never |
| Collect analytics | ❌ Not implemented |
| Track usage patterns | ❌ Not implemented |
| Use background processes to spy | ❌ Never |
| Use Accessibility Service | ❌ Not requested |
| Use notification listener | ❌ Not requested |
| Show fake antivirus/cleaner warnings | ❌ Never |
| Lock the user in | ❌ Switch-back always available |

---

## Permissions Used

| Permission | Reason | Required |
|-----------|--------|----------|
| `QUERY_ALL_PACKAGES` | Ciyato is a launcher and must see installed apps | Yes, for launcher function |

### Permissions NOT used in beta
- `READ_EXTERNAL_STORAGE` / `READ_MEDIA_IMAGES` — only requested when user opens Photos
- `READ_MEDIA_VIDEO` / `READ_MEDIA_AUDIO` — not requested
- `INTERNET` — not requested (all functionality is offline)
- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` — not requested
- `READ_CONTACTS` — not requested
- `READ_CALL_LOG` — not requested
- `CAMERA` — not requested
- `RECORD_AUDIO` / `MICROPHONE` — not requested
- `BIND_ACCESSIBILITY_SERVICE` — not requested
- `BIND_NOTIFICATION_LISTENER_SERVICE` — not requested

---

## Turning Off Ciyato

Ciyato never traps the user. You can always:

1. Go to **Ciyato Settings → Turn Off Ciyato → Switch back to system launcher**
2. This opens Android's **Default Apps / Home App** settings.
3. Select your old launcher.
4. Press Home — your old launcher is active immediately.
5. To uninstall: go to **App Info → Uninstall**.

> Note: Uninstall while Ciyato is still the default Home app will prompt you to switch first. This is normal Android behavior — not a Ciyato restriction.

---

## Data Storage

All preferences are stored via **Jetpack DataStore** in the app's private storage:
- `data/data/com.ciyato.launcher/files/datastore/ciyato_settings.preferences_pb`

This file is:
- Readable only by Ciyato (standard Android sandboxing)
- Deleted when the app is uninstalled
- Never synced to cloud

---

## Reporting Security Issues

If you discover a security issue, please open a private GitHub issue or contact the project maintainer directly. Do not disclose security issues publicly before a fix is available.

---

*Last updated: 2026-06-25*
