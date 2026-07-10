package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: LauncherViewModel,
    onOpenFiles: () -> Unit,
    onOpenPhotos: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenTheme: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val apps by viewModel.apps.collectAsState()
    val totalApps = remember(apps) { apps.size }
    val context = LocalContext.current
    val storage = remember {
        android.os.StatFs(context.filesDir.absolutePath).let { stats ->
            val total = stats.totalBytes.coerceAtLeast(1L)
            val used = (total - stats.availableBytes).coerceAtLeast(0L)
            Triple(used, total, (used.toFloat() / total.toFloat()).coerceIn(0f, 1f))
        }
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Ciyato", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Control center", color = CiyatoGold, fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(CiyatoGold.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSearch) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = "Smart Search",
                            tint = CiyatoGold, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = CiyatoSec)
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
                bottom = padding.calculateBottomPadding() + 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Greeting
            item {
                val greetingStr = remember {
                    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    when {
                        hour < 12 -> "Good morning ☀️"
                        hour < 17 -> "Good afternoon 🌞"
                        else      -> "Good evening 🌙"
                    }
                }
                Column {
                    Text(greetingStr, color = CiyatoWhite,
                        fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Choose what Ciyato can access.", color = CiyatoSec, fontSize = 13.sp)
                }
            }

            // Storage card
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CiyatoBgEl)
                        .border(1.dp, CiyatoBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Ciyato local storage", color = CiyatoWhite,
                                fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text("${formatStorage(storage.first)} used of ${formatStorage(storage.second)}", color = CiyatoSec,
                                fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                            Spacer(Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { storage.third },
                                modifier = Modifier.fillMaxWidth().height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CiyatoGold,
                                trackColor = CiyatoBgEl2,
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${(storage.third * 100).toInt()}%", color = CiyatoGold, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                            Text("Used", color = CiyatoMuted, fontSize = 11.sp)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("This is Ciyato's own app storage. Ciyato does not scan or clean your phone automatically.", color = CiyatoMuted, fontSize = 12.sp)
                }
            }

            // Quick actions
            item {
                Text("Quick Actions", color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickAction(Icons.Default.AutoFixHigh, "Smart Search", CiyatoGold, onOpenSearch, Modifier.weight(1f))
                    QuickAction(Icons.Default.Folder, "Files", CiyatoBlue, onOpenFiles, Modifier.weight(1f))
                    QuickAction(Icons.Default.FolderOpen, "Browse files",
                        MaterialTheme.colorScheme.error, onOpenFiles, Modifier.weight(1f))
                    QuickAction(Icons.Default.PhotoAlbum, "Photos", CiyatoGreen, onOpenPhotos, Modifier.weight(1f))
                }
            }

            // Recent activity
            item {
                Text("Privacy at a glance", color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Triple(Icons.Default.Apps, "$totalApps installed apps available to organize", "Available"),
                        Triple(Icons.Default.FolderOpen, "File access begins only after you choose a folder", "Your choice"),
                        Triple(Icons.Default.PhotoLibrary, "Photos use Android Photo Picker", "Selected only"),
                        Triple(Icons.Default.DeleteOutline, "Ciyato never deletes files automatically", "Protected"),
                    ).forEach { (icon, label, time) ->
                        ActivityRow(icon = icon, label = label, time = time)
                    }
                }
            }

            // Features — now includes Photos
            item {
                Text("Features", color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FeatureRow(Icons.Default.Folder, "Ciyato Files",
                        "Browse a folder you choose.", CiyatoBlue, onOpenFiles)
                    FeatureRow(Icons.Default.Photo, "Ciyato Photos",
                        "View only the photos you select.", CiyatoGoldSoft, onOpenPhotos)
                    FeatureRow(Icons.Default.Search, "AI Search",
                        "Search installed apps and categories.", CiyatoGold, onOpenSearch)
                    FeatureRow(Icons.Default.Palette, "Theme Studio",
                        "Customize your launcher.", CiyatoGreen, onOpenTheme)
                    FeatureRow(Icons.Default.Settings, "Settings",
                        "Launcher & privacy options.", CiyatoSec, onOpenSettings)
                }
            }
        }
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.12f))
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(6.dp))
        Text(label, color = CiyatoWhite, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

private fun formatStorage(bytes: Long): String {
    val gb = bytes.toDouble() / (1024.0 * 1024.0 * 1024.0)
    return if (gb >= 1.0) String.format(java.util.Locale.US, "%.1f GB", gb)
    else "${bytes / (1024 * 1024)} MB"
}

@Composable
private fun ActivityRow(icon: ImageVector, label: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                .background(CiyatoBgEl2),
        ) {
            Icon(icon, contentDescription = null, tint = CiyatoSec, modifier = Modifier.size(18.dp))
        }
        Text(label, color = CiyatoWhite, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(time, color = CiyatoMuted, fontSize = 11.sp)
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    title: String,
    sub: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.15f)),
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = CiyatoWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(sub, color = CiyatoMuted, fontSize = 11.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null,
            tint = CiyatoMuted, modifier = Modifier.size(18.dp))
    }
}
