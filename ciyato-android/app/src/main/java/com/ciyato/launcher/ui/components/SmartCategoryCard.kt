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
 * Premium Smart Category Card (Folder visual design).
 *
 * Visual layout:
 * - A rounded card representing the folder / box containing miniature app icons (no click intercept).
 * - The category name and app count are displayed below this card.
 * - Supports three sizes: "small", "medium", "large".
 */
@Composable
fun SmartCategoryCard(
    category: AppCategory,
    displayName: String,
    apps: List<InstalledApp>,
    onTap: () -> Unit,
    onAppTap: ((InstalledApp) -> Unit)? = null,
    modifier: Modifier = Modifier,
    tileSize: String = "medium", // "small" | "medium" | "large"
    isEditMode: Boolean = false,
    onMoveLeft: (() -> Unit)? = null,
    onMoveRight: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onToggleSize: (() -> Unit)? = null,
) {
    val boxHeight: Dp = when (tileSize) {
        "small" -> 76.dp
        "large" -> 136.dp
        else -> 100.dp // medium
    }

    val iconMiniSize: Dp = when (tileSize) {
        "small" -> 20.dp
        "large" -> 28.dp
        else -> 24.dp // medium
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        // Folder / Box container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(boxHeight)
                .clip(RoundedCornerShape(20.dp))
                .background(CiyatoBgEl)
                .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(20.dp))
                .clickable(onClick = onTap)
                .padding(if (tileSize == "small") 8.dp else 12.dp)
        ) {
            // Draw a grid of miniature app icons
            val maxVisible = when (tileSize) {
                "large" -> 6
                else -> 4
            }
            val hasOverflow = apps.size > maxVisible
            val visibleApps = if (hasOverflow) apps.take(maxVisible - 1) else apps.take(maxVisible)

            if (visibleApps.isEmpty()) {
                // Empty state indicator
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(CiyatoMuted.copy(alpha = 0.2f))
                        .align(Alignment.Center)
                )
            } else {
                val cols = 2
                val slotCount = visibleApps.size + if (hasOverflow) 1 else 0
                val rows = (slotCount + cols - 1) / cols
                Column(
                    verticalArrangement = Arrangement.spacedBy(if (tileSize == "small") 4.dp else 6.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    for (r in 0 until rows) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(if (tileSize == "small") 4.dp else 6.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (c in 0 until cols) {
                                val idx = r * cols + c
                                if (idx < visibleApps.size) {
                                    val app = visibleApps[idx]
                                    RealAppIcon(
                                        drawable = app.icon,
                                        size = iconMiniSize,
                                        cornerRadius = 6.dp,
                                        modifier = if (onAppTap != null) {
                                            Modifier.clickable { onAppTap(app) }
                                        } else Modifier
                                    )
                                } else if (hasOverflow && idx == visibleApps.size) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(iconMiniSize)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CiyatoGold.copy(alpha = 0.18f))
                                            .border(1.dp, CiyatoGold.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                            .clickable(onClick = onTap)
                                    ) {
                                        Text(
                                            "+${apps.size - visibleApps.size}",
                                            color = CiyatoGold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Edit overlays when in Edit Mode
            if (isEditMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CiyatoBg.copy(alpha = 0.65f))
                ) {
                    // Actions row
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onMoveLeft != null) {
                            Text(
                                "◀",
                                color = CiyatoWhite,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clickable(onClick = onMoveLeft)
                                    .padding(4.dp)
                            )
                        }
                        Text(
                            tileSize.first().uppercase(),
                            color = CiyatoGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable(onClick = onToggleSize ?: {})
                                .border(1.dp, CiyatoGold, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        if (onMoveRight != null) {
                            Text(
                                "▶",
                                color = CiyatoWhite,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clickable(onClick = onMoveRight)
                                    .padding(4.dp)
                            )
                        }
                    }

                    // Delete X icon on top-right
                    if (onDelete != null) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                                .clickable(onClick = onDelete)
                        ) {
                            Text("✕", color = CiyatoRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        // Name and count below the folder container
        Text(
            text = displayName,
            color = CiyatoWhite,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            maxLines = 1
        )
        Text(
            text = "${apps.size} apps",
            color = CiyatoMuted,
            fontSize = 10.sp
        )
    }
}
