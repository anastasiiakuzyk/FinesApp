package ua.anastasiia.finesapp.ui.screens.fine_entry

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.anastasiia.finesapp.data.FineRepository
import ua.anastasiia.finesapp.ui.screens.FineDetails
import ua.anastasiia.finesapp.ui.screens.FineUiState
import ua.anastasiia.finesapp.ui.screens.toFineWithCarAndViolations
import ua.anastasiia.finesapp.util.sdf
import ua.anastasiia.finesapp.util.isDateValid
import ua.anastasiia.finesapp.util.isLocationValid
import ua.anastasiia.finesapp.util.isMakeModelValid
import ua.anastasiia.finesapp.util.isPlateValid
import java.util.Date
import javax.inject.Inject

@SuppressLint("SimpleDateFormat")
@HiltViewModel
class FineViewModel @Inject constructor(private val fineRepository: FineRepository) : ViewModel() {

    var fineUiState by mutableStateOf(FineUiState())
        private set

    fun updateUiState(fineDetails: FineDetails) {
        fineUiState =
            FineUiState(fineDetails = fineDetails, isEntryValid = validateInput(fineDetails))
    }

    suspend fun saveFine() {
        if (validateInput()) {
            fineRepository.insertFineWithCarAndViolations(fineUiState.fineDetails.toFineWithCarAndViolations())
        }
    }

    var location by mutableStateOf("")
        private set

    /**
     * Updates the [location] with the value provided in the argument.
     */
    fun updateLocation(newLocation: String) {
        location = newLocation
        updateUiState(fineUiState.fineDetails.copy(location = location))
    }

    /**
     * Holds current location
     */
    var date by mutableStateOf("")
        private set


    /**
     * Updates the [date] with the value provided in the argument.
     */
    fun updateDateTime() {
        date = sdf.format(Date())
        updateUiState(fineUiState.fineDetails.copy(date = date))
    }

    private fun validateInput(uiState: FineDetails = fineUiState.fineDetails): Boolean {
        return with(uiState) {
            location.isNotBlank() && isLocationValid &&
                    date.isNotBlank() && isDateValid(date) &&
                    plate.isNotBlank() && isPlateValid(plate) &&
                    make.isNotBlank() && isMakeModelValid(make) &&
                    model.isNotBlank() && isMakeModelValid(model) &&
                    color.isNotBlank() &&
                    violations.isNotEmpty()
        }
    }


}