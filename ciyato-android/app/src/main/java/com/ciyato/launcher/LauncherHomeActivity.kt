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
import com.ciyato.launcher.ui.screens.AppDrawerScreen
import com.ciyato.launcher.ui.screens.HomeScreen
import com.ciyato.launcher.ui.screens.SearchScreen
import com.ciyato.launcher.ui.screens.SettingsScreen
import com.ciyato.launcher.ui.theme.CiyatoBg
import com.ciyato.launcher.ui.theme.CiyatoTheme
import com.ciyato.launcher.viewmodel.LauncherViewModel

/**
 * LauncherHomeActivity — the REAL launcher home screen.
 *
 * This activity has HOME + DEFAULT intent-filter categories in AndroidManifest.xml.
 * Android will offer Ciyato as a Home app because of this.
 *
 * When the user presses the physical Home button, Android launches this activity
 * (once Ciyato is set as the default Home app).
 *
 * Navigation is internal (no Jetpack Nav needed for the launcher shell):
 *   HOME  →  APP_DRAWER  →  (back to HOME)
 *         →  SEARCH
 *         →  SETTINGS
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
        // Uses the modern OnBackPressedCallback instead of deprecated onBackPressed().
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing — prevents launcher from closing when back is pressed on home.
            }
        })

        setContent {
            CiyatoTheme {
                LauncherRoot(viewModel = viewModel)
            }
        }
    }
}

/** Top-level navigation state for the launcher shell. */
private enum class LauncherDest { HOME, DRAWER, SEARCH, SETTINGS }

@Composable
private fun LauncherRoot(viewModel: LauncherViewModel) {
    var dest by remember { mutableStateOf(LauncherDest.HOME) }

    when (dest) {
        LauncherDest.HOME -> HomeScreen(
            viewModel = viewModel,
            onOpenDrawer   = { dest = LauncherDest.DRAWER },
            onOpenSettings = { dest = LauncherDest.SETTINGS },
            modifier = Modifier.fillMaxSize().background(CiyatoBg),
        )

        LauncherDest.DRAWER -> AppDrawerScreen(
            viewModel = viewModel,
            onBack    = { dest = LauncherDest.HOME },
        )

        LauncherDest.SEARCH -> SearchScreen(
            viewModel = viewModel,
            onBack    = { dest = LauncherDest.HOME },
        )

        LauncherDest.SETTINGS -> SettingsScreen(
            viewModel = viewModel,
            onBack    = { dest = LauncherDest.HOME },
        )
    }
}
