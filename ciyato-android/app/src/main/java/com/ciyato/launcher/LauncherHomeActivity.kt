package com.ciyato.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.data.CrashReporter
import com.ciyato.launcher.data.LocationHelper
import com.ciyato.launcher.ui.screens.*
import com.ciyato.launcher.ui.theme.CiyatoBg
import com.ciyato.launcher.ui.theme.CiyatoTheme
import com.ciyato.launcher.viewmodel.LauncherViewModel

/**
 * LauncherHomeActivity — the REAL home screen.
 *
 * Uses sealed-class navigation for zero-latency screen transitions.
 * Suggestions wired here: 75 (Focus), 139 (Permission Audit), 144 (Crash Reporter), 145 (Screenshot block).
 */
class LauncherHomeActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crash reporter install (Suggestion 144)
        CrashReporter.install(this)

        enableEdgeToEdge(
            statusBarStyle     = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                    addCategory(android.content.Intent.CATEGORY_HOME)
                }
                val resolveInfo = packageManager.resolveActivity(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
                val isDefault = resolveInfo?.activityInfo?.packageName == packageName
                if (isDefault) {
                    // Standard launcher: back does nothing on home screen
                } else {
                    finish()
                }
            }
        })

        setContent {
            CiyatoTheme {
                LauncherRoot(viewModel = viewModel, activity = this@LauncherHomeActivity)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApps()
    }

}

// ── Navigation sealed class ────────────────────────────────────────────────────

private sealed class LauncherDest {
    object Home               : LauncherDest()
    object Drawer             : LauncherDest()
    object Settings           : LauncherDest()
    object Search             : LauncherDest()
    object ThemeStudio        : LauncherDest()
    object HiddenApps         : LauncherDest()
    object RemovedApps        : LauncherDest()
    data class CategoryDetail(val category: AppCategory) : LauncherDest()
    object DuplicateShortcuts : LauncherDest()
    object WeatherDetail      : LauncherDest()
    object Agenda             : LauncherDest()
    object FocusSession       : LauncherDest()   // Suggestion 75
    object PermissionAudit    : LauncherDest()   // Suggestion 139
    object AIOptimizer        : LauncherDest()
}

// ── Root composable ───────────────────────────────────────────────────────────

@Composable
private fun LauncherRoot(viewModel: LauncherViewModel, activity: LauncherHomeActivity) {
    val context = LocalContext.current
    var dest by remember { mutableStateOf<LauncherDest>(LauncherDest.Home) }
    val useSystemWallpaper by viewModel.useSystemWallpaper.collectAsState()

    androidx.activity.compose.BackHandler(enabled = dest != LauncherDest.Home) {
        dest = when (dest) {
            is LauncherDest.PermissionAudit,
            is LauncherDest.HiddenApps,
            is LauncherDest.RemovedApps -> LauncherDest.Settings
            else -> LauncherDest.Home
        }
    }

    // Auto-fetch weather on startup if already permitted
    LaunchedEffect(Unit) {
        if (LocationHelper.hasPermission(context)) {
            viewModel.fetchWeather(context)
        }
        // Apply screenshot block setting (Suggestion 145)
        viewModel.applyScreenshotFlag(activity.window)
    }

    // Re-apply screenshot flag whenever the setting changes
    val screenshotBlocked by viewModel.screenshotBlocked.collectAsState()
    LaunchedEffect(screenshotBlocked) {
        viewModel.applyScreenshotFlag(activity.window)
    }

    when (val d = dest) {

        is LauncherDest.Home -> HomeScreen(
            viewModel       = viewModel,
            onOpenDrawer    = { dest = LauncherDest.Drawer },
            onOpenSettings  = { dest = LauncherDest.Settings },
            onOpenSearch    = { dest = LauncherDest.Search },
            onOpenTheme     = { dest = LauncherDest.ThemeStudio },
            onCategoryTap   = { category -> dest = LauncherDest.CategoryDetail(category) },
            onWeatherTap    = { dest = LauncherDest.WeatherDetail },
            onAgendaTap     = { dest = LauncherDest.Agenda },
            onDuplicatesTap = { dest = LauncherDest.DuplicateShortcuts },
            onOpenAIOptimizer = { dest = LauncherDest.AIOptimizer },
        )

        is LauncherDest.Drawer -> AppDrawerScreen(
            viewModel       = viewModel,
            onBack          = { dest = LauncherDest.Home },
            onDuplicatesTap = { dest = LauncherDest.DuplicateShortcuts },
        )

        is LauncherDest.Settings -> SettingsScreen(
            viewModel                  = viewModel,
            onBack                     = { dest = LauncherDest.Home },
            onNavigateToPermissionAudit= { dest = LauncherDest.PermissionAudit },
            onNavigateToFocus          = { dest = LauncherDest.FocusSession },
            onNavigateToTheme          = { dest = LauncherDest.ThemeStudio },
            onNavigateToHiddenApps     = { dest = LauncherDest.HiddenApps },
            onNavigateToRemovedApps    = { dest = LauncherDest.RemovedApps },
        )

        is LauncherDest.Search -> SearchScreen(
            viewModel = viewModel,
            onBack = {
                viewModel.setSearch("")
                dest = LauncherDest.Home
            },
            onCategoryFilter = { category -> dest = LauncherDest.CategoryDetail(category) },
        )

        is LauncherDest.ThemeStudio -> ThemeStudioScreen(
            viewModel = viewModel,
            onBack = { dest = LauncherDest.Home },
        )

        is LauncherDest.HiddenApps -> AppVisibilityScreen(
            mode = AppVisibilityMode.Hidden,
            viewModel = viewModel,
            onBack = { dest = LauncherDest.Settings },
        )

        is LauncherDest.RemovedApps -> AppVisibilityScreen(
            mode = AppVisibilityMode.Removed,
            viewModel = viewModel,
            onBack = { dest = LauncherDest.Settings },
        )

        is LauncherDest.CategoryDetail -> CategoryDetailScreen(
            category  = d.category,
            viewModel = viewModel,
            onBack    = { dest = LauncherDest.Home },
        )

        is LauncherDest.DuplicateShortcuts -> DuplicateShortcutsScreen(
            viewModel = viewModel,
            onBack    = { dest = LauncherDest.Home },
        )

        is LauncherDest.WeatherDetail -> WeatherDetailScreen(
            viewModel = viewModel,
            onBack    = { dest = LauncherDest.Home },
        )

        is LauncherDest.Agenda -> AgendaScreen(
            onBack = { dest = LauncherDest.Home },
        )

        is LauncherDest.FocusSession -> FocusSessionScreen(  // Suggestion 75
            viewModel = viewModel,
            onBack    = { dest = LauncherDest.Home },
        )

        is LauncherDest.PermissionAudit -> PermissionAuditScreen( // Suggestion 139
            viewModel = viewModel,
            onBack    = { dest = LauncherDest.Home },
        )

        is LauncherDest.AIOptimizer -> AIOptimizerScreen(
            viewModel = viewModel,
            onBack    = { dest = LauncherDest.Home }
        )
    }
}
