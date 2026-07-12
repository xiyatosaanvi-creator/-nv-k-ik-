package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.data.InstalledApp
import com.ciyato.launcher.ui.components.AppIconTile
import com.ciyato.launcher.ui.components.CiyatoSearchBar
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.ui.components.*
import com.ciyato.launcher.viewmodel.LauncherViewModel

// ── Light-mode palette (cream surface, matches reference) ─────────────────────
private val DrawerBg        = CiyatoBg
private val DrawerCard      = CiyatoBgEl
private val DrawerBorder    = CiyatoSubtleBorder
private val DrawerText      = CiyatoWhite
private val DrawerSec       = CiyatoSec
private val DrawerMuted     = CiyatoMuted
private val DrawerSearch    = CiyatoBgEl2

// ── Sections shown in the drawer (matches reference order) ────────────────────
private data class DrawerSection(
    val category: AppCategory?,   // null = "All" flat grid
    val label: String,
    val isExpandable: Boolean = true,
)

private val DRAWER_SECTIONS = listOf(
    DrawerSection(AppCategory.WORK,             "Work"),
    DrawerSection(AppCategory.SOCIAL,           "Social"),
    DrawerSection(AppCategory.UTILITIES,        "Utilities"),
    DrawerSection(AppCategory.CREATIVITY,       "Creativity"),
    DrawerSection(AppCategory.ENTERTAINMENT,    "Entertainment"),
    DrawerSection(AppCategory.TRAVEL,           "Travel"),
    DrawerSection(AppCategory.FINANCE,          "Finance"),
    DrawerSection(AppCategory.COMMUNICATION,    "Communication"),
    DrawerSection(AppCategory.CONTACTS,         "Contacts"),
    DrawerSection(AppCategory.AI,               "AI"),
    DrawerSection(AppCategory.VIDEO_EDITING,    "Video Editing"),
)

// Filter chips at the top
private val FILTER_CHIPS = listOf(
    null to "All",
    AppCategory.WORK to "Work",
    AppCategory.SOCIAL to "Social",
    AppCategory.CREATIVITY to "Creativity",
    AppCategory.UTILITIES to "Utilities",
    AppCategory.ENTERTAINMENT to "Entertainment",
    AppCategory.FINANCE to "Finance",
    AppCategory.TRAVEL to "Travel",
    AppCategory.AI to "AI",
    AppCategory.VIDEO_EDITING to "Video Editing",
)

/**
 * ═══════════════════════════════════════════════════════════════════
 *  CIYATO APP DRAWER / SMART APP LIBRARY — Functional Wiring Phase
 *  Sections expand/collapse ✓ (already worked)
 *  Duplicate shortcuts card is now fully clickable via onDuplicatesTap ✓
 * ═══════════════════════════════════════════════════════════════════
 */
@Composable
fun AppDrawerScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
) {
    val apps        by viewModel.apps.collectAsState()
    val isLoading   by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val drawerStyle by viewModel.drawerStyle.collectAsState()

    // Active filter chip (null = All)
    var activeFilter by remember { mutableStateOf<AppCategory?>(null) }
    var sortMode by remember { mutableStateOf("alpha") }
    var contextMenuApp by remember { mutableStateOf<InstalledApp?>(null) }

    // Sections that have apps (avoid empty section cards)
    val populatedSections = remember(apps) {
        DRAWER_SECTIONS.filter { section ->
            when (section.category) {
                null -> false
                AppCategory.RECENTLY_ADDED -> viewModel.recentlyAdded().isNotEmpty()
                else -> viewModel.byCategory(section.category).isNotEmpty()
            }
        }
    }

    // When a filter chip is active, flatten to a single section
    val sectionsToShow = remember(activeFilter, populatedSections) {
        val filter = activeFilter
        when (filter) {
            null -> populatedSections
            else -> listOf(DrawerSection(filter, filter.displayName, false))
        }
    }

    Scaffold(
        containerColor = DrawerBg,
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DrawerBg)
                .padding(scaffoldPadding)
        ) {
            // ── 1. Neutral drawer header ─────────────────────────────────────
            DrawerHeader(
                onBack = onBack,
                drawerStyle = drawerStyle,
            )

            // ── 2. Search Area ────────────────────────────────────────────────
            CiyatoSearchBar(
                query           = searchQuery,
                onQueryChange   = viewModel::setSearch,
                placeholder     = "Search apps...",
                backgroundColor = DrawerSearch,
                borderColor     = DrawerBorder,
                iconTint        = DrawerMuted,
                textColor       = DrawerText,
                placeholderColor= DrawerMuted,
                modifier        = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Spacer(Modifier.height(16.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(FILTER_CHIPS) { (cat, label) ->
                    val selected = activeFilter == cat
                    CiyatoToggleChip(
                        text = label,
                        selected = selected,
                        onClick = {
                            activeFilter = cat
                            viewModel.setSearch("")
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── 4. Pagination Indicator ───────────────────────────────────────
            // ── 5. Main content: search results OR section cards ─────────────────
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color       = CiyatoGold,
                        strokeWidth = 2.dp,
                        modifier    = Modifier.size(28.dp),
                    )
                }
            } else if (searchQuery.isNotBlank()) {
                // Search results — flat grid
                SearchResultsGrid(
                    results  = sortDrawerApps(searchResults, sortMode, viewModel),
                    onAppTap = viewModel::launchApp,
                    onAppLongTap = { contextMenuApp = it },
                )
            } else {
                // Section cards
                LazyColumn(
                    contentPadding = PaddingValues(
                        start  = 16.dp,
                        end    = 16.dp,
                        top    = 0.dp,
                        bottom = 32.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(sectionsToShow) { section ->
                        DrawerSectionCard(
                            section  = section,
                            viewModel= viewModel,
                            sortMode = sortMode,
                            drawerStyle = drawerStyle,
                            onAppLongTap = { contextMenuApp = it },
                        )
                    }

                    // ── 6. Duplicate Shortcuts Area ───────────────────────────
                }
            }
        }
    }

    contextMenuApp?.let { app ->
        AppContextMenu(
            app = app,
            viewModel = viewModel,
            onDismiss = { contextMenuApp = null }
        )
    }
}

private fun sortDrawerApps(
    apps: List<InstalledApp>,
    sortMode: String,
    viewModel: LauncherViewModel,
): List<InstalledApp> {
    return when (sortMode) {
        "frequent" -> apps.sortedWith(
            compareByDescending<InstalledApp> { viewModel.launchCount(it.packageName) }
                .thenBy { it.label.lowercase() }
        )
        "recent" -> apps.sortedWith(
            compareByDescending<InstalledApp> { it.installTime }
                .thenBy { it.label.lowercase() }
        )
        else -> apps.sortedBy { it.label.lowercase() }
    }
}

private fun sortModeLabel(sortMode: String): String {
    return when (sortMode) {
        "frequent" -> "Most used"
        "recent" -> "Recently installed"
        else -> "A to Z"
    }
}

private fun drawerStyleLabel(drawerStyle: String): String {
    return when (drawerStyle) {
        "dense" -> "Dense grid"
        "spacious" -> "Spacious grid"
        else -> "Standard grid"
    }
}

// ── Header row ─────────────────────────────────────────────────────────────────
@Composable
private fun DrawerHeader(
    onBack: () -> Unit = {},
    drawerStyle: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "Apps",
                color = DrawerText,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
        }

        // Right: current drawer mode label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                drawerStyleLabel(drawerStyle),
                color = CiyatoGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(CiyatoGold.copy(alpha = 0.12f))
                    .border(1.dp, CiyatoGold.copy(alpha = 0.20f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            )
        }
    }
}

// ── Section card ───────────────────────────────────────────────────────────────
@Composable
private fun DrawerSectionCard(
    section: DrawerSection,
    viewModel: LauncherViewModel,
    sortMode: String,
    drawerStyle: String,
    onAppLongTap: (InstalledApp) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showAll by remember(section.label) { mutableStateOf(false) }

    // Resolve apps for this section
    val category = section.category ?: return
    val rawSectionApps = when (category) {
        AppCategory.RECENTLY_ADDED -> viewModel.recentlyAdded()
        else -> viewModel.byCategory(category)
    }
    val sectionApps = sortDrawerApps(rawSectionApps, sortMode, viewModel)

    if (sectionApps.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(DrawerCard)
            .border(1.dp, DrawerBorder, RoundedCornerShape(22.dp)),
    ) {
        // ── Card header ────────────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = section.isExpandable) { expanded = !expanded }
                .padding(horizontal = 18.dp, vertical = 16.dp),
        ) {
            Text(
                text = section.label,
                color = DrawerText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = "${sectionApps.size} apps",
                color = DrawerMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 12.dp),
            )

            Icon(
                Icons.Default.FolderOpen,
                contentDescription = null,
                tint = DrawerMuted,
                modifier = Modifier.size(20.dp),
            )

            if (section.isExpandable) {
                Spacer(Modifier.width(10.dp))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = DrawerSec,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        if (expanded) {
            HorizontalDivider(
                color     = DrawerBorder,
                thickness = 1.dp,
                modifier  = Modifier.padding(horizontal = 16.dp),
            )

            val columns = if (drawerStyle == "spacious") 3 else 4
            val visibleLimit = when (drawerStyle) {
                "dense" -> 16
                "spacious" -> 9
                else -> 12
            }
            val iconSize = when (drawerStyle) {
                "dense" -> 48.dp
                "spacious" -> 62.dp
                else -> 56.dp
            }
            val visibleApps = if (showAll) sectionApps else sectionApps.take(visibleLimit)
            val rows = visibleApps.chunked(columns)
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(if (drawerStyle == "spacious") 12.dp else 8.dp),
            ) {
                rows.forEach { rowApps ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                    ) {
                        rowApps.forEach { app ->
                            AppIconTile(
                                app        = app,
                                iconSize   = iconSize,
                                labelColor = DrawerSec,
                                onClick    = { viewModel.launchApp(app) },
                                onLongClick = { onAppLongTap(app) },
                                modifier   = Modifier.weight(1f),
                            )
                        }
                        repeat(columns - rowApps.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }

            if (sectionApps.size > visibleLimit) {
                Text(
                    if (showAll) "Show fewer" else "View all ${sectionApps.size} apps",
                    color = CiyatoGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(start = 18.dp, top = 0.dp, end = 18.dp, bottom = 14.dp)
                        .align(Alignment.End)
                        .clickable { showAll = !showAll },
                )
            }
        }
    }
}

// ── Search results (flat 4-col grid) ──────────────────────────────────────────
@Composable
private fun SearchResultsGrid(
    results: List<InstalledApp>,
    onAppTap: (InstalledApp) -> Unit,
    onAppLongTap: (InstalledApp) -> Unit,
) {
    if (results.isEmpty()) {
        Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
            Text("No apps found", color = DrawerMuted, fontSize = 14.sp)
        }
        return
    }
    val rows = results.chunked(4)
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(rows) { rowApps ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowApps.forEach { app ->
                    AppIconTile(
                        app        = app,
                        iconSize   = 54.dp,
                        labelColor = DrawerSec,
                        onClick    = { onAppTap(app) },
                        onLongClick = { onAppLongTap(app) },
                        modifier   = Modifier.weight(1f),
                    )
                }
                repeat(4 - rowApps.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

// ── Duplicate Smart Shortcuts card — now fully clickable ──────────────────────
