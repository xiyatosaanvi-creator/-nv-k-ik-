package com.ciyato.launcher.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciyato.launcher.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class LauncherViewModel(app: Application) : AndroidViewModel(app) {

    val repo     = LauncherRepository(app)
    val settings = LauncherSettingsRepository(app)

    // ── App list ──────────────────────────────────────────────────────────────

    val apps      get() = repo.apps
    val isLoading get() = repo.isLoading

    // ── Search ────────────────────────────────────────────────────────────────

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<InstalledApp>> = searchQuery
        .combine(apps) { q, list -> if (q.isBlank()) list else repo.search(q) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setSearch(q: String) { _searchQuery.value = q }

    // ── Settings ──────────────────────────────────────────────────────────────

    val onboardingDone     = settings.onboardingDone   .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val denseLayout        = settings.denseLayout       .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val darkMode           = settings.darkMode          .stateIn(viewModelScope, SharingStarted.Eagerly, "auto")
    val goldAccent         = settings.goldAccent        .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val smartCategories    = settings.smartCategories   .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val duplicateShortcuts = settings.duplicateShortcuts.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val iconStyle          = settings.iconStyle         .stateIn(viewModelScope, SharingStarted.Eagerly, "real")

    fun setOnboardingDone()              = viewModelScope.launch { settings.setOnboardingDone(true) }
    fun setDenseLayout(v: Boolean)       = viewModelScope.launch { settings.setDenseLayout(v) }
    fun setDarkMode(v: String)           = viewModelScope.launch { settings.setDarkMode(v) }
    fun setGoldAccent(v: Boolean)        = viewModelScope.launch { settings.setGoldAccent(v) }
    fun setSmartCategories(v: Boolean)   = viewModelScope.launch { settings.setSmartCategories(v) }
    fun setDuplicateShortcuts(v: Boolean)= viewModelScope.launch { settings.setDuplicateShortcuts(v) }
    fun setIconStyle(v: String)          = viewModelScope.launch { settings.setIconStyle(v) }
    fun resetLayout()                    = viewModelScope.launch { settings.resetLayout() }

    // ── Category helpers ──────────────────────────────────────────────────────

    fun byCategory(cat: AppCategory)       = repo.byCategory(cat)
    fun multiCategoryApps()                = repo.multiCategoryApps()
    fun recentlyAdded()                    = repo.recentlyAdded()
    fun categoriesForApp(app: InstalledApp)= repo.categoriesForApp(app)

    // ── App launch ────────────────────────────────────────────────────────────

    fun launchApp(app: InstalledApp) {
        val launched = repo.launchApp(getApplication(), app)
        if (!launched) _launchError.value = "Could not open ${app.label}"
    }

    private val _launchError = MutableStateFlow<String?>(null)
    val launchError: StateFlow<String?> = _launchError.asStateFlow()
    fun clearLaunchError() { _launchError.value = null }

    fun refreshApps() = viewModelScope.launch { repo.loadApps() }

    // ── Live weather (Open-Meteo) ─────────────────────────────────────────────

    private val _weatherState = MutableStateFlow<WeatherRepository.WeatherState>(
        WeatherRepository.WeatherState.NoPermission
    )
    val weatherState: StateFlow<WeatherRepository.WeatherState> = _weatherState.asStateFlow()

    /**
     * Checks location permission, gets a GPS fix, then fetches Open-Meteo data.
     * Safe to call multiple times — shows Loading while in progress.
     */
    fun fetchWeather(context: Context) {
        if (!LocationHelper.hasPermission(context)) {
            _weatherState.value = WeatherRepository.WeatherState.NoPermission
            return
        }
        viewModelScope.launch {
            _weatherState.value = WeatherRepository.WeatherState.Loading
            val loc = LocationHelper.getLocation(context)
            _weatherState.value = if (loc != null) {
                WeatherRepository.fetchWeather(loc.lat, loc.lon)
            } else {
                WeatherRepository.WeatherState.NoLocation
            }
        }
    }

    // ── Greeting (de-duplicated from HomeScreen + DashboardScreen) ────────────

    val greeting: String by lazy {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good morning ☀️"
            hour < 17 -> "Good afternoon 🌞"
            else      -> "Good evening 🌙"
        }
    }

    // ── Init ──────────────────────────────────────────────────────────────────

    init {
        viewModelScope.launch { repo.loadApps() }
    }
}
