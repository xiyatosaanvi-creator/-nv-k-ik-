package com.ciyato.launcher.data

import android.graphics.drawable.Drawable

/**
 * Represents a single installed, launchable application.
 * Icons are kept as Drawable (the real system icon) — never faked.
 */
data class InstalledApp(
    val id: String,                   // packageName + activityName
    val label: String,
    val originalLabel: String = label,
    val packageName: String,
    val activityName: String,
    val icon: Drawable,
    val category: AppCategory,
    val secondaryCategories: List<AppCategory> = emptyList(),
    val isSystemApp: Boolean = false,
    val installTime: Long = 0L,
    val lastUpdateTime: Long = 0L,
    val customCategoryName: String? = null,
    val iconScale: Float = 1f,
    val iconRotation: Float = 0f,
    val iconAccent: String? = null,
)

enum class AppCategory(val displayName: String) {
    SUGGESTED("Suggested"),
    RECENTLY_ADDED("Recently Added"),
    WORK("Work"),
    SOCIAL("Social"),
    COMMUNICATION("Communication"),
    FINANCE("Finance"),
    CREATIVITY("Creativity"),
    UTILITIES("Utilities"),
    PRODUCTIVITY("Productivity"),
    ENTERTAINMENT("Entertainment"),
    TRAVEL("Travel"),
    SHOPPING("Shopping"),
    DAILY("Daily"),
    GAMES("Games"),
    AI("AI"),
    VIDEO_EDITING("Video Editing"),
    CONTACTS("Contacts"),
    HIDDEN("Hidden"),
    CUSTOM("Custom"),
    OTHER("Other"),
}
