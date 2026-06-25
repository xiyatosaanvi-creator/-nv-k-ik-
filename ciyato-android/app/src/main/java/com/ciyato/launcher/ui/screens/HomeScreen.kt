package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.data.InstalledApp
import com.ciyato.launcher.ui.components.*
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel
import java.text.SimpleDateFormat
import java.util.*

private val LAUNCHER_CATEGORIES = listOf(
    AppCategory.WORK,
    AppCategory.SOCIAL,
    AppCategory.FINANCE,
    AppCategory.CREATIVITY,
    AppCategory.UTILITIES,
    AppCategory.DAILY,
    AppCategory.ENTERTAINMENT,
    AppCategory.TRAVEL,
)

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val apps by viewModel.apps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val denseLayout by viewModel.denseLayout.collectAsState()
    val showDupes by viewModel.duplicateShortcuts.collectAsState()
    val launchError by viewModel.launchError.collectAsState()

    val now = remember { Date() }
    val timeStr  = remember { SimpleDateFormat("h:mm a", Locale.getDefault()).format(now) }
    val dateStr  = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(now) }

    // Dock: prefer Phone, Messages, Chrome, Camera — whatever is installed
    val dockApps = remember(apps) {
        val dockPkgs = listOf(
            "com.google.android.dialer", "com.android.dialer",
            "com.google.android.apps.messaging", "com.android.messaging",
            "com.android.chrome", "org.mozilla.firefox",
            "com.android.camera2", "com.google.android.GoogleCamera",
            "com.ciyato.launcher",
        )
        val byPkg = apps.associateBy { it.packageName }
        dockPkgs.mapNotNull { byPkg[it] }.take(5)
            .ifEmpty { apps.take(5) }
    }

    val dupeApps = remember(apps) { viewModel.multiCategoryApps().take(8) }

    // Snackbar for launch error
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(launchError) {
        launchError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearLaunchError()
        }
    }

    Scaffold(
        containerColor = CiyatoBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(CiyatoBgEl2, CiyatoBg, CiyatoBg)))
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = padding.calculateTopPadding() + 16.dp,
                    bottom = 140.dp,  // room for dock
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Top bar
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                "Good morning, Alex ☀",
                                color = CiyatoWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                            )
                            Text(dateStr, color = CiyatoSec, fontSize = 13.sp)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {},
                                modifier = Modifier.size(40.dp).clip(CircleShape)
                                    .background(CiyatoBgEl)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = "AI",
                                    tint = CiyatoGold, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = {},
                                modifier = Modifier.size(40.dp).clip(CircleShape)
                                    .background(CiyatoBgEl)
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications",
                                    tint = CiyatoSec, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // Search bar
                item {
                    CiyatoSearchBar(
                        query = searchQuery,
                        onQueryChange = viewModel::setSearch,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Search results (shown when searching)
                if (searchQuery.isNotBlank()) {
                    item {
                        Text("Results", color = CiyatoSec, fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold)
                    }
                    if (isLoading) {
                        item { CircularProgressIndicator(color = CiyatoGold) }
                    } else if (searchResults.isEmpty()) {
                        item { Text("No apps found", color = CiyatoMuted) }
                    } else {
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(searchResults) { app ->
                                    AppIconTile(
                                        app = app,
                                        onClick = { viewModel.launchApp(app) },
                                        modifier = Modifier.width(64.dp)
                                    )
                                }
                            }
                        }
                    }
                    return@LazyColumn
                }

                // Weather + Agenda
                item { WeatherAgendaRow(modifier = Modifier.fillMaxWidth()) }

                // Smart Categories header
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Smart categories",
                            color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text("Edit", color = CiyatoBlue, fontSize = 13.sp)
                    }
                }

                // Category grid (3 columns — dense; 2 columns — spacious)
                if (isLoading) {
                    item { CircularProgressIndicator(color = CiyatoGold, modifier = Modifier.padding(16.dp)) }
                } else {
                    item {
                        val columns = if (denseLayout) 3 else 2
                        val cardHeight = if (denseLayout) 110.dp else 130.dp
                        val categoriesToShow = LAUNCHER_CATEGORIES.filter {
                            viewModel.byCategory(it).isNotEmpty()
                        }
                        // Compose LazyVerticalGrid inside LazyColumn needs a fixed height
                        val rows = (categoriesToShow.size + columns - 1) / columns
                        val gridHeight = rows * (cardHeight.value + 10).dp

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            modifier = Modifier.fillMaxWidth().height(gridHeight),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            userScrollEnabled = false,
                        ) {
                            items(categoriesToShow) { cat ->
                                SmartCategoryCard(
                                    category = cat,
                                    apps = viewModel.byCategory(cat),
                                    onTap = { /* navigate to category */ },
                                    onAppTap = viewModel::launchApp,
                                    modifier = Modifier.height(cardHeight),
                                )
                            }
                        }
                    }
                }

                // Duplicate smart shortcuts
                if (showDupes && dupeApps.isNotEmpty()) {
                    item {
                        DuplicateShortcutStrip(
                            apps = dupeApps,
                            onAppTap = viewModel::launchApp,
                            onManage = {},
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            // Bottom dock — pinned above system nav
            BottomDock(
                dockApps = dockApps,
                onAppTap = viewModel::launchApp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = padding.calculateBottomPadding() + 12.dp)
            )

            // App Drawer FAB
            FloatingActionButton(
                onClick = onOpenDrawer,
                containerColor = CiyatoBgEl,
                contentColor = CiyatoSec,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = padding.calculateBottomPadding() + 100.dp)
                    .size(40.dp),
                shape = CircleShape,
            ) {
                Icon(Icons.Default.Apps, contentDescription = "App Drawer",
                    modifier = Modifier.size(20.dp))
            }
        }
    }
}
