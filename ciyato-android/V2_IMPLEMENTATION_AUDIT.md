# Ciyato V2 Implementation Audit

Specification reviewed: `Ciyato_Comprehensive_Implementation_Specification_Dark_Expanded.pdf`, pages 1-49.

## Active Architecture

- `LauncherHomeActivity` is the Android HOME activity. It hosts Home, the App Library, global launcher search, weather, agenda and launcher settings using local sealed-state navigation.
- `MainActivity` is the internal Ciyato application. It now exposes exactly Home, Files, Search, Photos and Settings as its bottom navigation destinations.
- `LauncherViewModel` owns application inventory, category view state, launcher preferences, weather state and persistent layout helpers.
- `LauncherRepository` scans launchable activities through `PackageManager`, applies user overrides and launches applications.
- `LauncherSettingsRepository` is the current persistence layer. It uses DataStore preferences, not Room.
- Files use user-selected SAF roots and `DocumentFile`. Photos use Android Photo Picker selections. Weather uses foreground location and Open-Meteo.

## V2 Migration Completed In This Slice

- Removed the active `Shared` destination and deleted its unused presentation screens.
- Replaced the rejected dashboard quick actions, privacy summary and feature list with a factual internal Home based on local state.
- Kept global launcher search and internal file/media search as separate active routes.
- Replaced the active stacked/filter-chip App Library with compact group clusters and one expanded group surface.
- Removed the permanent App Library button from Home; the existing intentional upward gesture remains the entry point.
- Restricted automatic Home Smart Categories to Work, Social, Finance, Creativity, Utilities and Daily. Other classifications remain available to the App Library and manual workspaces.
- Restored system wallpaper as the default and removed the decorative continuous Home background animation.

## Data And Migration Notes

- Existing `dashboard` deep links map to internal `home`; legacy `shared` deep links map to `photos`.
- Existing DataStore preferences are preserved. No destructive preference migration was performed.
- Existing manual category, app, dock, workspace and appearance preferences remain intact.
- The current workspace persistence is JSON/CSV in DataStore. It cannot yet meet the V2 stable-ID/order/migration requirements; a versioned Room model is required before a full workspace engine migration.

## Dependency Audit

- Active and justified: Compose, Navigation, DataStore, DocumentFile, OkHttp, Coil, lifecycle and coroutines.
- Declared but not yet used for the required production role: Room, WorkManager and Paging. The V2 file index, cleanup engine, search history and workspace model should use these rather than adding more preference blobs.
- Legacy/experimental presentation candidates remain in source and must be removed only after their dependencies and navigation references are mapped: `DuplicateShortcutStrip`, `SwipeableHomeDrawer`, `MultiPageHomeScreen`, old widget experiments and many disconnected feature screens.

## Permission And Policy Audit

- `QUERY_ALL_PACKAGES` is required for a general Android launcher but needs a matching Play Console declaration.
- Location is foreground-only and requests fine plus coarse together. Android can still grant approximate access.
- Calendar permission is declared but must have a contextual connection flow and a real event source before Today is production-ready.
- Photo Picker avoids broad photo access. SAF access is user-selected and persisted.
- There is no background location, all-files declaration or unsafe file URI exposure in the manifest.

## Remaining V2 Work

1. Replace preference-based workspace state with a versioned persistent placement model, stable workspace IDs and real drag/reflow/undo semantics.
2. Build the V2 Files data layer: authorised-root scanning, background checkpoints, real categories, cryptographic duplicate confirmation and explicit deletion results.
3. Replace internal media search's direct broad `MediaStore` assumptions with an index scoped to granted SAF/media access, including denial and revocation states.
4. Finish Wallpaper Studio with Photo Picker image input, Ciyato-only short-video background handling and honest system-wallpaper language.
5. Audit every Settings control for an observable persisted effect; remove any decorative setting.
6. Add a classifier confidence model, Review fallback and user-correction persistence that cannot be overwritten by automation.
7. Add accessibility and migration tests, then validate gestures, permission paths, wallpaper behavior and layout on physical Android devices.

## Verification In This Slice

- `assembleDebug`: passed without an emulator.
- `testDebugUnitTest`: passed without an emulator.
- `git diff --check`: passed.
- Physical-device validation remains required before any completion claim.
