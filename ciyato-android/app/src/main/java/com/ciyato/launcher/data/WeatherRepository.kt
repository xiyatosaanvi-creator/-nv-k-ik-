package com.ciyato.launcher.data

/**
 * WeatherRepository — placeholder weather provider interface.
 *
 * In beta, no live weather API is configured. The repository always returns
 * WeatherState.NotConfigured. When a real API key is added (e.g. OpenWeatherMap),
 * implement WeatherProvider and inject it here.
 *
 * Permission flow is handled in WeatherDetailScreen — this repository only
 * provides data after the user has granted location permission.
 */
object WeatherRepository {

    sealed class WeatherState {
        data object Loading : WeatherState()
        data object NotConfigured : WeatherState()
        data object NoPermission : WeatherState()
        data class Error(val message: String) : WeatherState()
        data class Success(
            val tempC: Int,
            val condition: String,
            val locationName: String,
            val feelsLikeC: Int,
            val highC: Int,
            val lowC: Int,
            val aqiIndex: Int?,
        ) : WeatherState()
    }

    /**
     * Fetch current weather for given lat/lon.
     * Currently returns NotConfigured until a real provider is wired up.
     */
    suspend fun fetchWeather(lat: Double, lon: Double): WeatherState {
        // TODO: integrate a weather API here (e.g. OpenWeatherMap, WeatherAPI)
        // Replace this stub once an API key is added to build config / secrets.
        return WeatherState.NotConfigured
    }
}
