package ua.anastasiia.finesapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ua.anastasiia.finesapp.data.FineRepository
import ua.anastasiia.finesapp.data.FineWithCarAndViolations
import javax.inject.Inject


/**
 * View Model to retrieve all fines in the Room database.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(dataRepository: FineRepository) : ViewModel() {

    val homeUiState: StateFlow<HomeUiState> = dataRepository.getFinesWithCarAndViolations().map {
        HomeUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = HomeUiState()
    )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for ua.anastasiia.finesapp.ui.home.HomeScreen
 */
data class HomeUiState(val fineList: List<FineWithCarAndViolations> = listOf())


