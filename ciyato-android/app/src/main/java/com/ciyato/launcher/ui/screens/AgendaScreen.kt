package com.ciyato.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

private data class AgendaEvent(
    val time: String,
    val title: String,
    val duration: String,
    val color: Color,
    val isUpcoming: Boolean = false,
)

private val todayEvents = listOf(
    AgendaEvent("10:00 AM", "Design Sync", "60 min", Color(0xFF7DB7FF)),
    AgendaEvent("02:30 PM", "Client Call", "45 min", CiyatoGold),
    AgendaEvent("06:00 PM", "Gym Session", "60 min", Color(0xFF4CAF50)),
)

private val upcomingEvents = listOf(
    AgendaEvent("Tomorrow · 9:00 AM", "Quarterly Review", "90 min", Color(0xFFFF6B8C), isUpcoming = true),
    AgendaEvent("Tomorrow · 3:00 PM", "Product Demo", "60 min", Color(0xFF9C6AFF), isUpcoming = true),
    AgendaEvent("Fri · 11:00 AM", "Team Standup", "30 min", CiyatoGold, isUpcoming = true),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(onBack: () -> Unit) {
    val dateStr = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    Scaffold(
        containerColor = CiyatoBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Agenda", color = CiyatoWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(dateStr, color = CiyatoSec, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CiyatoSec)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Add, contentDescription = "Add event", tint = CiyatoGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CiyatoBg),
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to CiyatoBgEl2,
                            0.18f to CiyatoBg,
                            1f to CiyatoBg,
                        )
                    )
                )
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = 32.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                // Today section
                item {
                    AgendaSectionHeader("Today", todayEvents.size)
                }

                items(todayEvents) { event ->
                    AgendaEventRow(event = event)
                }

                // Upcoming section
                item {
                    AgendaSectionHeader("Upcoming", upcomingEvents.size, modifier = Modifier.padding(top = 8.dp))
                }

                items(upcomingEvents) { event ->
                    AgendaEventRow(event = event)
                }

                // Calendar permission CTA
                item {
                    CalendarPermissionCard()
                }

                // Add event placeholder
                item {
                    AddEventPlaceholder()
                }
            }
        }
    }
}

@Composable
private fun AgendaSectionHeader(title: String, count: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = title,
            color = CiyatoWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(CiyatoGold.copy(alpha = 0.18f)),
        ) {
            Text("$count", color = CiyatoGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AgendaEventRow(event: AgendaEvent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Color bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(event.color),
        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(event.time, color = CiyatoMuted, fontSize = 11.sp)
            Text(event.title, color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(event.color.copy(alpha = 0.14f))
                .padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            Text(event.duration, color = event.color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun CalendarPermissionCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CiyatoGold.copy(alpha = 0.06f))
            .border(1.dp, CiyatoGold.copy(alpha = 0.16f), RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = CiyatoGold, modifier = Modifier.size(18.dp))
            Text("Connect your real calendar", color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
        Text(
            "The events above are sample agenda items. Tap below to connect your device calendar (coming in the next update).",
            color = CiyatoSec,
            fontSize = 12.sp,
            lineHeight = 18.sp,
        )
        OutlinedButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CiyatoGold),
            border = androidx.compose.foundation.BorderStroke(1.dp, CiyatoGold.copy(alpha = 0.4f)),
        ) {
            Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Enable Calendar — coming soon", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun AddEventPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(CiyatoBgEl2)
                .border(1.dp, CiyatoSubtleBorder, CircleShape),
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = CiyatoSec, modifier = Modifier.size(16.dp))
        }
        Text("Add manual item", color = CiyatoMuted, fontSize = 14.sp)
    }
}
