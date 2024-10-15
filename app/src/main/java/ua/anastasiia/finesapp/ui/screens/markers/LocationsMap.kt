package ua.anastasiia.finesapp.ui.screens.markers

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
import ua.anastasiia.finesapp.MainActivity
import ua.anastasiia.finesapp.R

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationsMap(
    modifier: Modifier = Modifier,
    markersList: ArrayList<MarkerWithPrice>
) {
    markersList.addAll(Markers.markers)

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
            cameraPositionState.position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
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
                for (marker in markersList) {
                    MarkerInfoWindowContent(
                        state = MarkerState(marker.marker)
                    ) {
                        Text("${marker.price}${stringResource(R.string.currency)}")
                    }
                }
            }
        }
    }
}

object Markers {
    val markers = listOf(
        MarkerWithPrice(LatLng(50.4087, 30.6284), 1190.0),
        MarkerWithPrice(LatLng(50.4090, 30.6285), 340.0),
        MarkerWithPrice(LatLng(50.4100, 30.6270), 340.0),
        MarkerWithPrice(LatLng(50.4078, 30.6220), 340.0),
        MarkerWithPrice(LatLng(50.4068, 30.6284), 1190.0),
        MarkerWithPrice(LatLng(50.4095, 30.6283), 1190.0),
        MarkerWithPrice(LatLng(50.4109, 30.6272), 680.0),
        MarkerWithPrice(LatLng(50.4088, 30.6230), 680.0),
        MarkerWithPrice(LatLng(50.4088, 30.6284), 680.0),
        MarkerWithPrice(LatLng(50.4091, 30.6285), 680.0),
        MarkerWithPrice(LatLng(50.4101, 30.6270), 680.0),
        MarkerWithPrice(LatLng(50.4079, 30.6220), 680.0),
        MarkerWithPrice(LatLng(50.4067, 30.6284), 340.0),
        MarkerWithPrice(LatLng(50.4096, 30.6283), 1700.0),
        MarkerWithPrice(LatLng(50.4108, 30.6272), 680.0),
        MarkerWithPrice(LatLng(50.4081, 30.6230), 340.0),
        MarkerWithPrice(LatLng(50.4088, 30.6231), 340.0),
        MarkerWithPrice(LatLng(50.4088, 30.6285), 340.0),
        MarkerWithPrice(LatLng(50.4091, 30.6282), 1700.0),
        MarkerWithPrice(LatLng(50.4101, 30.6271), 1700.0),
        MarkerWithPrice(LatLng(50.4079, 30.6221), 1700.0),
        MarkerWithPrice(LatLng(50.4067, 30.6285), 680.0),
        MarkerWithPrice(LatLng(50.4096, 30.6284), 680.0),
        MarkerWithPrice(LatLng(50.4108, 30.6271), 680.0),
        MarkerWithPrice(LatLng(50.4081, 30.6231), 680.0)
    )
}

data class MarkerWithPrice(
    val marker: LatLng,
    val price: Double
)