package com.ciyato.launcher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciyato.launcher.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LauncherViewModel(app: Application) : AndroidViewModel(app) {

    val repo     = LauncherRepository(app)
    val settings = LauncherSettingsRepository(app)

    val apps      get() = repo.apps
    val isLoading get() = repo.isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<InstalledApp>> = searchQuery
        .combine(apps) { q, list -> if (q.isBlank()) list else repo.search(q) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val onboardingDone    = settings.onboardingDone.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val denseLayout       = settings.denseLayout.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val darkMode          = settings.darkMode.stateIn(viewModelScope, SharingStarted.Eagerly, "auto")
    val goldAccent        = settings.goldAccent.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val smartCategories   = settings.smartCategories.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val duplicateShortcuts= settings.duplicateShortcuts.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val iconStyle         = settings.iconStyle.stateIn(viewModelScope, SharingStarted.Eagerly, "real")

    init {
        viewModelScope.launch { repo.loadApps() }
    }

    fun setSearch(q: String) { _searchQuery.value = q }

    fun byCategory(cat: AppCategory) = repo.byCategory(cat)
    fun multiCategoryApps() = repo.multiCategoryApps()
    fun recentlyAdded() = repo.recentlyAdded()

    fun launchApp(app: InstalledApp) {
        val launched = repo.launchApp(getApplication(), app)
        // If launch fails, a toast/snackbar is shown by the UI layer
        if (!launched) _launchError.value = "Could not open ${app.label}"
    }

    private val _launchError = MutableStateFlow<String?>(null)
    val launchError: StateFlow<String?> = _launchError.asStateFlow()
    fun clearLaunchError() { _launchError.value = null }

    fun setOnboardingDone() = viewModelScope.launch { settings.setOnboardingDone(true) }
    fun setDenseLayout(v: Boolean) = viewModelScope.launch { settings.setDenseLayout(v) }
    fun setDarkMode(v: String)     = viewModelScope.launch { settings.setDarkMode(v) }
    fun setGoldAccent(v: Boolean)  = viewModelScope.launch { settings.setGoldAccent(v) }
    fun setSmartCategories(v: Boolean)   = viewModelScope.launch { settings.setSmartCategories(v) }
    fun setDuplicateShortcuts(v: Boolean)= viewModelScope.launch { settings.setDuplicateShortcuts(v) }
    fun setIconStyle(v: String)    = viewModelScope.launch { settings.setIconStyle(v) }
    fun resetLayout()              = viewModelScope.launch { settings.resetLayout() }

    fun refreshApps() = viewModelScope.launch { repo.loadApps() }
}
