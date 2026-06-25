package com.ciyato.launcher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("ciyato_settings")

/** Persists user preferences locally via DataStore. No cloud sync. */
class LauncherSettingsRepository(private val context: Context) {

    companion object {
        val KEY_ONBOARDING_DONE   = booleanPreferencesKey("onboarding_done")
        val KEY_DENSE_LAYOUT      = booleanPreferencesKey("dense_layout")
        val KEY_DARK_MODE         = stringPreferencesKey("dark_mode")   // "auto" | "dark" | "light"
        val KEY_GOLD_ACCENT       = booleanPreferencesKey("gold_accent")
        val KEY_SMART_CATEGORIES  = booleanPreferencesKey("smart_categories")
        val KEY_DUPLICATE_SHORTCUTS = booleanPreferencesKey("duplicate_shortcuts")
        val KEY_ICON_STYLE        = stringPreferencesKey("icon_style")  // "real" | "rounded" | "minimal"
    }

    val onboardingDone: Flow<Boolean>  = context.dataStore.data.map { it[KEY_ONBOARDING_DONE]     ?: false }
    val denseLayout: Flow<Boolean>     = context.dataStore.data.map { it[KEY_DENSE_LAYOUT]         ?: true  }
    val darkMode: Flow<String>         = context.dataStore.data.map { it[KEY_DARK_MODE]            ?: "auto"}
    val goldAccent: Flow<Boolean>      = context.dataStore.data.map { it[KEY_GOLD_ACCENT]          ?: true  }
    val smartCategories: Flow<Boolean> = context.dataStore.data.map { it[KEY_SMART_CATEGORIES]     ?: true  }
    val duplicateShortcuts: Flow<Boolean>= context.dataStore.data.map { it[KEY_DUPLICATE_SHORTCUTS]?: true  }
    val iconStyle: Flow<String>        = context.dataStore.data.map { it[KEY_ICON_STYLE]           ?: "real"}

    suspend fun setOnboardingDone(done: Boolean) = context.dataStore.edit { it[KEY_ONBOARDING_DONE] = done }
    suspend fun setDenseLayout(dense: Boolean)   = context.dataStore.edit { it[KEY_DENSE_LAYOUT]    = dense }
    suspend fun setDarkMode(mode: String)         = context.dataStore.edit { it[KEY_DARK_MODE]       = mode  }
    suspend fun setGoldAccent(on: Boolean)        = context.dataStore.edit { it[KEY_GOLD_ACCENT]     = on    }
    suspend fun setSmartCategories(on: Boolean)   = context.dataStore.edit { it[KEY_SMART_CATEGORIES]= on    }
    suspend fun setDuplicateShortcuts(on: Boolean)= context.dataStore.edit { it[KEY_DUPLICATE_SHORTCUTS]= on }
    suspend fun setIconStyle(style: String)       = context.dataStore.edit { it[KEY_ICON_STYLE]      = style }

    suspend fun resetLayout() {
        context.dataStore.edit { prefs ->
            prefs[KEY_DENSE_LAYOUT]       = true
            prefs[KEY_GOLD_ACCENT]        = true
            prefs[KEY_SMART_CATEGORIES]   = true
            prefs[KEY_DUPLICATE_SHORTCUTS]= true
            prefs[KEY_ICON_STYLE]         = "real"
        }
    }
}
