package com.ciyato.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.data.WeatherRepository
import com.ciyato.launcher.data.WeatherRepository.WeatherState
import com.ciyato.launcher.ui.theme.*

/**
 * Weather + Agenda widget row — live weather via Open-Meteo.
 *
 * WeatherCard now reflects [weatherState] from the ViewModel:
 *   - NoPermission / null → shows "--°" with "Enable weather" hint
 *   - Loading             → spinner
 *   - Success             → shows real temperature, condition, location
 *   - Error               → shows "!" with error hint
 *
 * onWeatherTap and onAgendaTap are required callbacks.
 */

@Composable
fun WeatherAgendaRow(
    isDense: Boolean = true,
    weatherState: WeatherState? = null,
    onWeatherTap: () -> Unit = {},
    onAgendaTap: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val height = if (isDense) 160.dp else 190.dp
    Row(
        modifier = modifier.height(height),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        WeatherCard(
            isDense      = isDense,
            weatherState = weatherState,
            onTap        = onWeatherTap,
            modifier     = Modifier.weight(1f).fillMaxHeight(),
        )
        AgendaCard(
            isDense  = isDense,
            onTap    = onAgendaTap,
            modifier = Modifier.weight(1.35f).fillMaxHeight(),
        )
    }
}

// ─── Weather Card ─────────────────────────────────────────────────────────────

@Composable
fun WeatherCard(
    isDense: Boolean,
    weatherState: WeatherState? = null,
    onTap: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val padding    = if (isDense) 14.dp else 18.dp
    val tempSize   = if (isDense) 30.sp else 36.sp
    val iconSize   = if (isDense) 28.dp else 34.dp
    val subtextSz  = if (isDense) 10.sp else 12.sp

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(22.dp))
            .clickable(onClick = onTap)
            .semantics { contentDescription = "Weather — tap for details" }
            .padding(padding),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        when (val ws = weatherState) {
            is WeatherState.Success -> WeatherCardSuccess(ws, tempSize, iconSize, subtextSz, isDense)
            is WeatherState.Loading -> WeatherCardLoading(isDense)
            else                    -> WeatherCardFallback(tempSize, iconSize, subtextSz, isDense)
        }
    }
}

@Composable
private fun WeatherCardSuccess(
    ws: WeatherState.Success,
    tempSize: androidx.compose.ui.unit.TextUnit,
    iconSize: androidx.compose.ui.unit.Dp,
    subtextSz: androidx.compose.ui.unit.TextUnit,
    isDense: Boolean,
) {
    val icon: ImageVector = when {
        ws.weatherCode == 0 && ws.isDay  -> Icons.Outlined.WbSunny
        ws.weatherCode == 0 && !ws.isDay -> Icons.Default.DarkMode
        ws.weatherCode in 1..2           -> Icons.Default.Cloud
        ws.weatherCode in 51..99         -> Icons.Default.Umbrella
        else                             -> Icons.Default.Cloud
    }
    val iconTint = when {
        ws.weatherCode == 0  -> CiyatoGoldSoft
        ws.weatherCode in 51..99 -> CiyatoBlue
        else -> CiyatoSec
    }

    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(iconSize).padding(top = 2.dp))
        Column {
            Text("${ws.tempC}°", color = CiyatoWhite, fontSize = tempSize,
                fontWeight = FontWeight.Bold, lineHeight = tempSize * 1.05f)
            Text(ws.condition, color = CiyatoSec, fontSize = subtextSz, maxLines = 1)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("Feels like ${ws.feelsLikeC}°", color = CiyatoMuted, fontSize = subtextSz)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(ws.locationName, color = CiyatoMuted, fontSize = (subtextSz.value - 1).sp, maxLines = 1)
            Box(modifier = Modifier.size(6.dp).clip(androidx.compose.foundation.shape.CircleShape).background(CiyatoGreen))
        }
    }
}

@Composable
private fun ColumnScope.WeatherCardLoading(isDense: Boolean) {
    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = CiyatoGold,
            strokeWidth = 2.dp,
            modifier = Modifier.size(if (isDense) 22.dp else 26.dp),
        )
    }
    Text("Loading…", color = CiyatoMuted, fontSize = if (isDense) 10.sp else 11.sp)
}

@Composable
private fun WeatherCardFallback(
    tempSize: androidx.compose.ui.unit.TextUnit,
    iconSize: androidx.compose.ui.unit.Dp,
    subtextSz: androidx.compose.ui.unit.TextUnit,
    isDense: Boolean,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(Icons.Outlined.WbSunny, null, tint = CiyatoGoldSoft,
            modifier = Modifier.size(iconSize).padding(top = 2.dp))
        Column {
            Text("--°", color = CiyatoWhite, fontSize = tempSize,
                fontWeight = FontWeight.Bold, lineHeight = tempSize * 1.05f)
            Text("Set weather", color = CiyatoSec, fontSize = subtextSz)
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("Allow approximate location", color = CiyatoMuted, fontSize = subtextSz)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Tap to enable", color = CiyatoBlue, fontSize = (subtextSz.value - 1).sp)
        }
    }
}

// ─── Agenda Card ──────────────────────────────────────────────────────────────

@Composable
fun AgendaCard(
    isDense: Boolean,
    onTap: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val paddingH = if (isDense) 14.dp else 18.dp
    val paddingV = if (isDense) 12.dp else 16.dp

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(CiyatoBgEl)
            .border(1.dp, CiyatoSubtleBorder, RoundedCornerShape(22.dp))
            .clickable(onClick = onTap)
            .semantics { contentDescription = "Today's agenda — tap to view all" }
            .padding(horizontal = paddingH, vertical = paddingV),
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Today", color = CiyatoWhite, fontWeight = FontWeight.SemiBold,
                fontSize = if (isDense) 14.sp else 16.sp)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(if (isDense) 22.dp else 26.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(CiyatoGlassStr),
            ) {
                Icon(Icons.Default.Add, "Add event", tint = CiyatoSec,
                    modifier = Modifier.size(if (isDense) 14.dp else 16.dp))
            }
        }

        Spacer(Modifier.height(if (isDense) 8.dp else 12.dp))

        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "No schedule connected",
                color = CiyatoWhite,
                fontSize = if (isDense) 12.sp else 13.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Tap to add or connect calendar",
                color = CiyatoMuted,
                fontSize = if (isDense) 10.sp else 11.sp,
                lineHeight = if (isDense) 14.sp else 16.sp,
            )
        }

        Text("Open", color = CiyatoBlue, fontSize = if (isDense) 11.sp else 12.sp,
            modifier = Modifier.align(Alignment.End))
    }
}
