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
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

// The 6 categories shown on the Home Launcher — matches reference exactly.
private val HOME_CATEGORIES = listOf(
    AppCategory.WORK,
    AppCategory.SOCIAL,
    AppCategory.FINANCE,
    AppCategory.CREATIVITY,
    AppCategory.UTILITIES,
    AppCategory.DAILY,
)

/**
 * ═══════════════════════════════════════════════════════════════════
 *  CIYATO HOME LAUNCHER — 3-Pass Visual Parity Implementation
 *  Target: the uploaded premium Ciyato Home Launcher reference images.
 * ═══════════════════════════════════════════════════════════════════
 *
 * PASS 1 — Structure:
 *   Top bar (greeting + date + action buttons)
 *   Search bar (full-width pill)
 *   Weather + Agenda cards (side by side)
 *   "Smart categories" header + Edit link
 *   2-column (spacious) or 3-column (dense) category grid
 *   Duplicate smart shortcuts strip (gold tinted)
 *   Bottom dock (real app icons, pill shape)
 *   Dock overlays the scroll content — position: absolute at bottom
 *
 * PASS 2 — Styling:
 *   Background: vertical gradient CiyatoBgEl2 → CiyatoBg (top dark-teal fade)
 *   All cards: CiyatoBgEl (#12171B), radius 20–22dp, CiyatoSubtleBorder
 *   Greeting: 26sp Bold white
 *   Date: 13sp CiyatoSec
 *   Action buttons: 42dp circle, CiyatoBgEl bg
 *   Search: pill (999dp), CiyatoBgEl, muted placeholder, search icon left
 *   Category card: 20dp radius, 14sp title, 11sp count, 3 icons + overflow
 *   Dense card height: 114dp | Spacious: 134dp
 *   Dupe strip: gold-tinted (0.08 alpha), gold border (0.22), 22dp radius
 *   Dock: 28dp radius, CiyatoBgEl2 @ 0.88, subtle border, 54dp icons
 *
 * PASS 3 — Polish:
 *   Dock is fixed at bottom via Box overlay (not in LazyColumn)
 *   LazyColumn bottom padding accounts for dock height (~100dp + insets)
 *   Search results inline, no navigation break
 *   Category grid uses non-scrollable nested grid with exact pre-computed height
 *   Greeting uses real calendar time for "Good morning/afternoon/evening"
 *   Notification badge dot on bell icon (red, 8dp)
 *   Section divider line between greeting and search (subtle, matches reference)
 */
@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val apps         by viewModel.apps.collectAsState()
    val isLoading    by viewModel.isLoading.collectAsState()
    val searchQuery  by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val denseLayout  by viewModel.denseLayout.collectAsState()
    val showDupes    by viewModel.duplicateShortcuts.collectAsState()
    val launchError  by viewModel.launchError.collectAsState()

    // Dynamic greeting
    val greetingStr = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good morning, Alex ☀\uFE0F"
            hour < 17 -> "Good afternoon, Alex \uD83C\uDF1E"
            else      -> "Good evening, Alex \uD83C\uDF19"
        }
    }
    val dateStr = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    // Dock: Phone → Messages → Chrome → Camera → Ciyato (or fallback to first 5 apps)
    val dockApps = remember(apps) {
        val priority = listOf(
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
            "com.ciyato.launcher",
        )
        val byPkg = apps.associateBy { it.packageName }
        priority.mapNotNull { byPkg[it] }.distinctBy { it.packageName }.take(5)
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

    // Card/icon sizing by density
    val categoryColumns = if (denseLayout) 3 else 2
    val categoryIconSize: Dp = if (denseLayout) 38.dp else 44.dp
    val categoryCardH: Dp   = if (denseLayout) 114.dp else 136.dp

    Scaffold(
        containerColor = CiyatoBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { scaffoldPadding ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to CiyatoBgEl2,
                            0.18f to CiyatoBg,
                            1.00f to CiyatoBg,
                        )
                    )
                )
        ) {
            // ── Scrollable content ─────────────────────────────────────────────
            LazyColumn(
                contentPadding = PaddingValues(
                    start  = 16.dp,
                    end    = 16.dp,
                    top    = scaffoldPadding.calculateTopPadding() + 20.dp,
                    bottom = 110.dp,   // reserve space for dock overlay
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize(),
            ) {

                // ── 1. Top bar: greeting + action buttons ──────────────────────
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = greetingStr,
                                color = CiyatoWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                lineHeight = 28.sp,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = dateStr,
                                color = CiyatoSec,
                                fontSize = 13.sp,
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Ciyato AI button
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(CiyatoBgEl)
                                    .border(1.dp, CiyatoSubtleBorder, CircleShape)
                                    .clickable {},
                            ) {
                                Icon(
                                    Icons.Default.AutoFixHigh,
                                    contentDescription = "Ciyato AI",
                                    tint = CiyatoGold,
                                    modifier = Modifier.size(20.dp),
                                )
                            }

                            // Notifications button with dot badge
                            Box(contentAlignment = Alignment.TopEnd) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(CiyatoBgEl)
                                        .border(1.dp, CiyatoSubtleBorder, CircleShape)
                                        .clickable {},
                                ) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = CiyatoSec,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                                // Red notification dot
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .offset(x = (-1).dp, y = 1.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                        .border(1.5.dp, CiyatoBg, CircleShape),
                                )
                            }
                        }
                    }
                }

                // ── 2. Search bar (full-width pill) ────────────────────────────
                item {
                    CiyatoHomeSearchBar(
                        query = searchQuery,
                        onQueryChange = viewModel::setSearch,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // ── 3. Search results overlay (replaces rest of content) ───────
                if (searchQuery.isNotBlank()) {
                    if (isLoading) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = CiyatoGold, strokeWidth = 2.dp)
                            }
                        }
                    } else if (searchResults.isEmpty()) {
                        item {
                            Text(
                                "No apps found for \"$searchQuery\"",
                                color = CiyatoMuted,
                                modifier = Modifier.padding(vertical = 16.dp),
                            )
                        }
                    } else {
                        item {
                            Text("Results", color = CiyatoSec, fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold)
                        }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                items(searchResults.take(12)) { app ->
                                    AppIconTile(
                                        app = app,
                                        iconSize = 54.dp,
                                        onClick = { viewModel.launchApp(app) },
                                        modifier = Modifier.width(68.dp),
                                    )
                                }
                            }
                        }
                    }
                    return@LazyColumn   // don't render rest while searching
                }

                // ── 4. Weather + Agenda widgets ────────────────────────────────
                item {
                    WeatherAgendaRow(modifier = Modifier.fillMaxWidth())
                }

                // ── 5. Smart categories header ─────────────────────────────────
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                "Smart categories",
                                color = CiyatoWhite,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp,
                            )
                            // Thin separator line — matches reference
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(1.dp)
                                    .background(CiyatoSubtleBorder),
                            )
                        }
                        Text(
                            "Edit",
                            color = CiyatoBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                // ── 6. Category grid ───────────────────────────────────────────
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                color = CiyatoGold,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }
                } else {
                    item {
                        val catsToShow = HOME_CATEGORIES.filter {
                            viewModel.byCategory(it).isNotEmpty()
                        }

                        // Pre-compute grid height so nested grid doesn't conflict with LazyColumn
                        val rows      = (catsToShow.size + categoryColumns - 1) / categoryColumns
                        val gapDp     = 10.dp
                        val gridH     = categoryCardH * rows + gapDp * (rows - 1).coerceAtLeast(0)

                        if (catsToShow.isEmpty()) {
                            // Fallback — no categories yet (apps still loading or device has few apps)
                            Text(
                                "Installing apps…",
                                color = CiyatoMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 16.dp),
                            )
                        } else {
                            CategoryGrid(
                                categories    = catsToShow,
                                viewModel     = viewModel,
                                columns       = categoryColumns,
                                iconSize      = categoryIconSize,
                                cardHeight    = categoryCardH,
                                gridHeight    = gridH,
                            )
                        }
                    }
                }

                // ── 7. Duplicate Smart Shortcuts ───────────────────────────────
                if (showDupes && dupeApps.isNotEmpty()) {
                    item {
                        DuplicateShortcutStrip(
                            apps     = dupeApps,
                            onAppTap = viewModel::launchApp,
                            onManage = onOpenSettings,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                // Bottom spacer (dock height + safe area)
                item { Spacer(Modifier.height(16.dp)) }
            }

            // ── Dock: fixed overlay at the very bottom ─────────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        start  = 0.dp,
                        end    = 0.dp,
                        bottom = scaffoldPadding.calculateBottomPadding() + 20.dp,
                    ),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Dock row (real apps)
                    if (dockApps.isNotEmpty()) {
                        BottomDock(
                            dockApps = dockApps,
                            onAppTap = viewModel::launchApp,
                        )
                    }

                    // App drawer + settings nav bar below dock
                    LauncherNavBar(
                        onOpenDrawer   = onOpenDrawer,
                        onOpenSettings = onOpenSettings,
                    )
                }
            }
        }
    }
}

// ─── Inline category grid ──────────────────────────────────────────────────────
@Composable
private fun CategoryGrid(
    categories: List<AppCategory>,
    viewModel: LauncherViewModel,
    columns: Int,
    iconSize: Dp,
    cardHeight: Dp,
    gridHeight: Dp,
) {
    // Manual grid: rows of 'columns' items — avoids nested LazyVerticalGrid issues
    val rows = categories.chunked(columns)
    Column(
        modifier    = Modifier.fillMaxWidth().height(gridHeight),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        rows.forEach { rowCats ->
            Row(
                modifier            = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowCats.forEach { cat ->
                    SmartCategoryCard(
                        category  = cat,
                        apps      = viewModel.byCategory(cat),
                        onTap     = {},
                        onAppTap  = viewModel::launchApp,
                        iconSize  = iconSize,
                        modifier  = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
                // Fill empty cells if last row is incomplete
                repeat(columns - rowCats.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

// ─── Premium pill search bar (home-screen specific) ────────────────────────────
@Composable
private fun CiyatoHomeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(999.dp))
            .clickable { /* focus handled by BasicTextField */ },
        contentAlignment = Alignment.CenterStart,
    ) {
        if (query.isBlank()) {
            // Placeholder row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = CiyatoMuted,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    "Search apps, files, contacts…",
                    color = CiyatoMuted,
                    fontSize = 14.sp,
                )
            }
        }
        // Actual text field (transparent, overlays placeholder)
        androidx.compose.foundation.text.BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = CiyatoWhite,
                fontSize = 14.sp,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 13.dp),
        )
    }
}

// ─── Bottom launcher nav bar (drawer + settings) ───────────────────────────────
@Composable
private fun LauncherNavBar(
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 24.dp),
    ) {
        // App Drawer (grid icon)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(CiyatoBgEl.copy(alpha = 0.72f))
                .border(1.dp, CiyatoSubtleBorder, CircleShape)
                .clickable(onClick = onOpenDrawer),
        ) {
            Icon(Icons.Default.Apps, "App Drawer",
                tint = CiyatoWhite, modifier = Modifier.size(22.dp))
        }

        // Favorites placeholder
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(CiyatoBgEl.copy(alpha = 0.72f))
                .border(1.dp, CiyatoSubtleBorder, CircleShape)
                .clickable {},
        ) {
            Icon(
                Icons.Default.Star,
                "Favorites",
                tint = CiyatoMuted,
                modifier = Modifier.size(22.dp),
            )
        }

        // Settings
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(CiyatoBgEl.copy(alpha = 0.72f))
                .border(1.dp, CiyatoSubtleBorder, CircleShape)
                .clickable(onClick = onOpenSettings),
        ) {
            Icon(
                Icons.Default.Settings,
                "Settings",
                tint = CiyatoMuted,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
