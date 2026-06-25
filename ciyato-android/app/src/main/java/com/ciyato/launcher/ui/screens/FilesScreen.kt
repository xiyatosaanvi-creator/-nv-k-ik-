package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*

private data class FileCategory(val name: String, val icon: ImageVector, val color: Color, val count: String, val size: String)

private val fileCategories = listOf(
    FileCategory("Screenshots", Icons.Default.Screenshot, CiyatoBlue, "1,342", "2.1 GB"),
    FileCategory("Documents",   Icons.Default.Description, CiyatoGold, "842",   "1.4 GB"),
    FileCategory("Downloads",   Icons.Default.Download,    CiyatoGreen,"1,245", "4.8 GB"),
    FileCategory("Photos",      Icons.Default.Image,        Color(0xFF7DB7FF), "12,546", "18.2 GB"),
    FileCategory("Videos",      Icons.Default.VideoFile,    Color(0xFFE1306C), "528", "12.1 GB"),
    FileCategory("APKs",        Icons.Default.Android,      Color(0xFF39C66A), "92",  "1.6 GB"),
    FileCategory("WhatsApp",    Icons.Default.Chat,         Color(0xFF25D366), "2,105","3.4 GB"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Ciyato Files", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("AI Phone Organizer", color = CiyatoGold, fontSize = 11.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = CiyatoSec)
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Search, "Search", tint = CiyatoSec) }
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, "More", tint = CiyatoSec) }
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Storage overview
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CiyatoBgEl)
                        .border(1.dp, CiyatoBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Storage overview", color = CiyatoSec, fontSize = 12.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("68 GB used", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("128 GB total", color = CiyatoMuted, fontSize = 11.sp)
                        Spacer(Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { 0.53f },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = CiyatoGold,
                            trackColor = CiyatoBgEl2,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text("Clean suggestions: 2.4 GB →", color = CiyatoBlue, fontSize = 12.sp)
                    }
                    // Cleanup status
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(60.dp).clip(androidx.compose.foundation.shape.CircleShape)
                                .background(CiyatoGreen.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null,
                                tint = CiyatoGreen, modifier = Modifier.size(32.dp))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Excellent", color = CiyatoGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("Well organized", color = CiyatoMuted, fontSize = 10.sp)
                    }
                }
            }

            // Categories header
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Categories", color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("View all", color = CiyatoBlue, fontSize = 13.sp)
                }
            }

            // Category grid (3 cols)
            item {
                val rows = (fileCategories.size + 2) / 3
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (r in 0 until rows) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            for (c in 0..2) {
                                val idx = r * 3 + c
                                if (idx < fileCategories.size) {
                                    val fc = fileCategories[idx]
                                    Column(
                                        modifier = Modifier.weight(1f)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(CiyatoBgEl)
                                            .border(1.dp, CiyatoBorder, RoundedCornerShape(14.dp))
                                            .padding(12.dp),
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                                                .background(fc.color.copy(alpha = 0.15f))
                                        ) {
                                            Icon(fc.icon, null, tint = fc.color, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(Modifier.height(6.dp))
                                        Text(fc.name, color = CiyatoWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text(fc.count, color = fc.color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(fc.size, color = CiyatoMuted, fontSize = 10.sp)
                                    }
                                } else {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // Duplicate + Large files
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf(
                        Triple("Duplicate files", "1.8 GB", Icons.Default.CopyAll),
                        Triple("Large files", "8.3 GB", Icons.Default.FolderZip),
                    ).forEach { (title, size, icon) ->
                        Row(
                            modifier = Modifier.weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(CiyatoBgEl)
                                .border(1.dp, CiyatoBorder, RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(icon, null, tint = CiyatoGold, modifier = Modifier.size(18.dp))
                            Column {
                                Text(title, color = CiyatoWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text(size, color = CiyatoGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Review →", color = CiyatoMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            item {
                Text("⚠ File/photo permissions are requested only when you activate those features.",
                    color = CiyatoMuted, fontSize = 11.sp)
            }
        }
    }
}
