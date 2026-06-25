package com.ciyato.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.HorizontalDivider
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

// Mock data (no calendar/location permission required for beta)
private val agendaItems = listOf(
    Triple("10:00 AM", "Design Sync", "60m"),
    Triple("02:30 PM", "Client Call", "45m"),
    Triple("06:00 PM", "Gym Session", "60m"),
)

@Composable
fun WeatherAgendaRow(modifier: Modifier = Modifier) {
    Row(modifier = modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        // Weather card
        WeatherCard(modifier = Modifier.weight(1f).fillMaxHeight())
        // Agenda card
        AgendaCard(modifier = Modifier.weight(1.3f).fillMaxHeight())
    }
}

@Composable
fun WeatherCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.WbSunny, contentDescription = null,
                tint = CiyatoGoldSoft, modifier = Modifier.size(28.dp))
            Column {
                Text("24°", color = CiyatoWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("Partly sunny", color = CiyatoSec, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text("Feels like 26°", color = CiyatoMuted, fontSize = 11.sp)
        Text("New York  •  AQI 42 🟢", color = CiyatoMuted, fontSize = 10.sp)
    }
}

@Composable
fun AgendaCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Today", color = CiyatoWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            IconButton(onClick = {}, modifier = Modifier.size(20.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Add event",
                    tint = CiyatoSec, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(6.dp))
        agendaItems.forEachIndexed { i, (time, event, dur) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.width(3.dp).height(28.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(CiyatoGold)
                )
                Column {
                    Text(time, color = CiyatoSec, fontSize = 10.sp)
                    Text(event, color = CiyatoWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.weight(1f))
                Text(dur, color = CiyatoMuted, fontSize = 10.sp)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text("View all", color = CiyatoBlue, fontSize = 11.sp,
            modifier = Modifier.align(Alignment.End))
    }
}
