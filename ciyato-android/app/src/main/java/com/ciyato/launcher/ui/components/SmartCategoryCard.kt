package com.ciyato.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.data.AppCategory
import com.ciyato.launcher.data.InstalledApp
import com.ciyato.launcher.ui.theme.*

/**
 * PASS 1-2-3 — Premium Smart Category Card.
 *
 * Reference: the generated Ciyato Home screen image.
 * Each card shows:
 *   - Category name (bold, white)
 *   - App count below it (muted, small)
 *   - Row of 3–4 real app icons (larger)
 *   - +N overflow badge if more than 3 icons
 *
 * Corner radius: 20dp (premium, not sharp, not too round).
 * Background: CiyatoBgEl (#12171B) with subtle border.
 * Icons: real installed app icons, 40dp in dense, 46dp in spacious.
 */
@Composable
fun SmartCategoryCard(
    category: AppCategory,
    apps: List<InstalledApp>,
    onTap: () -> Unit,
    onAppTap: (InstalledApp) -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 40.dp,
) {
    // Show 3 icons max in the card — cleaner, matches reference (3 icons + "+N")
    val visible  = apps.take(3)
    val overflow = (apps.size - visible.size).coerceAtLeast(0)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onTap)
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
            // Category name + count
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = category.displayName,
                    color = CiyatoWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 17.sp,
                )
                Text(
                    text = "${apps.size} apps",
                    color = CiyatoMuted,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                )
            }

            // App icons row + overflow
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                visible.forEach { app ->
                    RealAppIcon(
                        drawable = app.icon,
                        size = iconSize,
                        modifier = Modifier.clickable { onAppTap(app) },
                    )
                }
                if (overflow > 0) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(iconSize)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CiyatoBgEl2)
                            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(12.dp)),
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
