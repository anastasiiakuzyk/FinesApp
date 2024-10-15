package ua.anastasiia.finesapp.ui.screens.fine.details

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.anastasiia.finesapp.rest.mapper.toFineWithSpecificTrafficTicket
import ua.anastasiia.finesapp.rest.mapper.toRequest
import ua.anastasiia.finesapp.service.FineService
import ua.anastasiia.finesapp.ui.screens.FineUIDetails
import ua.anastasiia.finesapp.ui.screens.FineUIState
import ua.anastasiia.finesapp.ui.screens.toFineUIDetails
import ua.anastasiia.finesapp.ui.screens.toUpdateFine
import ua.anastasiia.finesapp.util.sdf
import ua.anastasiia.finesapp.util.validateInput
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class FineDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fineService: FineService
) : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    private val trafficTicketId: String =
        checkNotNull(savedStateHandle[FineDetailsDestination.TRAFFIC_ID_ARG])
    private val carPlate: String =
        checkNotNull(savedStateHandle[FineDetailsDestination.CAR_PLATE_ARG])

    private val _uiState = MutableStateFlow(FineUIDetails())
    val uiState: StateFlow<FineUIDetails> = _uiState.asStateFlow()

    fun setContext(context: Context) {
        this.context = context
        loadFineDetails()
    }

    private fun loadFineDetails() {
        viewModelScope.launch {
            _uiState.value = fineService.getFineByCarPlateAndTicketId(carPlate, trafficTicketId)
                .toFineWithSpecificTrafficTicket(trafficTicketId)
                .toFineUIDetails(context)

            println("Loaded fine details with ${_uiState.value}")
        }
    }

    suspend fun validateFine(valid: Boolean) {
        println("Validating fine with id $trafficTicketId and car plate $carPlate as $valid -> ${_uiState.value}")
        if (validateInput(_uiState.value)) {
            fineService.updateTrafficTicketByCarPlateAndId(
                carPlate,
                trafficTicketId,
                _uiState.value.copy(valid = valid)
                    .toUpdateFine().trafficTicket.toRequest()
            )
        }
    }

    @SuppressLint("NewApi")
    fun updateDateTime() {
        println("Updating date... Current date: ${_uiState.value.date}")
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val newDate = LocalDateTime.parse(_uiState.value.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME).format(dateFormatter)

        println("Updated date: $newDate")
        _uiState.value = _uiState.value.copy(date = sdf.format(newDate))
    }

    suspend fun deleteFine() {
        runCatching {
            fineService.deleteTrafficTicketByCarPlateAndId(carPlate, trafficTicketId)
        }.onFailure { e ->
            println("Failed to delete fine due to network error: ${e.message}")
        }
    }
}
