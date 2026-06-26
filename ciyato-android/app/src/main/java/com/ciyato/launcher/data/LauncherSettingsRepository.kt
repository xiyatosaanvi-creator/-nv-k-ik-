package com.ciyato.launcher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("ciyato_settings")

/**
 * Persists ALL user preferences locally via DataStore.
 * Implements suggestions: 1, 7, 21, 23, 24, 25, 36, 72, 74, 75, 97, 113, 116, 138, 144, 145.
 * No cloud sync — everything is on-device only.
 */
class LauncherSettingsRepository(private val context: Context) {

    private companion object {
        // ── Core ─────────────────────────────────────────────────────────────
        val KEY_ONBOARDING_DONE        = booleanPreferencesKey("onboarding_done")
        val KEY_DENSE_LAYOUT           = booleanPreferencesKey("dense_layout")
        val KEY_DARK_MODE              = stringPreferencesKey("dark_mode")          // auto | dark | light | amoled
        val KEY_GOLD_ACCENT            = booleanPreferencesKey("gold_accent")
        val KEY_SMART_CATEGORIES       = booleanPreferencesKey("smart_categories")
        val KEY_DUPLICATE_SHORTCUTS    = booleanPreferencesKey("duplicate_shortcuts")
        val KEY_ICON_STYLE             = stringPreferencesKey("icon_style")         // real | rounded | squircle | circle

        // ── Appearance (suggestions 7, 97) ───────────────────────────────────
        val KEY_ICON_SHAPE             = stringPreferencesKey("icon_shape")         // squircle | circle | rounded | raw
        val KEY_FONT                   = stringPreferencesKey("font")               // inter | outfit | dm_sans | syne | geist
        val KEY_WALLPAPER_BLUR         = intPreferencesKey("wallpaper_blur")        // 0–20 intensity

        // ── Weather (suggestions 21, 116) ─────────────────────────────────────
        val KEY_TEMP_UNIT              = stringPreferencesKey("temp_unit")          // C | F
        val KEY_WEATHER_CACHE_JSON     = stringPreferencesKey("weather_cache_json")
        val KEY_WEATHER_CACHE_AT       = longPreferencesKey("weather_cache_at")     // epoch ms

        // ── Organization (suggestions 23, 24, 25) ─────────────────────────────
        val KEY_HIDDEN_APPS            = stringPreferencesKey("hidden_apps")        // comma-separated packageNames
        val KEY_CATEGORY_RENAMES       = stringPreferencesKey("category_renames")  // JSON: {"WORK":"Office"}
        val KEY_RECENTLY_LAUNCHED      = stringPreferencesKey("recently_launched")  // JSON list of pkgNames (max 10)
        val KEY_SHOW_RECENTLY_LAUNCHED = booleanPreferencesKey("show_recently_launched")

        // ── Search (suggestion 36) ────────────────────────────────────────────
        val KEY_RECENT_SEARCHES        = stringPreferencesKey("recent_searches")    // JSON list (max 10)

        // ── Smart Layout (suggestions 72, 74) ─────────────────────────────────
        val KEY_TIME_AWARE_LAYOUT      = booleanPreferencesKey("time_aware_layout")
        val KEY_BEDTIME_MODE           = booleanPreferencesKey("bedtime_mode")
        val KEY_BEDTIME_HOUR           = intPreferencesKey("bedtime_hour")          // default 23

        // ── Focus (suggestion 75) ─────────────────────────────────────────────
        val KEY_FOCUS_MODE_ACTIVE      = booleanPreferencesKey("focus_mode_active")
        val KEY_FOCUS_BLOCKED_CATS     = stringPreferencesKey("focus_blocked_cats") // comma-sep AppCategory names
        val KEY_FOCUS_DURATION_MIN     = intPreferencesKey("focus_duration_min")    // minutes, default 25

        // ── Haptic (suggestion 1) ─────────────────────────────────────────────
        val KEY_HAPTIC_FEEDBACK        = booleanPreferencesKey("haptic_feedback")

        // ── Privacy & Security (suggestions 138, 145, 144) ───────────────────
        val KEY_PRIVACY_MODE           = booleanPreferencesKey("privacy_mode")
        val KEY_SCREENSHOT_BLOCKED     = booleanPreferencesKey("screenshot_blocked")
        val KEY_CRASH_REPORTING        = booleanPreferencesKey("crash_reporting")

        // ── Debug (suggestion 113) ────────────────────────────────────────────
        val KEY_DEBUG_WEATHER_STUB     = booleanPreferencesKey("debug_weather_stub")
        val KEY_DEBUG_LOCATION_STUB    = booleanPreferencesKey("debug_location_stub")
    }

    // ── Flows ─────────────────────────────────────────────────────────────────

    val onboardingDone:       Flow<Boolean> = pref(KEY_ONBOARDING_DONE,     false)
    val denseLayout:          Flow<Boolean> = pref(KEY_DENSE_LAYOUT,        true)
    val darkMode:             Flow<String>  = pref(KEY_DARK_MODE,           "auto")
    val goldAccent:           Flow<Boolean> = pref(KEY_GOLD_ACCENT,         true)
    val smartCategories:      Flow<Boolean> = pref(KEY_SMART_CATEGORIES,    true)
    val duplicateShortcuts:   Flow<Boolean> = pref(KEY_DUPLICATE_SHORTCUTS, true)
    val iconStyle:            Flow<String>  = pref(KEY_ICON_STYLE,          "real")

    val iconShape:            Flow<String>  = pref(KEY_ICON_SHAPE,          "squircle")
    val font:                 Flow<String>  = pref(KEY_FONT,                "inter")
    val wallpaperBlur:        Flow<Int>     = pref(KEY_WALLPAPER_BLUR,      0)

    val tempUnit:             Flow<String>  = pref(KEY_TEMP_UNIT,           "C")
    val weatherCacheJson:     Flow<String>  = pref(KEY_WEATHER_CACHE_JSON,  "")
    val weatherCacheAt:       Flow<Long>    = pref(KEY_WEATHER_CACHE_AT,    0L)

    val hiddenApps:           Flow<String>  = pref(KEY_HIDDEN_APPS,         "")
    val categoryRenames:      Flow<String>  = pref(KEY_CATEGORY_RENAMES,    "{}")
    val recentlyLaunched:     Flow<String>  = pref(KEY_RECENTLY_LAUNCHED,   "[]")
    val showRecentlyLaunched: Flow<Boolean> = pref(KEY_SHOW_RECENTLY_LAUNCHED, true)

    val recentSearches:       Flow<String>  = pref(KEY_RECENT_SEARCHES,     "[]")

    val timeAwareLayout:      Flow<Boolean> = pref(KEY_TIME_AWARE_LAYOUT,   true)
    val bedtimeMode:          Flow<Boolean> = pref(KEY_BEDTIME_MODE,        false)
    val bedtimeHour:          Flow<Int>     = pref(KEY_BEDTIME_HOUR,        23)

    val focusModeActive:      Flow<Boolean> = pref(KEY_FOCUS_MODE_ACTIVE,   false)
    val focusBlockedCats:     Flow<String>  = pref(KEY_FOCUS_BLOCKED_CATS,  "SOCIAL,ENTERTAINMENT,GAMES")
    val focusDurationMin:     Flow<Int>     = pref(KEY_FOCUS_DURATION_MIN,  25)

    val hapticFeedback:       Flow<Boolean> = pref(KEY_HAPTIC_FEEDBACK,     true)

    val privacyMode:          Flow<Boolean> = pref(KEY_PRIVACY_MODE,        false)
    val screenshotBlocked:    Flow<Boolean> = pref(KEY_SCREENSHOT_BLOCKED,  false)
    val crashReporting:       Flow<Boolean> = pref(KEY_CRASH_REPORTING,     true)

    val debugWeatherStub:     Flow<Boolean> = pref(KEY_DEBUG_WEATHER_STUB,  false)
    val debugLocationStub:    Flow<Boolean> = pref(KEY_DEBUG_LOCATION_STUB, false)

    // ── Setters ───────────────────────────────────────────────────────────────

    suspend fun setOnboardingDone(v: Boolean)      = set(KEY_ONBOARDING_DONE,     v)
    suspend fun setDenseLayout(v: Boolean)         = set(KEY_DENSE_LAYOUT,        v)
    suspend fun setDarkMode(v: String)             = set(KEY_DARK_MODE,           v)
    suspend fun setGoldAccent(v: Boolean)          = set(KEY_GOLD_ACCENT,         v)
    suspend fun setSmartCategories(v: Boolean)     = set(KEY_SMART_CATEGORIES,    v)
    suspend fun setDuplicateShortcuts(v: Boolean)  = set(KEY_DUPLICATE_SHORTCUTS, v)
    suspend fun setIconStyle(v: String)            = set(KEY_ICON_STYLE,          v)

    suspend fun setIconShape(v: String)            = set(KEY_ICON_SHAPE,          v)
    suspend fun setFont(v: String)                 = set(KEY_FONT,                v)
    suspend fun setWallpaperBlur(v: Int)           = set(KEY_WALLPAPER_BLUR,      v.coerceIn(0, 20))

    suspend fun setTempUnit(v: String)             = set(KEY_TEMP_UNIT,           v)
    suspend fun setWeatherCache(json: String)      { context.dataStore.edit { it[KEY_WEATHER_CACHE_JSON] = json; it[KEY_WEATHER_CACHE_AT] = System.currentTimeMillis() } }
    suspend fun clearWeatherCache()               { context.dataStore.edit { it[KEY_WEATHER_CACHE_JSON] = ""; it[KEY_WEATHER_CACHE_AT] = 0L } }

    suspend fun setHiddenApps(csv: String)         = set(KEY_HIDDEN_APPS,         csv)
    suspend fun setCategoryRenames(json: String)   = set(KEY_CATEGORY_RENAMES,    json)
    suspend fun setRecentlyLaunched(json: String)  = set(KEY_RECENTLY_LAUNCHED,   json)
    suspend fun setShowRecentlyLaunched(v: Boolean)= set(KEY_SHOW_RECENTLY_LAUNCHED, v)

    suspend fun setRecentSearches(json: String)    = set(KEY_RECENT_SEARCHES,     json)

    suspend fun setTimeAwareLayout(v: Boolean)     = set(KEY_TIME_AWARE_LAYOUT,   v)
    suspend fun setBedtimeMode(v: Boolean)         = set(KEY_BEDTIME_MODE,        v)
    suspend fun setBedtimeHour(v: Int)             = set(KEY_BEDTIME_HOUR,        v.coerceIn(18, 23))

    suspend fun setFocusModeActive(v: Boolean)     = set(KEY_FOCUS_MODE_ACTIVE,   v)
    suspend fun setFocusBlockedCats(csv: String)   = set(KEY_FOCUS_BLOCKED_CATS,  csv)
    suspend fun setFocusDurationMin(v: Int)        = set(KEY_FOCUS_DURATION_MIN,  v.coerceIn(5, 120))

    suspend fun setHapticFeedback(v: Boolean)      = set(KEY_HAPTIC_FEEDBACK,     v)

    suspend fun setPrivacyMode(v: Boolean)         = set(KEY_PRIVACY_MODE,        v)
    suspend fun setScreenshotBlocked(v: Boolean)   = set(KEY_SCREENSHOT_BLOCKED,  v)
    suspend fun setCrashReporting(v: Boolean)      = set(KEY_CRASH_REPORTING,     v)

    suspend fun setDebugWeatherStub(v: Boolean)    = set(KEY_DEBUG_WEATHER_STUB,  v)
    suspend fun setDebugLocationStub(v: Boolean)   = set(KEY_DEBUG_LOCATION_STUB, v)

    suspend fun resetLayout() {
        context.dataStore.edit { p ->
            p[KEY_DENSE_LAYOUT]        = true
            p[KEY_GOLD_ACCENT]         = true
            p[KEY_SMART_CATEGORIES]    = true
            p[KEY_DUPLICATE_SHORTCUTS] = true
            p[KEY_ICON_STYLE]          = "real"
            p[KEY_ICON_SHAPE]          = "squircle"
            p[KEY_WALLPAPER_BLUR]      = 0
        }
    }

    // ── Generic helpers ───────────────────────────────────────────────────────

    private fun <T> pref(key: Preferences.Key<T>, default: T): Flow<T> =
        context.dataStore.data.map { it[key] ?: default }

    private suspend fun <T> set(key: Preferences.Key<T>, value: T) =
        context.dataStore.edit { it[key] = value }
}
