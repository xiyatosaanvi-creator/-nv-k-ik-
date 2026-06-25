package com.ciyato.launcher.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.ciyato.launcher.data.InstalledApp
import com.ciyato.launcher.ui.theme.CiyatoMuted
import com.ciyato.launcher.ui.theme.CiyatoSec

/**
 * Displays the REAL installed-app icon from the device.
 * Never uses fake/placeholder icons for real apps.
 * Falls back to a muted placeholder only if the icon is null (should not happen in practice).
 */
@Composable
fun RealAppIcon(
    drawable: Drawable,
    size: Dp = 52.dp,
    modifier: Modifier = Modifier,
) {
    val bmp = drawable.toBitmap(size.value.toInt() * 2, size.value.toInt() * 2)
    Image(
        bitmap = bmp.asImageBitmap(),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
    )
}

/**
 * Full app icon + label tile used in the app grid.
 */
@Composable
fun AppIconTile(
    app: InstalledApp,
    iconSize: Dp = 52.dp,
    showLabel: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(4.dp),
    ) {
        RealAppIcon(drawable = app.icon, size = iconSize)
        if (showLabel) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = app.label,
                color = CiyatoSec,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}
