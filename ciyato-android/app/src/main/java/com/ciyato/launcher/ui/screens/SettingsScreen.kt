package com.ciyato.launcher.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val denseLayout by viewModel.denseLayout.collectAsState()
    val goldAccent by viewModel.goldAccent.collectAsState()
    val smartCategories by viewModel.smartCategories.collectAsState()
    val duplicateShortcuts by viewModel.duplicateShortcuts.collectAsState()

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = CiyatoWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CiyatoSec)
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
                bottom = padding.calculateBottomPadding() + 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── Launcher ──────────────────────────────────────────────────────
            item { SectionHeader("Launcher") }
            item {
                SettingsAction(
                    icon = Icons.Default.Home,
                    title = "Set Ciyato as Home",
                    subtitle = "Choose Ciyato as your default launcher",
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
                    }
                )
            }
            item {
                SettingsAction(
                    icon = Icons.AutoMirrored.Filled.Launch,
                    title = "Switch back to system launcher",
                    subtitle = "Change Home app in system settings",
                    tintColor = CiyatoBlue,
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
                    }
                )
            }

            // ── Layout & Theme ────────────────────────────────────────────────
            item { SectionHeader("Layout & Theme") }
            item {
                SettingsToggle(
                    icon = Icons.Default.GridView,
                    title = "Dense Layout",
                    subtitle = "Show more content on home screen",
                    checked = denseLayout,
                    onCheckedChange = viewModel::setDenseLayout,
                )
            }
            item {
                SettingsToggle(
                    icon = Icons.Default.Star,
                    title = "Gold Accents",
                    subtitle = "Premium gold highlights",
                    checked = goldAccent,
                    onCheckedChange = viewModel::setGoldAccent,
                )
            }

            // ── Organization ──────────────────────────────────────────────────
            item { SectionHeader("Organization") }
            item {
                SettingsToggle(
                    icon = Icons.Default.Category,
                    title = "Smart Categories",
                    subtitle = "Automatic app grouping",
                    checked = smartCategories,
                    onCheckedChange = viewModel::setSmartCategories,
                )
            }
            item {
                SettingsToggle(
                    icon = Icons.Default.ContentCopy,
                    title = "Duplicate Shortcuts",
                    subtitle = "Show apps in multiple contexts",
                    checked = duplicateShortcuts,
                    onCheckedChange = viewModel::setDuplicateShortcuts,
                )
            }

            // ── Privacy ───────────────────────────────────────────────────────
            item { SectionHeader("Privacy") }
            item {
                InfoCard(
                    icon = Icons.Default.Lock,
                    title = "Local Organization Only",
                    body = "All app indexing and categorization happens locally on your device. No data is uploaded to any server."
                )
            }

            // ── Danger Zone ───────────────────────────────────────────────────
            item { SectionHeader("Danger Zone") }
            item {
                SettingsAction(
                    icon = Icons.Default.Info,
                    title = "App Info / Uninstall",
                    subtitle = "Open system settings to uninstall",
                    tintColor = Color(0xFFEF4444),
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = android.net.Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        color = CiyatoGold,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        color = CiyatoBgEl,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CiyatoSubtleBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, null, tint = CiyatoSec, modifier = Modifier.size(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(subtitle, color = CiyatoMuted, fontSize = 12.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CiyatoWhite,
                    checkedTrackColor = CiyatoGold,
                    uncheckedThumbColor = CiyatoMuted,
                    uncheckedTrackColor = CiyatoBgEl2
                )
            )
        }
    }
}

@Composable
private fun SettingsAction(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tintColor: Color = CiyatoWhite,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = CiyatoBgEl,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CiyatoSubtleBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, null, tint = if (tintColor == CiyatoWhite) CiyatoSec else tintColor, modifier = Modifier.size(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = tintColor, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(subtitle, color = CiyatoMuted, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, null, tint = CiyatoMuted)
        }
    }
}

@Composable
private fun InfoCard(icon: ImageVector, title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CiyatoBgEl2)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, null, tint = CiyatoGold, modifier = Modifier.size(20.dp))
            Text(title, color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Text(body, color = CiyatoSec, fontSize = 12.sp, lineHeight = 18.sp)
    }
}
