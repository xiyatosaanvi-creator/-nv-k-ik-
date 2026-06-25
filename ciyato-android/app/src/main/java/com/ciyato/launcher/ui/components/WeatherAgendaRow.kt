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
fun WeatherAgendaRow(
    isDense: Boolean = true,
    modifier: Modifier = Modifier
) {
    val height = if (isDense) 160.dp else 190.dp
    Row(
        modifier = modifier.height(height),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        WeatherCard(isDense = isDense, modifier = Modifier.weight(1f).fillMaxHeight())
        AgendaCard(isDense = isDense, modifier  = Modifier.weight(1.35f).fillMaxHeight())
    }
}

@Composable
fun WeatherCard(isDense: Boolean, modifier: Modifier = Modifier) {
    val padding = if (isDense) 16.dp else 20.dp
    val tempSize = if (isDense) 30.sp else 36.sp
    val iconSize = if (isDense) 30.dp else 36.dp

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(22.dp))
            .padding(padding),
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
                modifier = Modifier.size(iconSize).padding(top = 2.dp),
            )
            Column {
                Text(
                    "24°",
                    color = CiyatoWhite,
                    fontSize = tempSize,
                    fontWeight = FontWeight.Bold,
                    lineHeight = if (isDense) 32.sp else 38.sp,
                )
                Text(
                    "Partly sunny",
                    color = CiyatoSec,
                    fontSize = if (isDense) 12.sp else 13.sp,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Bottom location block
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Feels like 26°", color = CiyatoMuted, fontSize = if (isDense) 11.sp else 12.sp)
            Text("New York  ·  AQI 42", color = CiyatoMuted, fontSize = if (isDense) 10.sp else 11.sp)
        }
    }
}

@Composable
fun AgendaCard(isDense: Boolean, modifier: Modifier = Modifier) {
    val paddingH = if (isDense) 14.dp else 18.dp
    val paddingV = if (isDense) 12.dp else 16.dp

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(22.dp))
            .padding(horizontal = paddingH, vertical = paddingV),
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
                fontSize = if (isDense) 14.sp else 16.sp,
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(if (isDense) 22.dp else 26.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(CiyatoGlassStr),
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add event",
                    tint = CiyatoSec,
                    modifier = Modifier.size(if (isDense) 14.dp else 16.dp),
                )
            }
        }

        Spacer(Modifier.height(if (isDense) 8.dp else 12.dp))

        // Agenda items
        val itemsToShow = if (isDense) agendaItems else agendaItems + Triple("08:30 PM", "Dinner", "90m")
        Column(verticalArrangement = Arrangement.spacedBy(if (isDense) 8.dp else 12.dp)) {
            itemsToShow.forEach { (time, event, dur) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Gold left bar
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(if (isDense) 30.dp else 34.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(CiyatoGold),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(time, color = CiyatoMuted, fontSize = if (isDense) 10.sp else 11.sp)
                        Text(event, color = CiyatoWhite, fontSize = if (isDense) 12.sp else 13.sp,
                            fontWeight = FontWeight.Medium)
                    }
                    Text(dur, color = CiyatoMuted, fontSize = if (isDense) 10.sp else 11.sp)
                }
            }
        }

        Spacer(Modifier.weight(1f))
        Text(
            "View all",
            color = CiyatoBlue,
            fontSize = if (isDense) 11.sp else 12.sp,
            modifier = Modifier.align(Alignment.End),
        )
    }
}
