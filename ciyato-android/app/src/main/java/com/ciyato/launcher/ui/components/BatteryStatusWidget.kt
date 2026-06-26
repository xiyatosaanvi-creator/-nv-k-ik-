package com.ciyato.launcher.ui.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*

/**
 * BatteryStatusWidget — Suggestion #53
 * A compact battery level and charging indicator for the home screen.
 */

data class BatteryState(
    val level: Int = 100,
    val isCharging: Boolean = false,
    val voltage: Float = 0f,
    val temperature: Float = 0f,
)

@Composable
fun rememberBatteryState(): State<BatteryState> {
    val context = LocalContext.current
    val state = remember { mutableStateOf(BatteryState()) }

    DisposableEffect(Unit) {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val pct = if (scale > 0) (level * 100 / scale) else 0
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
                val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000f
                val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
                state.value = BatteryState(pct, charging, voltage, temp)
            }
        }
        val sticky = context.registerReceiver(receiver, filter)
        sticky?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val pct = if (scale > 0) (level * 100 / scale) else 0
            val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
            state.value = BatteryState(pct, charging)
        }
        onDispose { context.unregisterReceiver(receiver) }
    }

    return state
}

@Composable
fun BatteryStatusWidget(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val battery by rememberBatteryState()
    val color = when {
        battery.isCharging -> Color(0xFF39C66A)
        battery.level <= 20 -> Color(0xFFEF4444)
        battery.level <= 50 -> Color(0xFFFF9500)
        else -> CiyatoSec
    }

    val pulse = rememberInfiniteTransition(label = "bat_pulse")
    val alpha by pulse.animateFloat(
        0.7f, 1f,
        infiniteRepeatable(tween(1200, easing = EaseInOut), RepeatMode.Reverse),
        label = "bat_alpha",
    )

    if (compact) {
        Row(
            modifier = modifier.semantics {
                contentDescription = "Battery ${battery.level}%${if (battery.isCharging) ", charging" else ""}"
            },
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "${battery.level}%",
                color = color.copy(alpha = if (battery.isCharging) alpha else 1f),
                fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            )
            if (battery.isCharging) {
                Text("⚡", fontSize = 10.sp)
            }
        }
        return
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CiyatoBgEl),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BatteryFull, null, tint = color, modifier = Modifier.size(20.dp))
                    Text("Battery", color = CiyatoWhite, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    "${battery.level}%",
                    color = color.copy(alpha = if (battery.isCharging) alpha else 1f),
                    fontWeight = FontWeight.Bold, fontSize = 16.sp,
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth().height(8.dp)
                    .clip(RoundedCornerShape(4.dp)).background(Color(0xFF1E2128))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(battery.level / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(color.copy(alpha = if (battery.isCharging) alpha else 1f))
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    if (battery.isCharging) "⚡ Charging" else "On battery",
                    color = CiyatoMuted, fontSize = 12.sp,
                )
                if (battery.temperature > 0) {
                    Text("${battery.temperature}°C", color = CiyatoMuted, fontSize = 12.sp)
                }
            }
        }
    }
}
