package com.ciyato.launcher.data

import java.util.Calendar

/**
 * SeasonalThemeManager — Suggestion #98
 * Returns a ThemePreset automatically based on the current date.
 * Covers: Winter/New Year, Valentine's, Spring, Summer, Halloween, Thanksgiving, Christmas.
 */
object SeasonalThemeManager {

    data class SeasonalTheme(
        val name: String,
        val emoji: String,
        val darkMode: String,
        val goldAccent: Boolean,
        val iconShape: String,
        val description: String,
    )

    fun getCurrentSeasonalTheme(): SeasonalTheme? {
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH) + 1 // 1-indexed
        val day   = cal.get(Calendar.DAY_OF_MONTH)

        return when {
            month == 12 && day >= 20 || month == 1 && day <= 5 ->
                SeasonalTheme("Winter Glow", "❄️", "dark", true, "circle", "Cozy winter nights with warm gold accents")
            month == 1 && day in 1..15 ->
                SeasonalTheme("New Year", "🎆", "dark", true, "squircle", "Start fresh — bold and bright")
            month == 2 && day in 10..18 ->
                SeasonalTheme("Valentine's", "💝", "dark", false, "circle", "Soft rose tones and rounded shapes")
            month in 3..5 ->
                SeasonalTheme("Spring Fresh", "🌸", "light", false, "squircle", "Light and airy with pastel vibes")
            month in 6..8 ->
                SeasonalTheme("Summer Vibes", "☀️", "light", true, "rounded_rect", "Bright and energetic summer mode")
            month == 9 ->
                SeasonalTheme("Back to Work", "📚", "auto", false, "squircle", "Focused and professional September")
            month == 10 && day >= 25 || month == 10 ->
                SeasonalTheme("Halloween", "🎃", "dark", false, "square", "Dark and mysterious spooky mode")
            month == 11 && day in 20..30 ->
                SeasonalTheme("Thanksgiving", "🦃", "dark", true, "rounded_rect", "Warm amber harvest tones")
            month == 12 ->
                SeasonalTheme("Holiday Spirit", "🎄", "dark", true, "circle", "Festive gold and green magic")
            else -> null
        }
    }

    /** Whether a seasonal theme should auto-apply today. */
    fun shouldAutoApply(): Boolean = getCurrentSeasonalTheme() != null

    fun toThemePreset(seasonal: SeasonalTheme): ThemePresetExporter.ThemePreset =
        ThemePresetExporter.ThemePreset(
            name          = "${seasonal.emoji} ${seasonal.name}",
            darkMode      = seasonal.darkMode,
            goldAccent    = seasonal.goldAccent,
            iconShape     = seasonal.iconShape,
            font          = "inter",
            wallpaperBlur = if (seasonal.darkMode == "dark") 4 else 0,
            gridColumns   = 4,
        )
}
