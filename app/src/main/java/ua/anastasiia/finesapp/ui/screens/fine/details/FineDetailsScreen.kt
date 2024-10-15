package ua.anastasiia.finesapp.ui.screens.fine.details

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.ui.navigation.NavigationDestination
import ua.anastasiia.finesapp.ui.screens.CarViewModel
import ua.anastasiia.finesapp.ui.screens.FineUIDetails
import ua.anastasiia.finesapp.ui.screens.FineUIState
import ua.anastasiia.finesapp.ui.screens.FinesTopAppBar
import ua.anastasiia.finesapp.ui.screens.fine.entry.FineInputForm
import ua.anastasiia.finesapp.ui.screens.fine.entry.FineViewModel

object FineDetailsDestination : NavigationDestination {
    override val route = "fine_details"
    override val titleRes = R.string.fine_detail_title
    const val TRAFFIC_ID_ARG = "trafficTicketId"
    const val CAR_PLATE_ARG = "carPlate"
    val routeWithArgs = "$route/{$CAR_PLATE_ARG}/{$TRAFFIC_ID_ARG}"
}

@Composable
fun FineDetailsScreen(
    navigateToEditFine: (String, String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FineDetailsViewModel = hiltViewModel(),
    fineViewModel: FineViewModel = hiltViewModel(),
    carViewModel: CarViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    viewModel.setContext(context)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            FinesTopAppBar(
                title = stringResource(FineDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditFine(uiState.fineId, uiState.plate) },
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_fine_title),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
    ) { innerPadding ->
        FineDetailsBody(
            fineUIDetails = uiState,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteFine()
                    navigateBack()
                }
            },
            onValidate = {
                coroutineScope.launch {
                    viewModel.validateFine(it)
                    navigateBack()
                }
            },
            modifier = modifier.padding(innerPadding),
            viewModel = fineViewModel,
            carViewModel = carViewModel
        )
    }
}

@Composable
private fun FineDetailsBody(
    fineUIDetails: FineUIDetails,
    onDelete: () -> Unit,
    onValidate: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FineViewModel,
    carViewModel: CarViewModel
) {
    val isLoading = remember { mutableStateOf(false) }
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FineInputForm(
            fineUIDetails = fineUIDetails,
            modifier = modifier,
            onSaveClick = {},
            viewModel = viewModel,
            carViewModel = carViewModel,
            isLoading = isLoading,
            onDelete = onDelete,
            onValidate = onValidate,
            isViewMode = true,
            fineUiState = remember { FineUIState() }
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier.padding(16.dp),
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(R.string.yes))
            }
        }
    )
}