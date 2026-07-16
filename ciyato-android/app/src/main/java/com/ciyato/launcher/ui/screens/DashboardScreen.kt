package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.CiyatoBg
import com.ciyato.launcher.ui.theme.CiyatoBgEl
import com.ciyato.launcher.ui.theme.CiyatoBgEl2
import com.ciyato.launcher.ui.theme.CiyatoBorder
import com.ciyato.launcher.ui.theme.CiyatoGold
import com.ciyato.launcher.ui.theme.CiyatoMuted
import com.ciyato.launcher.ui.theme.CiyatoSec
import com.ciyato.launcher.ui.theme.CiyatoWhite
import com.ciyato.launcher.viewmodel.LauncherViewModel
import java.util.Calendar

/**
 * The internal Ciyato Home. It intentionally avoids a second feature dashboard:
 * destination navigation belongs to the bottom bar and every status shown here
 * is derived from local launcher state.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DashboardScreen(viewModel: LauncherViewModel) {
    val apps by viewModel.apps.collectAsState()
    val selectedFolderUri by viewModel.filesRootUri.collectAsState()
    val greeting = remember {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Ciyato", color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                        Text("On-device organizer", color = CiyatoMuted, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CiyatoBg),
            )
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = padding.calculateTopPadding() + 16.dp,
                bottom = padding.calculateBottomPadding() + 28.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Column {
                    Text(greeting, color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Your Ciyato setup", color = CiyatoSec, fontSize = 14.sp)
                }
            }
            item {
                SummaryCard(
                    icon = Icons.Default.Apps,
                    title = "App Library",
                    value = "${apps.size} installed apps",
                    detail = "Classification and layout changes stay on this device.",
                )
            }
            item {
                SummaryCard(
                    icon = Icons.Default.FolderOpen,
                    title = "Files access",
                    value = if (selectedFolderUri.isBlank()) "No folder selected" else "Selected folder connected",
                    detail = if (selectedFolderUri.isBlank()) {
                        "Choose a folder in Files when you are ready."
                    } else {
                        "Only the folder you selected is available to Ciyato."
                    },
                )
            }
            item {
                SummaryCard(
                    icon = Icons.Default.Shield,
                    title = "Privacy",
                    value = "You remain in control",
                    detail = "Permissions, selected media and search data can be reviewed in Settings.",
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    icon: ImageVector,
    title: String,
    value: String,
    detail: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(com.ciyato.launcher.ui.theme.CiyatoShapes.medium)
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoBorder, com.ciyato.launcher.ui.theme.CiyatoShapes.medium)
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(42.dp)
                .clip(com.ciyato.launcher.ui.theme.CiyatoShapes.small)
                .background(CiyatoBgEl2),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = CiyatoGold, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = CiyatoSec, fontSize = 12.sp)
            Spacer(Modifier.height(3.dp))
            Text(value, color = CiyatoWhite, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Spacer(Modifier.height(6.dp))
            Text(detail, color = CiyatoMuted, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}
