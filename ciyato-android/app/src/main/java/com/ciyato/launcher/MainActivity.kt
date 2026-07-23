package com.ciyato.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.data.CrashReporter
import com.ciyato.launcher.data.LocationHelper
import com.ciyato.launcher.ui.components.CiyatoBottomNavBar
import com.ciyato.launcher.ui.components.CiyatoNavItem
import com.ciyato.launcher.ui.screens.*
import com.ciyato.launcher.ui.theme.CiyatoTheme
import com.ciyato.launcher.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch

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
 *   weather_detail          →  Live weather (Open-Meteo)
 *   agenda                  →  Agenda / Today
 */
class MainActivity : ComponentActivity() {

    companion object {
        const val EXTRA_START_DESTINATION = "start_destination"
    }

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CrashReporter.install(this)
        lifecycleScope.launch {
            viewModel.crashReporting.collect { enabled ->
                CrashReporter.setLoggingEnabled(enabled)
            }
        }

        enableEdgeToEdge(
            statusBarStyle     = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        setContent {
            val font by viewModel.font.collectAsState()
            CiyatoTheme(darkMode = "dark", font = font, dynamicColor = false) {
                val context           = LocalContext.current
                val onboardingDone by viewModel.onboardingDone.collectAsState()
                val navController     = rememberNavController()
                val requestedDestination = intent.getStringExtra(EXTRA_START_DESTINATION)
                val startDest = when (requestedDestination) {
                    "home", "files", "photos", "search", "settings", "agenda" -> requestedDestination
                    "dashboard" -> "home"
                    "shared" -> "photos"
                    else -> if (onboardingDone) "home" else "onboarding"
                }

                // Auto-fetch weather if permission already granted
                LaunchedEffect(Unit) {
                    if (LocationHelper.hasPermission(context)) {
                        viewModel.fetchWeather(context)
                    }
                }

                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val activeRoute = currentBackStackEntry?.destination?.route
                val tabRoutes = listOf("home", "files", "search", "photos", "settings")
                val tabItems = listOf(
                    CiyatoNavItem(Icons.Default.Home, "Home"),
                    CiyatoNavItem(Icons.Default.FolderOpen, "Files"),
                    CiyatoNavItem(Icons.Default.Search, "Search"),
                    CiyatoNavItem(Icons.Default.PhotoLibrary, "Photos"),
                    CiyatoNavItem(Icons.Default.Settings, "Settings"),
                )
                val selectedTab = tabRoutes.indexOf(activeRoute).coerceAtLeast(0)

                Scaffold(
                    bottomBar = {
                        if (activeRoute in tabRoutes) {
                            CiyatoBottomNavBar(
                                items = tabItems,
                                selectedIndex = selectedTab,
                                onItemSelected = { index ->
                                    val target = tabRoutes[index]
                                    if (target != activeRoute) {
                                        navController.navigate(target) { launchSingleTop = true }
                                    }
                                },
                            )
                        }
                    },
                ) { contentPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDest,
                        modifier = Modifier.padding(contentPadding),
                    ) {

                    composable("onboarding") {
                        OnboardingScreen(onDone = {
                            viewModel.setOnboardingDone()
                            navController.navigate("home") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        })
                    }

                    composable("home") { DashboardScreen(viewModel = viewModel) }

                    composable("files")   { FilesScreen(viewModel = viewModel, onBack = { navController.popBackStack() }) }
                    composable("photos")  { PhotosScreen(viewModel = viewModel, onBack = { navController.popBackStack() }) }

                    composable("search") {
                        NlFileSearchScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                        )
                    }

                    composable("theme") {
                        ThemeStudioScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                    }

                    composable("settings") {
                        SettingsScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateToFiles = { navController.navigate("files") },
                            onNavigateToPhotos = { navController.navigate("photos") },
                            onNavigateToAgenda = { navController.navigate("agenda") },
                            onNavigateToTheme = { navController.navigate("theme") },
                            onNavigateToHiddenApps = { navController.navigate("hidden_apps") },
                            onNavigateToRemovedApps = { navController.navigate("removed_apps") },
                            onNavigateToPermissionAudit = { navController.navigate("permission_audit") },
                            onNavigateToFocus = { navController.navigate("focus") },
                        )
                    }

                    composable("permission_audit") {
                        PermissionAuditScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                        )
                    }

                    composable("focus") {
                        FocusSessionScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                        )
                    }

                    composable("hidden_apps") {
                        AppVisibilityScreen(
                            mode = AppVisibilityMode.Hidden,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                        )
                    }

                    composable("removed_apps") {
                        AppVisibilityScreen(
                            mode = AppVisibilityMode.Removed,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                        )
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

                    composable("weather_detail") {
                        // Shares viewModel.weatherState with home screen WeatherCard
                        WeatherDetailScreen(
                            viewModel = viewModel,
                            onBack    = { navController.popBackStack() },
                        )
                    }

                    composable("agenda") {
                        CalendarAgendaScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                        )
                    }
                }
                }
            }
        }
    }
}
