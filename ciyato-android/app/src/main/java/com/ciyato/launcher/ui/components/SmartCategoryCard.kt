package com.ciyato.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.ciyato.launcher.data.CustomCategoryPresentation
import com.ciyato.launcher.data.InstalledApp
import com.ciyato.launcher.ui.theme.CiyatoBg
import com.ciyato.launcher.ui.theme.CiyatoBgEl
import com.ciyato.launcher.ui.theme.CiyatoBgEl3
import com.ciyato.launcher.ui.theme.CiyatoGold
import com.ciyato.launcher.ui.theme.CiyatoMuted
import com.ciyato.launcher.ui.theme.CiyatoSec
import com.ciyato.launcher.ui.theme.CiyatoSubtleBorder
import com.ciyato.launcher.ui.theme.CiyatoWhite

/** Compact closed-category preview. Apps launch only after the category is opened. */
@Composable
fun SmartCategoryCard(
    category: AppCategory,
    displayName: String,
    apps: List<InstalledApp>,
    onTap: () -> Unit,
    customIcon: String = "folder",
    customPresentation: CustomCategoryPresentation = CustomCategoryPresentation.CARD,
    modifier: Modifier = Modifier,
    tileSize: String = "medium",
    isEditMode: Boolean = false,
    onToggleSize: (() -> Unit)? = null,
) {
    val boxHeight: Dp = when (tileSize) {
        "small" -> 76.dp
        "large" -> 136.dp
        else -> 100.dp
    }
    val iconMiniSize: Dp = when (tileSize) {
        "small" -> 20.dp
        "large" -> 28.dp
        else -> 24.dp
    }
    val isCompactGroup = category == AppCategory.CUSTOM &&
        customPresentation == CustomCategoryPresentation.GROUP
    val visibleApps = when {
        isCompactGroup -> apps.take(4)
        apps.size > 6 -> apps.take(5)
        else -> apps.take(6)
    }
    val hasOverflow = apps.size > visibleApps.size

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(vertical = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(boxHeight)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isCompactGroup) CiyatoBgEl3 else CiyatoBgEl)
                .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(20.dp))
                .clickable(onClick = onTap)
                .padding(if (tileSize == "small") 8.dp else 12.dp),
        ) {
            if (category == AppCategory.CUSTOM) {
                val icon = when (customIcon) {
                    "bookmark" -> Icons.Default.Bookmark
                    "star" -> Icons.Default.Star
                    else -> Icons.Default.Folder
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(24.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(CiyatoBgEl3)
                        .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(7.dp)),
                ) {
                    Icon(icon, contentDescription = null, tint = CiyatoSec, modifier = Modifier.size(14.dp))
                }
            }

            if (isCompactGroup && visibleApps.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.Center),
                ) {
                    visibleApps.forEach { app ->
                        RealAppIcon(
                            drawable = app.icon,
                            size = iconMiniSize,
                            cornerRadius = 6.dp,
                            scale = app.iconScale,
                            rotation = app.iconRotation,
                            accentHex = app.iconAccent,
                        )
                    }
                    if (hasOverflow) {
                        Text(
                            "+${apps.size - visibleApps.size}",
                            color = CiyatoGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            } else if (visibleApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(CiyatoMuted.copy(alpha = 0.2f))
                        .align(Alignment.Center),
                )
            } else {
                val slots = visibleApps.size + if (hasOverflow) 1 else 0
                val rows = (slots + 2) / 3
                Column(
                    verticalArrangement = Arrangement.spacedBy(if (tileSize == "small") 4.dp else 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center),
                ) {
                    repeat(rows) { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(if (tileSize == "small") 4.dp else 6.dp)) {
                            repeat(3) { column ->
                                val index = row * 3 + column
                                when {
                                    index < visibleApps.size -> {
                                        val app = visibleApps[index]
                                        RealAppIcon(
                                            drawable = app.icon,
                                            size = iconMiniSize,
                                            cornerRadius = 6.dp,
                                            scale = app.iconScale,
                                            rotation = app.iconRotation,
                                            accentHex = app.iconAccent,
                                        )
                                    }
                                    hasOverflow && index == visibleApps.size -> {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(iconMiniSize)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(CiyatoGold.copy(alpha = 0.18f))
                                                .border(1.dp, CiyatoGold.copy(alpha = 0.35f), RoundedCornerShape(6.dp)),
                                        ) {
                                            Text(
                                                "+${apps.size - visibleApps.size}",
                                                color = CiyatoGold,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isEditMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CiyatoBg.copy(alpha = 0.65f)),
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.DragIndicator,
                            contentDescription = "Long press and drag to move $displayName",
                            tint = CiyatoWhite,
                            modifier = Modifier.size(20.dp),
                        )
                        onToggleSize?.let { changeSize ->
                            IconButton(onClick = changeSize, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    Icons.Default.ZoomOutMap,
                                    contentDescription = "Change $displayName size",
                                    tint = CiyatoGold,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = displayName,
            color = CiyatoWhite,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            maxLines = 1,
        )
        Text(
            text = if (isCompactGroup) "${apps.size} apps - Group" else "${apps.size} apps - Card",
            color = CiyatoMuted,
            fontSize = 10.sp,
        )
    }
}
