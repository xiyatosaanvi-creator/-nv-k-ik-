package com.ciyato.launcher.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.ui.components.*

private data class FileItem(
    val name: String,
    val count: String,
    val icon: ImageVector,
    val color: Color
)

private val categories = listOf(
    FileItem("Screenshots", "1,342", Icons.Default.Screenshot, Color(0xFF7DB7FF)),
    FileItem("Documents", "842", Icons.Default.Description, Color(0xFF39C66A)),
    FileItem("Downloads", "1,245", Icons.Default.Download, Color(0xFFC6A15B)),
    FileItem("Photos", "12,546", Icons.Default.PhotoLibrary, Color(0xFF8E5CFF)),
    FileItem("Videos", "528", Icons.Default.VideoFile, Color(0xFFFF6B8C)),
    FileItem("APKs", "92", Icons.Default.Android, Color(0xFF9C6AFF)),
    FileItem("WhatsApp", "2,105", Icons.Default.Chat, Color(0xFF25D366))
)

private data class SmartCollectionItem(
    val name: String,
    val count: String,
    val icon: ImageVector,
    val color: Color,
    val previewIcons: List<String> = emptyList()
)

private val smartCollectionsList = listOf(
    SmartCollectionItem("Work Files", "1,234 files", Icons.Default.Work, Color(0xFF4A8CF7), listOf("DOC", "XLS", "PPT")),
    SmartCollectionItem("Receipts", "347 files", Icons.Default.ReceiptLong, Color(0xFF51C7A5), listOf("PDF", "PDF")),
    SmartCollectionItem("PDFs", "567 files", Icons.Default.PictureAsPdf, Color(0xFFEF5350), listOf("PDF", "PDF")),
    SmartCollectionItem("Contracts", "89 files", Icons.Default.Assignment, Color(0xFF9C6AFF), listOf("PDF", "DOC")),
    SmartCollectionItem("Screen Recordings", "124 files", Icons.Default.VideoLibrary, Color(0xFFB45CFF)),
    SmartCollectionItem("Design Assets", "312 files", Icons.Default.Palette, Color(0xFFF5C451), listOf("PS", "AI", "XD")),
    SmartCollectionItem("WhatsApp Media", "2,142 files", Icons.Default.Chat, Color(0xFF25D366)),
    SmartCollectionItem("Travel", "233 files", Icons.Default.Flight, Color(0xFF7DB7FF)),
    SmartCollectionItem("College", "445 files", Icons.Default.School, Color(0xFFFF7A45), listOf("PDF", "PPT", "DOC")),
    SmartCollectionItem("Recently Added", "1,025 files", Icons.Default.Schedule, Color(0xFFAEB4BA))
)

private data class FilesRecentFile(
    val name: String,
    val ext: String,
    val color: Color
)

private val recentFiles = listOf(
    FilesRecentFile("Project_Proposal", ".pdf", Color(0xFFEF5350)),
    FilesRecentFile("Brand_Deck", ".ppt", Color(0xFFFF9100)),
    FilesRecentFile("IMG_2024_05", ".jpg", Color(0xFF29B6F6))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var activeTab by remember { mutableStateOf("Organizer") } // Organizer | Smart Collections | Timeline

    if (selectedCategory != null) {
        FileCollectionDetailScreen(
            collectionTitle = selectedCategory!!,
            collectionIcon = Icons.Default.Folder,
            collectionColor = CiyatoGold,
            initialFolderUri = null,
            onBack = { selectedCategory = null }
        )
        return
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            CiyatoTopBar(
                title = "Ciyoto Files",
                subtitle = if (activeTab == "Organizer") "AI Phone Organizer" else "Smart Collections",
                onBack = onBack,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.AutoMode, "AI Cleanup", tint = CiyatoGold)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Notifications, "Notifications", tint = CiyatoSec)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, "Menu", tint = CiyatoSec)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding() + 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            // ── Tab Switcher Pill Row (Organizer | Smart Collections | Timeline) ──
            item {
                CiyatoTabRow(
                    tabs = listOf("Organizer", "Smart Collections", "Timeline"),
                    selectedIndex = listOf("Organizer", "Smart Collections", "Timeline").indexOf(activeTab),
                    onTabSelected = { idx -> activeTab = listOf("Organizer", "Smart Collections", "Timeline")[idx] }
                )
            }

            // ── Render View based on Active Tab ──────────────────────────────────
            if (activeTab == "Organizer") {
                // ─── ORGANIZER VIEW (Image 2) ───
                item {
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        Text("Good morning, Alex ☀️", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text("Tuesday, May 20", color = CiyatoMuted, fontSize = 13.sp)
                    }
                }

                item {
                    CiyatoSearchField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search files, folders...",
                        onClear = { searchQuery = "" }
                    )
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Storage overview
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(22.dp))
                                .background(CiyatoBgEl)
                                .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(22.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Storage overview", color = CiyatoSec, fontSize = 12.sp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(CiyatoGold.copy(alpha = 0.12f))
                                        .border(2.dp, CiyatoGold, CircleShape)
                                ) {
                                    Text("53%", color = CiyatoGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Column {
                                    Text("68 GB used", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("128 GB total", color = CiyatoMuted, fontSize = 11.sp)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CiyatoBgEl2)
                                    .clickable { }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Clean suggestions", color = CiyatoSec, fontSize = 10.sp)
                                    Text("2.4 GB >", color = CiyatoGold, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        // Cleanup status
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(22.dp))
                                .background(CiyatoBgEl)
                                .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(22.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Cleanup status", color = CiyatoSec, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(CiyatoGreen.copy(alpha = 0.15f))
                            ) {
                                Icon(Icons.Default.Check, null, tint = CiyatoGreen, modifier = Modifier.size(24.dp))
                            }
                            Text("Excellent", color = CiyatoGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Your device is well organized.", color = CiyatoMuted, fontSize = 10.sp, textAlign = TextAlign.Center)
                        }
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Categories", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("View all", color = CiyatoBlue, fontSize = 12.sp, modifier = Modifier.clickable { })
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        categories.chunked(2).forEach { rowItems ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                rowItems.forEach { cat ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(CiyatoBgEl)
                                            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(16.dp))
                                            .clickable { selectedCategory = cat.name }
                                            .padding(12.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(cat.color.copy(alpha = 0.12f))
                                        ) {
                                            Icon(cat.icon, null, tint = cat.color, modifier = Modifier.size(18.dp))
                                        }
                                        Column {
                                            Text(cat.name, color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                            Text(cat.count, color = CiyatoMuted, fontSize = 11.sp)
                                        }
                                    }
                                }
                                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }

                item {
                    Text("Recent files", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(recentFiles) { file ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier
                                    .width(180.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(CiyatoBgEl)
                                    .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(14.dp))
                                    .clickable { }
                                    .padding(10.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(file.color.copy(alpha = 0.15f))
                                ) {
                                    Text(file.ext.removePrefix(".").uppercase(), color = file.color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text(file.name, color = CiyatoWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(file.ext, color = CiyatoMuted, fontSize = 10.sp)
                                }
                            }
                        }
                        item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(CiyatoBgEl2)
                                    .border(1.dp, CiyatoBorder, RoundedCornerShape(14.dp))
                                    .clickable { }
                                    .padding(horizontal = 14.dp)
                            ) {
                                Text("View all\n+12", color = CiyatoGold, fontSize = 11.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(CiyatoBgEl)
                                .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(16.dp))
                                .clickable { }
                                .padding(12.dp)
                        ) {
                            Icon(Icons.Default.CopyAll, null, tint = CiyatoGold, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Duplicate files", color = CiyatoWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("1.8 GB", color = CiyatoMuted, fontSize = 10.sp)
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(CiyatoBgEl)
                                .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(16.dp))
                                .clickable { }
                                .padding(12.dp)
                        ) {
                            Icon(Icons.Default.FolderZip, null, tint = CiyatoBlue, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Large files", color = CiyatoWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("8.3 GB", color = CiyatoMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }

                item {
                    Text("Quick actions", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        QuickActionOption(Icons.Default.AutoMode, "AI Cleanup", "2.4 GB", CiyatoGold, Modifier.weight(1f))
                        QuickActionOption(Icons.Default.DeleteOutline, "Junk files", "1.1 GB", Color(0xFFEF5350), Modifier.weight(1f))
                        QuickActionOption(Icons.Default.Lock, "Vault", "Secure files", CiyatoBlue, Modifier.weight(1f))
                        QuickActionOption(Icons.Default.SwapHoriz, "File transfer", "Share files", CiyatoGreen, Modifier.weight(1f))
                    }
                }
            } else {
                // ─── SMART COLLECTIONS VIEW (Image 4) ───
                item {
                    Text("Find anything faster.", color = CiyatoMuted, fontSize = 13.sp)
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        smartCollectionsList.chunked(2).forEach { rowItems ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                rowItems.forEach { collection ->
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(CiyatoBgEl)
                                            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(18.dp))
                                            .clickable { selectedCategory = collection.name }
                                            .padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(collection.color.copy(alpha = 0.12f))
                                            ) {
                                                Icon(collection.icon, null, tint = collection.color, modifier = Modifier.size(18.dp))
                                            }
                                            Text(collection.count, color = CiyatoMuted, fontSize = 11.sp)
                                        }
                                        Text(collection.name, color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                                        if (collection.previewIcons.isNotEmpty()) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                collection.previewIcons.forEach { ext ->
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(CiyatoBgEl2)
                                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(ext, color = CiyatoSec, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Storage summary footer
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(CiyatoBgEl)
                            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("62% Storage used", color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Manage", color = CiyatoGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        LinearProgressIndicator(
                            progress = { 0.62f },
                            color = CiyatoGold,
                            trackColor = CiyatoBgEl2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        Text("63.2 GB / 128 GB", color = CiyatoMuted, fontSize = 11.sp)
                    }
                }

                // Duplicates and Large files cards
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(CiyatoBgEl)
                                .clickable { }
                                .padding(12.dp)
                        ) {
                            Icon(Icons.Default.CopyAll, null, tint = CiyatoGold, modifier = Modifier.size(18.dp))
                            Column {
                                Text("Duplicates found", color = CiyatoWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("1.32 GB", color = CiyatoMuted, fontSize = 10.sp)
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(CiyatoBgEl)
                                .clickable { }
                                .padding(12.dp)
                        ) {
                            Icon(Icons.Default.FolderZip, null, tint = CiyatoBlue, modifier = Modifier.size(18.dp))
                            Column {
                                Text("Large files", color = CiyatoWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("1.13 GB", color = CiyatoMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionOption(
    icon: ImageVector,
    title: String,
    desc: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(16.dp))
            .clickable { }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f))
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        }
        Text(title, color = CiyatoWhite, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(desc, color = CiyatoMuted, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
