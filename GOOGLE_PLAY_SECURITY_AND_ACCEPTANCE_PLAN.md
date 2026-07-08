# GOOGLE PLAY SECURITY AND ACCEPTANCE PLAN
## Ciyato — AI Phone Organizer for Android
### Internal Operating Document · Confidential

---

## 1. WHAT CIYATO IS

Ciyato is an Android launcher and intelligent phone organizer. It replaces the default home screen with a cleaner, smarter experience — organizing installed apps into contextual categories, surfacing duplicate shortcuts, and providing an AI-powered search and content discovery layer.

**Core capabilities (Phase 1 release scope):**
- Android launcher / home screen replacement
- Smart app library with automatically generated categories (Work, Social, Finance, Creativity, Utilities, Daily, etc.)
- Duplicate smart shortcuts: one app accessible from multiple categories
- Personalization: themes, layout density, icon styles, wallpapers, hidden apps
- Before/After onboarding experience to explain what changes

**Companion capabilities (later phases):**
- Smart file and photo organization (Ciyato Files, Ciyato Photos)
- AI-powered cross-app search
- Optional cloud-enhanced features (opt-in only)

**What Ciyato does NOT do:**
- Does not uninstall apps
- Does not modify app data
- Does not alter system permissions of other apps
- Does not silently track or upload user behavior
- Does not display ads inside the launcher
- Does not require an account to use the core launcher

---

## 2. CORE TRUST PRINCIPLES

Ciyato must be built and operated according to these non-negotiable principles:

| Principle | Commitment |
|---|---|
| Privacy First | No personal data collection without explicit user consent |
| Minimum Permissions | Request only what is truly needed for each feature |
| Transparent Behavior | Every system action is visible, understandable, and reversible |
| No Misleading Claims | Onboarding and Play Store listing make no false promises |
| No Silent Uploads | Nothing is sent to servers without user-initiated action |
| No Deceptive Device Language | Never use wording that implies full device control or surveillance |
| User Can Switch Back | Android's default app settings are always accessible |
| User Can Disable Features | Each feature group can be independently disabled |
| No Harmful Behavior | No background data harvesting, no battery abuse, no dark patterns |

---

## 3. PERMISSION STRATEGY

Every permission used in Ciyato must be documented here. Any new permission added must be reviewed against this framework before implementation.

### 3.1 Launcher / Home Role

| Property | Detail |
|---|---|
| **What it is** | Android's home app role — allows Ciyato to serve as the default launcher |
| **When requested** | Final onboarding step (Screen 6), after full explanation |
| **Is it optional?** | Yes — user can skip and use Ciyato as a secondary experience |
| **How explained** | Onboarding Screen 6 explicitly states: "This changes how your home screen looks and feels. Nothing is deleted. You can switch back at any time from Android Settings." |
| **If denied** | App opens but shows explore/prototype mode; user can manually set via Settings → Default Apps |

### 3.2 Installed App Visibility (`QUERY_ALL_PACKAGES`)

| Property | Detail |
|---|---|
| **What it is** | Allows Ciyato to read the list of installed apps for category generation |
| **When requested** | On first launch, immediately after onboarding |
| **Is it optional?** | No — core categorization depends on it |
| **How explained** | Onboarding Screen 2 explains: "Ciyato intelligently organizes your installed apps into smart categories." |
| **If denied** | Feature is disabled; user sees empty launcher with notice to grant permission |
| **Play Store notes** | Must declare use in Play Store Data Safety form; requires policy justification for apps targeting API 30+ |

### 3.3 Photo / Media Access (`READ_MEDIA_IMAGES`)

| Property | Detail |
|---|---|
| **What it is** | Access to the device photo library for Ciyato Photos smart collections |
| **When requested** | Only when user opens Ciyato Photos — not at first launch |
| **Is it optional?** | Yes — core launcher works without it |
| **How explained** | Contextual rationale shown at point of request: "To organize your photo collections, Ciyato needs access to your gallery." |
| **If denied** | Ciyato Photos feature is disabled with clear messaging; rest of app functions normally |

### 3.4 File / Storage Access (`READ_EXTERNAL_STORAGE` / Scoped Storage)

| Property | Detail |
|---|---|
| **What it is** | Access to the device file system for Ciyato Files smart organization |
| **When requested** | Only when user opens Ciyato Files — not at first launch |
| **Is it optional?** | Yes — core launcher works without it |
| **How explained** | Contextual rationale: "To organize your files, Ciyato needs access to your storage." |
| **If denied** | Ciyato Files feature is hidden; rest of app functions normally |
| **Implementation note** | Use Android scoped storage APIs. Never request `MANAGE_EXTERNAL_STORAGE` unless absolutely required and policy-approved. |

### 3.5 Location (Coarse, for Weather only)

| Property | Detail |
|---|---|
| **What it is** | Approximate location for displaying weather widget on home screen |
| **When requested** | Only when user enables weather widget |
| **Is it optional?** | Yes — user can enter city manually instead |
| **How explained** | "To show local weather, Ciyato needs your approximate location." |
| **If denied** | Manual city input is offered; widget shows generic state |
| **Important** | Use `ACCESS_COARSE_LOCATION` only — never `ACCESS_FINE_LOCATION` for weather. Never request background location. |

### 3.6 Notification Access

| Property | Detail |
|---|---|
| **What it is** | Allows reading notification counts for app badges |
| **When requested** | Only if notification badge feature is implemented and user enables it |
| **Is it optional?** | Yes |
| **If denied** | Badges are hidden; core launcher unaffected |
| **Implementation note** | Never use notification access to read notification content |

---

## 4. PERMISSIONS WE MUST AVOID

The following permissions are high-risk and must **not** be used unless strictly required, fully justified, and policy-compliant:

| Permission | Risk Level | Policy |
|---|---|---|
| `MANAGE_EXTERNAL_STORAGE` | 🔴 Very High | Requires explicit Google Play approval. Only use if scoped storage cannot achieve the needed functionality. |
| `BIND_ACCESSIBILITY_SERVICE` | 🔴 Very High | Forbidden for most apps. Only allowed if core functionality truly requires it and cannot be achieved other ways. Never use for UI automation or passive monitoring. |
| `BIND_NOTIFICATION_LISTENER_SERVICE` | 🟠 High | May be used for badge counts only — must never read or log notification content. |
| `READ_CALL_LOG` / `PROCESS_OUTGOING_CALLS` | 🔴 Very High | Not needed for Ciyato's use case. Do not implement. |
| `RECORD_AUDIO` | 🟠 High | Only if AI voice search is implemented — then contextual only, never background. |
| `CAMERA` | 🟡 Medium | Only if user-initiated photo capture is a feature. Never passive. |
| `ACCESS_BACKGROUND_LOCATION` | 🔴 Very High | Do not implement. Coarse foreground-only location is sufficient for weather. |
| `DEVICE_ADMIN` | 🔴 Extreme | Do not implement. |

---

## 5. GOOGLE PLAY COMPLIANCE MINDSET

### Least Privilege
Only request permissions when the user is directly engaging with the feature that needs them. Batch permission requests are a red flag.

### User Benefit Must Be Clear
Every permission request must be preceded by a clear, honest, user-friendly explanation of why it benefits them — not Ciyato.

### Permission Use Must Match Core Functionality
A launcher app requesting camera, microphone, and contacts at install time would be flagged immediately. Only request what a launcher genuinely needs.

### Permission Rationale Must Be Contextual
Explain permissions at the moment they are needed — not in a wall-of-text upfront.

### App Must Not Appear Deceptive
- Do not make the app appear more powerful than it is
- Do not make the app appear to have system-level control it does not have
- Do not use security or "cleaner" language that implies invasive behavior

### Privacy Policy Must Match Actual Behavior
The Privacy Policy hosted externally and linked in Play Store must be written after the actual data flows are confirmed — not before. It must accurately reflect what data is collected, processed, and stored.

### Data Safety Declarations Must Be Accurate
Complete the Play Store Data Safety form accurately:
- Declare which data types are collected
- Declare whether data is shared with third parties
- Declare whether data can be deleted by users
- Do not leave any field blank if data is touched

### Onboarding Must Not Overclaim
Forbidden phrases:
- "Full control over your phone"
- "Manages all your device settings"
- "AI sees everything on your phone"
- "Complete access to your files"

Acceptable phrases:
- "Organizes how your apps appear and are accessed"
- "Displays your files in smart collections"
- "AI-powered search helps you find things faster"
- "You stay in control at all times"

### No Vague "AI Needs Everything" Logic
Do not use AI as a justification for broad permissions. AI features must only use permissions that are individually justified.

---

## 6. SECURITY ARCHITECTURE EXPECTATIONS

### No Hardcoded Secrets
- No API keys, tokens, or credentials in source code or app resources
- Use Android Keystore for any stored credentials
- Use environment-specific build configs for server endpoints

### Secure Local Storage
- Use `EncryptedSharedPreferences` for any sensitive user preferences
- Avoid storing sensitive data in plain SharedPreferences
- Do not store authentication tokens in plain text files

### Encrypted Sensitive Preferences
- Theme and layout preferences: standard SharedPreferences (non-sensitive)
- Any user account data: EncryptedSharedPreferences
- Any session tokens: Android Keystore

### Account and Session Safety (if login is added)
- Use industry-standard OAuth 2.0 or OpenID Connect
- Store tokens securely in Android Keystore
- Implement token refresh and expiry
- Allow full account deletion from within the app

### No Exporting Private User Data Without Consent
- Never transmit installed app lists to any server without explicit opt-in
- Never transmit file metadata without explicit opt-in
- Provide a clear data export/delete flow if any data is stored server-side

### Safe Logging Practices
- Do not log file names, app names, or any user-identifiable content at INFO or higher log levels in release builds
- Strip all verbose logs in release builds using ProGuard/R8 rules
- Use log levels appropriately: DEBUG only in debug builds

### Crash Handling Without Leaking Sensitive Information
- Use a crash reporting SDK (e.g., Firebase Crashlytics) that is GDPR-compliant
- Ensure crash reports do not include file contents, app lists, or personal data
- Review crash report data fields before enabling

---

## 7. PRIVACY ARCHITECTURE EXPECTATIONS

### On-Device-First Approach
All core launcher functionality (app categorization, smart shortcuts, theme application, file organization display) must work entirely on-device without any server calls.

### File and Photo Scanning — Local Only
- File and photo scanning is performed locally on the device
- No file content is uploaded
- No photo is analyzed by remote AI without explicit user consent
- Thumbnails and previews are generated from device data only

### If Cloud AI Is Added Later — Opt-In and Transparent
When cloud-based AI features are introduced:
1. Must be clearly labeled as "cloud-powered" in UI
2. Must be disabled by default
3. Must explain exactly what data is sent (query text, file names, etc.)
4. Must provide a way to use the feature locally instead, if possible
5. Must update Privacy Policy and Data Safety form before launch

### User Data Categories

| Data Type | Stored Locally | Sent to Server | User Controlled |
|---|---|---|---|
| Installed app list | Yes (cache) | No | Yes (clear cache) |
| Theme preferences | Yes | No | Yes (reset) |
| Layout preferences | Yes | No | Yes (reset) |
| File metadata | No (displayed only) | No | N/A |
| Photo metadata | No (displayed only) | No | N/A |
| Weather city | Yes (if entered) | Yes (weather API query only) | Yes (change/delete) |
| Search queries | No | No | N/A |
| Crash reports | No | Yes (crash reporter only) | Configurable |

### What Is Not Collected
- Personal identity information
- Device IMEI or unique hardware identifiers
- Contact data
- Communication metadata (calls, SMS)
- Location history
- Behavioral analytics (unless explicitly opted-in to analytics)
- App usage frequency (unless explicitly opted-in to AI learning)

### Deletion and Reset Controls
- "Reset Ciyato" option in Settings must clear all local preferences and cached data
- If server-side data exists (future feature): "Delete my account and all data" must be available in-app

---

## 8. UX FOR TRUST

### Onboarding Must Explain Permissions Clearly
- Each onboarding screen primes the user for what they will be asked later
- Screen 4 (Privacy) explicitly states: permissions are requested only when needed
- Screen 6 (Setup) explains the home app role before the system dialog appears

### Setup Flow Must Reassure the User
- The home launcher dialog is preceded by an explanation screen
- "You can switch back anytime" appears on the final onboarding screen
- No onboarding screen presents a permission dialog without prior explanation

### Settings Must Show What Features Are Active
- A "Ciyato Permissions" or "Active Features" section in Settings
- Each feature group shows: Enabled / Disabled / Permission Granted / Permission Needed
- User can disable any feature group from Settings without reinstalling

### Users Must Be Able to Switch Back from Ciyato Launcher
- Settings → Active Features → Launcher Role → "Change Default Launcher"
- Tapping this guides the user to Android's system Default Apps screen
- This is always accessible and never hidden

### Users Must Be Able to Reset Preferences
- Settings → Advanced → Reset Ciyato
- Clears: themes, layout, hidden apps, cached app categories
- Does not delete: Android apps, files, or photos

### Users Must Understand What "Hidden Apps" and "Removed from Display" Mean
- When hiding an app: show a dialog stating "This removes [App] from your Ciyato home screen. The app is not uninstalled and can be found in your full app list."
- "Hidden" label is shown clearly in the hidden apps list
- A "Restore all hidden apps" option is always available

### Nothing Should Imply Uninstalling When It Only Hides
- Never use the word "remove" without clarifying it means "remove from display"
- Never use the word "delete" for app visibility changes
- Use: "Hide from launcher" or "Remove from home screen" — not "Remove app" or "Delete"

---

## 9. RISK REGISTER

| Risk | Severity | Mitigation |
|---|---|---|
| Over-requesting permissions at install | 🔴 High | Request only at point of feature use; document all permissions in this plan before implementing |
| Misleading permission rationale dialog copy | 🟠 Medium | All permission rationale strings reviewed by non-technical team member for clarity |
| Using `MANAGE_EXTERNAL_STORAGE` without approval | 🔴 High | Use scoped storage APIs; only escalate if proven impossible; apply for Play approval first |
| Claiming AI features that are not real | 🔴 High | Feature descriptions must match implemented capabilities; AI labels only for real ML/AI flows |
| Not providing switch-back safety | 🔴 High | Always-visible "Change Launcher" option in Settings; tested before every release |
| File/photo access not being optional | 🟠 Medium | Ciyato Files and Ciyato Photos are feature-gated; core launcher works without them |
| Confusing users about app hiding vs. uninstalling | 🔴 High | Mandatory confirmation dialog with explicit "not uninstalled" language for hide actions |
| Unclear data handling in Privacy Policy | 🔴 High | Privacy Policy written after Data Safety form is completed; reviewed by legal counsel |
| Accessibility permission abuse risk | 🔴 Extreme | Do not implement accessibility services unless unavoidable; document and justify to Google |
| Background monitoring risk | 🔴 High | No background services that continuously monitor user activity; all background work is user-initiated |
| App appearing on restricted app lists | 🟠 Medium | Avoid all permission combinations that trigger Play Protect warnings; test with Play Integrity API |
| Crash on permission denial | 🟡 Low-Medium | Every permission denial path tested; app must degrade gracefully with clear user messaging |
| Debug logs containing sensitive data | 🟡 Medium | ProGuard rules strip debug logs; LogCat reviewed before release |

---

## 10. DEVELOPER CHECKLIST (PRE-RELEASE)

### Permissions
- [ ] Every `<uses-permission>` in AndroidManifest.xml is documented in Section 3 of this plan
- [ ] Every permission has a contextual rationale string in `strings.xml`
- [ ] No permission is requested before the relevant feature is opened
- [ ] All permission denial states are tested and handled gracefully
- [ ] No permission causes a crash if denied

### Play Store Compliance
- [ ] Privacy Policy URL is live and accurate
- [ ] Data Safety form is completed and matches actual data flows in this document
- [ ] App description contains no forbidden phrases (see Section 5)
- [ ] Screenshots and preview video accurately represent the app
- [ ] Content rating questionnaire is completed accurately
- [ ] Target API level meets current Play Store requirements

### Onboarding
- [ ] Onboarding language reviewed against Section 5 forbidden phrases
- [ ] Every permission used in the app is explained (or primed) in onboarding
- [ ] Screen 6 "Set as Home App" dialog is preceded by the explanation screen
- [ ] "Explore first" / "Skip for now" option on final onboarding screen functions correctly
- [ ] Onboarding can be skipped and re-entered from Settings → About → Onboarding

### Safety and UX
- [ ] "Switch back to default launcher" path is tested and accessible
- [ ] "Reset Ciyato preferences" in Settings clears all local data
- [ ] Hiding an app shows confirmation dialog with "not uninstalled" language
- [ ] "Unhide all apps" option is accessible and functional
- [ ] No dead buttons or placeholder actions in release build
- [ ] No mocked/demo features labeled as real features

### Security
- [ ] No hardcoded API keys or tokens in source code
- [ ] Sensitive preferences use EncryptedSharedPreferences
- [ ] Debug log statements removed or stripped in release build
- [ ] Crash reporter is configured and does not capture PII
- [ ] ProGuard/R8 is enabled for release builds
- [ ] Network calls use HTTPS only; no HTTP fallback

### Testing
- [ ] Tested on minimum supported Android version (API 26+)
- [ ] Tested on latest Android version
- [ ] Tested with every permission individually denied
- [ ] Tested after fresh install with no prior data
- [ ] Tested after switching from another launcher and back

---

## 11. RECOMMENDED RELEASE POLICY

A phased release approach reduces review risk, improves quality, and builds user trust incrementally.

### Phase 1 — Launcher Only (First Public Release)
**Scope:** Home launcher, smart app categories, duplicate shortcuts, theme studio, personalization, onboarding, settings.
**Permissions:** Launcher role, `QUERY_ALL_PACKAGES`, optional coarse location (weather).
**Goal:** Establish Ciyato as a clean, premium, trustworthy launcher. Build initial user base and reviews.
**Review risk:** Low. Launcher apps are well understood by Google Play review teams.

### Phase 2 — Contextual File and Photo Features
**Scope:** Ciyato Files smart organization, Ciyato Photos collections.
**Permissions:** Adds `READ_MEDIA_IMAGES`, scoped file access — both optional, feature-gated.
**Goal:** Extend value beyond the launcher without changing the core trust model.
**Review risk:** Low-Medium. Permissions are well-justified by new feature scope.

### Phase 3 — Smarter AI Assistant (On-Device)
**Scope:** On-device AI search, smart categorization improvements, contextual suggestions.
**Permissions:** No new permissions required (uses existing app/file/photo access).
**Goal:** Deliver the AI-powered organizing promise with on-device privacy.
**Review risk:** Low. No new permissions, behavior is user-initiated.

### Phase 4 — Optional Cloud-Enhanced Features (Opt-In)
**Scope:** Cloud-backed search indexing, multi-device sync (optional), enhanced AI suggestions (opt-in).
**Permissions:** Internet access (already declared). New: account creation (optional).
**Goal:** Premium tier features for users who want deeper capability and sync.
**Review risk:** Medium. Requires updated Privacy Policy, Data Safety form re-submission, and clear opt-in UX.

---

## 12. WHAT CIYATO MUST DO TO FEEL SAFE TO GOOGLE AND SAFE TO USERS

This is the final summary of the principles that must guide every product, engineering, and marketing decision.

### Transparent Permission Usage
Every permission is earned, not assumed. Users understand — before granting — exactly what each permission enables, why it helps them, and what happens if they decline. Permissions are never bundled, never front-loaded, and never used beyond their stated purpose.

### Beautiful but Honest Onboarding
The onboarding flow sets accurate expectations. It communicates what Ciyato does, what it does not do, and how users can stay in control. Language is warm, premium, and product-led — but never exaggerated or misleading. It must pass a simple test: if a user reads it and uses the app, they should feel the experience matches exactly what they were promised.

### Strong Security
Ciyato handles device-level data with the rigor of a financial app. No secrets in code. Encrypted local storage. Minimal and justified network activity. Clean crash handling. Release builds strip all debug information. Security is a baseline, not a feature.

### Clear User Control
At any moment, users can:
- Switch back to their previous launcher
- Reset all Ciyato preferences
- Unhide all hidden apps
- Disable any feature group
- Delete all Ciyato data

These controls are never hidden behind dark patterns or multiple confirmation dialogs.

### No Fake Claims
Features that are not yet implemented are not described as if they are. AI features that use real ML models are labeled accurately. Categories generated by heuristics are described as "smart" — not falsely attributed to deep AI when they are rule-based. The roadmap is honest.

### Policy-Friendly Architecture
Ciyato is built to pass review, not to game review. Every architectural decision that touches permissions, data, or background behavior is made with Play Store policies as a hard constraint — not an afterthought. When in doubt, do less, explain more, and request permission later.

---

*Document version: 1.0*
*Last updated: July 2026*
*Owner: Ciyato Product & Engineering Team*
*Review cadence: Before every major release*
