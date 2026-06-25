package com.ciyato.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ciyato.launcher.ui.screens.*
import com.ciyato.launcher.ui.theme.CiyatoTheme
import com.ciyato.launcher.viewmodel.LauncherViewModel

/**
 * MainActivity — the app's dashboard/settings entry point.
 * Launched when the user taps the Ciyato icon from another launcher.
 *
 * Routes:
 *   onboarding  →  first launch
 *   dashboard   →  main control center
 *   files       →  Ciyato Files
 *   photos      →  Ciyato Photos
 *   search      →  AI Search
 *   theme       →  Theme Studio
 *   settings    →  Settings
 */
class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        setContent {
            CiyatoTheme {
                val onboardingDone by viewModel.onboardingDone.collectAsStateWithLifecycle()
                val navController = rememberNavController()

                val startDest = if (onboardingDone) "dashboard" else "onboarding"

                NavHost(navController = navController, startDestination = startDest) {

                    composable("onboarding") {
                        OnboardingScreen(
                            onDone = {
                                viewModel.setOnboardingDone()
                                navController.navigate("dashboard") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("dashboard") {
                        DashboardScreen(
                            viewModel      = viewModel,
                            onOpenFiles    = { navController.navigate("files") },
                            onOpenPhotos   = { navController.navigate("photos") },
                            onOpenSearch   = { navController.navigate("search") },
                            onOpenTheme    = { navController.navigate("theme") },
                            onOpenSettings = { navController.navigate("settings") },
                        )
                    }

                    composable("files") {
                        FilesScreen(onBack = { navController.popBackStack() })
                    }

                    composable("photos") {
                        PhotosScreen(onBack = { navController.popBackStack() })
                    }

                    composable("search") {
                        SearchScreen(
                            viewModel = viewModel,
                            onBack    = { navController.popBackStack() },
                        )
                    }

                    composable("theme") {
                        ThemeStudioScreen(
                            viewModel = viewModel,
                            onBack    = { navController.popBackStack() },
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            viewModel = viewModel,
                            onBack    = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
