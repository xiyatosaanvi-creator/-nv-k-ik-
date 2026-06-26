package com.ciyato.launcher.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * LauncherRepository — loads real installed apps from the system.
 *
 * SECURITY:
 * - Everything is local. No network calls.
 * - Reads only: label, icon, package name, launchable activity.
 * - Does NOT upload or share the app list.
 */
class LauncherRepository(private val context: Context) {

    private val _apps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val apps: StateFlow<List<InstalledApp>> = _apps.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** Load all launchable apps asynchronously. Safe to call multiple times. */
    suspend fun loadApps() = withContext(Dispatchers.IO) {
        _isLoading.value = true
        try {
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
                    val icon     = ri.loadIcon(pm)

                    val appInfo = pm.getApplicationInfo(pkg, 0)
                    val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                    // Single getPackageInfo call to get both install and update times
                    val pkgInfo = try { pm.getPackageInfo(pkg, 0) } catch (_: Exception) { null }
                    val installMs = pkgInfo?.firstInstallTime ?: 0L
                    val updateMs  = pkgInfo?.lastUpdateTime   ?: 0L

                    val primary   = AppCategorizer.categorize(pkg, label)
                    val secondary = AppCategorizer.secondaryCategories(pkg, label, primary)

                    InstalledApp(
                        id                 = "$pkg/$activity",
                        label              = label,
                        packageName        = pkg,
                        activityName       = activity,
                        icon               = icon,
                        category           = primary,
                        secondaryCategories= secondary,
                        isSystemApp        = isSystem,
                        installTime        = installMs,
                        lastUpdateTime     = updateMs,
                    )
                } catch (e: Exception) {
                    // If icon or info fails, skip this app gracefully
                    null
                }
            }

            _apps.value = installed
        } finally {
            _isLoading.value = false
        }
    }

    /** Launch a real app by package + activity name. Returns success. */
    fun launchApp(context: Context, app: InstalledApp): Boolean {
        return try {
            val intent = context.packageManager
                .getLaunchIntentForPackage(app.packageName)
                ?: return false
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Return apps sorted by install time descending (recently added first). */
    fun recentlyAdded(): List<InstalledApp> =
        _apps.value.sortedByDescending { it.installTime }.take(12)

    /** Return apps by category. */
    fun byCategory(category: AppCategory): List<InstalledApp> =
        _apps.value.filter { it.category == category || category in it.secondaryCategories }

    /** Apps that appear in 2+ categories — the "Duplicate Smart Shortcuts". */
    fun multiCategoryApps(): List<InstalledApp> =
        _apps.value.filter { it.secondaryCategories.isNotEmpty() }

    /** Search by label or package name. */
    fun search(query: String): List<InstalledApp> {
        if (query.isBlank()) return _apps.value
        val q = query.trim().lowercase()
        return _apps.value.filter {
            it.label.lowercase().contains(q) ||
            it.packageName.lowercase().contains(q) ||
            it.category.displayName.lowercase().contains(q)
        }
    }
}
