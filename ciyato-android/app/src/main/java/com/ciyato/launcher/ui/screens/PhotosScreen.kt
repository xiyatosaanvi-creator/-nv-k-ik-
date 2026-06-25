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

/**
 * Ciyato Photos — AI-sorted moments and media.
 *
 * BETA NOTE: Real photo/media access is NOT requested here.
 * The screen uses mock collection data. Media permission is
 * requested only if the user explicitly enables real photo access.
 * This screen matches the uploaded reference screenshot exactly.
 */

private data class PhotoCollection(
    val name: String,
    val count: Int,
    val icon: ImageVector,
    val accentColor: Color,
    val badge: String? = null,
)

private val mockCollections = listOf(
    PhotoCollection("Family Moments", 1248, Icons.Default.People, Color(0xFF6B8CFF), badge = "👨‍👩‍👧"),
    PhotoCollection("Friends Forever", 843, Icons.Default.Group, Color(0xFFFF6B8C), badge = "👥"),
    PhotoCollection("Travel Adventures", 521, Icons.Default.Place, Color(0xFF4CAF50), badge = "📍"),
    PhotoCollection("Delicious Eats", 398, Icons.Default.Restaurant, Color(0xFFFF9800), badge = "🍴"),
    PhotoCollection("Furry Friends", 286, Icons.Default.Pets, Color(0xFFA67C52), badge = "🐾"),
    PhotoCollection("Sunset Vibes", 207, Icons.Default.Image, Color(0xFFFF6B35), badge = "🖼"),
    PhotoCollection("Little Wonders", 615, Icons.Default.ChildCare, Color(0xFF9C27B0), badge = "⭐"),
    PhotoCollection("Memories", 1102, Icons.Default.Favorite, Color(0xFFE91E63), badge = null),
)

private val filterChips = listOf("Personal", "Shared", "Activity", "Work", "Recent")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(onBack: () -> Unit) {
    var selectedChip by remember { mutableStateOf("Personal") }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Ciyato Photos",
                            color = CiyatoWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                        Text(
                            "AI-sorted moments and media ✦",
                            color = CiyatoGold,
                            fontSize = 12.sp,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CiyatoSec)
                    }
                },
                actions = {
                    // Avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(CiyatoBgEl2)
                            .border(1.dp, CiyatoBorder, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile",
                            tint = CiyatoSec, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = CiyatoSec)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CiyatoBg),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = CiyatoBgEl,
                contentColor = CiyatoWhite,
                shape = CircleShape,
                modifier = Modifier.size(52.dp),
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search photos", tint = CiyatoWhite)
            }
        },
        bottomBar = {
            PhotosBottomNav()
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Search bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CiyatoBgEl)
                        .border(1.dp, CiyatoBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(Icons.Default.Search, contentDescription = null,
                        tint = CiyatoMuted, modifier = Modifier.size(18.dp))
                    Text("Search photos, albums, places...", color = CiyatoMuted, fontSize = 14.sp,
                        modifier = Modifier.weight(1f))
                }
            }

            // Filter chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterChips) { chip ->
                        val isSelected = chip == selectedChip
                        Text(
                            chip,
                            color = if (isSelected) CiyatoBg else CiyatoSec,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) CiyatoGold else CiyatoBgEl)
                                .border(1.dp, if (isSelected) CiyatoGold else CiyatoBorder, RoundedCornerShape(20.dp))
                                .clickable { selectedChip = chip }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                        )
                    }
                }
            }

            // Beta notice
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CiyatoGold.copy(alpha = 0.08f))
                        .border(1.dp, CiyatoGold.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null,
                        tint = CiyatoGold, modifier = Modifier.size(14.dp))
                    Text(
                        "Collections shown below are AI-organized previews. Media permission requested only when you enable real photos.",
                        color = CiyatoGold,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                    )
                }
            }

            // Collections grid (2 columns matching reference)
            item {
                val grid = mockCollections
                val rows = (grid.size + 1) / 2
                val gridH = (rows * 130 + (rows - 1) * 10).dp

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth().height(gridH),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    userScrollEnabled = false,
                ) {
                    items(grid) { collection ->
                        PhotoCollectionCard(collection = collection)
                    }
                }
            }

            // AI Organized badge row
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CiyatoBgEl)
                            .border(1.dp, CiyatoBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null,
                            tint = CiyatoGold, modifier = Modifier.size(14.dp))
                        Text("AI ORGANIZED", color = CiyatoWhite,
                            fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoCollectionCard(collection: PhotoCollection) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        collection.accentColor.copy(alpha = 0.25f),
                        CiyatoBgEl,
                    )
                )
            )
            .border(1.dp, CiyatoBorder, RoundedCornerShape(16.dp))
            .clickable {}
            .padding(14.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            // Badge icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(collection.accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(collection.icon, contentDescription = null,
                    tint = collection.accentColor, modifier = Modifier.size(18.dp))
            }

            Column {
                Text(
                    collection.name,
                    color = CiyatoWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "${collection.count} items",
                    color = CiyatoSec,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun PhotosBottomNav() {
    val items = listOf(
        Icons.Default.Image to "Photos",
        Icons.Default.CollectionsBookmark to "Collections",
        Icons.Default.Search to "Search",
        Icons.Default.Map to "Map",
    )
    var selected by remember { mutableStateOf(1) } // Collections selected by default

    NavigationBar(
        containerColor = CiyatoBgEl,
        tonalElevation = 0.dp,
    ) {
        items.forEachIndexed { i, (icon, label) ->
            NavigationBarItem(
                selected = selected == i,
                onClick = { selected = i },
                icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(22.dp)) },
                label = { Text(label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CiyatoGold,
                    selectedTextColor = CiyatoGold,
                    unselectedIconColor = CiyatoSec,
                    unselectedTextColor = CiyatoSec,
                    indicatorColor = CiyatoGold.copy(alpha = 0.15f),
                ),
            )
        }
    }
}
