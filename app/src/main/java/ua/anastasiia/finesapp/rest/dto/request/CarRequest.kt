package ua.anastasiia.finesapp.rest.dto.request

data class CarRequest(
    val plate: String,
    val make: String,
    val model: String?,
    val color: String
) : RestRequest
