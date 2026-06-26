package com.ciyato.launcher.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.data.CrashReporter
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch

/**
 * SettingsScreen — fully expanded with all configurable options.
 *
 * Suggestions covered: 1 (haptic), 7 (icon shape), 21 (temp unit),
 * 23 (hide apps), 24 (category rename), 72 (time-aware), 74 (bedtime),
 * 97 (dark modes), 98 (font), 113 (debug), 138 (privacy mode),
 * 139 (permission audit entry), 144 (crash report), 145 (screenshot block).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
    onNavigateToPermissionAudit: (() -> Unit)? = null,
    onNavigateToFocus: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val view    = LocalView.current

    // Collect all settings
    val denseLayout        by viewModel.denseLayout.collectAsState()
    val goldAccent         by viewModel.goldAccent.collectAsState()
    val smartCategories    by viewModel.smartCategories.collectAsState()
    val duplicateShortcuts by viewModel.duplicateShortcuts.collectAsState()
    val darkMode           by viewModel.darkMode.collectAsState()
    val iconShape          by viewModel.iconShape.collectAsState()
    val font               by viewModel.font.collectAsState()
    val wallpaperBlur      by viewModel.wallpaperBlur.collectAsState()
    val tempUnit           by viewModel.tempUnit.collectAsState()
    val timeAwareLayout    by viewModel.timeAwareLayout.collectAsState()
    val bedtimeMode        by viewModel.bedtimeMode.collectAsState()
    val bedtimeHour        by viewModel.bedtimeHour.collectAsState()
    val hapticFeedback     by viewModel.hapticFeedback.collectAsState()
    val privacyMode        by viewModel.privacyMode.collectAsState()
    val screenshotBlocked  by viewModel.screenshotBlocked.collectAsState()
    val crashReporting     by viewModel.crashReporting.collectAsState()
    val showRecentLaunched by viewModel.showRecentlyLaunched.collectAsState()

    // Screenshot FLAG_SECURE (Suggestion 145)
    val activity = (context as? android.app.Activity)
    LaunchedEffect(screenshotBlocked) {
        activity?.window?.let { window ->
            if (screenshotBlocked) window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            else window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    var showBedtimeDialog by remember { mutableStateOf(false) }
    var showBlurDialog    by remember { mutableStateOf(false) }
    var showCrashLogs     by remember { mutableStateOf(false) }

    if (showCrashLogs) {
        CrashLogsScreen(context = context, onBack = { showCrashLogs = false })
        return
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = CiyatoWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CiyatoSec)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CiyatoBg),
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding() + 40.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {

            // ── Launcher ──────────────────────────────────────────────────────
            item { SectionHeader("Launcher") }
            item { SettingsAction(Icons.Default.Home, "Set Ciyato as Home",
                "Choose Ciyato as your default launcher") {
                context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
            }}

            // ── Appearance ────────────────────────────────────────────────────
            item { SectionHeader("Appearance") }
            item { SettingsToggle(Icons.Default.GridView, "Dense Layout",
                "Fit more content on screen", denseLayout, viewModel::setDenseLayout) }
            item { SettingsToggle(Icons.Default.Star, "Gold Accents", "Premium gold highlights",
                goldAccent, viewModel::setGoldAccent) }

            // Dark mode picker (Suggestion 97)
            item {
                SettingsOptionRow(
                    icon = Icons.Default.DarkMode,
                    title = "Appearance",
                    selected = darkMode,
                    options = listOf("auto" to "Auto", "dark" to "Dark", "light" to "Light", "amoled" to "AMOLED"),
                    onSelect = viewModel::setDarkMode,
                )
            }

            // Icon shape picker (Suggestion 7)
            item {
                SettingsOptionRow(
                    icon = Icons.Default.Widgets,
                    title = "Icon Shape",
                    selected = iconShape,
                    options = listOf("squircle" to "Squircle", "circle" to "Circle", "rounded" to "Rounded", "raw" to "Raw"),
                    onSelect = viewModel::setIconShape,
                )
            }

            // Font picker (Suggestion 98)
            item {
                SettingsOptionRow(
                    icon = Icons.Default.TextFields,
                    title = "Font",
                    selected = font,
                    options = listOf("inter" to "Inter", "outfit" to "Outfit", "dm_sans" to "DM Sans", "syne" to "Syne", "geist" to "Geist"),
                    onSelect = viewModel::setFont,
                )
            }

            // Wallpaper blur (Suggestion 93)
            item {
                SettingsSlider(
                    icon    = Icons.Default.BlurOn,
                    title   = "Background Blur",
                    value   = wallpaperBlur.toFloat(),
                    range   = 0f..20f,
                    label   = if (wallpaperBlur == 0) "Off" else "$wallpaperBlur",
                    onValueChange = { viewModel.setWallpaperBlur(it.toInt()) },
                )
            }

            // ── Smart Layout ──────────────────────────────────────────────────
            item { SectionHeader("Smart Layout") }
            item { SettingsToggle(Icons.Default.Schedule, "Time-Aware Layout",
                "Show relevant categories based on time of day", timeAwareLayout, viewModel::setTimeAwareLayout) }
            item { SettingsToggle(Icons.Default.Bedtime, "Bedtime Mode",
                "Hide social/entertainment apps after bedtime hour", bedtimeMode, viewModel::setBedtimeMode) }
            if (bedtimeMode) {
                item {
                    SettingsAction(
                        icon     = Icons.Default.Bedtime,
                        title    = "Bedtime Hour",
                        subtitle = "${bedtimeHour}:00 — apps hidden after this time",
                        tintColor= CiyatoBlue,
                        onClick  = { showBedtimeDialog = true },
                    )
                }
            }
            item { SettingsToggle(Icons.Default.History, "Show Recently Launched",
                "Quick access strip for recently opened apps", showRecentLaunched, viewModel::setShowRecentlyLaunched) }

            // ── Organization ──────────────────────────────────────────────────
            item { SectionHeader("Organization") }
            item { SettingsToggle(Icons.Default.Category, "Smart Categories", "Automatic app grouping",
                smartCategories, viewModel::setSmartCategories) }
            item { SettingsToggle(Icons.Default.ContentCopy, "Duplicate Shortcuts",
                "Show apps in multiple contexts", duplicateShortcuts, viewModel::setDuplicateShortcuts) }

            // ── Weather ───────────────────────────────────────────────────────
            item { SectionHeader("Weather") }
            item {
                SettingsOptionRow(
                    icon     = Icons.Default.Thermostat,
                    title    = "Temperature Unit",
                    selected = tempUnit,
                    options  = listOf("C" to "°Celsius", "F" to "°Fahrenheit"),
                    onSelect = viewModel::setTempUnit,
                )
            }

            // ── Accessibility ─────────────────────────────────────────────────
            item { SectionHeader("Accessibility") }
            item { SettingsToggle(Icons.Default.Vibration, "Haptic Feedback",
                "Feel taps, toggles and actions", hapticFeedback, viewModel::setHapticFeedback) }

            // ── Focus ─────────────────────────────────────────────────────────
            item { SectionHeader("Focus") }
            item {
                SettingsAction(Icons.Default.Timer, "Focus Sessions",
                    "Block distracting apps for a set duration",
                    tintColor = CiyatoGold,
                    onClick   = { onNavigateToFocus?.invoke() })
            }

            // ── Privacy & Security ────────────────────────────────────────────
            item { SectionHeader("Privacy & Security") }
            item {
                InfoCard(Icons.Default.Lock, "Local Only",
                    "All app indexing, categorization, and preferences stay on your device. Nothing is uploaded.")
            }
            item { SettingsToggle(Icons.Default.VisibilityOff, "Privacy Mode",
                "Hide notification counts and app labels", privacyMode, viewModel::setPrivacyMode) }
            item { SettingsToggle(Icons.Default.Screenshot, "Block Screenshots",
                "Prevents screen capture of Ciyato (FLAG_SECURE)", screenshotBlocked) {
                viewModel.setScreenshotBlocked(it)
                activity?.window?.let { window ->
                    if (it) window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    else window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }}
            item {
                SettingsAction(Icons.Default.Security, "Permission Audit",
                    "See which apps have access to sensitive permissions",
                    tintColor = CiyatoBlue,
                    onClick   = { onNavigateToPermissionAudit?.invoke() })
            }

            // ── Diagnostics ───────────────────────────────────────────────────
            item { SectionHeader("Diagnostics") }
            item { SettingsToggle(Icons.Default.BugReport, "Crash Reporting",
                "Save crash logs locally (never uploaded)", crashReporting, viewModel::setCrashReporting) }
            if (crashReporting) {
                item {
                    SettingsAction(Icons.Default.Description, "View Crash Logs",
                        "See locally stored crash reports",
                        onClick = { showCrashLogs = true })
                }
            }

            // ── Danger Zone ───────────────────────────────────────────────────
            item { SectionHeader("Danger Zone") }
            item { SettingsAction(Icons.Default.RestartAlt, "Reset Layout", "Restore default layout settings",
                tintColor = Color(0xFFF5C542), onClick = viewModel::resetLayout) }
            item {
                SettingsAction(Icons.Default.Info, "App Info / Uninstall",
                    "Open system settings to manage or uninstall",
                    tintColor = Color(0xFFEF4444),
                    onClick = {
                        context.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = android.net.Uri.fromParts("package", context.packageName, null)
                            }
                        )
                    }
                )
            }
            item {
                SettingsAction(Icons.AutoMirrored.Filled.Launch, "Switch back to system launcher",
                    "Change Home app in system settings",
                    tintColor = CiyatoSec,
                    onClick = { context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) })
            }
        }
    }

    // ── Bedtime hour picker dialog ────────────────────────────────────────────
    if (showBedtimeDialog) {
        AlertDialog(
            onDismissRequest = { showBedtimeDialog = false },
            containerColor   = CiyatoBgEl,
            title = { Text("Bedtime Hour", color = CiyatoWhite) },
            text  = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Hide apps after:", color = CiyatoSec, fontSize = 13.sp)
                    Slider(
                        value = bedtimeHour.toFloat(), onValueChange = { viewModel.setBedtimeHour(it.toInt()) },
                        valueRange = 18f..23f, steps = 4,
                        colors = SliderDefaults.colors(thumbColor = CiyatoGold, activeTrackColor = CiyatoGold),
                    )
                    Text("${bedtimeHour}:00", color = CiyatoGold, fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            },
            confirmButton = {
                TextButton(onClick = { showBedtimeDialog = false }) {
                    Text("Done", color = CiyatoGold)
                }
            },
        )
    }
}

// ─── Crash Logs inline screen ──────────────────────────────────────────────────

@Composable
private fun CrashLogsScreen(context: Context, onBack: () -> Unit) {
    val logs = remember { CrashReporter.getLogs(context) }
    var selectedContent by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    if (selectedContent != null) {
        Scaffold(
            containerColor = CiyatoBg,
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text("Crash Log", color = CiyatoWhite, fontSize = 16.sp) },
                    navigationIcon = { IconButton(onClick = { selectedContent = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CiyatoSec)
                    }},
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = CiyatoBg),
                )
            }
        ) { p ->
            androidx.compose.foundation.lazy.LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize().padding(p),
            ) {
                item {
                    Text(selectedContent ?: "", color = CiyatoSec, fontSize = 11.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                }
            }
        }
        return
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Crash Logs", color = CiyatoWhite) },
                navigationIcon = { IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CiyatoSec)
                }},
                actions = {
                    TextButton(onClick = { CrashReporter.clearLogs(context) }) {
                        Text("Clear All", color = Color(0xFFEF4444))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CiyatoBg),
            )
        }
    ) { p ->
        if (logs.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) {
                Text("No crash logs yet \uD83C\uDF89", color = CiyatoMuted, fontSize = 16.sp)
            }
        } else {
            androidx.compose.foundation.lazy.LazyColumn(
                contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize().padding(p),
            ) {
                items(logs) { file ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(CiyatoBgEl)
                            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                scope.launch {
                                    selectedContent = CrashReporter.readLog(file)
                                }
                            }.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(file.name.replace("crash_", "").replace(".txt", ""),
                                color = CiyatoWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("%.1f KB".format(file.length() / 1024f), color = CiyatoMuted, fontSize = 11.sp)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = CiyatoMuted)
                    }
                }
            }
        }
    }
}

// ─── Shared Settings Components ───────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Text(title.uppercase(), color = CiyatoGold, fontSize = 11.sp, fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp, modifier = Modifier.padding(top = 10.dp, bottom = 2.dp))
}

@Composable
private fun SettingsToggle(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(onClick = { onCheckedChange(!checked) }, color = CiyatoBgEl,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CiyatoSubtleBorder)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(icon, null, tint = CiyatoSec, modifier = Modifier.size(22.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, color = CiyatoMuted, fontSize = 12.sp)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedThumbColor = CiyatoWhite, checkedTrackColor = CiyatoGold,
                    uncheckedThumbColor = CiyatoMuted, uncheckedTrackColor = CiyatoBgEl2))
        }
    }
}

@Composable
private fun SettingsAction(icon: ImageVector, title: String, subtitle: String,
    tintColor: Color = CiyatoWhite, onClick: () -> Unit) {
    Surface(onClick = onClick, color = CiyatoBgEl, shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CiyatoSubtleBorder)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(icon, null, tint = if (tintColor == CiyatoWhite) CiyatoSec else tintColor, modifier = Modifier.size(22.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = tintColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, color = CiyatoMuted, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, null, tint = CiyatoMuted)
        }
    }
}

@Composable
private fun SettingsOptionRow(
    icon: ImageVector,
    title: String,
    selected: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(CiyatoBgEl)
        .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(18.dp)).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, null, tint = CiyatoSec, modifier = Modifier.size(22.dp))
            Text(title, color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            options.forEach { (value, label) ->
                val isSelected = selected == value
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f).height(38.dp).clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CiyatoGold else CiyatoBgEl2)
                        .clickable { onSelect(value) }) {
                    Text(label, color = if (isSelected) CiyatoBg else CiyatoSec,
                        fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
private fun SettingsSlider(
    icon: ImageVector, title: String, value: Float, range: ClosedFloatingPointRange<Float>,
    label: String, onValueChange: (Float) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(CiyatoBgEl)
        .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(18.dp)).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, null, tint = CiyatoSec, modifier = Modifier.size(22.dp))
            Text(title, color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text(label, color = CiyatoGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Slider(value = value, onValueChange = onValueChange, valueRange = range,
            colors = SliderDefaults.colors(thumbColor = CiyatoGold, activeTrackColor = CiyatoGold, inactiveTrackColor = CiyatoBgEl2))
    }
}

@Composable
private fun InfoCard(icon: ImageVector, title: String, body: String) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CiyatoBgEl2).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = CiyatoGold, modifier = Modifier.size(18.dp))
            Text(title, color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Text(body, color = CiyatoSec, fontSize = 12.sp, lineHeight = 17.sp)
    }
}
