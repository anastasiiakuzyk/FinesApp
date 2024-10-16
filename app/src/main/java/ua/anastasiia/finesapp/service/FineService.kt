package ua.anastasiia.finesapp.service

import android.annotation.SuppressLint
import android.net.Uri
import androidx.core.net.toFile
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ua.anastasiia.finesapp.entity.Fine
import ua.anastasiia.finesapp.rest.dto.request.RestRequest
import ua.anastasiia.finesapp.rest.dto.request.TrafficTicketRequest
import ua.anastasiia.finesapp.rest.dto.response.FineResponse
import ua.anastasiia.finesapp.rest.mapper.toRequest
import ua.anastasiia.finesapp.ui.screens.FineUIDetails
import ua.anastasiia.finesapp.ui.screens.toCreateFine
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class FineService @Inject constructor(
    private val client: OkHttpClient,
    private val gson: Gson
) {

    suspend fun getFineByCarPlateAndTicketId(
        carPlate: String,
        ticketId: String
    ): FineResponse {
        return withContext(Dispatchers.IO) {
            val request = createRequest(
                url = "/fines/car/$carPlate/ticket/$ticketId",
                method = "GET"
            )
            val response = executeRequest(request)
            val jsonData = response.body?.string()

            println("Get fine by car plate $carPlate and ticket id $ticketId response: $jsonData")

            gson.fromJson(jsonData, FineResponse::class.java)
        }
    }

    suspend fun updateTrafficTicketByCarPlateAndId(
        carPlate: String,
        ticketId: String,
        trafficTicketRequest: TrafficTicketRequest
    ): FineResponse {
        return withContext(Dispatchers.IO) {
            val request = createRequest(
                request = trafficTicketRequest,
                url = "/tickets/car/$carPlate/ticket/$ticketId",
                method = "PATCH"
            )
            val response = executeRequest(request)
            gson.fromJson(response.body?.string(), FineResponse::class.java)
        }
    }

    suspend fun deleteTrafficTicketByCarPlateAndId(carPlate: String, ticketId: String) {
        return withContext(Dispatchers.IO) {
            val request = createRequest(
                url = "/tickets/car/$carPlate/ticket/$ticketId",
                method = "DELETE"
            )
            executeRequest(request)
        }
    }

    suspend fun getAllFines(): List<FineResponse> {
        return withContext(Dispatchers.IO) {
            val request = createRequest(url = "/fines", method = "GET")
            val response = executeRequest(request)

            val jsonDataList = response.body?.string()
                ?.lines()
                ?.mapNotNull { line ->
                    line.substringAfter("data:", "")
                        .takeIf { it.isNotBlank() }
                }
                ?: emptyList()

            println("Get all fines response: $jsonDataList")

            jsonDataList.map { jsonData ->
                gson.fromJson(jsonData, FineResponse::class.java)
            }
        }
    }

    @SuppressLint("NewApi")
    suspend fun saveFine(fineUIDetails: FineUIDetails): FineResponse {
        val toCreateFine: Fine = fineUIDetails.toCreateFine()
        val file = Uri.parse(toCreateFine.trafficTicket.photoUrl).toFile()
        val key = toCreateFine.car.plate.replace(
            " ",
            "_"
        ) + "_" + LocalDateTime.parse(toCreateFine.trafficTicket.dateTime).toInstant(ZoneOffset.UTC)
            .toEpochMilli() + ".jpg"
        uploadImage(file, key)
        val fine = toCreateFine.copy(
            trafficTicket = toCreateFine.trafficTicket.copy(
                photoUrl = S3_BUCKET.plus("/$key")
            )
        )

        return withContext(Dispatchers.IO) {
            val request = createRequest(
                request = fine.toRequest(), url = "/fines", method = "POST"
            )
            val response = executeRequest(request)
            gson.fromJson(response.body?.string(), FineResponse::class.java)
        }
    }

    suspend fun uploadImage(file: File, key: String): String {
        return withContext(Dispatchers.IO) {
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    file.name,
                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                .build()

            val request = Request.Builder()
                .url("$SERVER_BASE_URL/photo/upload/key/$key")
                .post(requestBody)
                .build()
            val response = executeRequest(request)
            response.body?.string() ?: ""
        }
    }

    private fun createRequest(
        request: RestRequest? = null,
        url: String,
        method: String
    ): Request {
        val requestBody: RequestBody? = request?.let {
            val json = gson.toJson(it)
            json.toRequestBody("application/json; charset=utf-8".toMediaType())
        }
        return Request.Builder()
            .url("$SERVER_BASE_URL$url")
            .method(method, requestBody)
            .build()
    }

    private suspend fun executeRequest(request: Request): Response {
        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute()
            } catch (e: Exception) {
                println("Network error: ${e.message}")
                throw e
            }
        }
    }

    companion object {
        private const val SERVER_BASE_URL = "http://10.0.2.2:8085"
        const val S3_BUCKET = "http://localhost:4566/fine-car-images"
    }
}
