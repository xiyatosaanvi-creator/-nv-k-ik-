package com.ciyato.launcher.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val CiyatoTypography = Typography(
    displayLarge  = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 32.sp, color = CiyatoWhite),
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 24.sp, color = CiyatoWhite),
    headlineMedium= TextStyle(fontWeight = FontWeight.Bold,   fontSize = 20.sp, color = CiyatoWhite),
    titleLarge    = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = CiyatoWhite),
    titleMedium   = TextStyle(fontWeight = FontWeight.Medium, fontSize = 15.sp, color = CiyatoWhite),
    bodyLarge     = TextStyle(fontWeight = FontWeight.Normal, fontSize = 15.sp, color = CiyatoWhite),
    bodyMedium    = TextStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp, color = CiyatoSec),
    labelSmall    = TextStyle(fontWeight = FontWeight.Normal, fontSize = 11.sp, color = CiyatoMuted),
)
