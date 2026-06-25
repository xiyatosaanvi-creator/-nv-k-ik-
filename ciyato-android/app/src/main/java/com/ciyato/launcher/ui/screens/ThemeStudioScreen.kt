package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeStudioScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
) {
    val darkMode by viewModel.darkMode.collectAsState()
    val goldAccent by viewModel.goldAccent.collectAsState()
    val iconStyle by viewModel.iconStyle.collectAsState()
    val denseLayout by viewModel.denseLayout.collectAsState()
    val smartCategories by viewModel.smartCategories.collectAsState()
    val duplicateShortcuts by viewModel.duplicateShortcuts.collectAsState()

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = { Text("Theme Studio", color = CiyatoWhite, fontWeight = FontWeight.Bold) },
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
                bottom = padding.calculateBottomPadding() + 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Ciyato Home Active toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CiyatoBgEl)
                        .border(1.dp, CiyatoBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Ciyato Home Active", color = CiyatoWhite,
                            fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text("Experience the full power of Ciyato launcher.",
                            color = CiyatoMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = smartCategories,
                        onCheckedChange = viewModel::setSmartCategories,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CiyatoGold,
                            checkedTrackColor = CiyatoGold.copy(alpha = 0.3f),
                        )
                    )
                }
            }

            // Dark mode selector
            item { StudioLabel("Dark Mode") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CiyatoBgEl)
                        .border(1.dp, CiyatoBorder, RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("auto" to "Auto", "light" to "Light", "dark" to "Dark").forEach { (v, l) ->
                        val sel = darkMode == v
                        Text(
                            l, color = if (sel) CiyatoBg else CiyatoSec,
                            fontWeight = FontWeight.Medium, fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) CiyatoGold else Color.Transparent)
                                .clickable { viewModel.setDarkMode(v) }
                                .padding(vertical = 8.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            // Icon style
            item { StudioLabel("Icon Style") }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("real" to "Real", "rounded" to "Rounded", "minimal" to "Minimal").forEach { (v, l) ->
                        val sel = iconStyle == v
                        Text(
                            l, color = if (sel) CiyatoBg else CiyatoSec,
                            fontSize = 13.sp, fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) CiyatoGold else CiyatoBgEl)
                                .border(1.dp, if (sel) CiyatoGold else CiyatoBorder, RoundedCornerShape(10.dp))
                                .clickable { viewModel.setIconStyle(v) }
                                .padding(10.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            // Card density
            item { StudioLabel("Card Density") }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf(true to "Dense", false to "Spacious").forEach { (v, l) ->
                        val sel = denseLayout == v
                        Text(
                            l, color = if (sel) CiyatoBg else CiyatoSec,
                            fontSize = 13.sp, fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) CiyatoGold else CiyatoBgEl)
                                .border(1.dp, if (sel) CiyatoGold else CiyatoBorder, RoundedCornerShape(10.dp))
                                .clickable { viewModel.setDenseLayout(v) }
                                .padding(10.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            // Gold accent toggle
            item { StudioLabel("Gold Accent") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CiyatoBgEl)
                        .border(1.dp, CiyatoBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Gold accent colour", color = CiyatoWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("Ciyato signature look", color = CiyatoMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = goldAccent, onCheckedChange = viewModel::setGoldAccent,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CiyatoGold,
                            checkedTrackColor = CiyatoGold.copy(alpha = 0.3f),
                        )
                    )
                }
            }

            // Duplicate shortcuts toggle
            item { StudioLabel("Smart Features") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Triple(smartCategories, viewModel::setSmartCategories, "Smart categories"),
                        Triple(duplicateShortcuts, viewModel::setDuplicateShortcuts, "Duplicate smart shortcuts"),
                    ).forEach { (checked, toggle, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(CiyatoBgEl)
                                .border(1.dp, CiyatoBorder, RoundedCornerShape(14.dp))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(label, color = CiyatoWhite, fontSize = 14.sp, Modifier.weight(1f))
                            Switch(
                                checked = checked, onCheckedChange = toggle,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CiyatoGold,
                                    checkedTrackColor = CiyatoGold.copy(alpha = 0.3f),
                                )
                            )
                        }
                    }
                }
            }

            // Reset layout
            item {
                OutlinedButton(
                    onClick = { viewModel.resetLayout() },
                    modifier = Modifier.fillMaxWidth(),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(CiyatoBorder)
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Reset Layout", color = CiyatoSec)
                }
            }
        }
    }
}

@Composable
private fun StudioLabel(text: String) {
    Text(text, color = CiyatoGold, fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
}
