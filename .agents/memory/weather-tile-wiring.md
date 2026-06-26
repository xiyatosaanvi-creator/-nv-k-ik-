---
name: Weather tile refresh consumer wiring
description: CiyatoWeatherTileService writes a SharedPreferences flag; LauncherHomeActivity must consume it on resume.
---

## Rule
Any Quick Settings tile that signals a deferred action via SharedPreferences must have its consumer wired in the host Activity's `onResume()`.

## Why
TileService has an independent lifecycle from the Activity; the only safe handoff is a SharedPreferences flag. If `onResume()` doesn't read and clear the flag, the feature silently does nothing (tile tap → no weather refresh).

## How to apply
- `CiyatoWeatherTileService.PREFS_NAME` / `KEY_REFRESH_REQUESTED_AT` are the public constants.
- `LauncherHomeActivity.onResume()` reads the flag, clears it, and calls `viewModel.forceRefreshWeather(this)` (which bypasses the 30-min cache).
- `LauncherViewModel.forceRefreshWeather()` calls `settings.clearWeatherCache()` then re-fetches.
