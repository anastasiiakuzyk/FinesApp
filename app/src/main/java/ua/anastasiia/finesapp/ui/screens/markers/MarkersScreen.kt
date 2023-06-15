package ua.anastasiia.finesapp.ui.screens.markers

import android.annotation.SuppressLint
import android.location.Geocoder
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import ua.anastasiia.finesapp.ui.navigation.NavigationDestination
import ua.anastasiia.finesapp.ui.screens.FinesTopAppBar
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.ui.screens.home.HomeViewModel
import java.util.Locale

object MarkersDestination : NavigationDestination {
    override val route = "markers"
    override val titleRes = R.string.markers
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MarkersScreen(
    navigateBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val markers: ArrayList<MarkerWithPrice> = arrayListOf()

    val geocoder = Geocoder(LocalContext.current, Locale.getDefault());
    homeUiState.fineList.forEach { fine ->
        val addressList = geocoder.getFromLocationName(fine.fine.location, 1);
        val address = addressList?.get(0)
        markers.add(MarkerWithPrice(LatLng(address!!.latitude, address.longitude), fine.violations.sumOf { it.price }))
    }

    Scaffold(
        topBar = {
            FinesTopAppBar(
                title = stringResource(MarkersDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
    ) {
        LocationsMap(markersList = markers)
    }
}