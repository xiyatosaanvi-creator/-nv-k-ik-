package com.ciyato.launcher.ui.screens

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(onBack: () -> Unit) {
    var hasPermission by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = { Text("Ciyato Files", color = CiyatoWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = CiyatoSec)
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
                    PermissionCard(onEnable = { hasPermission = true })
                }
            }

            // Categories
            item {
                Text("Categories", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(300.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(fileCategories) { cat ->
                        FileCategoryCard(cat)
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
        Text("Organize Your Files", color = CiyatoWhite, fontWeight = FontWeight.Bold)
        Text("Ciyato can help you find large files, duplicates, and WhatsApp media locally.", color = CiyatoSec, fontSize = 12.sp)
        Button(
            onClick = onEnable,
            colors = ButtonDefaults.buttonColors(containerColor = CiyatoGold),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enable File Access", color = CiyatoBg, fontWeight = FontWeight.Bold)
        }
    }
}

private data class FileCategory(val name: String, val icon: ImageVector, val color: Color)
private val fileCategories = listOf(
    FileCategory("Screenshots", Icons.Default.Screenshot, Color(0xFF7DB7FF)),
    FileCategory("Documents", Icons.Default.Description, Color(0xFF39C66A)),
    FileCategory("Downloads", Icons.Default.Download, Color(0xFFC6A15B)),
    FileCategory("WhatsApp", Icons.Default.Chat, Color(0xFF25D366))
)

@Composable
private fun FileCategoryCard(cat: FileCategory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CiyatoBgEl)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(cat.icon, null, tint = cat.color, modifier = Modifier.size(24.dp))
        Text(cat.name, color = CiyatoWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
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
