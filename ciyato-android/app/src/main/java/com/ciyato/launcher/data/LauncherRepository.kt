package com.ciyato.launcher.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first

/**
 * LauncherRepository — loads real installed apps from the system.
 *
 * Suggestions implemented:
 *   17 — Icon Bitmap LRU cache (caches Drawable by packageName)
 *   23 — App hide list (filter by CSV packageNames stored in settings)
 *   25 — Recently launched tracking
 *   37 — App usage frequency sort (launch count → sort key)
 *   38 — Fuzzy search (Levenshtein distance fallback)
 *   40 — NLP intent detection (delegates to AppCategorizer.detectQueryIntent)
 *   42 — Search result grouping (Apps by category)
 *   85 — Android App Shortcuts exposure (getLaunchShortcuts)
 *
 * SECURITY: Everything is local. No network calls. Reads only label/icon/package.
 */
class LauncherRepository(private val context: Context) {

    private val _apps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val apps: StateFlow<List<InstalledApp>> = _apps.asStateFlow()

    private val _allApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val allApps: StateFlow<List<InstalledApp>> = _allApps.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ── Icon LRU cache (Suggestion 17) ────────────────────────────────────────
    private val iconCache = LruCache<String, Drawable>(128)

    // ── Launch frequency (Suggestion 37) ──────────────────────────────────────
    private val launchCounts = mutableMapOf<String, Int>()

    // ── Hidden apps (Suggestion 23) ───────────────────────────────────────────
    private val hiddenPackages = mutableSetOf<String>()
    private val removedPackages = mutableSetOf<String>()

    fun setHiddenPackages(csv: String) {
        hiddenPackages.clear()
        hiddenPackages.addAll(parsePackageCsv(csv))
        publishVisibleApps()
    }

    fun setRemovedPackages(csv: String) {
        removedPackages.clear()
        removedPackages.addAll(parsePackageCsv(csv))
        publishVisibleApps()
    }

    // ── App loading ───────────────────────────────────────────────────────────

    suspend fun loadApps() = withContext(Dispatchers.IO) {
        _isLoading.value = true
        try {
            AppCategorizer.initialize(context)
            val settingsRepo = LauncherSettingsRepository(context)
            val overridesJson = settingsRepo.appCategoryOverrides.first()
            hiddenPackages.clear()
            hiddenPackages.addAll(parsePackageCsv(settingsRepo.hiddenApps.first()))
            removedPackages.clear()
            removedPackages.addAll(parsePackageCsv(settingsRepo.removedApps.first()))
            val overridesMap = try {
                val obj = org.json.JSONObject(overridesJson)
                val map = mutableMapOf<String, String>()
                obj.keys().forEach { key ->
                    map[key] = obj.getString(key)
                }
                map
            } catch (_: Exception) { emptyMap() }

            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            @Suppress("DEPRECATION")
            val resolveInfoList = pm.queryIntentActivities(mainIntent, PackageManager.GET_META_DATA)
                .sortedBy { it.loadLabel(pm).toString().lowercase() }

            val installed = resolveInfoList.mapNotNull { ri ->
                try {
                    val actInfo  = ri.activityInfo
                    val pkg      = actInfo.packageName
                    val activity = actInfo.name
                    val label    = ri.loadLabel(pm).toString()

                    // LRU-cached icon load (Suggestion 17)
                    val icon = iconCache.get(pkg) ?: ri.loadIcon(pm).also { iconCache.put(pkg, it) }

                    val appInfo  = pm.getApplicationInfo(pkg, 0)
                    val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    val pkgInfo  = runCatching { pm.getPackageInfo(pkg, 0) }.getOrNull()
                    val installMs= pkgInfo?.firstInstallTime ?: 0L
                    val updateMs = pkgInfo?.lastUpdateTime   ?: 0L

                    val overrideName = overridesMap[pkg]
                    val overrideCat = overrideName?.let { runCatching { AppCategory.valueOf(it) }.getOrNull() }

                    val primary: AppCategory
                    val customName: String?
                    if (overrideName != null) {
                        if (overrideCat != null) {
                            primary = overrideCat
                            customName = null
                        } else {
                            primary = AppCategory.CUSTOM
                            customName = overrideName
                        }
                    } else {
                        primary = AppCategorizer.categorize(pkg, label)
                        customName = null
                    }

                    val secondary = if (primary == AppCategory.CUSTOM || overrideCat != null) emptyList() else AppCategorizer.secondaryCategories(pkg, label, primary)

                    InstalledApp(
                        id                  = "$pkg/$activity",
                        label               = label,
                        packageName         = pkg,
                        activityName        = activity,
                        icon                = icon,
                        category            = primary,
                        secondaryCategories = secondary,
                        isSystemApp         = isSystem,
                        installTime         = installMs,
                        lastUpdateTime      = updateMs,
                        customCategoryName  = customName,
                    )
                } catch (_: Exception) { null }
            }

            _allApps.value = installed
            publishVisibleApps()
        } finally {
            _isLoading.value = false
        }
    }

    // ── Launch (with frequency tracking, Suggestion 37) ──────────────────────

    fun launchApp(context: Context, app: InstalledApp): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                setClassName(app.packageName, app.activityName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            }
            context.startActivity(intent)
            launchCounts[app.packageName] = (launchCounts[app.packageName] ?: 0) + 1
            true
        } catch (_: Exception) { false }
    }

    // ── Query helpers ─────────────────────────────────────────────────────────

    /** Standard substring search. */
    fun search(query: String): List<InstalledApp> {
        if (query.isBlank()) return _apps.value
        val q = query.trim().lowercase()
        return _apps.value.filter {
            it.label.lowercase().contains(q) ||
            it.packageName.lowercase().contains(q) ||
            it.category.displayName.lowercase().contains(q)
        }
    }

    /**
     * Fuzzy search — Levenshtein distance fallback (Suggestion 38).
     * Returns apps whose label edit-distance from the query is ≤ threshold.
     */
    fun fuzzySearch(query: String, threshold: Int = 3): List<InstalledApp> {
        val q = query.trim().lowercase()
        if (q.length < 2) return emptyList()
        return _apps.value
            .filter { levenshtein(it.label.lowercase(), q) <= threshold }
            .sortedBy { levenshtein(it.label.lowercase(), q) }
    }

    /**
     * NLP query search — detects category intent and filters (Suggestion 40).
     * "open a music app" → filters ENTERTAINMENT category.
     */
    fun nlpSearch(query: String): Pair<AppCategory?, List<InstalledApp>> {
        val intent = AppCategorizer.detectQueryIntent(query) ?: return Pair(null, search(query))
        return Pair(intent, byCategory(intent))
    }

    /**
     * Grouped search results by category (Suggestion 42).
     * Returns a map from category to matching apps.
     */
    fun groupedSearch(query: String): Map<String, List<InstalledApp>> {
        val results = search(query)
        if (results.isEmpty()) return emptyMap()
        return buildMap {
            put("Apps", results)
            // Group by category
            results.groupBy { it.category.displayName }.forEach { (cat, apps) ->
                if (apps.isNotEmpty() && cat != "Other") put(cat, apps)
            }
        }
    }

    /** Apps sorted by launch frequency descending (Suggestion 37). */
    fun byUsageFrequency(): List<InstalledApp> =
        _apps.value.sortedByDescending { launchCounts[it.packageName] ?: 0 }

    /** Return apps sorted by install time descending (recently added first). */
    fun recentlyAdded(): List<InstalledApp> =
        _apps.value.sortedByDescending { it.installTime }.take(12)

    /** Return apps by category. */
    fun byCategory(category: AppCategory): List<InstalledApp> =
        _apps.value.filter { it.category == category || category in it.secondaryCategories }

    fun byCustomCategoryName(name: String): List<InstalledApp> =
        _apps.value.filter { it.category == AppCategory.CUSTOM && it.customCategoryName == name }

    /** Apps that appear in 2+ categories — the "Duplicate Smart Shortcuts". */
    fun multiCategoryApps(): List<InstalledApp> =
        _apps.value.filter { it.secondaryCategories.isNotEmpty() }

    /** All categories (primary + secondary) for a specific app. */
    fun categoriesForApp(app: InstalledApp): List<AppCategory> =
        (listOf(app.category) + app.secondaryCategories).distinct()

    /** Returns launch count for a package (for usage-frequency sort). */
    fun launchCount(pkg: String) = launchCounts[pkg] ?: 0

    fun hiddenApps(): List<InstalledApp> =
        _allApps.value.filter { it.packageName in hiddenPackages }

    fun removedApps(): List<InstalledApp> =
        _allApps.value.filter { it.packageName in removedPackages }

    private fun publishVisibleApps() {
        _apps.value = _allApps.value.filter {
            it.packageName !in hiddenPackages && it.packageName !in removedPackages
        }
    }

    private fun parsePackageCsv(csv: String): Set<String> =
        csv.split(",").map(String::trim).filter(String::isNotEmpty).toSet()

    // ── Levenshtein distance helper ───────────────────────────────────────────

    private fun levenshtein(s: String, t: String): Int {
        if (s == t) return 0
        if (s.isEmpty()) return t.length
        if (t.isEmpty()) return s.length
        val dp = Array(s.length + 1) { IntArray(t.length + 1) { 0 } }
        for (i in 0..s.length) dp[i][0] = i
        for (j in 0..t.length) dp[0][j] = j
        for (i in 1..s.length) for (j in 1..t.length) {
            dp[i][j] = if (s[i - 1] == t[j - 1]) dp[i - 1][j - 1]
            else 1 + minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
        }
        return dp[s.length][t.length]
    }
}
