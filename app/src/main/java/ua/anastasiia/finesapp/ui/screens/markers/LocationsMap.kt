package ua.anastasiia.finesapp.ui.screens.markers

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
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
import ua.anastasiia.finesapp.data.entity.Violation
import ua.anastasiia.finesapp.ui.screens.fine_entry.FineViewModel
import java.util.Locale


@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationsMap(
    modifier: Modifier = Modifier,
    markersList: List<LatLng>
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
                    Marker(
                        state = MarkerState(marker)
                    )
                }
            }
        }
    }
}

object Markers {
    val markers = listOf(
        LatLng(50.4087, 30.6284),
        LatLng(50.4090, 30.6285),
        LatLng(50.4100, 30.6270),
        LatLng(50.4078, 30.6220),
        LatLng(50.4068, 30.6284),
        LatLng(50.4095, 30.6283),
        LatLng(50.4109, 30.6272),
        LatLng(50.4088, 30.6230),
        LatLng(50.4088, 30.6284),
        LatLng(50.4091, 30.6285),
        LatLng(50.4101, 30.6270),
        LatLng(50.4079, 30.6220),
        LatLng(50.4067, 30.6284),
        LatLng(50.4096, 30.6283),
        LatLng(50.4108, 30.6272),
        LatLng(50.4081, 30.6230),
        LatLng(50.4088, 30.6231),
        LatLng(50.4088, 30.6285),
        LatLng(50.4091, 30.6282),
        LatLng(50.4101, 30.6271),
        LatLng(50.4079, 30.6221),
        LatLng(50.4067, 30.6285),
        LatLng(50.4096, 30.6284),
        LatLng(50.4108, 30.6271),
        LatLng(50.4081, 30.6231)
    )
}