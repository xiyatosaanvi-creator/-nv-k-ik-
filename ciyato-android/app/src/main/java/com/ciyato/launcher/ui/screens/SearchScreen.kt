package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.components.AppIconTile
import com.ciyato.launcher.ui.components.CiyatoSearchBar
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel

private val suggestions = listOf(
    "Show me photos from last month",
    "Find large files I can delete",
    "Screenshots from this week",
    "Work apps",
    "Recently added apps",
    "Duplicate photos",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val hasQuery = searchQuery.isNotBlank()

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = { Text("AI Search", color = CiyatoWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = CiyatoSec)
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
            item {
                CiyatoSearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::setSearch,
                    placeholder = "Search apps, files, photos, documents…",
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Category filter chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("All", "Apps", "Files", "Photos", "Documents")) { f ->
                        Text(
                            f, color = if (f == "All") CiyatoBg else CiyatoSec,
                            fontSize = 13.sp, fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (f == "All") CiyatoGold else CiyatoBgEl)
                                .border(1.dp, CiyatoBorder, RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            if (!hasQuery) {
                // Suggestions
                item { Text("Try asking…", color = CiyatoSec, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        suggestions.forEach { s ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CiyatoBgEl)
                                    .border(1.dp, CiyatoBorder, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setSearch(s) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Icon(Icons.Default.AutoFixHigh, contentDescription = null,
                                    tint = CiyatoGold, modifier = Modifier.size(16.dp))
                                Text(s, color = CiyatoWhite, fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else {
                // App results
                if (searchResults.isNotEmpty()) {
                    item {
                        Text("Apps (${searchResults.size})", color = CiyatoSec,
                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(searchResults.take(10)) { app ->
                                AppIconTile(
                                    app = app,
                                    iconSize = 52.dp,
                                    onClick = { viewModel.launchApp(app) },
                                    modifier = Modifier.width(64.dp),
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No results for \"$searchQuery\"", color = CiyatoMuted)
                        }
                    }
                }

                // Files section (mock for beta)
                item { Text("Files  (beta — permissions needed)", color = CiyatoSec, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "Project_Proposal.pdf" to "2.4 MB · Yesterday · Documents",
                            "Brand_Deck.pptx" to "18.7 MB · Yesterday · Work",
                            "Invoice_0421.pdf" to "1.8 MB · 2 days ago · Downloads",
                        ).forEach { (name, meta) ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CiyatoBgEl)
                                    .border(1.dp, CiyatoBorder, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                                        .background(CiyatoGold.copy(alpha = 0.15f))
                                ) {
                                    Icon(Icons.Default.Description, null,
                                        tint = CiyatoGold, modifier = Modifier.size(18.dp))
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(name, color = CiyatoWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Text(meta, color = CiyatoMuted, fontSize = 11.sp)
                                }
                                Icon(Icons.Default.MoreVert, null, tint = CiyatoMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
