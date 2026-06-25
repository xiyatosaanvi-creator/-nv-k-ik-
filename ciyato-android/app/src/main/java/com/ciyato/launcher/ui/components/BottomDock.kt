package com.ciyato.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ciyato.launcher.data.InstalledApp
import com.ciyato.launcher.ui.theme.CiyatoBgEl2
import com.ciyato.launcher.ui.theme.CiyatoBorder

/**
 * Bottom dock showing up to 5 real installed app icons.
 * Falls back gracefully if fewer apps are available.
 */
@Composable
fun BottomDock(
    dockApps: List<InstalledApp>,
    onAppTap: (InstalledApp) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CiyatoBgEl2.copy(alpha = 0.92f))
            .border(1.dp, CiyatoBorder, RoundedCornerShape(24.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        dockApps.take(5).forEach { app ->
            RealAppIcon(
                drawable = app.icon,
                size = 52.dp,
                modifier = Modifier.clickable { onAppTap(app) }
            )
        }
    }
}
