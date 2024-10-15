package ua.anastasiia.finesapp.ui.screens.fine.entry

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
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.ui.navigation.NavigationDestination
import ua.anastasiia.finesapp.ui.screens.CarViewModel
import ua.anastasiia.finesapp.ui.screens.FineUIDetails
import ua.anastasiia.finesapp.ui.screens.FineUIState
import ua.anastasiia.finesapp.ui.screens.FinesTopAppBar

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

    val context = LocalContext.current
    viewModel.setContext(context)

    val viewModelState = viewModel.uiState.collectAsState()

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

        Column(
            modifier = modifier.padding(innerPadding)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            FineInputForm(
                fineUiState = viewModelState.value,
                fineUIDetails = viewModelState.value.fineUIDetails,
                onValueChange = viewModel::updateUiState,
                viewModel = viewModel,
                carViewModel = carViewModel,
                isLoading = isLoading,
                onSaveClick = {
                    coroutineScope.launch {
                        viewModel.saveFine()
                        navigateBack()
                    }
                },
                isPhotoTaking = true,
                isCreateMode = true,
            )
        }
    }
}

@Composable
fun FineEditBody(
    fineUiState: FineUIState,
    onFineValueChange: (FineUIDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FineViewModel,
    carViewModel: CarViewModel,
    isLoading: MutableState<Boolean>,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        FineInputForm(
            fineUiState = fineUiState,
            fineUIDetails = fineUiState.fineUIDetails,
            onValueChange = onFineValueChange,
            viewModel = viewModel,
            carViewModel = carViewModel,
            isLoading = isLoading,
            onSaveClick = onSaveClick,
            isEditMode = true,
        )
    }
}

val EMPTY_IMAGE_URI: Uri = Uri.parse("file://dev/null")


