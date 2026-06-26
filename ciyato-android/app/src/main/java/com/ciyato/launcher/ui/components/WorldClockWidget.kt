package com.ciyato.launcher.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*
import kotlinx.coroutines.delay
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * WorldClockWidget — Suggestion #56
 * Scrollable row of world clocks for popular time zones.
 */

data class WorldClock(
    val city: String,
    val zoneId: String,
    val emoji: String,
)

private val DEFAULT_CLOCKS = listOf(
    WorldClock("New York", "America/New_York", "🗽"),
    WorldClock("London", "Europe/London", "🇬🇧"),
    WorldClock("Paris", "Europe/Paris", "🗼"),
    WorldClock("Tokyo", "Asia/Tokyo", "🗾"),
    WorldClock("Dubai", "Asia/Dubai", "🏙️"),
    WorldClock("Sydney", "Australia/Sydney", "🦘"),
    WorldClock("LA", "America/Los_Angeles", "🌴"),
    WorldClock("São Paulo", "America/Sao_Paulo", "🇧🇷"),
)

private val TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")
private val DAY_FORMAT = DateTimeFormatter.ofPattern("EEE")

@Composable
fun WorldClockWidget(
    clocks: List<WorldClock> = DEFAULT_CLOCKS,
    modifier: Modifier = Modifier,
) {
    var now by remember { mutableStateOf(ZonedDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000L)
            now = ZonedDateTime.now()
        }
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        items(clocks) { clock ->
            WorldClockChip(clock = clock, now = now)
        }
    }
}

@Composable
private fun WorldClockChip(clock: WorldClock, now: ZonedDateTime) {
    val zdt = remember(now, clock.zoneId) {
        try { now.withZoneSameInstant(ZoneId.of(clock.zoneId)) }
        catch (_: Exception) { now }
    }
    val timeStr = zdt.format(TIME_FORMAT)
    val dayStr = zdt.format(DAY_FORMAT)
    val isToday = zdt.dayOfYear == now.dayOfYear

    Card(
        colors = CardDefaults.cardColors(containerColor = CiyatoBgEl),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.semantics {
            contentDescription = "${clock.city}: $dayStr $timeStr"
        },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(clock.emoji, fontSize = 18.sp)
            Text(timeStr, color = CiyatoWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(dayStr, color = if (isToday) CiyatoGold else CiyatoMuted, fontSize = 10.sp,
                fontWeight = if (isToday) FontWeight.SemiBold else FontWeight.Normal)
            Text(clock.city, color = CiyatoSec, fontSize = 10.sp)
        }
    }
}
