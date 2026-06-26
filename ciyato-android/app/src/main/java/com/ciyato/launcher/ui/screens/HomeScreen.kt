package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.data.InstalledApp
import com.ciyato.launcher.ui.components.*
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel
import java.text.SimpleDateFormat
import java.util.*

private val HOME_CATEGORIES = listOf(
    AppCategory.WORK,
    AppCategory.SOCIAL,
    AppCategory.FINANCE,
    AppCategory.CREATIVITY,
    AppCategory.UTILITIES,
    AppCategory.DAILY,
)

// Priority dock packages — matched by package name against what's installed
private val DOCK_PRIORITY_PACKAGES = listOf(
    "com.google.android.dialer",
    "com.android.dialer",
    "com.samsung.android.dialer",
    "com.google.android.apps.messaging",
    "com.android.messaging",
    "com.samsung.android.messaging",
    "com.android.chrome",
    "org.mozilla.firefox",
    "com.google.android.GoogleCamera",
    "com.android.camera2",
    "com.sec.android.app.camera",
)

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit,
    onCategoryTap: (AppCategory) -> Unit = {},
    onWeatherTap: () -> Unit = {},
    onAgendaTap: () -> Unit = {},
    onDuplicatesTap: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val apps          by viewModel.apps.collectAsState()
    val isLoading     by viewModel.isLoading.collectAsState()
    val searchQuery   by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val denseLayout   by viewModel.denseLayout.collectAsState()
    val showDupes     by viewModel.duplicateShortcuts.collectAsState()
    val launchError   by viewModel.launchError.collectAsState()
    val weatherState  by viewModel.weatherState.collectAsState()

    // Resolved greeting comes from ViewModel (de-duplicated)
    val dateStr = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    // Resolved dock apps — priority order, falls back to first 5 installed
    val dockApps = remember(apps) {
        val byPkg = apps.associateBy { it.packageName }
        DOCK_PRIORITY_PACKAGES.mapNotNull { byPkg[it] }
            .distinctBy { it.packageName }
            .take(5)
            .ifEmpty { apps.take(5) }
    }

    val dupeApps = remember(apps) { viewModel.multiCategoryApps().take(7) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(launchError) {
        launchError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearLaunchError()
        }
    }

    // Density sizing
    val columns        = if (denseLayout) 3 else 2
    val categoryIconSz: Dp = if (denseLayout) 38.dp else 46.dp
    val categoryCardH: Dp  = if (denseLayout) 114.dp else 142.dp
    val topPad             = if (denseLayout) 20.dp else 36.dp
    val spacing            = if (denseLayout) 14.dp else 22.dp
    val greetingSize       = if (denseLayout) 24.sp else 30.sp

    Scaffold(
        containerColor = CiyatoBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { scaffoldPadding ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(0f to CiyatoBgEl2, 0.18f to CiyatoBg, 1f to CiyatoBg)
                    )
                )
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start  = 16.dp, end = 16.dp,
                    top    = scaffoldPadding.calculateTopPadding() + topPad,
                    bottom = 120.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.fillMaxSize(),
            ) {

                // ── 1. Greeting ───────────────────────────────────────────────
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                viewModel.greeting,
                                color = CiyatoWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = greetingSize,
                                lineHeight = greetingSize * 1.1f,
                            )
                            Spacer(Modifier.height(if (denseLayout) 2.dp else 4.dp))
                            Text(dateStr, color = CiyatoSec, fontSize = if (denseLayout) 13.sp else 14.sp)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ActionCircle(Icons.Default.AutoFixHigh, CiyatoGold, if (denseLayout) 42.dp else 48.dp)
                            Box(contentAlignment = Alignment.TopEnd) {
                                ActionCircle(Icons.Default.Notifications, CiyatoSec, if (denseLayout) 42.dp else 48.dp)
                                Box(
                                    modifier = Modifier
                                        .size(if (denseLayout) 9.dp else 10.dp)
                                        .offset(x = (-1).dp, y = 1.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                        .border(1.5.dp, CiyatoBg, CircleShape),
                                )
                            }
                        }
                    }
                }

                // ── 2. Search ─────────────────────────────────────────────────
                item {
                    CiyatoHomeSearchBar(
                        query = searchQuery,
                        onQueryChange = viewModel::setSearch,
                        isDense = denseLayout,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // ── 3. Search results (early-return style) ────────────────────
                if (searchQuery.isNotBlank()) {
                    if (isLoading) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = CiyatoGold, strokeWidth = 2.dp)
                            }
                        }
                    } else if (searchResults.isEmpty()) {
                        item {
                            Text("No apps match \"$searchQuery\"", color = CiyatoMuted,
                                modifier = Modifier.padding(vertical = 16.dp))
                        }
                    } else {
                        item {
                            Text("Results", color = CiyatoSec, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                items(searchResults.take(12)) { app ->
                                    AppIconTile(
                                        app      = app,
                                        iconSize = if (denseLayout) 54.dp else 60.dp,
                                        onClick  = { viewModel.launchApp(app) },
                                        modifier = Modifier.width(if (denseLayout) 68.dp else 76.dp),
                                    )
                                }
                            }
                        }
                    }
                    return@LazyColumn
                }

                // ── 4. Weather + Agenda ────────────────────────────────────────
                item {
                    WeatherAgendaRow(
                        isDense      = denseLayout,
                        weatherState = weatherState,
                        onWeatherTap = onWeatherTap,
                        onAgendaTap  = onAgendaTap,
                        modifier     = Modifier.fillMaxWidth(),
                    )
                }

                // ── 5. Smart categories header ────────────────────────────────
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Smart categories", color = CiyatoWhite, fontWeight = FontWeight.SemiBold,
                                fontSize = if (denseLayout) 17.sp else 20.sp)
                            Box(Modifier.width(32.dp).height(1.dp).background(CiyatoSubtleBorder))
                        }
                        Text("Edit", color = CiyatoBlue,
                            fontSize = if (denseLayout) 13.sp else 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                // ── 6. Category grid ───────────────────────────────────────────
                if (isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = CiyatoGold, strokeWidth = 2.dp,
                                modifier = Modifier.size(28.dp))
                        }
                    }
                } else {
                    item {
                        val catsToShow = HOME_CATEGORIES.filter { viewModel.byCategory(it).isNotEmpty() }
                        if (catsToShow.isEmpty()) {
                            Text("No apps found", color = CiyatoMuted, modifier = Modifier.padding(16.dp))
                        } else {
                            val rows  = (catsToShow.size + columns - 1) / columns
                            val gridH = categoryCardH * rows + 10.dp * (rows - 1).coerceAtLeast(0)
                            CategoryGrid(
                                categories    = catsToShow,
                                viewModel     = viewModel,
                                columns       = columns,
                                iconSize      = categoryIconSz,
                                cardHeight    = categoryCardH,
                                gridHeight    = gridH,
                                onCategoryTap = onCategoryTap,
                            )
                        }
                    }
                }

                // ── 7. Duplicate Shortcuts ────────────────────────────────────
                if (showDupes && dupeApps.isNotEmpty()) {
                    item {
                        DuplicateShortcutStrip(
                            apps       = dupeApps,
                            onAppTap   = viewModel::launchApp,
                            onManage   = onDuplicatesTap,
                            onStripTap = onDuplicatesTap,
                            modifier   = Modifier.fillMaxWidth(),
                        )
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }

            // ── Fixed dock overlay ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = scaffoldPadding.calculateBottomPadding() + 20.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (dockApps.isNotEmpty()) {
                        BottomDock(dockApps = dockApps, onAppTap = viewModel::launchApp)
                    }
                    LauncherNavBar(onOpenDrawer = onOpenDrawer, onOpenSettings = onOpenSettings)
                }
            }
        }
    }
}

// ─── Shared helpers ───────────────────────────────────────────────────────────

@Composable
private fun ActionCircle(icon: ImageVector, color: Color, size: Dp, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size).clip(CircleShape).background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, CircleShape),
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(size * 0.5f))
    }
}

@Composable
private fun CategoryGrid(
    categories: List<AppCategory>,
    viewModel: LauncherViewModel,
    columns: Int,
    iconSize: Dp,
    cardHeight: Dp,
    gridHeight: Dp,
    onCategoryTap: (AppCategory) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().height(gridHeight),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        categories.chunked(columns).forEach { rowCats ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowCats.forEach { cat ->
                    SmartCategoryCard(
                        category = cat,
                        apps     = viewModel.byCategory(cat),
                        onTap    = { onCategoryTap(cat) },
                        onAppTap = viewModel::launchApp,
                        iconSize = iconSize,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                }
                repeat(columns - rowCats.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun CiyatoHomeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isDense: Boolean,
    modifier: Modifier = Modifier,
) {
    val height   = if (isDense) 50.dp else 56.dp
    val fontSize = if (isDense) 14.sp else 15.sp

    Box(
        modifier = modifier.height(height)
            .clip(RoundedCornerShape(999.dp)).background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(999.dp)),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (query.isBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Icon(Icons.Default.Search, null, tint = CiyatoMuted, modifier = Modifier.size(18.dp))
                Text("Search apps, files, contacts…", color = CiyatoMuted, fontSize = fontSize)
            }
        }
        androidx.compose.foundation.text.BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = CiyatoWhite, fontSize = fontSize),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp, vertical = 13.dp),
        )
    }
}

@Composable
private fun LauncherNavBar(onOpenDrawer: () -> Unit, onOpenSettings: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(36.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 24.dp),
    ) {
        NavCircle(Icons.Default.Apps, "App Drawer", onOpenDrawer)
        NavCircle(Icons.Default.Settings, "Settings", onOpenSettings)
    }
}

@Composable
private fun NavCircle(icon: ImageVector, label: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(52.dp).clip(CircleShape).background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, CircleShape).clickable(onClick = onClick),
    ) {
        Icon(icon, label, tint = CiyatoSec, modifier = Modifier.size(24.dp))
    }
}
