package com.ciyato.launcher.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val darkMode by viewModel.darkMode.collectAsState()
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CiyatoSec)
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            // ── LAUNCHER SECTION ──────────────────────────────────────────────
            item { SectionHeader("Launcher") }

            item {
                SettingsAction(
                    icon = Icons.Default.Home,
                    title = "Set Ciyato as default Home app",
                    subtitle = "Open Android Home app selector",
                    onClick = {
                        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                )
            }
            item {
                SettingsAction(
                    icon = Icons.Default.SettingsApplications,
                    title = "Switch back to system launcher",
                    subtitle = "Settings → Default apps → Home app",
                    tintColor = CiyatoBlue,
                    onClick = {
                        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                )
            }

            // ── LAYOUT SECTION ──────────────────────────────────────────────
            item { SectionHeader("Layout") }

            item {
                SettingsToggle(
                    icon = Icons.Default.GridView,
                    title = "Dense layout",
                    subtitle = "3-column category grid (off = 2-column)",
                    checked = denseLayout,
                    onCheckedChange = viewModel::setDenseLayout,
                )
            }
            item {
                SettingsToggle(
                    icon = Icons.Default.Star,
                    title = "Gold accent",
                    subtitle = "Ciyato's signature gold colour",
                    checked = goldAccent,
                    onCheckedChange = viewModel::setGoldAccent,
                )
            }
            item {
                SettingsToggle(
                    icon = Icons.Default.Apps,
                    title = "Smart categories",
                    subtitle = "Auto-organize apps into categories",
                    checked = smartCategories,
                    onCheckedChange = viewModel::setSmartCategories,
                )
            }
            item {
                SettingsToggle(
                    icon = Icons.Default.CopyAll,
                    title = "Duplicate smart shortcuts",
                    subtitle = "Show one app in multiple categories",
                    checked = duplicateShortcuts,
                    onCheckedChange = viewModel::setDuplicateShortcuts,
                )
            }
            item {
                SettingsAction(
                    icon = Icons.Default.RestartAlt,
                    title = "Reset layout",
                    subtitle = "Restore default categories and settings",
                    onClick = { viewModel.resetLayout() }
                )
            }

            // ── PRIVACY SECTION ──────────────────────────────────────────────
            item { SectionHeader("Privacy") }

            item {
                InfoCard(
                    icon = Icons.Default.Lock,
                    title = "Your data stays on your device",
                    body = "• Ciyato reads installed app names and icons to build your launcher.\n" +
                           "• Nothing is uploaded or shared.\n" +
                           "• No analytics. No ads. No tracking.\n" +
                           "• File/photo features request permission only when you use them."
                )
            }

            // ── DANGER / UNINSTALL SECTION ───────────────────────────────────
            item { SectionHeader("Turn off Ciyato") }

            item {
                SettingsAction(
                    icon = Icons.Default.ExitToApp,
                    title = "Disable Ciyato Home Mode",
                    subtitle = "Open Default Apps → choose another Home app",
                    tintColor = MaterialTheme.colorScheme.error,
                    onClick = {
                        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                )
            }
            item {
                SettingsAction(
                    icon = Icons.Default.Delete,
                    title = "Uninstall Ciyato",
                    subtitle = "Open App Info → Uninstall",
                    tintColor = MaterialTheme.colorScheme.error,
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_DELETE,
                            Uri.parse("package:${context.packageName}")
                        ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                        context.startActivity(intent)
                    }
                )
            }

            item {
                Text(
                    "Ciyato remains your Home app only because you selected it.\n" +
                    "You can switch back or uninstall at any time.",
                    color = CiyatoMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        color = CiyatoGold,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = CiyatoSec, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = CiyatoWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = CiyatoMuted, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = CiyatoGold, checkedTrackColor = CiyatoGold.copy(alpha = 0.3f)),
        )
    }
}

@Composable
private fun SettingsAction(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tintColor: androidx.compose.ui.graphics.Color = CiyatoSec,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = tintColor, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = CiyatoWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = CiyatoMuted, fontSize = 11.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null,
            tint = CiyatoMuted, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun InfoCard(icon: ImageVector, title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CiyatoGold.copy(alpha = 0.08f))
            .border(1.dp, CiyatoGold.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = CiyatoGold, modifier = Modifier.size(18.dp))
            Text(title, color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(body, color = CiyatoSec, fontSize = 12.sp, lineHeight = 18.sp)
    }
}
