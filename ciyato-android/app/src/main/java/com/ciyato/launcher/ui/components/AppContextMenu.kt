package com.ciyato.launcher.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ciyato.launcher.data.InstalledApp
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel
import com.ciyato.launcher.viewmodel.isPinned
import com.ciyato.launcher.viewmodel.isHidden
import com.ciyato.launcher.viewmodel.pinApp
import com.ciyato.launcher.viewmodel.unpinApp
import com.ciyato.launcher.viewmodel.hideApp
import com.ciyato.launcher.viewmodel.unhideApp

/**
 * AppContextMenu — Suggestion #16
 * Long-press context menu for app icons: uninstall, hide, info, add shortcut.
 */

sealed class ContextAction {
    object OpenApp : ContextAction()
    object AppInfo : ContextAction()
    object Uninstall : ContextAction()
    object Hide : ContextAction()
    object AddToFocus : ContextAction()
    object PinToDock : ContextAction()
}

@Composable
fun AppContextMenu(
    app: InstalledApp,
    viewModel: LauncherViewModel,
    onDismiss: () -> Unit,
    onAction: (ContextAction) -> Unit = {},
) {
    val context = LocalContext.current
    val isPinned by remember { derivedStateOf { viewModel.isPinned(app) } }
    val isHidden by remember { derivedStateOf { viewModel.isHidden(app) } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(CiyatoBgEl)
                    .border(1.dp, CiyatoBorder, RoundedCornerShape(24.dp))
                    .clickable(enabled = false, onClick = {}),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // App header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CiyatoBgEl2)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    AppIconView(app = app, size = 60.dp)
                    Text(app.label, color = CiyatoWhite, fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp)
                    Text(app.packageName.substringAfterLast('.'), color = CiyatoMuted,
                        fontSize = 12.sp)
                }

                HorizontalDivider(color = CiyatoBorder)

                // Action items
                val actions = buildList {
                    add(ContextMenuItem(
                        icon = Icons.Default.OpenInNew,
                        label = "Open",
                        color = CiyatoWhite,
                        action = {
                            viewModel.launchApp(app)
                            onDismiss()
                        }
                    ))
                    add(ContextMenuItem(
                        icon = if (isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                        label = if (isPinned) "Unpin from Dock" else "Pin to Dock",
                        color = CiyatoGold,
                        action = {
                            if (isPinned) viewModel.unpinApp(app) else viewModel.pinApp(app)
                            onDismiss()
                        }
                    ))
                    add(ContextMenuItem(
                        icon = Icons.Default.VisibilityOff,
                        label = if (isHidden) "Unhide App" else "Hide App",
                        color = CiyatoSec,
                        action = {
                            if (isHidden) viewModel.unhideApp(app) else viewModel.hideApp(app)
                            onDismiss()
                        }
                    ))
                    add(ContextMenuItem(
                        icon = Icons.Default.Timer,
                        label = "Add to Focus Block",
                        color = Color(0xFF7DB7FF),
                        action = {
                            onAction(ContextAction.AddToFocus)
                            onDismiss()
                        }
                    ))
                    add(ContextMenuItem(
                        icon = Icons.Default.Info,
                        label = "App Info",
                        color = CiyatoSec,
                        action = {
                            openAppInfo(context, app.packageName)
                            onDismiss()
                        }
                    ))
                    add(ContextMenuItem(
                        icon = Icons.Default.Delete,
                        label = "Uninstall",
                        color = Color(0xFFEF4444),
                        action = {
                            uninstallApp(context, app.packageName)
                            onDismiss()
                        }
                    ))
                }

                actions.forEach { item ->
                    ContextMenuRow(item = item)
                    if (item != actions.last()) {
                        HorizontalDivider(
                            color = CiyatoBorder,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

private data class ContextMenuItem(
    val icon: ImageVector,
    val label: String,
    val color: Color,
    val action: () -> Unit,
)

@Composable
private fun ContextMenuRow(item: ContextMenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.action)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(item.icon, contentDescription = item.label, tint = item.color, modifier = Modifier.size(20.dp))
        Text(item.label, color = item.color, fontSize = 15.sp, fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f))
    }
}

private fun openAppInfo(context: Context, packageName: String) {
    context.startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}

private fun uninstallApp(context: Context, packageName: String) {
    context.startActivity(
        Intent(Intent.ACTION_DELETE).apply {
            data = Uri.fromParts("package", packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}
