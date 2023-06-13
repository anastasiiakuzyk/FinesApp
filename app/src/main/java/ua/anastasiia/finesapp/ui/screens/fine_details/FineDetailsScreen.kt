package ua.anastasiia.finesapp.ui.screens.fine_details

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
import ua.anastasiia.finesapp.ui.screens.FinesTopAppBar
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.ui.screens.FineDetails
import ua.anastasiia.finesapp.ui.screens.fine_entry.FineInputForm
import ua.anastasiia.finesapp.ui.screens.FineUiState
import ua.anastasiia.finesapp.ui.screens.CarViewModel
import ua.anastasiia.finesapp.ui.screens.fine_entry.FineViewModel

object FineDetailsDestination : NavigationDestination {
    override val route = "fine_details"
    override val titleRes = R.string.fine_detail_title
    const val fineIdArg = "fineId"
    val routeWithArgs = "$route/{$fineIdArg}"
}

@Composable
fun FineDetailsScreen(
    navigateToEditFine: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FineDetailsViewModel = hiltViewModel(),
    fineViewModel: FineViewModel = hiltViewModel(),
    carViewModel: CarViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsState()
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
                onClick = { navigateToEditFine(uiState.value.id) },
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
            fineDetails = uiState.value,
            onDelete = {
                // If the user rotates the screen very fast, the operation may get cancelled
                // and the fine may not be deleted from the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.deleteFine()
                    navigateBack()
                }
            },
            modifier = modifier.padding(innerPadding),
            fineViewModel,
            carViewModel
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun FineDetailsBody(
    fineDetails: FineDetails,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FineViewModel,
    carViewModel: CarViewModel
) {
    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FineInputForm(
            fineDetails = fineDetails,
            enabled = false,
            modifier = modifier,
            onSaveClick = {},
            viewModel = viewModel,
            carViewModel = carViewModel,
            isLoading = mutableStateOf(false),
            onDelete = onDelete,
            isViewMode = false,
            fineUiState = FineUiState()
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
