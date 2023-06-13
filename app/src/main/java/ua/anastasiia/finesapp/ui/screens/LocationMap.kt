package ua.anastasiia.finesapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.*
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
import com.google.maps.android.compose.*
import ua.anastasiia.finesapp.MainActivity
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.ui.screens.fine_entry.FineViewModel
import ua.anastasiia.finesapp.ui.screens.markers.Markers
import java.util.Locale


@SuppressLint("MissingPermission")
@Suppress("DEPRECATION")
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

    remember { LocationServices.getFusedLocationProviderClient(context) }.lastLocation.addOnCompleteListener(
        context as MainActivity
    ) { task ->
        if (task.isSuccessful) {
            deviceLatLng = LatLng(task.result!!.latitude, task.result!!.longitude)
            val addresses: List<Address>?
            val geocoder = Geocoder(context, Locale.getDefault())

            addresses = geocoder.getFromLocation(
                deviceLatLng.latitude, deviceLatLng.longitude, 1
            )
            val address: String = addresses!![0].getAddressLine(0)
            if (location.isNotBlank().and(location.isNotEmpty())) {
                Log.d("location", location)
                val addressList = geocoder.getFromLocationName(location, 1);
                val addres = addressList?.get(0)
                deviceLatLng = LatLng(addres!!.latitude, addres.longitude)
            }

            viewModel.updateLocation(address)
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