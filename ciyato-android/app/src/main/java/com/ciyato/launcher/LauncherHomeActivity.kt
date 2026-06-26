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
import com.ciyato.launcher.data.LocationHelper
import com.ciyato.launcher.ui.screens.*
import com.ciyato.launcher.ui.theme.CiyatoBg
import com.ciyato.launcher.ui.theme.CiyatoTheme
import com.ciyato.launcher.viewmodel.LauncherViewModel

/**
 * LauncherHomeActivity — the REAL home screen.
 *
 * Has HOME + DEFAULT intent-filter categories so Android offers Ciyato
 * as a default Home app. singleTask prevents duplicate instances.
 *
 * Navigation uses a sealed class (not Jetpack Nav) for zero-latency
 * transitions without the overhead of a NavController.
 */
class LauncherHomeActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        // Suppress back press — standard launcher behaviour (pressing Home never closes it).
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = Unit
        })

        setContent {
            CiyatoTheme {
                LauncherRoot(viewModel = viewModel)
            }
        }
    }
}

// ── Navigation sealed class ────────────────────────────────────────────────────

private sealed class LauncherDest {
    object Home              : LauncherDest()
    object Drawer            : LauncherDest()
    object Settings          : LauncherDest()
    data class CategoryDetail(val category: AppCategory) : LauncherDest()
    object DuplicateShortcuts: LauncherDest()
    object WeatherDetail     : LauncherDest()
    object Agenda            : LauncherDest()
}

// ── Root composable ───────────────────────────────────────────────────────────

@Composable
private fun LauncherRoot(viewModel: LauncherViewModel) {
    val context = LocalContext.current
    var dest by remember { mutableStateOf<LauncherDest>(LauncherDest.Home) }

    // Auto-fetch weather on startup if location permission is already granted.
    // This ensures the home screen WeatherCard shows live data immediately.
    LaunchedEffect(Unit) {
        if (LocationHelper.hasPermission(context)) {
            viewModel.fetchWeather(context)
        }
    }

    when (val d = dest) {

        is LauncherDest.Home -> HomeScreen(
            viewModel       = viewModel,
            onOpenDrawer    = { dest = LauncherDest.Drawer },
            onOpenSettings  = { dest = LauncherDest.Settings },
            onCategoryTap   = { cat -> dest = LauncherDest.CategoryDetail(cat) },
            onWeatherTap    = { dest = LauncherDest.WeatherDetail },
            onAgendaTap     = { dest = LauncherDest.Agenda },
            onDuplicatesTap = { dest = LauncherDest.DuplicateShortcuts },
            modifier        = Modifier.fillMaxSize().background(CiyatoBg),
        )

        is LauncherDest.Drawer -> AppDrawerScreen(
            viewModel       = viewModel,
            onBack          = { dest = LauncherDest.Home },
            onDuplicatesTap = { dest = LauncherDest.DuplicateShortcuts },
        )

        is LauncherDest.Settings -> SettingsScreen(
            viewModel = viewModel,
            onBack    = { dest = LauncherDest.Home },
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
            viewModel = viewModel,           // ← shares live state with HomeScreen WeatherCard
            onBack    = { dest = LauncherDest.Home },
        )

        is LauncherDest.Agenda -> AgendaScreen(
            onBack = { dest = LauncherDest.Home },
        )
    }
}
