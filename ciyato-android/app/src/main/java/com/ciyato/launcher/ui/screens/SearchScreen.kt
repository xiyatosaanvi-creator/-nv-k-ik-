package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.ui.components.AppIconTile
import com.ciyato.launcher.ui.components.CiyatoSearchBar
import com.ciyato.launcher.ui.components.RealAppIcon
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel

private data class SearchChip(
    val label: String,
    val icon: ImageVector,
    val action: ChipAction,
)

private sealed class ChipAction {
    data class FilterCategory(val query: String, val category: AppCategory?) : ChipAction()
    object OpenFilesPermission : ChipAction()
    object OpenPhotosPermission : ChipAction()
}

private val SUGGESTION_CHIPS = listOf(
    SearchChip("Work apps", Icons.Default.Work, ChipAction.FilterCategory("Work", AppCategory.WORK)),
    SearchChip("Social apps", Icons.Default.People, ChipAction.FilterCategory("Social", AppCategory.SOCIAL)),
    SearchChip("Show PDFs from yesterday", Icons.Default.Description, ChipAction.OpenFilesPermission),
    SearchChip("Find payment screenshots", Icons.Default.Payments, ChipAction.OpenPhotosPermission),
    SearchChip("Recent WhatsApp files", Icons.AutoMirrored.Filled.Chat, ChipAction.OpenFilesPermission),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
    onCategoryFilter: ((AppCategory) -> Unit)? = null,
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var activeTab by remember { mutableStateOf("All") }
    val tabs = listOf("All", "Apps", "Files", "Photos", "Documents")

    // Sub-screen state for permission flows triggered by chips
    var showFilesFlow by remember { mutableStateOf(false) }
    var showPhotosFlow by remember { mutableStateOf(false) }

    if (showFilesFlow) {
        FilesScreen(onBack = { showFilesFlow = false })
        return
    }
    if (showPhotosFlow) {
        PhotosScreen(onBack = { showPhotosFlow = false })
        return
    }

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
                // Suggestion chips — now functional
                item {
                    Text("Suggestions", color = CiyatoWhite, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SUGGESTION_CHIPS.forEach { chip ->
                            SuggestionChipRow(chip = chip) {
                                when (val action = chip.action) {
                                    is ChipAction.FilterCategory -> {
                                        if (action.category != null) {
                                            if (onCategoryFilter != null) {
                                                onCategoryFilter(action.category)
                                            } else {
                                                viewModel.setSearch(action.query)
                                            }
                                        } else {
                                            viewModel.setSearch(action.query)
                                        }
                                    }
                                    is ChipAction.OpenFilesPermission -> showFilesFlow = true
                                    is ChipAction.OpenPhotosPermission -> showPhotosFlow = true
                                }
                            }
                        }
                    }
                }

                // Locked sections — show what requires permissions
                item {
                    LockedSearchSection(
                        title = "Files & Documents",
                        hint = "Enable folder access to search files",
                        icon = Icons.Default.FolderOpen,
                        onEnable = { showFilesFlow = true },
                    )
                }

                item {
                    LockedSearchSection(
                        title = "Photos & Media",
                        hint = "Enable photo access to search media",
                        icon = Icons.Default.PhotoLibrary,
                        onEnable = { showPhotosFlow = true },
                    )
                }

            } else {
                if (isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = CiyatoGold)
                        }
                    }
                } else {
                    if (searchResults.isNotEmpty()) {
                        item {
                            Text("Top Match", color = CiyatoGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CiyatoBgEl)
                                    .clickable { viewModel.launchApp(searchResults.first()) }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                RealAppIcon(searchResults.first().icon, size = 52.dp, cornerRadius = 14.dp)
                                Column {
                                    Text(searchResults.first().label, color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                    Text(searchResults.first().category.displayName, color = CiyatoGold, fontSize = 12.sp)
                                }
                            }
                        }

                        item {
                            Text("Apps (${searchResults.size})", color = CiyatoWhite, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                searchResults.take(8).forEach { app ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(CiyatoBgEl)
                                            .clickable { viewModel.launchApp(app) }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    ) {
                                        RealAppIcon(app.icon, size = 40.dp, cornerRadius = 10.dp)
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(app.label, color = CiyatoWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                            Text(app.category.displayName, color = CiyatoMuted, fontSize = 11.sp)
                                        }
                                        Icon(Icons.Default.ChevronRight, null, tint = CiyatoMuted, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    } else {
                        item {
                            Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                Text("No apps match \"$searchQuery\"", color = CiyatoMuted)
                            }
                        }
                    }

                    // Locked file/photo sections shown below app results
                    item {
                        LockedSearchSection(
                            title = "Files & Media (Locked)",
                            hint = "Results will appear here after folder access is granted.",
                            icon = Icons.Default.Lock,
                            onEnable = { showFilesFlow = true },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionChipRow(chip: SearchChip, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CiyatoBgEl)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(CiyatoGold.copy(alpha = 0.14f)),
        ) {
            Icon(chip.icon, null, tint = CiyatoGold, modifier = Modifier.size(16.dp))
        }
        Text(chip.label, color = CiyatoSec, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null, tint = CiyatoMuted, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun LockedSearchSection(
    title: String,
    hint: String,
    icon: ImageVector,
    onEnable: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CiyatoBgEl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, null, tint = CiyatoMuted, modifier = Modifier.size(18.dp))
            Text(title, color = CiyatoSec, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
        }
        Text(hint, color = CiyatoMuted, fontSize = 12.sp)
        OutlinedButton(
            onClick = onEnable,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CiyatoGold),
            border = androidx.compose.foundation.BorderStroke(1.dp, CiyatoGold.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().height(40.dp),
        ) {
            Text("Enable Access", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}
