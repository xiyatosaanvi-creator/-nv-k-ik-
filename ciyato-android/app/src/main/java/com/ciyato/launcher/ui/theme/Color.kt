package com.ciyato.launcher.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ─── Ciyato Dark Premium Palette ─────────────────────────────────────────────
val CiyatoBg          = Color(0xFF0B0F12)   // main launcher background (obsidian)
val CiyatoBgEl        = Color(0xFF12171B)   // elevated surfaces / cards
val CiyatoBgEl2       = Color(0xFF1A2028)   // second-level elevation
val CiyatoBgEl3       = Color(0xFF222B35)   // third-level elevation
val CiyatoActiveTab   = Color(0xFF1E2730)   // active tab state background
val CiyatoCardHover   = Color(0xFF1C2530)   // card pressed/hover state

// Glass surfaces
val CiyatoGlassCard   = Color(0x0FFFFFFF)   // rgba(255,255,255,0.06)
val CiyatoGlassStr    = Color(0x17FFFFFF)   // rgba(255,255,255,0.09)
val CiyatoGlassMed    = Color(0x1FFFFFFF)   // rgba(255,255,255,0.12)
val CiyatoGlassHeavy  = Color(0x2BFFFFFF)   // rgba(255,255,255,0.17)
val CiyatoGlassOverlay= Color(0x08FFFFFF)   // very subtle glass overlay

// Borders
val CiyatoBorder      = Color(0x1AFFFFFF)   // rgba(255,255,255,0.10)
val CiyatoSubtleBorder= Color(0x0FFFFFFF)   // rgba(255,255,255,0.06)
val CiyatoStrongBorder= Color(0x26FFFFFF)   // rgba(255,255,255,0.15)
val CiyatoDivider     = Color(0x08FFFFFF)   // list dividers

// Text
val CiyatoWhite       = Color(0xFFF4F5F6)   // primary text
val CiyatoSec         = Color(0xFFAEB4BA)   // secondary text
val CiyatoMuted       = Color(0xFF727A82)   // muted / metadata
val CiyatoDisabled    = Color(0xFF3D4550)   // disabled state

// Accents
val CiyatoGold        = Color(0xFFC6A15B)   // gold accent (primary)
val CiyatoGoldSoft    = Color(0xFFE2C37B)   // soft gold (highlight)
val CiyatoGoldDark    = Color(0xFF8A6A2E)   // deep gold (gradient end)
val CiyatoGoldDim     = Color(0x33C6A15B)   // gold at 20% opacity
val CiyatoBlue        = Color(0xFF7DB7FF)   // blue accent
val CiyatoBlueDark    = Color(0xFF4A8CF7)   // deeper blue
val CiyatoGreen       = Color(0xFF39C66A)   // green success
val CiyatoGreenDark   = Color(0xFF27A355)   // deep green
val CiyatoRed         = Color(0xFFEF5350)   // red warning/danger
val CiyatoRedDark     = Color(0xFFD32F2F)   // deep red
val CiyatoPurple      = Color(0xFF9C6AFF)   // AI/Magic accent
val CiyatoAmber       = Color(0xFFFFB347)   // warning states
val CiyatoCyan        = Color(0xFF51C7A5)   // teal / finance
val CiyatoRose        = Color(0xFFFF6B8C)   // social / entertainment
val CiyatoPeach       = Color(0xFFFF7A45)   // education / college
val CiyatoNeonBlue    = Color(0xFF00C6FF)   // neon highlight
val CiyatoNeonGold    = Color(0xFFFFD700)   // neon gold highlight
val CiyatoAIGlow      = Color(0xFF9C6AFF)   // AI action glow color

// State containers (filled backgrounds for state cards)
val CiyatoErrorContainer   = Color(0x1AEF5350)
val CiyatoSuccessContainer = Color(0x1A39C66A)
val CiyatoWarningContainer = Color(0x1AFFB347)
val CiyatoInfoContainer    = Color(0x1A7DB7FF)
val CiyatoAIContainer      = Color(0x1A9C6AFF)

// Shimmer (skeleton loading)
val CiyatoShimmer1    = Color(0xFF1A2028)
val CiyatoShimmer2    = Color(0xFF22303D)

// Category Accent Colors
val CatWork          = Color(0xFF4A8CF7)   // Work = Blue
val CatSocial        = Color(0xFFFF6B8C)   // Social = Rose
val CatFinance       = Color(0xFF51C7A5)   // Finance = Cyan
val CatEntertainment = Color(0xFF9C6AFF)   // Entertainment = Purple
val CatCreativity    = Color(0xFFFF7A45)   // Creativity = Peach
val CatUtilities     = Color(0xFF727A82)   // Utilities = Muted
val CatHealth        = Color(0xFF39C66A)   // Health = Green
val CatTravel        = Color(0xFF7DB7FF)   // Travel = Sky Blue
val CatEducation     = Color(0xFFFFB347)   // Education = Amber
val CatDaily         = Color(0xFFE2C37B)   // Daily = Soft Gold
val CatGaming        = Color(0xFFFF6B8C)   // Gaming = Rose
val CatShopping      = Color(0xFF51C7A5)   // Shopping = Cyan

// ─── Light Mode (App Drawer) ──────────────────────────────────────────────────
val CiyatoLightBg     = Color(0xFFF5F2EC)
val CiyatoLightCard   = Color(0xB8FFFFFF)
val CiyatoLightBorder = Color(0x0F000000)
val CiyatoLightText   = Color(0xFF191B1F)
val CiyatoLightSec    = Color(0xFF5F646A)

// ─── Gradient Brushes ─────────────────────────────────────────────────────────
val goldGradient = Brush.linearGradient(
    colors = listOf(CiyatoGoldSoft, CiyatoGoldDark)
)

val goldGradientVertical = Brush.verticalGradient(
    colors = listOf(CiyatoGoldSoft, CiyatoGoldDark)
)

val blueGlowGradient = Brush.radialGradient(
    colors = listOf(CiyatoBlue.copy(alpha = 0.3f), Color.Transparent)
)

val purpleAIGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF9C6AFF), Color(0xFF6A3FBF))
)

val darkCardGradient = Brush.verticalGradient(
    colors = listOf(CiyatoBgEl2, CiyatoBg)
)

val backgroundScrimGradient = Brush.verticalGradient(
    colors = listOf(Color.Transparent, CiyatoBg.copy(alpha = 0.92f))
)

val glassSurfaceGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0x17FFFFFF),
        Color(0x08FFFFFF)
    )
)

val homeScreenOverlayGradient = Brush.verticalGradient(
    colors = listOf(
        CiyatoBg.copy(alpha = 0.7f),
        Color.Transparent,
        CiyatoBg.copy(alpha = 0.85f)
    ),
    startY = 0f,
    endY = Float.POSITIVE_INFINITY
)

val aiAgentGradient = Brush.sweepGradient(
    colors = listOf(
        CiyatoPurple,
        CiyatoBlue,
        CiyatoGreen,
        CiyatoPurple
    )
)

val goldShimmerGradient = Brush.linearGradient(
    colors = listOf(
        CiyatoGold.copy(alpha = 0f),
        CiyatoGold.copy(alpha = 0.6f),
        CiyatoGold.copy(alpha = 0f)
    )
)
