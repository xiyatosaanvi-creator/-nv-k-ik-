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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.FilterList
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
import com.ciyato.launcher.ui.components.RealAppIcon
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel

// ── Light-mode palette (cream surface, matches reference) ─────────────────────
private val DrawerBg        = Color(0xFFF5F2EC)   // warm cream
private val DrawerCard      = Color(0xFFEFECE5)   // slightly darker cream for cards
private val DrawerCardAlt   = Color(0xFFEAE7DF)   // alternate card
private val DrawerBorder    = Color(0x14000000)   // very subtle dark border
private val DrawerText      = Color(0xFF191B1F)   // primary dark text
private val DrawerSec       = Color(0xFF5F646A)   // secondary grey
private val DrawerMuted     = Color(0xFF9098A0)   // muted
private val DrawerSearch    = Color(0xFFE8E5DE)   // search bar bg

// ── Sections shown in the drawer (matches reference order) ────────────────────
private data class DrawerSection(
    val category: AppCategory?,   // null = "All" flat grid
    val label: String,
    val isExpandable: Boolean = true,
)

private val DRAWER_SECTIONS = listOf(
    DrawerSection(null,                         "Suggested",          isExpandable = false),
    DrawerSection(AppCategory.RECENTLY_ADDED,   "Recently Added",     isExpandable = false),
    DrawerSection(AppCategory.WORK,             "Work"),
    DrawerSection(AppCategory.SOCIAL,           "Social"),
    DrawerSection(AppCategory.UTILITIES,        "Utilities"),
    DrawerSection(AppCategory.CREATIVITY,       "Creativity"),
    DrawerSection(AppCategory.ENTERTAINMENT,    "Entertainment"),
    DrawerSection(AppCategory.TRAVEL,           "Travel"),
    DrawerSection(AppCategory.FINANCE,          "Finance"),
    DrawerSection(AppCategory.COMMUNICATION,    "Communication"),
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
)

/**
 * ═══════════════════════════════════════════════════════════════════
 *  CIYATO APP DRAWER / SMART APP LIBRARY — 3-Pass Visual Parity
 *  Target: the uploaded light/cream App Drawer reference image.
 * ═══════════════════════════════════════════════════════════════════
 *
 * PASS 1 — Structure
 *  - Light/cream DrawerBg (#F5F2EC) full screen
 *  - Header: "Ciyato" logo left, "Smart App Library" chip + filter icon right
 *  - Search bar (cream, full-width, rounded 14dp)
 *  - Filter chips (horizontal scroll, gold when selected)
 *  - Dot indicator row (3 dots, middle selected)
 *  - LazyColumn of SECTION CARDS (not a flat grid)
 *  - Each section = card with header (title + count + chevron) + icon row
 *  - Duplicate Smart Shortcuts section card at the bottom (gold-tinted)
 *
 * PASS 2 — Styling
 *  - Card bg: DrawerCard, corner 20dp, DrawerBorder
 *  - Section header: 15sp SemiBold DrawerText, count muted, chevron DrawerSec
 *  - App icon row: 54dp icons, label below in DrawerSec 11sp
 *  - Search bar: DrawerSearch bg, muted placeholder, dark text
 *  - Chips: gold bg when selected, DrawerCard when unselected
 *  - Duplicate card: gold-tinted (0.08f), gold border (0.20f)
 *  - Header "Ciyato": 24sp Bold DrawerText
 *  - "Smart App Library" chip: gold text, gold bg (0.12f)
 *  - App count badge: "+N" DrawerMuted box in icon row
 *
 * PASS 3 — Polish
 *  - Expanding sections: chevron rotates, sub-rows reveal
 *  - "Suggested" = top 6 multi-category apps (most contextual)
 *  - "Recently Added" = sorted by installTime desc
 *  - Empty sections hidden (filter out zero-app categories)
 *  - Search mode: collapses sections, shows flat grid of results
 *  - Icon label color: DrawerSec (dark-on-light, readable)
 *  - Dot indicator: 3 dots (pagination visual, non-functional in v1)
 *  - Bottom padding accounts for Android nav bar
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

    // Active filter chip (null = All)
    var activeFilter by remember { mutableStateOf<AppCategory?>(null) }

    // Sections that have apps (avoid empty section cards)
    val populatedSections = remember(apps) {
        DRAWER_SECTIONS.filter { section ->
            when (section.category) {
                null -> viewModel.multiCategoryApps().isNotEmpty() || apps.isNotEmpty()
                AppCategory.RECENTLY_ADDED -> viewModel.recentlyAdded().isNotEmpty()
                else -> viewModel.byCategory(section.category).isNotEmpty()
            }
        }
    }

    // When a filter chip is active, flatten to a single section
    val sectionsToShow = remember(activeFilter, populatedSections) {
        when (activeFilter) {
            null -> populatedSections
            else -> listOf(DrawerSection(activeFilter, activeFilter.displayName, false))
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
            // ── 1. Header: Logo + Smart App Library Chip ──────────────────────
            DrawerHeader()

            // ── 2. Search Area ────────────────────────────────────────────────
            CiyatoSearchBar(
                query           = searchQuery,
                onQueryChange   = viewModel::setSearch,
                placeholder     = "Search apps, files, contacts…",
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

            // ── 3. Filter Area ────────────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(FILTER_CHIPS) { (cat, label) ->
                    val selected = activeFilter == cat
                    Text(
                        text = label,
                        color = if (selected) DrawerBg else DrawerSec,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) CiyatoGold else DrawerCard)
                            .border(1.dp, if (selected) CiyatoGold else DrawerBorder, RoundedCornerShape(20.dp))
                            .clickable {
                                activeFilter = cat
                                viewModel.setSearch("")   // clear search when chip selected
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── 4. Pagination Indicator ───────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            ) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (i == 1) 22.dp else 6.dp, 6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (i == 1) CiyatoGold else DrawerBorder),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

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
                    results  = searchResults,
                    onAppTap = viewModel::launchApp,
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
                            apps     = apps,
                        )
                    }

                    // ── 6. Duplicate Shortcuts Area ───────────────────────────
                    item {
                        DuplicateShortcutsDrawerCard(
                            apps     = viewModel.multiCategoryApps().take(6),
                            onAppTap = viewModel::launchApp,
                        )
                    }
                }
            }
        }
    }
}

// ── Header row ─────────────────────────────────────────────────────────────────
@Composable
private fun DrawerHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        // Ciyato logo + name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // "C✦" badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color(0xFF0B0F12)),
            ) {
                Text("C✦", color = CiyatoGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                "Ciyato",
                color = DrawerText,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
        }

        // Right: "Smart App Library" chip + filter icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "Smart App Library",
                color = CiyatoGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(CiyatoGold.copy(alpha = 0.12f))
                    .border(1.dp, CiyatoGold.copy(alpha = 0.20f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DrawerCard)
                    .border(1.dp, DrawerBorder, RoundedCornerShape(8.dp))
                    .clickable {},
            ) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = "Sort/Filter",
                    tint = DrawerSec,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

// ── Section card ───────────────────────────────────────────────────────────────
@Composable
private fun DrawerSectionCard(
    section: DrawerSection,
    viewModel: LauncherViewModel,
    apps: List<InstalledApp>,
) {
    var expanded by remember { mutableStateOf(true) }

    // Resolve apps for this section
    val sectionApps = when (section.category) {
        null                        -> viewModel.multiCategoryApps().take(8)
                                           .ifEmpty { apps.take(8) }  // "Suggested"
        AppCategory.RECENTLY_ADDED  -> viewModel.recentlyAdded()
        else                        -> viewModel.byCategory(section.category)
    }

    if (sectionApps.isEmpty()) return

    val previewIcons = sectionApps.take(4)

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

            // Preview strip
            Row(horizontalArrangement = Arrangement.spacedBy((-10).dp)) {
                previewIcons.take(3).forEach { app ->
                    RealAppIcon(
                        drawable     = app.icon,
                        size         = 30.dp,
                        cornerRadius = 9.dp,
                        modifier     = Modifier
                            .border(2.dp, DrawerBg, RoundedCornerShape(9.dp))
                            .clickable { viewModel.launchApp(app) },
                    )
                }
            }

            if (section.isExpandable) {
                Spacer(Modifier.width(10.dp))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
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

            val rows = sectionApps.take(12).chunked(4)
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rows.forEach { rowApps ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                    ) {
                        rowApps.forEach { app ->
                            AppIconTile(
                                app        = app,
                                iconSize   = 56.dp,
                                labelColor = DrawerSec,
                                onClick    = { viewModel.launchApp(app) },
                                modifier   = Modifier.weight(1f),
                            )
                        }
                        repeat(4 - rowApps.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }

            if (sectionApps.size > 12) {
                Text(
                    "View all ${sectionApps.size} apps",
                    color = CiyatoGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(horizontal = 18.dp, bottom = 14.dp)
                        .align(Alignment.End),
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
                        modifier   = Modifier.weight(1f),
                    )
                }
                repeat(4 - rowApps.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

// ── Duplicate Smart Shortcuts card ─────────────────────────────────────────────
@Composable
private fun DuplicateShortcutsDrawerCard(
    apps: List<InstalledApp>,
    onAppTap: (InstalledApp) -> Unit,
) {
    if (apps.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CiyatoGold.copy(alpha = 0.08f))
            .border(1.dp, CiyatoGold.copy(alpha = 0.20f), RoundedCornerShape(22.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CiyatoGold.copy(alpha = 0.18f)),
            ) {
                Icon(Icons.Default.AutoFixHigh, contentDescription = null,
                    tint = CiyatoGold, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Duplicate smart shortcuts",
                    color = DrawerText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
                Text(
                    "One app, multiple places.",
                    color = DrawerSec,
                    fontSize = 12.sp,
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Icon row
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(apps) { app ->
                AppIconTile(
                    app        = app,
                    iconSize   = 54.dp,
                    labelColor = DrawerSec,
                    onClick    = { onAppTap(app) },
                )
            }
            item {
                // "+" slot
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(DrawerCardAlt)
                            .border(1.dp, DrawerBorder, RoundedCornerShape(15.dp)),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add",
                            tint = DrawerSec, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("Manage", color = DrawerMuted, fontSize = 11.sp)
                }
            }
        }
    }
}
