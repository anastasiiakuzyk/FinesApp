package ua.anastasiia.finesapp.ui.screens.home

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.anastasiia.finesapp.rest.dto.response.FineResponse
import ua.anastasiia.finesapp.rest.mapper.toFineList
import ua.anastasiia.finesapp.service.FineService
import ua.anastasiia.finesapp.ui.screens.FineUIDetails
import ua.anastasiia.finesapp.ui.screens.toFineUIDetails
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fineService: FineService
) : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState

    private val _homeValidatedUiState = MutableStateFlow(HomeUiState())
    val homeValidatedUiState: StateFlow<HomeUiState> = _homeValidatedUiState

    fun setContext(context: Context) {
        this.context = context
        loadHomePageData()
    }

    private fun loadHomePageData() {

        viewModelScope.launch {
            val finesResponse = fineService.getAllFines()
            _homeUiState.value = HomeUiState(finesResponse
                .flatMap {
                it.toFineList().map { fine -> fine.toFineUIDetails(context) }
            })
        }

        viewModelScope.launch {
            val finesResponse = fineService.getAllFines()
            _homeValidatedUiState.value = HomeUiState(
                finesResponse
                    .filter { fine -> fine.trafficTickets.map { it.valid }.equals(true) }
                    .flatMap {
                        it.toFineList().map { fine -> fine.toFineUIDetails(context) }
                    }
            )
        }
    }
}

data class HomeUiState(val fineList: List<FineUIDetails> = listOf())


