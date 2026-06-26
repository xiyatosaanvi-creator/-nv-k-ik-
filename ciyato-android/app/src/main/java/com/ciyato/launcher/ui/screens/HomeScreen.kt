package com.ciyato.launcher.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.data.FocusSessionManager
import com.ciyato.launcher.data.InstalledApp
import com.ciyato.launcher.ui.components.*
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// Default home categories
private val ALL_HOME_CATEGORIES = listOf(
    AppCategory.WORK,
    AppCategory.SOCIAL,
    AppCategory.FINANCE,
    AppCategory.CREATIVITY,
    AppCategory.UTILITIES,
    AppCategory.DAILY,
    AppCategory.ENTERTAINMENT,
    AppCategory.PRODUCTIVITY,
)

private val DOCK_PRIORITY_PACKAGES = listOf(
    "com.google.android.dialer", "com.android.dialer", "com.samsung.android.dialer",
    "com.google.android.apps.messaging", "com.android.messaging", "com.samsung.android.messaging",
    "com.android.chrome", "org.mozilla.firefox",
    "com.google.android.GoogleCamera", "com.android.camera2", "com.sec.android.app.camera",
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
    val apps              by viewModel.apps.collectAsState()
    val isLoading         by viewModel.isLoading.collectAsState()
    val searchQuery       by viewModel.searchQuery.collectAsState()
    val searchResults     by viewModel.searchResults.collectAsState()
    val denseLayout       by viewModel.denseLayout.collectAsState()
    val showDupes         by viewModel.duplicateShortcuts.collectAsState()
    val toastEvent        by viewModel.toastEvent.collectAsState()
    val weatherState      by viewModel.weatherState.collectAsState()
    val timeAwareLayout   by viewModel.timeAwareLayout.collectAsState()
    val hapticEnabled     by viewModel.hapticFeedback.collectAsState()
    val showRecentLaunched by viewModel.showRecentlyLaunched.collectAsState()
    val privacyMode       by viewModel.privacyMode.collectAsState()
    val focusSession      by FocusSessionManager.activeSession.collectAsState()

    val haptic = LocalHapticFeedback.current

    // ── Live clock (Suggestion 4) ─────────────────────────────────────────────
    var liveClock by remember { mutableStateOf(currentTimeString()) }
    LaunchedEffect(Unit) {
        while (true) {
            liveClock = currentTimeString()
            delay(1_000L)
        }
    }

    val dateStr = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()) }

    // ── Dock apps ─────────────────────────────────────────────────────────────
    val dockApps = remember(apps) {
        val byPkg = apps.associateBy { it.packageName }
        DOCK_PRIORITY_PACKAGES.mapNotNull { byPkg[it] }.distinctBy { it.packageName }.take(5)
            .ifEmpty { apps.take(5) }
    }

    // ── Smart categories for current context (Suggestions 72, 74, 75) ─────────
    val displayCategories = remember(apps, timeAwareLayout, focusSession) {
        val timeCats = if (timeAwareLayout) viewModel.timeAwareCategories() else ALL_HOME_CATEGORIES
        val beadtimeHide = viewModel.isBedtimeNow()
        val allVisible = (timeCats + ALL_HOME_CATEGORIES).distinct()
        allVisible.filter { cat ->
            val hasApps = viewModel.byCategory(cat).isNotEmpty()
            val notBlocked = !FocusSessionManager.isBlocked(cat)
            val notBedtime = !beadtimeHide || cat !in listOf(AppCategory.SOCIAL, AppCategory.ENTERTAINMENT, AppCategory.GAMES)
            hasApps && notBlocked && notBedtime
        }.take(8)
    }

    // ── Recently launched (Suggestion 25) ─────────────────────────────────────
    val recentApps = remember(apps) { viewModel.getRecentlyLaunchedApps() }

    // ── Duplicate apps ────────────────────────────────────────────────────────
    val dupeApps = remember(apps) { viewModel.multiCategoryApps().take(7) }

    // ── Toast / snackbar ──────────────────────────────────────────────────────
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(toastEvent) {
        toastEvent?.consume()?.let { msg ->
            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            snackbarHostState.showSnackbar(msg)
        }
    }

    // ── Layout density vars ───────────────────────────────────────────────────
    val columns        = if (denseLayout) 3 else 2
    val cardHeight: Dp = if (denseLayout) 114.dp else 142.dp
    val spacing        = if (denseLayout) 14.dp else 22.dp
    val topPad         = if (denseLayout) 20.dp else 36.dp
    val greetingSize   = if (denseLayout) 22.sp else 28.sp
    val clockSize      = if (denseLayout) 36.sp else 46.sp

    Scaffold(
        containerColor = CiyatoBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { scaffoldPadding ->
        Box(
            modifier = modifier.fillMaxSize()
                .background(Brush.verticalGradient(listOf(CiyatoBgEl2, CiyatoBg, CiyatoBg)))
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start  = 16.dp, end = 16.dp,
                    top    = scaffoldPadding.calculateTopPadding() + topPad,
                    bottom = 140.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.fillMaxSize(),
            ) {

                // ── 1. Clock + Greeting ───────────────────────────────────────
                item {
                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            // Live clock (Suggestion 4)
                            AnimatedContent(targetState = liveClock, transitionSpec = {
                                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                            }, label = "clock") { time ->
                                Text(time, color = CiyatoWhite, fontSize = clockSize,
                                    fontWeight = FontWeight.ExtraBold, lineHeight = clockSize * 1.05f)
                            }
                            Text(
                                if (!privacyMode) viewModel.greeting else "Welcome back",
                                color = CiyatoSec,
                                fontSize = if (denseLayout) 13.sp else 14.sp,
                            )
                            Text(dateStr, color = CiyatoMuted, fontSize = if (denseLayout) 12.sp else 13.sp)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Focus session indicator (Suggestion 75)
                            if (focusSession != null) {
                                FocusBadge(focusSession!!)
                            }
                            ActionCircle(Icons.Default.AutoFixHigh, CiyatoGold, 42.dp)
                            Box(contentAlignment = Alignment.TopEnd) {
                                ActionCircle(Icons.Default.Notifications, CiyatoSec, 42.dp)
                                if (!privacyMode) {
                                    Box(modifier = Modifier.size(9.dp).offset((-1).dp, 1.dp)
                                        .clip(CircleShape).background(Color(0xFFEF4444))
                                        .border(1.5.dp, CiyatoBg, CircleShape))
                                }
                            }
                        }
                    }
                }

                // ── 2. Search bar ─────────────────────────────────────────────
                item {
                    HomeSearchBar(
                        query         = searchQuery,
                        onQueryChange = {
                            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.setSearch(it)
                        },
                        isDense  = denseLayout,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // ── 3. Search results (early-exit) ────────────────────────────
                if (searchQuery.isNotBlank()) {
                    if (searchResults.isEmpty()) {
                        item { Text("No results for \"$searchQuery\"", color = CiyatoMuted,
                            modifier = Modifier.padding(vertical = 16.dp)) }
                    } else {
                        item { Text("Results", color = CiyatoSec, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(searchResults.take(14), key = { it.packageName }) { app ->
                                    AppIconTile(app = app, iconSize = if (denseLayout) 50.dp else 56.dp,
                                        onClick = {
                                            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            viewModel.launchApp(app)
                                        },
                                        modifier = Modifier.width(if (denseLayout) 62.dp else 68.dp))
                                }
                            }
                        }
                    }
                    return@LazyColumn
                }

                // ── 4. Weather + Agenda row ───────────────────────────────────
                item {
                    WeatherAgendaRow(
                        isDense      = denseLayout,
                        weatherState = if (privacyMode) null else weatherState,
                        onWeatherTap = {
                            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onWeatherTap()
                        },
                        onAgendaTap  = onAgendaTap,
                        modifier     = Modifier.fillMaxWidth(),
                    )
                }

                // ── 5. Recently launched (Suggestion 25) ──────────────────────
                if (showRecentLaunched && recentApps.isNotEmpty() && !privacyMode) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                Text("Recent", color = CiyatoWhite, fontWeight = FontWeight.SemiBold,
                                    fontSize = if (denseLayout) 15.sp else 17.sp)
                            }
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(recentApps.take(8), key = { it.packageName }) { app ->
                                    AppIconTile(app = app, iconSize = if (denseLayout) 44.dp else 50.dp,
                                        onClick = {
                                            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            viewModel.launchApp(app)
                                        },
                                        modifier = Modifier.width(if (denseLayout) 56.dp else 62.dp))
                                }
                            }
                        }
                    }
                }

                // ── 6. Smart categories header ────────────────────────────────
                item {
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                if (timeAwareLayout) "Right now" else "Smart categories",
                                color = CiyatoWhite, fontWeight = FontWeight.SemiBold,
                                fontSize = if (denseLayout) 17.sp else 20.sp,
                            )
                            if (focusSession != null) {
                                Box(Modifier.clip(RoundedCornerShape(6.dp))
                                    .background(CiyatoGold.copy(0.15f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                                    Text("Focus", color = CiyatoGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text("Edit", color = CiyatoBlue,
                            fontSize = if (denseLayout) 13.sp else 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                // ── 7. Category grid (with skeleton, Suggestion 132) ──────────
                item {
                    if (isLoading) {
                        // Skeleton loading (Suggestion 132)
                        SkeletonCategoryGrid(columns = columns, rows = 2, cardHeight = cardHeight)
                    } else if (displayCategories.isEmpty()) {
                        Text("No apps found", color = CiyatoMuted, modifier = Modifier.padding(16.dp))
                    } else {
                        val rows = (displayCategories.size + columns - 1) / columns
                        val gridH = cardHeight * rows + 10.dp * (rows - 1).coerceAtLeast(0)
                        Column(
                            modifier = Modifier.fillMaxWidth().height(gridH),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            displayCategories.chunked(columns).forEach { rowCats ->
                                Row(modifier = Modifier.fillMaxWidth().weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    rowCats.forEach { cat ->
                                        SmartCategoryCard(
                                            category = cat,
                                            apps     = viewModel.byCategory(cat),
                                            onTap    = {
                                                if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                onCategoryTap(cat)
                                            },
                                            onAppTap = {
                                                if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                viewModel.launchApp(it)
                                            },
                                            iconSize = if (denseLayout) 38.dp else 46.dp,
                                            modifier = Modifier.weight(1f).fillMaxHeight(),
                                        )
                                    }
                                    repeat(columns - rowCats.size) { Spacer(Modifier.weight(1f)) }
                                }
                            }
                        }
                    }
                }

                // ── 8. Duplicate shortcuts (Suggestion 25 wiring) ────────────
                if (showDupes && dupeApps.isNotEmpty() && !privacyMode) {
                    item {
                        DuplicateShortcutStrip(
                            apps       = dupeApps,
                            onAppTap   = {
                                if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.launchApp(it)
                            },
                            onManage   = onDuplicatesTap,
                            onStripTap = onDuplicatesTap,
                            modifier   = Modifier.fillMaxWidth(),
                        )
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }

            // ── Fixed dock overlay ────────────────────────────────────────────
            Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .padding(bottom = scaffoldPadding.calculateBottomPadding() + 20.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (dockApps.isNotEmpty()) {
                        BottomDock(dockApps = dockApps, onAppTap = {
                            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.launchApp(it)
                        })
                    }
                    HomeNavBar(
                        onOpenDrawer = {
                            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onOpenDrawer()
                        },
                        onOpenSettings = {
                            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onOpenSettings()
                        }
                    )
                }
            }
        }
    }
}

// ─── Focus session badge ──────────────────────────────────────────────────────

@Composable
private fun FocusBadge(session: FocusSessionManager.FocusSession) {
    val pulse by rememberInfiniteTransition(label = "focus_pulse").animateFloat(
        initialValue = 1f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse",
    )
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.scale(pulse).clip(RoundedCornerShape(10.dp))
            .background(CiyatoGold.copy(0.15f)).border(1.dp, CiyatoGold.copy(0.35f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Timer, null, tint = CiyatoGold, modifier = Modifier.size(14.dp))
            Text("%02d:%02d".format(session.remainingMin, session.remainingSec),
                color = CiyatoGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun currentTimeString(): String =
    SimpleDateFormat("h:mm", Locale.getDefault()).format(Date())

@Composable
private fun ActionCircle(icon: ImageVector, color: Color, size: Dp) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.size(size).clip(CircleShape).background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, CircleShape)) {
        Icon(icon, null, tint = color, modifier = Modifier.size(size * 0.5f))
    }
}

@Composable
private fun HomeSearchBar(query: String, onQueryChange: (String) -> Unit, isDense: Boolean, modifier: Modifier) {
    Box(modifier = modifier.height(if (isDense) 50.dp else 56.dp)
        .clip(RoundedCornerShape(999.dp)).background(CiyatoBgEl)
        .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(999.dp)),
        contentAlignment = Alignment.CenterStart) {
        if (query.isBlank()) {
            Row(modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.Search, null, tint = CiyatoMuted, modifier = Modifier.size(18.dp))
                Text("Search apps, files, contacts…", color = CiyatoMuted, fontSize = if (isDense) 14.sp else 15.sp)
            }
        }
        androidx.compose.foundation.text.BasicTextField(
            value = query, onValueChange = onQueryChange, singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = CiyatoWhite, fontSize = if (isDense) 14.sp else 15.sp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp, vertical = 14.dp),
        )
    }
}

@Composable
private fun HomeNavBar(onOpenDrawer: () -> Unit, onOpenSettings: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(36.dp), verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 24.dp)) {
        NavCircle(Icons.Default.Apps, "App Drawer", onOpenDrawer)
        NavCircle(Icons.Default.Settings, "Settings", onOpenSettings)
    }
}

@Composable
private fun NavCircle(icon: ImageVector, label: String, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.size(52.dp).clip(CircleShape).background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, CircleShape).clickable(onClick = onClick)) {
        Icon(icon, label, tint = CiyatoSec, modifier = Modifier.size(24.dp))
    }
}
