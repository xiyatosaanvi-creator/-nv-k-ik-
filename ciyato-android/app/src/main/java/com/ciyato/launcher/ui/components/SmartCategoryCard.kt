package com.ciyato.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.data.InstalledApp
import com.ciyato.launcher.ui.theme.*

/**
 * A category card showing up to 4 real app icons, category name, and app count.
 * Tapping the card navigates to the full category list.
 */
@Composable
fun SmartCategoryCard(
    category: AppCategory,
    apps: List<InstalledApp>,
    onTap: () -> Unit,
    onAppTap: (InstalledApp) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visible = apps.take(4)
    val overflow = apps.size - visible.size

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onTap)
            .padding(12.dp)
    ) {
        Column {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = category.displayName,
                    color = CiyatoWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${apps.size} apps",
                    color = CiyatoMuted,
                    fontSize = 10.sp,
                )
            }
            Spacer(Modifier.height(10.dp))
            // App icons row
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                visible.forEach { app ->
                    RealAppIcon(
                        drawable = app.icon,
                        size = 38.dp,
                        modifier = Modifier.clickable { onAppTap(app) }
                    )
                }
                if (overflow > 0) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CiyatoBgEl2)
                    ) {
                        Text(
                            text = "+$overflow",
                            color = CiyatoSec,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}
