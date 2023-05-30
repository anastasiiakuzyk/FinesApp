package ua.anastasiia.finesapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ua.anastasiia.finesapp.web.CarRepository
import ua.anastasiia.finesapp.web.model.NumberPlateResponse
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CarViewModel @Inject constructor(private val carRepository: CarRepository) : ViewModel() {

    private val _response = MutableStateFlow(NumberPlateResponse())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val response: StateFlow<NumberPlateResponse>
        get() = _response

    fun getResultsFromFile(imageFile: File) {
        _isLoading.value = true
        val imagePart = MultipartBody.Part.createFormData(
            "upload",
            imageFile.name,
            imageFile
                .asRequestBody("image/*".toMediaTypeOrNull())
        )
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = carRepository.getResults(imagePart)
            _isLoading.value = false
        }
    }
}