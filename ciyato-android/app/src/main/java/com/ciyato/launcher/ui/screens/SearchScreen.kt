package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.components.AppIconTile
import com.ciyato.launcher.ui.components.CiyatoSearchBar
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var activeTab by remember { mutableStateOf("All") }
    val tabs = listOf("All", "Apps", "Files", "Photos", "Documents")

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            Column(modifier = Modifier.background(CiyatoBg)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = CiyatoSec)
                    }
                    Text("AI Search", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }

                CiyatoSearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::setSearch,
                    placeholder = "Search apps, files, photos...",
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tabs) { tab ->
                        val selected = activeTab == tab
                        Text(
                            text = tab,
                            color = if (selected) CiyatoBg else CiyatoSec,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) CiyatoGold else CiyatoBgEl)
                                .clickable { activeTab = tab }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (searchQuery.isBlank()) {
                item {
                    Text("Suggestions", color = CiyatoWhite, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SuggestionPill("Show PDFs from yesterday", Icons.Default.Description)
                        SuggestionPill("Find payment screenshots", Icons.Default.Payments)
                        SuggestionPill("Recent WhatsApp files", Icons.AutoMirrored.Filled.Chat)
                    }
                }
            } else {
                if (isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = CiyatoGold)
                        }
                    }
                } else {
                    item {
                        Text("Top Match", color = CiyatoGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        if (searchResults.isNotEmpty()) {
                            AppIconTile(
                                app = searchResults.first(),
                                onClick = { viewModel.launchApp(searchResults.first()) },
                                modifier = Modifier.fillMaxWidth().background(CiyatoBgEl, RoundedCornerShape(16.dp)).padding(16.dp)
                            )
                        }
                    }

                    item {
                        Text("Apps", color = CiyatoWhite, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        searchResults.take(8).forEach { app ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { viewModel.launchApp(app) }.padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                com.ciyato.launcher.ui.components.RealAppIcon(app.icon, size = 40.dp)
                                Text(app.label, color = CiyatoWhite)
                            }
                        }
                    }

                    item {
                        Text("Files & Media (Beta)", color = CiyatoMuted, fontSize = 12.sp)
                        Text("Results will appear here after permission is granted.", color = CiyatoMuted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionPill(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CiyatoBgEl)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, null, tint = CiyatoGold, modifier = Modifier.size(18.dp))
        Text(text, color = CiyatoSec, fontSize = 14.sp)
    }
}
