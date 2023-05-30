package ua.anastasiia.finesapp.web

import ua.anastasiia.finesapp.web.model.NumberPlateResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Manager @Inject constructor(val service: CarService) {
    suspend fun getResults(imagePart: MultipartBody.Part): NumberPlateResponse =
        withContext(Dispatchers.IO) {
            val titlePart: MultipartBody.Part = MultipartBody.Part.createFormData("mmc", "true")
            service.getCarDetails(imagePart, titlePart)
        }
}
