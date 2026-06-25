package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.ui.components.AppIconTile
import com.ciyato.launcher.ui.components.CiyatoSearchBar
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel

private val DRAWER_CATS = listOf(
    null to "All",
    AppCategory.SUGGESTED to "Suggested",
    AppCategory.RECENTLY_ADDED to "Recent",
    AppCategory.WORK to "Work",
    AppCategory.SOCIAL to "Social",
    AppCategory.COMMUNICATION to "Communication",
    AppCategory.UTILITIES to "Utilities",
    AppCategory.CREATIVITY to "Creativity",
    AppCategory.ENTERTAINMENT to "Entertainment",
    AppCategory.TRAVEL to "Travel",
    AppCategory.FINANCE to "Finance",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
) {
    val apps by viewModel.apps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    var selectedCat by remember { mutableStateOf<AppCategory?>(null) }

    val displayApps = remember(selectedCat, apps, searchResults, searchQuery) {
        when {
            searchQuery.isNotBlank() -> searchResults
            selectedCat == AppCategory.RECENTLY_ADDED -> viewModel.recentlyAdded()
            selectedCat != null -> viewModel.byCategory(selectedCat!!)
            else -> apps
        }
    }

    Scaffold(containerColor = CiyatoLightBg) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(CiyatoLightBg, CiyatoLightBg)))
                .padding(padding)
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text("Ciyato", color = CiyatoLightText, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Smart App Library", color = CiyatoGold, fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CiyatoGold.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 4.dp))
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter",
                            tint = CiyatoLightSec, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Search
            CiyatoSearchBar(
                query = searchQuery,
                onQueryChange = viewModel::setSearch,
                placeholder = "Search apps…",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(12.dp))

            // Category chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(DRAWER_CATS) { (cat, label) ->
                    val isSelected = selectedCat == cat
                    Text(
                        text = label,
                        color = if (isSelected) CiyatoBg else CiyatoLightSec,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) CiyatoGold else CiyatoLightCard)
                            .clickable { selectedCat = cat }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CiyatoGold)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(displayApps) { app ->
                        AppIconTile(
                            app = app,
                            iconSize = 54.dp,
                            onClick = { viewModel.launchApp(app) },
                        )
                    }
                    if (displayApps.isEmpty()) {
                        item {
                            Text("No apps found", color = CiyatoLightSec,
                                modifier = Modifier.padding(20.dp))
                        }
                    }
                }
            }
        }
    }
}
