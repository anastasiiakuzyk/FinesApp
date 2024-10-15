package ua.anastasiia.finesapp.util

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.util.Locale
import android.util.Log

object GeocoderUtil {
    private const val TIMEOUT_DURATION = 5000L
    private const val MAX_RETRIES = 3
    private const val INITIAL_DELAY = 1000L

    suspend fun getAddressFromLatLng(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String? {
        var currentDelay = INITIAL_DELAY
        repeat(MAX_RETRIES) { attempt ->
            runCatching {
                withTimeout(TIMEOUT_DURATION) {
                    Geocoder(context, Locale.getDefault()).getFromLocation(latitude, longitude, 1)
                }
            }.onSuccess { addresses ->
                return addresses?.firstOrNull()?.getAddressLine(0)
            }.onFailure { e ->
                Log.e("GeocoderUtil", "Geocoding attempt $attempt failed", e)
                if (attempt == MAX_RETRIES - 1) {
                    throw e
                }
            }
            delay(currentDelay)
            currentDelay *= 2
        }
        return null
    }

    fun getLatLngFromAddress(context: Context, location: String): LatLng? =
        Geocoder(context, Locale.getDefault())
            .getFromLocationName(location, 1)
            ?.firstOrNull()
            ?.let { LatLng(it.latitude, it.longitude) }
}