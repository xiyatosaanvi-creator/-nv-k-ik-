package com.ciyato.launcher.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.ciyato.launcher.ui.theme.CiyatoGold

@Composable
fun GoldGradientText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineSmall
) {
    val brush = Brush.linearGradient(
        colors = listOf(CiyatoGold, Color(0xFFFFD54F), CiyatoGold)
    )
    Text(
        text = text,
        modifier = modifier,
        style = style.copy(brush = brush)
    )
}
