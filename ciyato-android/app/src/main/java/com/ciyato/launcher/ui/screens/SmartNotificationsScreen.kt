package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*
import com.ciyato.launcher.viewmodel.LauncherViewModel

/**
 * SmartNotificationsScreen — Suggestion #29
 * Smart notification grouping and priority ranking.
 * Groups notifications by urgency level and app category.
 */

data class NotificationGroup(
    val label: String,
    val emoji: String,
    val priority: NotificationPriority,
    val notifications: List<MockNotification>,
)

data class MockNotification(
    val app: String,
    val title: String,
    val body: String,
    val timeAgo: String,
    val category: String,
)

enum class NotificationPriority { URGENT, IMPORTANT, INFORMATIONAL, SILENT }

private val MOCK_GROUPS = listOf(
    NotificationGroup(
        label = "Needs Your Attention",
        emoji = "🔴",
        priority = NotificationPriority.URGENT,
        notifications = listOf(
            MockNotification("Authenticator", "Sign-in attempt", "A login was attempted from Chrome on Windows", "now", "Security"),
            MockNotification("Bank", "Transaction alert", "₹12,500 debited from your account", "2 min ago", "Finance"),
        )
    ),
    NotificationGroup(
        label = "Messages",
        emoji = "💬",
        priority = NotificationPriority.IMPORTANT,
        notifications = listOf(
            MockNotification("WhatsApp", "Arjun: Hey are you free tonight?", "3 messages from 2 chats", "5 min ago", "Social"),
            MockNotification("Telegram", "Work Group", "Meeting moved to 3pm", "12 min ago", "Work"),
        )
    ),
    NotificationGroup(
        label = "Updates",
        emoji = "📦",
        priority = NotificationPriority.INFORMATIONAL,
        notifications = listOf(
            MockNotification("Play Store", "3 app updates available", "Tap to update Ciyato, Chrome, Maps", "1h ago", "System"),
            MockNotification("Drive", "Backup complete", "Your photos have been backed up", "2h ago", "Productivity"),
        )
    ),
    NotificationGroup(
        label = "Muted",
        emoji = "🔕",
        priority = NotificationPriority.SILENT,
        notifications = listOf(
            MockNotification("Twitter", "10 new notifications", "Trending and mentions", "3h ago", "Social"),
            MockNotification("News", "Breaking: Global markets rally", "Financial news today", "4h ago", "News"),
        )
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartNotificationsScreen(
    viewModel: LauncherViewModel,
    onBack: () -> Unit,
) {
    var expandedGroup by remember { mutableStateOf<NotificationPriority?>(NotificationPriority.URGENT) }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = { Text("Smart Notifications", color = CiyatoWhite, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = CiyatoWhite)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.DoneAll, "Clear all", tint = CiyatoSec)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CiyatoBg),
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text("Ciyato groups your notifications by priority and type, so you never miss what matters.",
                    color = CiyatoMuted, fontSize = 13.sp, lineHeight = 18.sp)
            }

            MOCK_GROUPS.forEach { group ->
                item(key = group.priority.name) {
                    val isExpanded = expandedGroup == group.priority
                    val priorityColor = when (group.priority) {
                        NotificationPriority.URGENT -> Color(0xFFEF4444)
                        NotificationPriority.IMPORTANT -> CiyatoGold
                        NotificationPriority.INFORMATIONAL -> Color(0xFF7DB7FF)
                        NotificationPriority.SILENT -> CiyatoMuted
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CiyatoBgEl),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isExpanded) priorityColor.copy(alpha = 0.06f) else Color.Transparent)
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Text(group.emoji, fontSize = 20.sp)
                                    Column {
                                        Text(group.label, color = CiyatoWhite, fontWeight = FontWeight.SemiBold)
                                        Text("${group.notifications.size} notifications", color = CiyatoMuted, fontSize = 12.sp)
                                    }
                                }
                                IconButton(onClick = {
                                    expandedGroup = if (isExpanded) null else group.priority
                                }) {
                                    Icon(
                                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        null, tint = CiyatoSec,
                                    )
                                }
                            }

                            if (isExpanded) {
                                group.notifications.forEachIndexed { i, notif ->
                                    if (i > 0) HorizontalDivider(color = CiyatoBorder, thickness = 0.5.dp,
                                        modifier = Modifier.padding(horizontal = 14.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.Top,
                                    ) {
                                        Box(
                                            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(9.dp))
                                                .background(priorityColor.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(notif.app.first().toString(), color = priorityColor,
                                                fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(notif.app, color = CiyatoMuted, fontSize = 11.sp)
                                            Text(notif.title, color = CiyatoWhite, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                            Text(notif.body, color = CiyatoSec, fontSize = 12.sp, maxLines = 2,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                                        }
                                        Text(notif.timeAgo, color = CiyatoMuted, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
