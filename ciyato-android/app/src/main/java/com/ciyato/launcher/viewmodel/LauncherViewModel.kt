package com.ciyato.launcher.viewmodel

import android.app.Application
import android.content.Context
import android.view.Window
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciyato.launcher.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

/**
 * LauncherViewModel — central state hub.
 *
 * Suggestions implemented:
 *  1  Haptic feedback setting exposed
 *  21 Temp unit (°C/°F)
 *  23 App hide list management
 *  24 Category renames
 *  25 Recently launched tracking
 *  36 Recent searches history
 *  37 Usage frequency sort
 *  38 Fuzzy search
 *  40 NLP intent detection
 *  72 Time-aware layout
 *  74 Bedtime mode
 *  75 Focus sessions (FocusSessionManager integration)
 *  103 UiState sealed class
 *  104 Event<T> one-shot events
 *  112 Coroutine dispatcher injection pattern (IO on repo, Main on ViewModel)
 *  113 Debug stubs via settings
 *  116 Weather cache (30-min TTL)
 *  117 Offline state
 *  118 Retry with backoff (in WeatherRepository)
 *  138 Privacy mode
 *  145 Screenshot blocking (FLAG_SECURE)
 */
class LauncherViewModel(app: Application) : AndroidViewModel(app) {

    val repo     = LauncherRepository(app)
    val settings = LauncherSettingsRepository(app)
    // Retained only for a legacy screen that is compiled but no longer reachable.
    val aiOptimizer = AIOptimizerManager(app)

    fun optimizeSystem() {
        viewModelScope.launch {
            aiOptimizer.optimizeSystem(this@LauncherViewModel)
        }
    }
    // ── App list ──────────────────────────────────────────────────────────────

    val apps      get() = repo.apps
    val allApps   get() = repo.allApps
    val isLoading get() = repo.isLoading

    // ── Search ────────────────────────────────────────────────────────────────

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<InstalledApp>> = searchQuery
        .combine(apps) { q, list ->
            when {
                q.isBlank()   -> list
                else          -> repo.search(q).ifEmpty { repo.fuzzySearch(q) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setSearch(q: String) {
        _searchQuery.value = q
    }

    fun recordSearch(q: String) {
        if (q.isNotBlank()) viewModelScope.launch { addRecentSearch(q.trim()) }
    }

    // ── NLP / grouped search (Suggestions 40, 42) ─────────────────────────────

    val nlpSearchResult: StateFlow<Pair<AppCategory?, List<InstalledApp>>?> = searchQuery
        .debounce(400L)
        .map { q -> if (q.length >= 3) repo.nlpSearch(q) else null }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // ── Settings ──────────────────────────────────────────────────────────────

    val onboardingDone     = settings.onboardingDone    .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val homeTipDismissed   = settings.homeTipDismissed  .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val denseLayout        = settings.denseLayout        .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val darkMode           = settings.darkMode           .stateIn(viewModelScope, SharingStarted.Eagerly, "auto")
    val goldAccent         = settings.goldAccent         .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val smartCategories    = settings.smartCategories    .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val duplicateShortcuts = settings.duplicateShortcuts .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val iconStyle          = settings.iconStyle          .stateIn(viewModelScope, SharingStarted.Eagerly, "real")
    val iconShape          = settings.iconShape          .stateIn(viewModelScope, SharingStarted.Eagerly, "squircle")
    val font               = settings.font               .stateIn(viewModelScope, SharingStarted.Eagerly, "inter")
    val wallpaperBlur      = settings.wallpaperBlur      .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val tempUnit           = settings.tempUnit           .stateIn(viewModelScope, SharingStarted.Eagerly, "C")
    val hiddenApps         = settings.hiddenApps         .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val removedApps        = settings.removedApps        .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val dockPackages       = settings.dockPackages       .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val categoryRenames    = settings.categoryRenames    .stateIn(viewModelScope, SharingStarted.Eagerly, "{}")
    val appCategoryOverrides = settings.appCategoryOverrides.stateIn(viewModelScope, SharingStarted.Eagerly, "{}")
    val showRecentlyLaunched= settings.showRecentlyLaunched.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val timeAwareLayout    = settings.timeAwareLayout    .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val bedtimeMode        = settings.bedtimeMode        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val bedtimeHour        = settings.bedtimeHour        .stateIn(viewModelScope, SharingStarted.Eagerly, 23)
    val focusModeActive    = settings.focusModeActive    .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val focusBlockedCats   = settings.focusBlockedCats   .stateIn(viewModelScope, SharingStarted.Eagerly, "SOCIAL,ENTERTAINMENT,GAMES")
    val focusDurationMin   = settings.focusDurationMin   .stateIn(viewModelScope, SharingStarted.Eagerly, 25)
    val hapticFeedback     = settings.hapticFeedback     .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val privacyMode        = settings.privacyMode        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val screenshotBlocked  = settings.screenshotBlocked  .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val crashReporting     = settings.crashReporting     .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val biometricLock      = settings.biometricLock      .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val appLockTimerMin    = settings.appLockTimerMin    .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val networkCallLog     = settings.networkCallLog     .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val useSystemWallpaper = settings.useSystemWallpaper.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val categoryOrder      = settings.categoryOrder     .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val categoryTilesSizes = settings.categoryTilesSizes.stateIn(viewModelScope, SharingStarted.Eagerly, "{}")
    val customCategories   = settings.customCategories  .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val page0Apps          = settings.page0Apps         .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val page2Apps          = settings.page2Apps         .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val filesRootUri       = settings.filesRootUri      .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val drawerStyle        = settings.drawerStyle       .stateIn(viewModelScope, SharingStarted.Eagerly, "smart")

    // ── Setters ───────────────────────────────────────────────────────────────

    fun setOnboardingDone()               = viewModelScope.launch { settings.setOnboardingDone(true) }
    fun dismissHomeTip()                  = viewModelScope.launch { settings.setHomeTipDismissed(true) }
    fun resetGuidance()                   = viewModelScope.launch {
        settings.setHomeTipDismissed(false)
        settings.setOnboardingDone(false)
    }
    fun setDenseLayout(v: Boolean)        = viewModelScope.launch { settings.setDenseLayout(v) }
    fun setDarkMode(v: String)            = viewModelScope.launch { settings.setDarkMode(v) }
    fun setGoldAccent(v: Boolean)         = viewModelScope.launch { settings.setGoldAccent(v) }
    fun setSmartCategories(v: Boolean)    = viewModelScope.launch { settings.setSmartCategories(v) }
    fun setDuplicateShortcuts(v: Boolean) = viewModelScope.launch { settings.setDuplicateShortcuts(v) }
    fun setIconStyle(v: String)           = viewModelScope.launch { settings.setIconStyle(v) }
    fun setIconShape(v: String)           = viewModelScope.launch { settings.setIconShape(v) }
    fun setFont(v: String)                = viewModelScope.launch { settings.setFont(v) }
    fun setWallpaperBlur(v: Int)          = viewModelScope.launch { settings.setWallpaperBlur(v) }
    fun setTempUnit(v: String)            = viewModelScope.launch { settings.setTempUnit(v) }
    fun setTimeAwareLayout(v: Boolean)    = viewModelScope.launch { settings.setTimeAwareLayout(v) }
    fun setBedtimeMode(v: Boolean)        = viewModelScope.launch { settings.setBedtimeMode(v) }
    fun setBedtimeHour(v: Int)            = viewModelScope.launch { settings.setBedtimeHour(v) }
    fun setFocusDurationMin(v: Int)       = viewModelScope.launch { settings.setFocusDurationMin(v) }
    fun setFocusBlockedCats(csv: String)  = viewModelScope.launch { settings.setFocusBlockedCats(csv) }
    fun setHapticFeedback(v: Boolean)     = viewModelScope.launch { settings.setHapticFeedback(v) }
    fun setPrivacyMode(v: Boolean)        = viewModelScope.launch { settings.setPrivacyMode(v) }
    fun setScreenshotBlocked(v: Boolean)  = viewModelScope.launch { settings.setScreenshotBlocked(v) }
    fun setCrashReporting(v: Boolean)     = viewModelScope.launch { settings.setCrashReporting(v) }
    fun setBiometricLock(v: Boolean)      = viewModelScope.launch { settings.setBiometricLock(v) }
    fun setAppLockTimerMin(v: Int)        = viewModelScope.launch { settings.setAppLockTimerMin(v) }
    fun setNetworkCallLog(v: Boolean)     = viewModelScope.launch { settings.setNetworkCallLog(v) }
    fun setShowRecentlyLaunched(v: Boolean)= viewModelScope.launch { settings.setShowRecentlyLaunched(v) }

    fun setUseSystemWallpaper(v: Boolean)  = viewModelScope.launch { settings.setUseSystemWallpaper(v) }
    fun setCategoryOrder(v: String)        = viewModelScope.launch { settings.setCategoryOrder(v) }
    fun setCategoryTilesSizes(v: String)    = viewModelScope.launch { settings.setCategoryTilesSizes(v) }
    fun setCustomCategories(v: String)     = viewModelScope.launch { settings.setCustomCategories(v) }
    fun setPage0Apps(v: String)            = viewModelScope.launch { settings.setPage0Apps(v) }
    fun setPage2Apps(v: String)            = viewModelScope.launch { settings.setPage2Apps(v) }
    fun setFilesRootUri(v: String)         = viewModelScope.launch { settings.setFilesRootUri(v) }
    fun clearFilesRootUri()                = viewModelScope.launch { settings.setFilesRootUri("") }
    fun setDrawerStyle(v: String)          = viewModelScope.launch { settings.setDrawerStyle(v) }

    fun resetLayout() = viewModelScope.launch { settings.resetLayout() }
    fun resetAllPreferences() = viewModelScope.launch {
        settings.resetAllPreferences()
        repo.setHiddenPackages("")
        repo.setRemovedPackages("")
        repo.loadApps()
    }

    // ── Custom Category Customizers ───────────────────────────────────────────

    fun byCustomCategory(name: String): List<InstalledApp> = repo.byCustomCategoryName(name)

    fun addCustomCategory(name: String) = viewModelScope.launch {
        val current = parsePackageCsv(customCategories.value).toMutableList()
        if (name !in current) {
            current.add(name)
            settings.setCustomCategories(current.joinToString(","))
        }
    }

    fun removeCustomCategory(name: String) = viewModelScope.launch {
        val current = parsePackageCsv(customCategories.value).toMutableList()
        current.remove(name)
        settings.setCustomCategories(current.joinToString(","))
        
        // Cleanup overrides mapped to this custom category
        val overrides = try { JSONObject(appCategoryOverrides.value) } catch(_: Exception) { JSONObject() }
        val toRemove = mutableListOf<String>()
        overrides.keys().forEach { key ->
            if (overrides.getString(key) == name) {
                toRemove.add(key)
            }
        }
        toRemove.forEach { overrides.remove(it) }
        settings.setAppCategoryOverrides(overrides.toString())
        repo.loadApps()
    }

    fun setAppCustomCategoryOverride(packageName: String, customName: String?) = viewModelScope.launch {
        val map = try { JSONObject(appCategoryOverrides.value) } catch (_: Exception) { JSONObject() }
        if (customName == null) {
            map.remove(packageName)
        } else {
            map.put(packageName, customName)
        }
        settings.setAppCategoryOverrides(map.toString())
        repo.loadApps()
    }

    fun setCategoryTileSize(categoryKey: String, size: String) = viewModelScope.launch {
        val map = try { JSONObject(categoryTilesSizes.value) } catch (_: Exception) { JSONObject() }
        map.put(categoryKey, size)
        settings.setCategoryTilesSizes(map.toString())
    }

    fun getCategoryTileSize(categoryKey: String): String {
        return try {
            val map = JSONObject(categoryTilesSizes.value)
            map.optString(categoryKey, "medium")
        } catch (_: Exception) { "medium" }
    }

    // ── Multi-Page Custom Apps ───────────────────────────────────────────────

    fun addAppToPage(pageIndex: Int, pkg: String) = viewModelScope.launch {
        val flow = if (pageIndex == 0) settings.page0Apps else settings.page2Apps
        val current = parsePackageCsv(flow.first()).toMutableList()
        if (pkg !in current) {
            current.add(pkg)
            if (pageIndex == 0) settings.setPage0Apps(current.joinToString(","))
            else settings.setPage2Apps(current.joinToString(","))
        }
    }

    fun removeAppFromPage(pageIndex: Int, pkg: String) = viewModelScope.launch {
        val flow = if (pageIndex == 0) settings.page0Apps else settings.page2Apps
        val current = parsePackageCsv(flow.first()).toMutableList()
        if (current.remove(pkg)) {
            if (pageIndex == 0) settings.setPage0Apps(current.joinToString(","))
            else settings.setPage2Apps(current.joinToString(","))
        }
    }

    fun getAppsForPage(pageIndex: Int): List<InstalledApp> {
        val csv = if (pageIndex == 0) page0Apps.value else page2Apps.value
        val pkgs = parsePackageCsv(csv)
        val byPkg = apps.value.associateBy { it.packageName }
        return pkgs.mapNotNull { byPkg[it] }
    }

    // ── Screenshot blocking (Suggestion 145) ──────────────────────────────────

    fun applyScreenshotFlag(window: Window) {
        val flag = android.view.WindowManager.LayoutParams.FLAG_SECURE
        if (screenshotBlocked.value) window.addFlags(flag) else window.clearFlags(flag)
    }

    // ── Hidden apps (Suggestion 23) ───────────────────────────────────────────

    fun hideApp(pkg: String) = viewModelScope.launch {
        val hidden = parsePackageCsv(settings.hiddenApps.first()).toMutableSet().apply { add(pkg) }
        val removed = parsePackageCsv(settings.removedApps.first()).toMutableSet().apply { remove(pkg) }
        val hiddenCsv = hidden.sorted().joinToString(",")
        val removedCsv = removed.sorted().joinToString(",")
        settings.setHiddenApps(hiddenCsv)
        settings.setRemovedApps(removedCsv)
        repo.setHiddenPackages(hiddenCsv)
        repo.setRemovedPackages(removedCsv)
    }

    fun unhideApp(pkg: String) = viewModelScope.launch {
        val hidden = parsePackageCsv(settings.hiddenApps.first()).toMutableSet().apply { remove(pkg) }
        val hiddenCsv = hidden.sorted().joinToString(",")
        settings.setHiddenApps(hiddenCsv)
        repo.setHiddenPackages(hiddenCsv)
    }

    fun removeAppFromDisplay(pkg: String) = viewModelScope.launch {
        val removed = parsePackageCsv(settings.removedApps.first()).toMutableSet().apply { add(pkg) }
        val hidden = parsePackageCsv(settings.hiddenApps.first()).toMutableSet().apply { remove(pkg) }
        val removedCsv = removed.sorted().joinToString(",")
        val hiddenCsv = hidden.sorted().joinToString(",")
        settings.setRemovedApps(removedCsv)
        settings.setHiddenApps(hiddenCsv)
        repo.setRemovedPackages(removedCsv)
        repo.setHiddenPackages(hiddenCsv)
    }

    fun restoreApp(pkg: String) = viewModelScope.launch {
        val removed = parsePackageCsv(settings.removedApps.first()).toMutableSet().apply { remove(pkg) }
        val hidden = parsePackageCsv(settings.hiddenApps.first()).toMutableSet().apply { remove(pkg) }
        val removedCsv = removed.sorted().joinToString(",")
        val hiddenCsv = hidden.sorted().joinToString(",")
        settings.setRemovedApps(removedCsv)
        settings.setHiddenApps(hiddenCsv)
        repo.setRemovedPackages(removedCsv)
        repo.setHiddenPackages(hiddenCsv)
    }

    fun isHidden(pkg: String): Boolean = pkg in parsePackageCsv(hiddenApps.value)
    fun isRemoved(pkg: String): Boolean = pkg in parsePackageCsv(removedApps.value)
    fun hiddenAppItems(): List<InstalledApp> = repo.hiddenApps()
    fun removedAppItems(): List<InstalledApp> = repo.removedApps()

    fun isPinnedToDock(pkg: String): Boolean = pkg in parsePackageCsv(dockPackages.value)

    fun pinToDock(pkg: String) = viewModelScope.launch {
        val current = settings.dockPackages.first()
            .split(",")
            .map(String::trim)
            .filter(String::isNotEmpty)
            .filterNot { it == pkg }
            .toMutableList()
        current.add(0, pkg)
        settings.setDockPackages(current.take(5).joinToString(","))
    }

    fun unpinFromDock(pkg: String) = viewModelScope.launch {
        val updated = settings.dockPackages.first()
            .split(",")
            .map(String::trim)
            .filter(String::isNotEmpty)
            .filterNot { it == pkg }
        settings.setDockPackages(updated.joinToString(","))
    }

    // ── Category renames (Suggestion 24) ──────────────────────────────────────

    fun setCategoryRename(cat: AppCategory, newName: String) = viewModelScope.launch {
        val map = try { JSONObject(categoryRenames.value) } catch (_: Exception) { JSONObject() }
        map.put(cat.name, newName)
        settings.setCategoryRenames(map.toString())
    }

    fun setAppCategoryOverride(packageName: String, newCategory: AppCategory?) = viewModelScope.launch {
        val map = try { JSONObject(appCategoryOverrides.value) } catch (_: Exception) { JSONObject() }
        if (newCategory == null) {
            map.remove(packageName)
        } else {
            map.put(packageName, newCategory.name)
        }
        settings.setAppCategoryOverrides(map.toString())
        repo.loadApps()
    }

    fun getCategoryDisplayName(category: AppCategory): String {
        return try {
            val map = JSONObject(categoryRenames.value)
            map.optString(category.name).takeIf { it.isNotBlank() } ?: category.displayName
        } catch (_: Exception) { category.displayName }
    }

    // ── Recently launched (Suggestion 25) ────────────────────────────────────

    val recentlyLaunchedPackages = settings.recentlyLaunched
        .stateIn(viewModelScope, SharingStarted.Eagerly, "[]")

    fun getRecentlyLaunchedApps(): List<InstalledApp> {
        val pkgs = try {
            val arr = JSONArray(recentlyLaunchedPackages.value)
            (0 until arr.length()).map { arr.getString(it) }
        } catch (_: Exception) { emptyList() }
        val byPkg = apps.value.associateBy { it.packageName }
        return pkgs.mapNotNull { byPkg[it] }
    }

    private suspend fun recordLaunch(pkg: String) {
        val pkgs = try {
            val arr = JSONArray(settings.recentlyLaunched.first())
            (0 until arr.length()).map { arr.getString(it) }.toMutableList()
        } catch (_: Exception) { mutableListOf() }
        pkgs.remove(pkg)
        pkgs.add(0, pkg)
        val capped = pkgs.take(10)
        val arr = JSONArray().also { capped.forEach { p -> it.put(p) } }
        settings.setRecentlyLaunched(arr.toString())
    }

    // ── Recent searches (Suggestion 36) ───────────────────────────────────────

    val recentSearches = settings.recentSearches
        .map { json ->
            try { val arr = JSONArray(json); (0 until arr.length()).map { arr.getString(it) } }
            catch (_: Exception) { emptyList() }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private suspend fun addRecentSearch(q: String) {
        val current = try {
            val arr = JSONArray(settings.recentSearches.first())
            (0 until arr.length()).map { arr.getString(it) }.toMutableList()
        } catch (_: Exception) { mutableListOf() }
        current.remove(q)
        current.add(0, q)
        val capped = current.take(10)
        settings.setRecentSearches(JSONArray().also { capped.forEach { s -> it.put(s) } }.toString())
    }

    fun removeRecentSearch(q: String) = viewModelScope.launch {
        val current = try {
            val arr = JSONArray(settings.recentSearches.first())
            (0 until arr.length()).map { arr.getString(it) }.toMutableList()
        } catch (_: Exception) { mutableListOf() }
        current.remove(q)
        settings.setRecentSearches(JSONArray().also { current.forEach { s -> it.put(s) } }.toString())
    }

    fun clearRecentSearches() = viewModelScope.launch { settings.setRecentSearches("[]") }

    // ── Category helpers ──────────────────────────────────────────────────────

    fun byCategory(cat: AppCategory)       = repo.byCategory(cat)
    fun multiCategoryApps()                = repo.multiCategoryApps()
    fun recentlyAdded()                    = repo.recentlyAdded()
    fun categoriesForApp(app: InstalledApp)= repo.categoriesForApp(app)
    fun byUsageFrequency()                 = repo.byUsageFrequency()
    fun launchCount(pkg: String)           = repo.launchCount(pkg)

    // ── Time-aware layout helpers (Suggestion 72) ─────────────────────────────

    val currentHour: Int get() = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val currentDayOfWeek: Int get() = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    /** Returns the "featured" categories for the current time of day. */
    fun timeAwareCategories(): List<AppCategory> {
        val hour = currentHour
        val isWeekend = currentDayOfWeek in listOf(Calendar.SATURDAY, Calendar.SUNDAY)
        return when {
            hour < 7              -> listOf(AppCategory.DAILY, AppCategory.UTILITIES, AppCategory.PRODUCTIVITY)
            hour < 12             -> listOf(AppCategory.WORK, AppCategory.PRODUCTIVITY, AppCategory.COMMUNICATION)
            hour < 14             -> listOf(AppCategory.SOCIAL, AppCategory.ENTERTAINMENT, AppCategory.DAILY)
            hour < 18 && !isWeekend -> listOf(AppCategory.WORK, AppCategory.PRODUCTIVITY, AppCategory.FINANCE)
            hour < 18 && isWeekend  -> listOf(AppCategory.ENTERTAINMENT, AppCategory.SOCIAL, AppCategory.TRAVEL)
            hour < 22             -> listOf(AppCategory.ENTERTAINMENT, AppCategory.SOCIAL, AppCategory.CREATIVITY)
            else                  -> listOf(AppCategory.DAILY, AppCategory.ENTERTAINMENT, AppCategory.UTILITIES)
        }
    }

    /** True if bedtime mode should be active right now. */
    fun isBedtimeNow(): Boolean {
        if (!bedtimeMode.value) return false
        return currentHour >= bedtimeHour.value
    }

    // ── Focus sessions (Suggestion 75) ────────────────────────────────────────

    val focusSession = FocusSessionManager.activeSession

    fun startFocusSession() {
        val blockedCats = focusBlockedCats.value
            .split(",").mapNotNull { runCatching { AppCategory.valueOf(it.trim()) }.getOrNull() }
        FocusSessionManager.startSession(
            durationMin      = focusDurationMin.value,
            blockedCategories= blockedCats,
            scope            = viewModelScope,
        )
        viewModelScope.launch { settings.setFocusModeActive(true) }
    }

    fun endFocusSession() {
        FocusSessionManager.endSession()
        viewModelScope.launch { settings.setFocusModeActive(false) }
    }

    fun isCategoryBlocked(cat: AppCategory): Boolean = FocusSessionManager.isBlocked(cat)

    // ── App launch ────────────────────────────────────────────────────────────

    fun launchApp(app: InstalledApp) {
        if (isCategoryBlocked(app.category)) {
            _toastEvent.value = Event("${app.label} is blocked during Focus Session")
            return
        }
        val launched = repo.launchApp(getApplication(), app)
        if (launched) {
            viewModelScope.launch { recordLaunch(app.packageName) }
        } else {
            _toastEvent.value = Event("Could not open ${app.label}")
        }
    }

    private val _toastEvent = MutableStateFlow<Event<String>?>(null)
    val toastEvent: StateFlow<Event<String>?> = _toastEvent.asStateFlow()

    fun refreshApps() = viewModelScope.launch { repo.loadApps() }

    // ── Live weather (Open-Meteo) ─────────────────────────────────────────────

    private val _weatherState = MutableStateFlow<WeatherRepository.WeatherState>(
        WeatherRepository.WeatherState.NoPermission
    )
    val weatherState: StateFlow<WeatherRepository.WeatherState> = _weatherState.asStateFlow()

    private val WEATHER_CACHE_TTL_MS = 30 * 60 * 1_000L  // 30 minutes (Suggestion 116)

    fun fetchWeather(context: Context) {
        if (!LocationHelper.hasPermission(context)) {
            _weatherState.value = WeatherRepository.WeatherState.NoPermission
            return
        }
        viewModelScope.launch {
            // Check cache first (Suggestion 116)
            val cacheAt  = settings.weatherCacheAt.first()
            val cacheAge = System.currentTimeMillis() - cacheAt
            if (cacheAge < WEATHER_CACHE_TTL_MS && _weatherState.value is WeatherRepository.WeatherState.Success) {
                return@launch // Serve from memory cache
            }

            _weatherState.value = WeatherRepository.WeatherState.Loading
            val loc = LocationHelper.getLocation(context)
            val result = if (loc != null) {
                WeatherRepository.fetchWeather(loc.lat, loc.lon)
            } else {
                WeatherRepository.WeatherState.NoLocation
            }
            _weatherState.value = result
        }
    }

    fun forceRefreshWeather(context: Context) {
        viewModelScope.launch {
            settings.clearWeatherCache()
            _weatherState.value = WeatherRepository.WeatherState.Loading
            val loc = LocationHelper.getLocation(context)
            _weatherState.value = if (loc != null)
                WeatherRepository.fetchWeather(loc.lat, loc.lon)
            else
                WeatherRepository.WeatherState.NoLocation
        }
    }

    // ── Greeting (de-duplicated) ──────────────────────────────────────────────

    val greeting: String by lazy {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 5  -> "Good night"
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            hour < 21 -> "Good evening"
            else      -> "Good night"
        }
    }

    // ── Init ──────────────────────────────────────────────────────────────────

    init {
        viewModelScope.launch {
            repo.loadApps()
        }
    }

    private fun parsePackageCsv(csv: String): Set<String> =
        csv.split(",").map(String::trim).filter(String::isNotEmpty).toSet()
}
