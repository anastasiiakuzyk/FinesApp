package ua.anastasiia.finesapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await
import ua.anastasiia.finesapp.MainActivity
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.ui.screens.fine.entry.FineViewModel
import ua.anastasiia.finesapp.util.GeocoderUtil
import ua.anastasiia.finesapp.util.isLocationValid

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationMap(
    modifier: Modifier = Modifier,
    viewModel: FineViewModel,
    location: String
) {
    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        multiplePermissionState.launchMultiplePermissionRequest()
    }

    val context = LocalContext.current

    var deviceLatLng by remember {
        mutableStateOf(LatLng(0.0, 0.0))
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
    }

    LaunchedEffect(context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationResult = fusedLocationClient.lastLocation.await()
        if (locationResult != null) {
            deviceLatLng = LatLng(locationResult.latitude, locationResult.longitude)
            val addressLine = GeocoderUtil.getAddressFromLatLng(context, deviceLatLng.latitude, deviceLatLng.longitude)
            isLocationValid = true
            if (location.isNotBlank().and(location.isNotEmpty())) {
                val newLatLng = GeocoderUtil.getLatLngFromAddress(context, location)
                if (newLatLng != null) {
                    deviceLatLng = newLatLng
                } else {
                    isLocationValid = false
                }
            }

            viewModel.updateLocation(addressLine ?: "", deviceLatLng.latitude, deviceLatLng.longitude)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(deviceLatLng, 15f)
        }
    }

    Column(
        modifier = modifier
    ) {
        PermissionsRequired(multiplePermissionsState = multiplePermissionState,
            permissionsNotGrantedContent = {
                Text(text = stringResource(R.string.permissionsNotGrantedContent))
            },
            permissionsNotAvailableContent = {
                Text(text = stringResource(R.string.permissionsNotAvailableContent))
            }) {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(compassEnabled = true)
            ) {
                MarkerInfoWindowContent(
                    state = MarkerState(
                        position = deviceLatLng
                    )
                ) { marker ->
                    Text(marker.title ?: stringResource(R.string.you), color = Color.Red)
                }
            }
        }
    }
}