package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    val denseLayout by viewModel.denseLayout.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    val goldAccent by viewModel.goldAccent.collectAsState()

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Theme Studio", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Customize your experience", color = CiyatoGold, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CiyatoSec)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CiyatoBg)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Live Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.verticalGradient(listOf(CiyatoBgEl, CiyatoBg)))
                    .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AutoFixHigh, null, tint = CiyatoGold, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Live Preview", color = CiyatoWhite, fontWeight = FontWeight.Bold)
                    Text("Visual changes apply instantly", color = CiyatoMuted, fontSize = 12.sp)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ThemeOption(
                        title = "Layout Density",
                        subtitle = if (denseLayout) "Dense (3-column)" else "Spacious (2-column)",
                        icon = Icons.Default.ViewCompact,
                        onClick = { viewModel.setDenseLayout(!denseLayout) }
                    )
                }

                item {
                    ThemeOption(
                        title = "Appearance",
                        subtitle = "Dark Mode Active",
                        icon = Icons.Default.Brightness4,
                        onClick = { /* Toggle logic */ }
                    )
                }

                item {
                    ThemeOption(
                        title = "Gold Accents",
                        subtitle = if (goldAccent) "Enabled" else "Disabled",
                        icon = Icons.Default.Star,
                        onClick = { viewModel.setGoldAccent(!goldAccent) }
                    )
                }

                item {
                    Button(
                        onClick = { viewModel.resetLayout() },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CiyatoBgEl2),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Reset to Default", color = CiyatoWhite)
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = CiyatoBgEl,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CiyatoSubtleBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(CiyatoBgEl2),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = CiyatoGold, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = CiyatoWhite, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = CiyatoSec, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, null, tint = CiyatoMuted)
        }
    }
}
