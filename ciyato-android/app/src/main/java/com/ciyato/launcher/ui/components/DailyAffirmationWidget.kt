package com.ciyato.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciyato.launcher.ui.theme.*
import java.util.Calendar

/**
 * DailyAffirmationWidget — Suggestion #58
 * Rotates through motivational quotes daily, personalized by time of day.
 */

private val MORNING_AFFIRMATIONS = listOf(
    "Today is a gift. Open it with gratitude.",
    "You have everything you need within you.",
    "Small steps every day lead to big change.",
    "Show up. Do the work. Be proud.",
    "Progress, not perfection.",
)

private val AFTERNOON_AFFIRMATIONS = listOf(
    "Stay focused — the finish line is closer than you think.",
    "You've handled harder things than this.",
    "Keep going. You're doing great.",
    "Every hour of effort compounds.",
    "One task at a time, one moment at a time.",
)

private val EVENING_AFFIRMATIONS = listOf(
    "You earned today's rest. Take it.",
    "Tomorrow is a fresh page.",
    "Reflect. Recharge. Return stronger.",
    "What you did today matters.",
    "Growth happens in the quiet moments.",
)

private fun todayAffirmation(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    val list = when (hour) {
        in 5..11 -> MORNING_AFFIRMATIONS
        in 12..17 -> AFTERNOON_AFFIRMATIONS
        else -> EVENING_AFFIRMATIONS
    }
    return list[dayOfYear % list.size]
}

@Composable
fun DailyAffirmationWidget(modifier: Modifier = Modifier) {
    var affirmation by remember { mutableStateOf(todayAffirmation()) }
    var tapCount by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1A1200), Color(0xFF2D1F00), Color(0xFF1A1200))
                )
            )
            .semantics { contentDescription = "Daily affirmation: $affirmation" }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(Icons.Default.AutoAwesome, null, tint = CiyatoGold, modifier = Modifier.size(22.dp))

            Text(
                "\"$affirmation\"",
                color = CiyatoWhite,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Daily Affirmation", color = CiyatoGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                IconButton(
                    onClick = {
                        tapCount++
                        affirmation = MORNING_AFFIRMATIONS[(MORNING_AFFIRMATIONS.indices).random()]
                    },
                    modifier = Modifier.size(28.dp),
                ) {
                    Icon(Icons.Default.Refresh, "New affirmation", tint = CiyatoGold,
                        modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
