package ua.anastasiia.finesapp.ui.screens.fine_entry

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ua.anastasiia.finesapp.ui.screens.FinesTopAppBar
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.ui.navigation.NavigationDestination
import ua.anastasiia.finesapp.ui.screens.CarViewModel
import ua.anastasiia.finesapp.ui.screens.FineDetails
import ua.anastasiia.finesapp.ui.screens.FineUiState
import java.util.*

object FineEntryDestination : NavigationDestination {
    override val route = "fine_entry"
    override val titleRes = R.string.fine_entry_title
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun FineEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    viewModel: FineViewModel = hiltViewModel(),
    carViewModel: CarViewModel = hiltViewModel()
) {
    val loading by carViewModel.isLoading.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            FinesTopAppBar(
                title = stringResource(FineEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        val isLoading = mutableStateOf(loading)

        val context = LocalContext.current
        FineEntryBody(
            fineUiState = viewModel.fineUiState,
            onFineValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveFine()
                    navigateBack()
                }
            },
            modifier = modifier.padding(innerPadding),
            carViewModel = carViewModel,
            viewModel = viewModel,

            isLoading = isLoading
        )
    }

}

@Composable
fun FineEntryBody(
    fineUiState: FineUiState,
    onFineValueChange: (FineDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FineViewModel,
    carViewModel: CarViewModel,
    isLoading: MutableState<Boolean>,
    isPhotoTaking: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        FineInputForm(
            fineUiState = fineUiState,
            fineDetails = fineUiState.fineDetails,
            onValueChange = onFineValueChange,
            viewModel = viewModel,
            carViewModel = carViewModel,
            isLoading = isLoading,
            onSaveClick = onSaveClick,
            isViewMode = isPhotoTaking
        )
    }
}

val EMPTY_IMAGE_URI: Uri = Uri.parse("file://dev/null")


