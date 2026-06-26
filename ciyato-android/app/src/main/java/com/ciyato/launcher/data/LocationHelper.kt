package com.ciyato.launcher.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/**
 * LocationHelper — returns the device's current coarse location.
 *
 * Requires ACCESS_COARSE_LOCATION (already in Manifest) and must only
 * be called AFTER the user has explicitly granted that permission.
 *
 * Strategy (no Play Services required):
 *   1. Try NETWORK provider last-known location (fast, ~city-level accuracy).
 *   2. If unavailable, try GPS last-known.
 *   3. If still nothing, request a fresh NETWORK update and wait up to 8 s.
 *   4. Return null only if all three steps fail.
 */
object LocationHelper {

    data class LatLon(val lat: Double, val lon: Double)

    /**
     * Returns a [LatLon] or null.
     * Must be called from a coroutine; suspends for at most ~8 seconds.
     */
    suspend fun getLocation(context: Context): LatLon? {
        if (!hasPermission(context)) return null

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null

        // 1. Fast path — last known from NETWORK provider
        bestOf(
            lm.getLastKnownSafe(LocationManager.NETWORK_PROVIDER),
            lm.getLastKnownSafe(LocationManager.GPS_PROVIDER),
        )?.let { return it.toLatLon() }

        // 2. Request a fresh NETWORK fix with an 8-second timeout
        return withTimeoutOrNull(8_000L) {
            requestSingleUpdate(lm)
        }?.toLatLon()
    }

    fun hasPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    // ── Helpers ───────────────────────────────────────────────────────────────

    @Suppress("MissingPermission")
    private fun LocationManager.getLastKnownSafe(provider: String): Location? =
        runCatching { getLastKnownLocation(provider) }.getOrNull()

    private fun bestOf(vararg locations: Location?): Location? =
        locations.filterNotNull().maxByOrNull { it.time }

    private fun Location.toLatLon() = LatLon(latitude, longitude)

    @Suppress("MissingPermission")
    private suspend fun requestSingleUpdate(lm: LocationManager): Location? =
        suspendCancellableCoroutine { cont ->
            val listener = object : LocationListener {
                override fun onLocationChanged(loc: Location) {
                    lm.removeUpdates(this)
                    if (cont.isActive) cont.resume(loc)
                }
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
                override fun onProviderEnabled(provider: String)  = Unit
                override fun onProviderDisabled(provider: String) {
                    if (cont.isActive) cont.resume(null)
                }
            }

            runCatching {
                lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0L,    // minTimeMs
                    0f,    // minDistanceM
                    listener,
                )
            }.onFailure {
                if (cont.isActive) cont.resume(null)
                return@suspendCancellableCoroutine
            }

            cont.invokeOnCancellation { lm.removeUpdates(listener) }
        }
}
