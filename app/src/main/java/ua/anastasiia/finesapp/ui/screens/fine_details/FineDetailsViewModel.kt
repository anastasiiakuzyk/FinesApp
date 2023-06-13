package ua.anastasiia.finesapp.ui.screens.fine_details

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ua.anastasiia.finesapp.data.FineRepository
import ua.anastasiia.finesapp.ui.screens.FineDetails
import ua.anastasiia.finesapp.ui.screens.toFineWithCarAndViolations
import javax.inject.Inject

/**
 * ViewModel to retrieve, update and delete fine from the data source.
 */
@HiltViewModel
class FineDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fineRepository: FineRepository
) : ViewModel() {

    private val fineId: Int = checkNotNull(savedStateHandle[FineDetailsDestination.fineIdArg])

    /**
     * Holds the fine details ui state. The data is retrieved from [FineRepository] and mapped to
     * the UI state.
     */
    val uiState: StateFlow<FineDetails> =
        fineRepository.getFineWithCarAndViolationsStream(fineId)
            .filterNotNull()
            .map { fineWithCarAndViolations ->
                FineDetails(
                    id = fineWithCarAndViolations.fine.fine_id,
                    location = fineWithCarAndViolations.fine.location,
                    date = fineWithCarAndViolations.fine.date,
                    plate = fineWithCarAndViolations.carInfo.plate,
                    make = fineWithCarAndViolations.carInfo.make,
                    model = fineWithCarAndViolations.carInfo.model,
                    color = fineWithCarAndViolations.carInfo.color,
                    imageUri = Uri.parse(fineWithCarAndViolations.fine.imageUri),
                    valid = fineWithCarAndViolations.fine.valid,
                    violations = fineWithCarAndViolations.violations,
                    sum = fineWithCarAndViolations.violations.sumOf { it.price }
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FineDetails()
            )

    /**
     * Deletes the fine from the [FineRepository]'s data source.
     */
    suspend fun deleteFine() {
        fineRepository.deleteFine(uiState.value.toFineWithCarAndViolations())
    }

    suspend fun updateFine() {
        fineRepository.updateFullFine(uiState.value.copy(valid = true).toFineWithCarAndViolations())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}
