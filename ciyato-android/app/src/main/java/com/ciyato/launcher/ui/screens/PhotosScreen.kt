package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(onBack: () -> Unit) {
    var hasPermission by remember { mutableStateOf(false) }
    var selectedChip by remember { mutableStateOf("Personal") }
    val filterChips = listOf("Personal", "Shared", "Activity", "Work", "Recent")

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Ciyato Photos", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("AI-sorted moments and media", color = CiyatoGold, fontSize = 12.sp)
                    }
                },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterChips) { chip ->
                        val selected = selectedChip == chip
                        Text(
                            text = chip,
                            color = if (selected) CiyatoBg else CiyatoSec,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) CiyatoGold else CiyatoBgEl)
                                .clickable { selectedChip = chip }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (!hasPermission) {
                item {
                    PermissionRequestCard(
                        title = "Enable AI Photo Organization",
                        body = "Ciyato needs media permission to group your photos into Family, Travel, and Work collections automatically. Your photos never leave your device.",
                        onEnable = { hasPermission = true }
                    )
                }

                item {
                    Text("Preview Collections", color = CiyatoWhite, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                }

                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.height(400.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        userScrollEnabled = false
                    ) {
                        items(mockCollections) { collection ->
                            PhotoCollectionCard(collection)
                        }
                    }
                }
            } else {
                item {
                    Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = CiyatoGold)
                            Spacer(Modifier.height(16.dp))
                            Text("Indexing your photos locally...", color = CiyatoSec)
                        }
                    }
                }
            }
        }
    }
}

private data class PhotoCollection(val name: String, val count: Int, val icon: ImageVector, val color: Color)
private val mockCollections = listOf(
    PhotoCollection("Family Moments", 1248, Icons.Default.People, Color(0xFF6B8CFF)),
    PhotoCollection("Friends Forever", 843, Icons.Default.Group, Color(0xFFFF6B8C)),
    PhotoCollection("Travel Adventures", 521, Icons.Default.Place, Color(0xFF4CAF50)),
    PhotoCollection("Delicious Eats", 398, Icons.Default.Restaurant, Color(0xFFFF9800))
)

@Composable
private fun PhotoCollectionCard(collection: PhotoCollection) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Box(
                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(collection.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(collection.icon, null, tint = collection.color, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(collection.name, color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("${collection.count} items", color = CiyatoMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun PermissionRequestCard(title: String, body: String, onEnable: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CiyatoGold.copy(alpha = 0.08f))
            .border(1.dp, CiyatoGold.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(body, color = CiyatoSec, fontSize = 13.sp, lineHeight = 20.sp)
        Button(
            onClick = onEnable,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CiyatoGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Enable Photos", color = CiyatoBg, fontWeight = FontWeight.Bold)
        }
    }
}
