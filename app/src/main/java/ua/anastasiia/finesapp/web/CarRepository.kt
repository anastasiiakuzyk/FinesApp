package ua.anastasiia.finesapp.web

import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CarRepository @Inject constructor(private val manager: Manager) {
    suspend fun getResults(imagePart: MultipartBody.Part) = manager.getResults(imagePart)
}
