package org.christophertwo.car.feature.car.data.repository

import android.content.Context
import android.location.LocationManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.christophertwo.car.feature.car.domain.model.UserLocation
import org.christophertwo.car.feature.car.domain.repository.LocationRepository
import kotlin.coroutines.resume

/**
 * Implementación real de [LocationRepository] para Android.
 * Usa [LocationManager] del sistema para obtener la ubicación GPS del dispositivo.
 * No depende de Google Play Services para mayor compatibilidad.
 */
class AndroidLocationRepositoryImpl(
    private val context: Context
) : LocationRepository {

    override suspend fun getCurrentLocation(): UserLocation {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Intentar obtener la última ubicación conocida de los proveedores disponibles
        val lastKnown = getLastKnownLocation(locationManager)
        if (lastKnown != null) {
            return lastKnown
        }

        // Si no hay ubicación conocida, esperar un fix GPS con timeout de 10 segundos
        val fresh = withTimeoutOrNull(10_000L) {
            requestSingleUpdate(locationManager)
        }

        return fresh ?: UserLocation(
            latitude = 0.0,
            longitude = 0.0,
            walkingMinutes = 0,
            distanceMeters = 0
        )
    }

    @Suppress("MissingPermission")
    private fun getLastKnownLocation(locationManager: LocationManager): UserLocation? {
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )
        var bestLocation: android.location.Location? = null
        for (provider in providers) {
            if (!locationManager.isProviderEnabled(provider)) continue
            val loc = runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull()
                ?: continue
            if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                bestLocation = loc
            }
        }
        return bestLocation?.toUserLocation()
    }

    @Suppress("MissingPermission")
    private suspend fun requestSingleUpdate(
        locationManager: LocationManager
    ): UserLocation? = suspendCancellableCoroutine { cont ->
        val provider = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                LocationManager.NETWORK_PROVIDER
            else -> null
        } ?: run {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }

        val listener = object : android.location.LocationListener {
            override fun onLocationChanged(location: android.location.Location) {
                locationManager.removeUpdates(this)
                if (cont.isActive) cont.resume(location.toUserLocation())
            }
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) = Unit
        }

        runCatching {
            locationManager.requestLocationUpdates(provider, 0L, 0f, listener)
        }.onFailure {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }

        cont.invokeOnCancellation { locationManager.removeUpdates(listener) }
    }

    private fun android.location.Location.toUserLocation() = UserLocation(
        latitude = latitude,
        longitude = longitude,
        walkingMinutes = 0,
        distanceMeters = 0
    )
}

