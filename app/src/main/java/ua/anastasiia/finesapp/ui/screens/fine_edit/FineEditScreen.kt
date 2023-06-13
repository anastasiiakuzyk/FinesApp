package ua.anastasiia.finesapp.ui.screens.fine_edit

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.ui.screens.fine_entry.FineEntryBody
import ua.anastasiia.finesapp.ui.navigation.NavigationDestination
import ua.anastasiia.finesapp.ui.screens.CarViewModel
import ua.anastasiia.finesapp.ui.screens.FinesTopAppBar
import ua.anastasiia.finesapp.ui.screens.fine_entry.FineViewModel

object FineEditDestination : NavigationDestination {
    override val route = "fine_edit"
    override val titleRes = R.string.edit_fine_title
    const val fineIdArg = "fineId"
    val routeWithArgs = "$route/{$fineIdArg}"
}

@SuppressLint("UnrememberedMutableState", "LongLogTag")
@Composable
fun FineEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FineEditViewModel = hiltViewModel(),
    fineViewModel: FineViewModel = hiltViewModel(),
    carViewModel: CarViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            FinesTopAppBar(
                title = stringResource(FineEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        FineEntryBody(
            fineUiState = viewModel.fineUiState,
            onFineValueChange = viewModel::updateUiState,
            onSaveClick = {
                // If the user rotates the screen very fast, the operation may get cancelled
                // and the fine may not be updated in the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.updateFine()
                    navigateBack()
                }
            },
            modifier = modifier.padding(innerPadding),
            viewModel = fineViewModel,
            carViewModel = carViewModel,
            isLoading = mutableStateOf(false),
            isPhotoTaking = false
        )
    }
}