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
import com.ciyato.launcher.ui.theme.*

/**
 * PASS 1-2-3 — Premium bottom dock.
 *
 * Reference image: 5 real app icons in a frosted-glass pill at the very bottom.
 * - Corner radius: 28dp (pill-like, premium).
 * - Background: CiyatoBgEl2 at 0.88f alpha — not fully opaque, lets bg show through.
 * - Border: CiyatoSubtleBorder (0.06 white) — very subtle, just enough depth.
 * - Icon size: 54dp — same as reference.
 * - Horizontal padding: 20dp, vertical: 14dp.
 * - SpaceEvenly so icons are perfectly balanced.
 */
@Composable
fun BottomDock(
    dockApps: List<InstalledApp>,
    onAppTap: (InstalledApp) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 0.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(CiyatoBgEl2.copy(alpha = 0.88f))
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(28.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            dockApps.take(5).forEach { app ->
                RealAppIcon(
                    drawable = app.icon,
                    size = 54.dp,
                    modifier = Modifier.clickable { onAppTap(app) },
                )
            }
        }
    }
}
