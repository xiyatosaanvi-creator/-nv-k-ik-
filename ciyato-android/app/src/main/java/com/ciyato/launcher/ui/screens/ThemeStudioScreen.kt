package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.ui.components.*
import com.ciyato.launcher.viewmodel.LauncherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeStudioScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
) {
    val denseLayout by viewModel.denseLayout.collectAsState()
    val goldAccent by viewModel.goldAccent.collectAsState()
    val useSystemWallpaper by viewModel.useSystemWallpaper.collectAsState()
    val wallpaperBlur by viewModel.wallpaperBlur.collectAsState()
    val selectedFont by viewModel.font.collectAsState()
    val accent = if (goldAccent) CiyatoGold else CiyatoBlue

    var ciyatoHomeActive by remember { mutableStateOf(true) }
    var selectedAccentColor by remember { mutableStateOf("Gold") } // Gold, Blue, Green, Cyan, Purple
    var selectedGlassMode by remember { mutableStateOf("Medium") } // None, Light, Medium, Heavy
    var selectedSmartBox by remember { mutableStateOf("Elevated") } // Flat, Elevated, Blurred
    var selectedDockStyle by remember { mutableStateOf("Platform") } // Hidden, Line, Platform
    var activeDarkModeTab by remember { mutableStateOf("Auto") } // Auto | Light | Dark

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            CiyatoTopBar(
                title = "Theme Studio",
                subtitle = "Customize your live home layout",
                onBack = onBack,
                subtitleColor = accent
            )
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── LEFT PANEL: OPTIONS (Weight 1.1) ──────────────────────────────────
            LazyColumn(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Option 1: Ciyato Home Active Switch
                item {
                    CiyatoSettingSwitch(
                        title = "Ciyoto Home Active",
                        subtitle = "Experience the full power of launcher",
                        icon = Icons.Default.Home,
                        checked = ciyatoHomeActive,
                        onCheckedChange = { active -> ciyatoHomeActive = active },
                        accentColor = accent
                    )
                }

                // Option 2: Icon Style Selector
                item {
                    ThemeControlCard(
                        title = "Icon Style",
                        value = "Glass Rounded",
                        icon = Icons.Default.GridView
                    )
                }

                // Option 3: Color Accents Row
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(CiyatoBgEl)
                            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(18.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Color Accents", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf(
                                "Gold" to CiyatoGold,
                                "Blue" to CiyatoBlue,
                                "Green" to CiyatoGreen,
                                "Cyan" to Color(0xFF51C7A5),
                                "Purple" to Color(0xFF9C6AFF)
                            ).forEach { (name, color) ->
                                val selected = selectedAccentColor == name
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (selected) 2.dp else 0.dp,
                                            color = CiyatoWhite,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            selectedAccentColor = name
                                            viewModel.setGoldAccent(name == "Gold")
                                        }
                                )
                            }
                        }
                    }
                }

                // Option 4: Glass Mode Selector
                item {
                    ThemeControlCard(
                        title = "Glass Mode",
                        value = selectedGlassMode,
                        icon = Icons.Default.FilterFrames,
                        onClick = {
                            selectedGlassMode = when (selectedGlassMode) {
                                "None" -> "Light"
                                "Light" -> "Medium"
                                "Medium" -> "Heavy"
                                else -> "None"
                            }
                        }
                    )
                }

                // Option 5: Typography Selector
                item {
                    ThemeControlCard(
                        title = "Typography",
                        value = selectedFont.replaceFirstChar { it.uppercase() },
                        icon = Icons.Default.TextFields,
                        onClick = {
                            val nextFont = when (selectedFont) {
                                "inter" -> "outfit"
                                "outfit" -> "dm_sans"
                                "dm_sans" -> "syne"
                                "syne" -> "geist"
                                else -> "inter"
                            }
                            viewModel.setFont(nextFont)
                        }
                    )
                }

                // Option 6: Smart Box Style
                item {
                    ThemeControlCard(
                        title = "Smart Box Style",
                        value = selectedSmartBox,
                        icon = Icons.Default.CropFree,
                        onClick = {
                            selectedSmartBox = when (selectedSmartBox) {
                                "Flat" -> "Elevated"
                                "Elevated" -> "Blurred"
                                else -> "Flat"
                            }
                        }
                    )
                }

                // Option 7: Dock Style
                item {
                    ThemeControlCard(
                        title = "Dock Style",
                        value = selectedDockStyle,
                        icon = Icons.Default.CallToAction,
                        onClick = {
                            selectedDockStyle = when (selectedDockStyle) {
                                "Hidden" -> "Line"
                                "Line" -> "Platform"
                                else -> "Hidden"
                            }
                        }
                    )
                }

                // Option 8: Dark Mode Selectors (Auto | Light | Dark)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(CiyatoBgEl)
                            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(18.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Dark Mode", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CiyatoBgEl2)
                                .padding(2.dp)
                        ) {
                            listOf("Auto", "Light", "Dark").forEach { mode ->
                                val selected = activeDarkModeTab == mode
                                Text(
                                    text = mode,
                                    color = if (selected) CiyatoBg else CiyatoWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) CiyatoGoldSoft else Color.Transparent)
                                        .clickable { activeDarkModeTab = mode }
                                        .padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ─── RIGHT PANEL: LIVE HOME SCREEN PREVIEW (Weight 0.9) ───────────────
            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(32.dp))
                    .background(CiyatoBgEl2)
                    .border(2.dp, CiyatoBorder, RoundedCornerShape(32.dp))
                    .padding(8.dp)
            ) {
                // Device frame wrapper
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(26.dp))
                        .background(CiyatoBg)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Status bar row
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("9:30", color = CiyatoWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Wifi, null, tint = CiyatoWhite, modifier = Modifier.size(9.dp))
                            Icon(Icons.Default.BatteryFull, null, tint = CiyatoWhite, modifier = Modifier.size(9.dp))
                        }
                    }

                    // Greeting
                    Column {
                        Text("Good morning, Alex ☀️", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Tuesday, May 20", color = CiyatoMuted, fontSize = 8.sp)
                    }

                    // Mock Search
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(CiyatoBgEl)
                            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text("Search apps, files, contacts...", color = CiyatoMuted, fontSize = 8.sp)
                    }

                    // Mock Twin Widgets
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        // Weather widget
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CiyatoBgEl)
                                .padding(6.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("24° Partly sunny", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                            Text("New York", color = CiyatoMuted, fontSize = 7.sp)
                        }
                        // Agenda widget
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CiyatoBgEl)
                                .padding(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Today", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("• 10:00 AM Sync", color = CiyatoSec, fontSize = 6.sp, maxLines = 1)
                                Text("• 02:30 PM Call", color = CiyatoSec, fontSize = 6.sp, maxLines = 1)
                            }
                        }
                    }

                    // Mock Categories Grid
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            ("Work" to "12 apps") to ("Social" to "9 apps"),
                            ("Finance" to "6 apps") to ("Utilities" to "16 apps")
                        ).forEach { (left, right) ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CiyatoBgEl)
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).background(accent.copy(0.12f)))
                                    Column {
                                        Text(left.first, color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                                        Text(left.second, color = CiyatoMuted, fontSize = 6.sp)
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CiyatoBgEl)
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).background(accent.copy(0.12f)))
                                    Column {
                                        Text(right.first, color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                                        Text(right.second, color = CiyatoMuted, fontSize = 6.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Mock Dock
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CiyatoBgEl.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(5) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(accent.copy(alpha = 0.4f))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeControlCard(
    title: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, null, tint = CiyatoSec, modifier = Modifier.size(20.dp))
            Text(title, color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(value, color = CiyatoGold, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Icon(Icons.Default.ChevronRight, null, tint = CiyatoMuted, modifier = Modifier.size(16.dp))
        }
    }
}
