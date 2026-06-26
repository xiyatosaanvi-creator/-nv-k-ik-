package com.ciyato.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.data.LocationHelper
import com.ciyato.launcher.ui.screens.*
import com.ciyato.launcher.ui.theme.CiyatoTheme
import com.ciyato.launcher.viewmodel.LauncherViewModel

/**
 * MainActivity — dashboard/settings entry point.
 * Launched from the app drawer (LAUNCHER intent-filter).
 *
 * Routes:
 *   onboarding              →  first-run experience
 *   dashboard               →  main control center
 *   files                   →  Ciyato Files (SAF)
 *   photos                  →  Ciyato Photos
 *   search                  →  AI Search
 *   theme                   →  Theme Studio
 *   settings                →  Settings
 *   category_detail/{name}  →  Category detail (real apps)
 *   duplicate_shortcuts     →  Duplicate shortcuts management
 *   weather_detail          →  Live weather (Open-Meteo)
 *   agenda                  →  Agenda / Today
 */
class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle     = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        setContent {
            CiyatoTheme {
                val context           = LocalContext.current
                val onboardingDone by viewModel.onboardingDone.collectAsState()
                val navController     = rememberNavController()
                val startDest         = if (onboardingDone) "dashboard" else "onboarding"

                // Auto-fetch weather if permission already granted
                LaunchedEffect(Unit) {
                    if (LocationHelper.hasPermission(context)) {
                        viewModel.fetchWeather(context)
                    }
                }

                NavHost(navController = navController, startDestination = startDest) {

                    composable("onboarding") {
                        OnboardingScreen(onDone = {
                            viewModel.setOnboardingDone()
                            navController.navigate("dashboard") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        })
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

                    composable("files")   { FilesScreen(onBack = { navController.popBackStack() }) }
                    composable("photos")  { PhotosScreen(onBack = { navController.popBackStack() }) }

                    composable("search") {
                        SearchScreen(
                            viewModel        = viewModel,
                            onBack           = { navController.popBackStack() },
                            onCategoryFilter = { cat ->
                                navController.navigate("category_detail/${cat.name}")
                            },
                        )
                    }

                    composable("theme") {
                        ThemeStudioScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                    }

                    composable("settings") {
                        SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                    }

                    // ── Functional wiring screens ──────────────────────────────

                    composable(
                        route     = "category_detail/{categoryName}",
                        arguments = listOf(navArgument("categoryName") { type = NavType.StringType }),
                    ) { backStack ->
                        val name     = backStack.arguments?.getString("categoryName") ?: ""
                        val category = runCatching { AppCategory.valueOf(name) }.getOrNull()
                        if (category != null) {
                            CategoryDetailScreen(
                                category  = category,
                                viewModel = viewModel,
                                onBack    = { navController.popBackStack() },
                            )
                        } else {
                            navController.popBackStack()
                        }
                    }

                    composable("duplicate_shortcuts") {
                        DuplicateShortcutsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                    }

                    composable("weather_detail") {
                        // Shares viewModel.weatherState with home screen WeatherCard
                        WeatherDetailScreen(
                            viewModel = viewModel,
                            onBack    = { navController.popBackStack() },
                        )
                    }

                    composable("agenda") {
                        AgendaScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
