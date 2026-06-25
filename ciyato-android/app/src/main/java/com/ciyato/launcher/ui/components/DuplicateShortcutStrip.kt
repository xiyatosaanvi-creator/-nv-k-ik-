package com.ciyato.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.data.InstalledApp
import com.ciyato.launcher.ui.theme.*

/**
 * "Duplicate smart shortcuts" strip.
 * One app visually appears in multiple categories — same installed app, multiple contexts.
 * This is a core Ciyato differentiator.
 */
@Composable
fun DuplicateShortcutStrip(
    apps: List<InstalledApp>,
    onAppTap: (InstalledApp) -> Unit,
    onManage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Duplicate smart shortcuts",
                    color = CiyatoWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
                Text(
                    "One app, multiple places.",
                    color = CiyatoMuted,
                    fontSize = 11.sp,
                )
            }
            Text(
                "Manage",
                color = CiyatoBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onManage)
            )
        }
        Spacer(Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(apps.take(6)) { app ->
                Box(contentAlignment = Alignment.TopEnd) {
                    RealAppIcon(
                        drawable = app.icon,
                        size = 44.dp,
                        modifier = Modifier.clickable { onAppTap(app) }
                    )
                    // Small "+" badge
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(CiyatoGold)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null,
                            tint = CiyatoBg, modifier = Modifier.size(9.dp))
                    }
                }
            }
            item {
                // "+" add button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CiyatoBgEl2)
                        .border(1.dp, CiyatoBorder, RoundedCornerShape(12.dp))
                        .clickable(onClick = onManage)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add shortcut",
                        tint = CiyatoSec, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
