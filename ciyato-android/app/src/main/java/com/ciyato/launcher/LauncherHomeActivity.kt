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
import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.ui.screens.*
import com.ciyato.launcher.ui.theme.CiyatoBg
import com.ciyato.launcher.ui.theme.CiyatoTheme
import com.ciyato.launcher.viewmodel.LauncherViewModel

/**
 * LauncherHomeActivity — the REAL launcher home screen.
 *
 * This activity has HOME + DEFAULT intent-filter categories in AndroidManifest.xml.
 * Android will offer Ciyato as a Home app because of this.
 *
 * Navigation uses a sealed class so screens can carry parameters (e.g. which category).
 * Zero-latency switching — no Jetpack Nav overhead for the launcher shell.
 */
class LauncherHomeActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        // Suppress back-press on the launcher home screen (standard launcher behaviour).
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing — prevents launcher from closing on Home.
            }
        })

        setContent {
            CiyatoTheme {
                LauncherRoot(viewModel = viewModel)
            }
        }
    }
}

/** Top-level navigation sealed class — carries parameters where needed. */
private sealed class LauncherDest {
    object Home : LauncherDest()
    object Drawer : LauncherDest()
    object Search : LauncherDest()
    object Settings : LauncherDest()
    data class CategoryDetail(val category: AppCategory) : LauncherDest()
    object DuplicateShortcuts : LauncherDest()
    object WeatherDetail : LauncherDest()
    object Agenda : LauncherDest()
}

@Composable
private fun LauncherRoot(viewModel: LauncherViewModel) {
    var dest by remember { mutableStateOf<LauncherDest>(LauncherDest.Home) }

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

        is LauncherDest.Search -> SearchScreen(
            viewModel = viewModel,
            onBack    = { dest = LauncherDest.Home },
            onCategoryFilter = { cat -> dest = LauncherDest.CategoryDetail(cat) },
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
            onBack = { dest = LauncherDest.Home },
        )

        is LauncherDest.Agenda -> AgendaScreen(
            onBack = { dest = LauncherDest.Home },
        )
    }
}
