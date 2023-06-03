package ua.anastasiia.finesapp.ui.screens.markers

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ua.anastasiia.finesapp.ui.navigation.NavigationDestination
import ua.anastasiia.finesapp.FinesTopAppBar
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.ui.screens.FineDetails
import ua.anastasiia.finesapp.ui.screens.fine_entry.FineInputForm
import ua.anastasiia.finesapp.ui.screens.FineUiState
import ua.anastasiia.finesapp.ui.screens.CarViewModel
import ua.anastasiia.finesapp.ui.screens.LocationMap
import ua.anastasiia.finesapp.ui.screens.fine_entry.FineViewModel

object MarkersDestination : NavigationDestination {
    override val route = "markers"
    override val titleRes = R.string.markers
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MarkersScreen(
    navigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            FinesTopAppBar(
                title = stringResource(MarkersDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
    ) {
        LocationsMap()
    }
}