package com.ciyato.launcher.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.data.InstalledApp
import com.ciyato.launcher.ui.components.RealAppIcon
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    category: AppCategory,
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
) {
    val allApps by viewModel.apps.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val categoryApps = remember(allApps, category) {
        viewModel.byCategory(category)
    }

    val filteredApps = remember(categoryApps, searchQuery) {
        if (searchQuery.isBlank()) categoryApps
        else categoryApps.filter {
            it.label.contains(searchQuery, ignoreCase = true) ||
                it.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            category.displayName,
                            color = CiyatoWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                        Text(
                            "${categoryApps.size} apps",
                            color = CiyatoGold,
                            fontSize = 12.sp,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CiyatoSec,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Tune, contentDescription = "Manage", tint = CiyatoSec)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CiyatoBg),
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to CiyatoBgEl2,
                            0.15f to CiyatoBg,
                            1f to CiyatoBg,
                        )
                    )
                )
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 32.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Search bar
                item {
                    CategorySearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                    )
                }

                // Category badge / hero
                item {
                    CategoryHeroBadge(category = category, appCount = categoryApps.size)
                }

                // Apps header
                item {
                    Text(
                        text = if (searchQuery.isBlank()) "All Apps" else "Results (${filteredApps.size})",
                        color = CiyatoWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }

                if (filteredApps.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                if (searchQuery.isBlank()) "No apps in this category yet."
                                else "No apps match \"$searchQuery\".",
                                color = CiyatoMuted,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                } else {
                    // 4-column grid rows
                    val rows = filteredApps.chunked(4)
                    items(rows) { rowApps ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            rowApps.forEach { app ->
                                AppTile(
                                    app = app,
                                    onTap = { viewModel.launchApp(app) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            repeat(4 - rowApps.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }

                // Manage section
                item {
                    ManageCategoryCard(category = category)
                }
            }
        }
    }
}

@Composable
private fun CategorySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = CiyatoMuted, modifier = Modifier.size(18.dp))
            androidx.compose.foundation.text.BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = CiyatoWhite, fontSize = 14.sp),
                decorationBox = { inner ->
                    if (query.isBlank()) {
                        Text("Search in this category…", color = CiyatoMuted, fontSize = 14.sp)
                    }
                    inner()
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CategoryHeroBadge(category: AppCategory, appCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CiyatoGold.copy(alpha = 0.08f))
            .border(1.dp, CiyatoGold.copy(alpha = 0.20f), RoundedCornerShape(20.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(CiyatoGold.copy(alpha = 0.18f)),
        ) {
            Text(
                text = category.emoji,
                fontSize = 22.sp,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(category.displayName, color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text("$appCount installed apps in this category", color = CiyatoSec, fontSize = 12.sp)
        }
    }
}

@Composable
private fun AppTile(
    app: InstalledApp,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .clickable(onClick = onTap)
            .padding(vertical = 10.dp, horizontal = 4.dp),
    ) {
        RealAppIcon(drawable = app.icon, size = 54.dp, cornerRadius = 14.dp)
        Text(
            text = app.label,
            color = CiyatoSec,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ManageCategoryCard(category: AppCategory) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("About this category", color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Text(
            "${category.displayName} apps are automatically identified and grouped by Ciyato — entirely on your device with no cloud processing.",
            color = CiyatoMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp,
        )
    }
}

private val AppCategory.emoji: String
    get() = when (this) {
        AppCategory.WORK         -> "💼"
        AppCategory.SOCIAL       -> "💬"
        AppCategory.FINANCE      -> "💰"
        AppCategory.CREATIVITY   -> "🎨"
        AppCategory.UTILITIES    -> "🔧"
        AppCategory.DAILY        -> "☀️"
        AppCategory.ENTERTAINMENT-> "🎬"
        AppCategory.TRAVEL       -> "✈️"
        AppCategory.PRODUCTIVITY -> "📋"
        AppCategory.COMMUNICATION-> "📞"
        AppCategory.RECENTLY_ADDED -> "🆕"
        else                     -> "📱"
    }
