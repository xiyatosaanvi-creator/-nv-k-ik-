package com.ciyato.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CiyatoDarkColorScheme = darkColorScheme(
    primary          = CiyatoGold,
    onPrimary        = CiyatoBg,
    secondary        = CiyatoBlue,
    onSecondary      = CiyatoBg,
    tertiary         = CiyatoGreen,
    background       = CiyatoBg,
    onBackground     = CiyatoWhite,
    surface          = CiyatoBgEl,
    onSurface        = CiyatoWhite,
    surfaceVariant   = CiyatoBgEl2,
    onSurfaceVariant = CiyatoSec,
    outline          = CiyatoBorder,
    error            = Color(0xFFEF4444),
)

@Composable
fun CiyatoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CiyatoDarkColorScheme,
        typography  = CiyatoTypography,
        content     = content,
    )
}
