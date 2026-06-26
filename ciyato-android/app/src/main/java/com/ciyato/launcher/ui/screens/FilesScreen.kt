package com.ciyato.launcher.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
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

private data class FileCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val description: String = "",
)

private val fileCategories = listOf(
    FileCategory("Screenshots", Icons.Default.Screenshot, Color(0xFF7DB7FF), "Screen captures"),
    FileCategory("Documents", Icons.Default.Description, Color(0xFF39C66A), "PDFs, Word, text"),
    FileCategory("Downloads", Icons.Default.Download, Color(0xFFC6A15B), "Downloaded files"),
    FileCategory("WhatsApp", Icons.AutoMirrored.Filled.Chat, Color(0xFF25D366), "Media & docs"),
    FileCategory("Videos", Icons.Default.VideoFile, Color(0xFFFF6B8C), "MP4, MKV, etc"),
    FileCategory("APKs", Icons.Default.Android, Color(0xFF9C6AFF), "Installer files"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<FileCategory?>(null) }
    var selectedFolderUri by remember { mutableStateOf<Uri?>(null) }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            selectedFolderUri = uri
            hasPermission = true
        }
    }

    // Navigate to collection detail
    if (selectedCategory != null) {
        FileCollectionDetailScreen(
            collectionTitle = selectedCategory!!.name,
            collectionIcon  = selectedCategory!!.icon,
            collectionColor = selectedCategory!!.color,
            onBack          = { selectedCategory = null },
        )
        return
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = { Text("Ciyato Files", color = CiyatoWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = CiyatoSec)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CiyatoBg)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Storage Overview
            item {
                StorageCard()
            }

            if (!hasPermission) {
                item {
                    PermissionCard(onEnable = { folderPickerLauncher.launch(null) })
                }
            }

            // Categories header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Categories", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    if (hasPermission) {
                        Text("Folder access active", color = CiyatoGold, fontSize = 12.sp)
                    }
                }
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(360.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(fileCategories) { cat ->
                        FileCategoryCard(
                            cat = cat,
                            hasPermission = hasPermission,
                            onTap = {
                                if (hasPermission) {
                                    selectedCategory = cat
                                } else {
                                    folderPickerLauncher.launch(null)
                                }
                            },
                        )
                    }
                }
            }

            // Quick Actions
            item {
                Text("Quick Actions", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionIcon(Icons.Default.AutoFixHigh, "Cleanup", CiyatoGold, Modifier.weight(1f))
                    QuickActionIcon(Icons.Default.Delete, "Junk", Color(0xFFEF4444), Modifier.weight(1f))
                    QuickActionIcon(Icons.Default.VpnKey, "Vault", CiyatoBlue, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StorageCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CiyatoBgEl)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Phone Storage", color = CiyatoWhite, fontWeight = FontWeight.SemiBold)
            Text("53% Used", color = CiyatoGold, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { 0.53f },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = CiyatoGold,
            trackColor = CiyatoBgEl2
        )
        Text("68 GB / 128 GB used", color = CiyatoMuted, fontSize = 12.sp)
    }
}

@Composable
private fun PermissionCard(onEnable: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CiyatoGold.copy(alpha = 0.08f))
            .border(1.dp, CiyatoGold.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Enable File Organization", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(
            "Choose the folders or files you want Ciyato to organize. Ciyato will only access what you select — no broad storage access.",
            color = CiyatoSec,
            fontSize = 12.sp,
            lineHeight = 18.sp,
        )
        Button(
            onClick = onEnable,
            colors = ButtonDefaults.buttonColors(containerColor = CiyatoGold),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Choose Folder", color = CiyatoBg, fontWeight = FontWeight.Bold)
        }
        Text("Uses Android Storage Access Framework", color = CiyatoMuted, fontSize = 11.sp)
    }
}

@Composable
private fun FileCategoryCard(
    cat: FileCategory,
    hasPermission: Boolean,
    onTap: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CiyatoBgEl)
            .border(
                1.dp,
                if (hasPermission) cat.color.copy(alpha = 0.20f) else CiyatoSubtleBorder,
                RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onTap)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(cat.color.copy(alpha = 0.15f)),
        ) {
            Icon(cat.icon, null, tint = cat.color, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(cat.name, color = CiyatoWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            if (!hasPermission) {
                Text("Tap to enable", color = CiyatoMuted, fontSize = 10.sp)
            } else {
                Text(cat.description, color = CiyatoMuted, fontSize = 10.sp)
            }
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = if (hasPermission) cat.color.copy(alpha = 0.7f) else CiyatoMuted,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun QuickActionIcon(icon: ImageVector, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(CiyatoBgEl),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        }
        Text(label, color = CiyatoSec, fontSize = 12.sp)
    }
}
