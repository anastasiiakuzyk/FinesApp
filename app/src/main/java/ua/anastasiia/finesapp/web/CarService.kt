package ua.anastasiia.finesapp.web

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import ua.anastasiia.finesapp.web.model.NumberPlateResponse

interface CarService {

    @Multipart
    @POST("plate-reader")
    suspend fun getCarDetails(
        @Part
        imagePart: MultipartBody.Part,
        @Part
        mmc: MultipartBody.Part
    ): NumberPlateResponse
}