package ua.anastasiia.finesapp.ui.screens.fine_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ua.anastasiia.finesapp.data.FineRepository
import ua.anastasiia.finesapp.ui.screens.FineDetails
import ua.anastasiia.finesapp.ui.screens.FineUiState
import ua.anastasiia.finesapp.ui.screens.toFineUiState
import ua.anastasiia.finesapp.ui.screens.toFineWithCarAndViolations
import ua.anastasiia.finesapp.util.isDateValid
import ua.anastasiia.finesapp.util.isLocationValid
import ua.anastasiia.finesapp.util.isMakeModelValid
import ua.anastasiia.finesapp.util.isPlateValid
import javax.inject.Inject

@HiltViewModel
class FineEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fineRepository: FineRepository
) : ViewModel() {

    /**
     * Holds current fine ui state
     */
    var fineUiState by mutableStateOf(FineUiState())
        private set

    private val fineId: Int = checkNotNull(savedStateHandle[FineEditDestination.fineIdArg])

    init {
        viewModelScope.launch {
            fineUiState = fineRepository.getFineWithCarAndViolationsStream(fineId)
                .filterNotNull()
                .first()
                .toFineUiState(true)
        }
    }

    /**
     * Update the fine in the [FineRepository]'s data source
     */
    suspend fun updateFine() {
        if (validateInput(fineUiState.fineDetails)) {
            fineRepository.updateFullFine(fineUiState.fineDetails.toFineWithCarAndViolations())
        }
    }

    /**
     * Update the fine in the [FineRepository]'s data source
     */
    suspend fun validateFine(valid: Boolean) {
        if (validateInput(fineUiState.fineDetails)) {
            fineRepository.updateFullFine(
                fineUiState.fineDetails.copy(valid = valid).toFineWithCarAndViolations()
            )
        }
    }

    /**
     * Updates the [fineUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(fineDetails: FineDetails) {
        fineUiState =
            FineUiState(fineDetails = fineDetails, isEntryValid = validateInput(fineDetails))
    }

    private fun validateInput(fineDetails: FineDetails = fineUiState.fineDetails): Boolean {
        return with(fineDetails) {
            location.isNotBlank() && isLocationValid &&
                    date.isNotBlank() && isDateValid(date).first &&
                    plate.isNotBlank() && isPlateValid(plate) &&
                    make.isNotBlank() && isMakeModelValid(make) &&
                    model.isNotBlank() && isMakeModelValid(model) &&
                    color.isNotBlank() &&
                    violations.isNotEmpty()
        }
    }
}