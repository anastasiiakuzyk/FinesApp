package ua.anastasiia.finesapp.ui.screens.fine.edit

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
import ua.anastasiia.finesapp.ui.screens.fine.details.FineDetailsDestination
import ua.anastasiia.finesapp.ui.screens.toFineUIState
import ua.anastasiia.finesapp.ui.screens.toUpdateFine
import ua.anastasiia.finesapp.util.validateInput
import javax.inject.Inject

@HiltViewModel
class FineEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fineService: FineService
) : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    private val _uiState = MutableStateFlow(FineUIState())
    val uiState: StateFlow<FineUIState> = _uiState.asStateFlow()

    private val trafficTicketId: String =
        checkNotNull(savedStateHandle[FineDetailsDestination.TRAFFIC_ID_ARG])

    private val carPlate: String =
        checkNotNull(savedStateHandle[FineDetailsDestination.CAR_PLATE_ARG])

    fun setContext(context: Context) {
        this.context = context
        loadFineEdit()
    }

    private fun loadFineEdit() {
        viewModelScope.launch {
            _uiState.value = fineService.getFineByCarPlateAndTicketId(carPlate, trafficTicketId)
                .toFineWithSpecificTrafficTicket(trafficTicketId)
                .toFineUIState(true, context)

            println("Loaded fine edit with ${_uiState.value.fineUIDetails}")
        }
    }

    suspend fun updateFine() {
        if (validateInput(_uiState.value.fineUIDetails)) {
            fineService.updateTrafficTicketByCarPlateAndId(
                carPlate,
                trafficTicketId,
                _uiState.value.fineUIDetails.toUpdateFine().trafficTicket.toRequest()
            )
        }
    }

    fun updateUiState(fineUIDetails: FineUIDetails) {
        println("Updating UI state with $fineUIDetails")
        _uiState.value =
            FineUIState(fineUIDetails = fineUIDetails, isEntryValid = validateInput(_uiState.value.fineUIDetails))
    }
}
