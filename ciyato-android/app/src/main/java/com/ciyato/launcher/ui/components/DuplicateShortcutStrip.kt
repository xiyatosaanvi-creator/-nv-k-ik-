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
import androidx.compose.material.icons.filled.AutoFixHigh
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
 * PASS 1-2-3 — Duplicate Smart Shortcuts strip.
 *
 * Reference: the generated Ciyato Home screen — gold sparkle icon + title
 * "Duplicate smart shortcuts", subtitle "One app, multiple places. Always in context."
 * Then a horizontal row of real app icons with a small "+" badge on each.
 * Card background: CiyatoGold tinted (~0.08f alpha) — matches the warm gold card
 * in the reference image. Border: CiyatoGold at ~0.20f.
 * "Learn more" link in gold at the bottom right.
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
            .clip(RoundedCornerShape(22.dp))
            .background(CiyatoGold.copy(alpha = 0.08f))
            .border(1.dp, CiyatoGold.copy(alpha = 0.22f), RoundedCornerShape(22.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        // Header row: sparkle icon + titles
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Gold sparkle badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CiyatoGold.copy(alpha = 0.18f)),
            ) {
                Icon(
                    Icons.Default.AutoFixHigh,
                    contentDescription = null,
                    tint = CiyatoGold,
                    modifier = Modifier.size(18.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Duplicate smart shortcuts",
                    color = CiyatoWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                )
                Text(
                    "One app, multiple places. Always in context.",
                    color = CiyatoSec,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Icon strip with "+" badge on each app
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(apps.take(6)) { app ->
                Box(contentAlignment = Alignment.TopEnd) {
                    RealAppIcon(
                        drawable = app.icon,
                        size = 48.dp,
                        modifier = Modifier.clickable { onAppTap(app) },
                    )
                    // Small gold "+" badge top-right
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = 2.dp, y = (-2).dp)
                            .clip(CircleShape)
                            .background(CiyatoGold)
                            .border(1.5.dp, CiyatoBg, CircleShape),
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = CiyatoBg,
                            modifier = Modifier.size(10.dp),
                        )
                    }
                }
            }

            // "+" add slot at the end
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CiyatoBgEl2)
                        .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(14.dp))
                        .clickable(onClick = onManage),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add",
                        tint = CiyatoSec, modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // "Learn more" / Manage link
        Text(
            "Learn more",
            color = CiyatoGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.End)
        )
    }
}
