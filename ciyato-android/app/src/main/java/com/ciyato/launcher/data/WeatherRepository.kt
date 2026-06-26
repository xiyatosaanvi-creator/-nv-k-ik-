package com.ciyato.launcher.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

/**
 * WeatherRepository — live weather via Open-Meteo + Nominatim.
 *
 *  ▸ Open-Meteo   https://open-meteo.com  — free, no API key required
 *  ▸ Nominatim    https://nominatim.org   — free OSM reverse-geocoding
 *
 * All calls are made on Dispatchers.IO.
 * JSON is parsed with org.json (bundled with Android — no extra dep).
 *
 * WeatherCode → human-readable condition is mapped by [weatherCodeToCondition].
 */
object WeatherRepository {

    // ── Public sealed result type ─────────────────────────────────────────────

    sealed class WeatherState {
        data object Loading      : WeatherState()
        data object NoPermission : WeatherState()
        data object NoLocation   : WeatherState()
        data class  Error(val message: String) : WeatherState()
        data class  Success(
            val tempC       : Int,
            val feelsLikeC  : Int,
            val highC       : Int,
            val lowC        : Int,
            val condition   : String,
            val weatherCode : Int,
            val windKmh     : Double,
            val humidity    : Int,
            val locationName: String,
            val isDay       : Boolean,
        ) : WeatherState()
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    /**
     * Fetches current conditions from Open-Meteo and city name from Nominatim.
     * Returns [WeatherState.Success] on success, [WeatherState.Error] on network/parse issues.
     */
    suspend fun fetchWeather(lat: Double, lon: Double): WeatherState = withContext(Dispatchers.IO) {
        try {
            val weatherJson = fetchWeatherJson(lat, lon)
            val cityName    = fetchCityName(lat, lon)
            parseWeather(weatherJson, cityName)
        } catch (e: Exception) {
            WeatherState.Error("${e.javaClass.simpleName}: ${e.message?.take(80)}")
        }
    }

    // ── HTTP helpers ──────────────────────────────────────────────────────────

    private fun fetchWeatherJson(lat: Double, lon: Double): JSONObject {
        val url = buildString {
            append("https://api.open-meteo.com/v1/forecast")
            append("?latitude=$lat&longitude=$lon")
            append("&current=temperature_2m,apparent_temperature,weather_code")
            append(",wind_speed_10m,relative_humidity_2m,is_day")
            append("&daily=temperature_2m_max,temperature_2m_min")
            append("&timezone=auto&forecast_days=1")
        }
        return JSONObject(fetchString(url))
    }

    private fun fetchCityName(lat: Double, lon: Double): String {
        return try {
            val url = "https://nominatim.openstreetmap.org/reverse" +
                    "?format=json&lat=$lat&lon=$lon&zoom=10"
            val json    = JSONObject(fetchString(url, extraHeaders = mapOf(
                "User-Agent" to "Ciyato Launcher/1.0 (Android)"
            )))
            val address = json.optJSONObject("address")
            address?.optString("city")?.takeIf { it.isNotBlank() }
                ?: address?.optString("town")?.takeIf { it.isNotBlank() }
                ?: address?.optString("village")?.takeIf { it.isNotBlank() }
                ?: address?.optString("county")?.takeIf { it.isNotBlank() }
                ?: json.optString("display_name")?.split(",")?.firstOrNull()?.trim()
                ?: "Your Location"
        } catch (_: Exception) {
            "Your Location"
        }
    }

    private fun fetchString(
        urlStr: String,
        extraHeaders: Map<String, String> = emptyMap(),
    ): String {
        val conn = URL(urlStr).openConnection().apply {
            connectTimeout = 8_000
            readTimeout    = 8_000
            extraHeaders.forEach { (k, v) -> setRequestProperty(k, v) }
        }
        return conn.getInputStream().bufferedReader().readText()
    }

    // ── JSON parsing ──────────────────────────────────────────────────────────

    private fun parseWeather(json: JSONObject, cityName: String): WeatherState {
        val current = json.getJSONObject("current")
        val daily   = json.getJSONObject("daily")

        val tempC      = current.getDouble("temperature_2m").toInt()
        val feelsLike  = current.getDouble("apparent_temperature").toInt()
        val weatherCode= current.getInt("weather_code")
        val windKmh    = current.getDouble("wind_speed_10m")
        val humidity   = current.getInt("relative_humidity_2m")
        val isDay      = current.optInt("is_day", 1) == 1

        val maxTemps   = daily.getJSONArray("temperature_2m_max")
        val minTemps   = daily.getJSONArray("temperature_2m_min")
        val highC      = if (maxTemps.length() > 0) maxTemps.getDouble(0).toInt() else tempC + 3
        val lowC       = if (minTemps.length() > 0) minTemps.getDouble(0).toInt() else tempC - 5

        return WeatherState.Success(
            tempC        = tempC,
            feelsLikeC   = feelsLike,
            highC        = highC,
            lowC         = lowC,
            condition    = weatherCodeToCondition(weatherCode),
            weatherCode  = weatherCode,
            windKmh      = windKmh,
            humidity     = humidity,
            locationName = cityName,
            isDay        = isDay,
        )
    }

    // ── WMO weather code → human label ───────────────────────────────────────
    // Full WMO 4677 interpretation table
    fun weatherCodeToCondition(code: Int): String = when (code) {
        0              -> "Clear Sky"
        1              -> "Mainly Clear"
        2              -> "Partly Cloudy"
        3              -> "Overcast"
        45             -> "Foggy"
        48             -> "Icy Fog"
        51             -> "Light Drizzle"
        53             -> "Drizzle"
        55             -> "Heavy Drizzle"
        56             -> "Freezing Drizzle"
        57             -> "Heavy Freezing Drizzle"
        61             -> "Light Rain"
        63             -> "Rain"
        65             -> "Heavy Rain"
        66             -> "Freezing Rain"
        67             -> "Heavy Freezing Rain"
        71             -> "Light Snow"
        73             -> "Snow"
        75             -> "Heavy Snow"
        77             -> "Snow Grains"
        80             -> "Light Showers"
        81             -> "Showers"
        82             -> "Heavy Showers"
        85             -> "Snow Showers"
        86             -> "Heavy Snow Showers"
        95             -> "Thunderstorm"
        96             -> "Thunderstorm w/ Hail"
        99             -> "Severe Thunderstorm"
        else           -> "Unknown"
    }

    /** Returns a Material icon name string that callers can map to an actual icon. */
    fun weatherIcon(code: Int, isDay: Boolean): String = when {
        code == 0  && isDay  -> "wb_sunny"
        code == 0  && !isDay -> "nights_stay"
        code in 1..2         -> "partly_cloudy_day"
        code == 3            -> "cloud"
        code in 45..48       -> "foggy"
        code in 51..67       -> "grain"          // drizzle / rain
        code in 71..77       -> "ac_unit"        // snow
        code in 80..82       -> "umbrella"
        code in 85..86       -> "cloudy_snowing"
        code in 95..99       -> "thunderstorm"
        else                 -> "cloud"
    }

    /** Celsius → Fahrenheit helper exposed for display. */
    fun cToF(c: Int): Int = (c * 9 / 5) + 32
}
