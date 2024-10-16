package ua.anastasiia.finesapp.ui.screens.fine.entry

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ua.anastasiia.finesapp.service.FineService
import ua.anastasiia.finesapp.ui.screens.FineUIDetails
import ua.anastasiia.finesapp.ui.screens.FineUIState
import ua.anastasiia.finesapp.util.sdf
import ua.anastasiia.finesapp.util.validateInput
import java.util.Date
import javax.inject.Inject

@SuppressLint("SimpleDateFormat")
@HiltViewModel
class FineViewModel @Inject constructor(
    private val fineService: FineService
) : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    private val _uiState = MutableStateFlow(FineUIState())
    val uiState: StateFlow<FineUIState> = _uiState.asStateFlow()

    fun setContext(context: Context) {
        this.context = context
    }

    fun updateUiState(fineUIDetails: FineUIDetails) {
        _uiState.value = FineUIState(fineUIDetails, validateInput(fineUIDetails))
    }

    suspend fun saveFine() {
        if (validateInput(_uiState.value.fineUIDetails)) {
            runCatching {
                fineService.saveFine(_uiState.value.fineUIDetails)
            }.onSuccess { response ->
                response.id?.let { id ->
                    _uiState.value = _uiState.value.copy(
                        fineUIDetails = _uiState.value.fineUIDetails.copy(fineId = id)
                    )
                } ?: println("Failed to save fine: response id is null.")
            }.onFailure { e ->
                println("Failed to save fine due to network error: ${e.message}")
            }
        }
    }

    fun updateLocation(newLocation: String, latitude: Double, longitude: Double) {
        updateUiState(
            _uiState.value.fineUIDetails.copy(
                location = newLocation,
                latitude = latitude,
                longitude = longitude
            )
        )
    }

    fun updateDateTime() {
        val newDate = sdf.format(Date())
        updateUiState(_uiState.value.fineUIDetails.copy(date = newDate))
    }
}
