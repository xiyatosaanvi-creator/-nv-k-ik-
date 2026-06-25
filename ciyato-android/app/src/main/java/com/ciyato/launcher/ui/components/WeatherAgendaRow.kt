package com.ciyato.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*

/**
 * PASS 1-2-3 — Weather + Agenda widget row.
 *
 * Reference: left card is weather (shorter, narrower).
 *            Right card is Today/Agenda (taller, wider — ratio ~1:1.3).
 * Corner radius: 22dp — premium.
 * Cards use CiyatoBgEl with CiyatoSubtleBorder.
 * Weather card: sun icon (gold-soft), big temperature, subtitle, location.
 * Agenda card: "Today" header + "+" button, gold left-bar accent per event,
 *              "View all" in blue at bottom.
 */

private val agendaItems = listOf(
    Triple("10:00 AM", "Design Sync", "60m"),
    Triple("02:30 PM", "Client Call", "45m"),
    Triple("06:00 PM", "Gym Session", "60m"),
)

@Composable
fun WeatherAgendaRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        WeatherCard(modifier = Modifier.weight(1f).fillMaxHeight())
        AgendaCard(modifier  = Modifier.weight(1.35f).fillMaxHeight())
    }
}

@Composable
fun WeatherCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(22.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // Sun + temp top block
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                Icons.Outlined.WbSunny,
                contentDescription = "Weather",
                tint = CiyatoGoldSoft,
                modifier = Modifier.size(30.dp).padding(top = 2.dp),
            )
            Column {
                Text(
                    "24°",
                    color = CiyatoWhite,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                )
                Text(
                    "Partly sunny",
                    color = CiyatoSec,
                    fontSize = 12.sp,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Bottom location block
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Feels like 26°", color = CiyatoMuted, fontSize = 11.sp)
            Text("New York  ·  AQI 42", color = CiyatoMuted, fontSize = 10.sp)
        }
    }
}

@Composable
fun AgendaCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(22.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        // Header row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "Today",
                color = CiyatoWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(CiyatoGlassStr),
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add event",
                    tint = CiyatoSec,
                    modifier = Modifier.size(14.dp),
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Agenda items
        agendaItems.forEach { (time, event, dur) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Gold left bar
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(30.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(CiyatoGold),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(time, color = CiyatoMuted, fontSize = 10.sp, lineHeight = 13.sp)
                    Text(event, color = CiyatoWhite, fontSize = 12.sp,
                        fontWeight = FontWeight.Medium, lineHeight = 15.sp)
                }
                Text(dur, color = CiyatoMuted, fontSize = 10.sp)
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            "View all",
            color = CiyatoBlue,
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.End),
        )
    }
}
