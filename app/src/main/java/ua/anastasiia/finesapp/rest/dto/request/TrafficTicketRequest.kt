package ua.anastasiia.finesapp.rest.dto.request

data class TrafficTicketRequest(
    val longitude: Double,
    val latitude: Double,
    val dateTime: String,
    val photoUrl: String,
    val valid: Boolean,
    val violationIds: List<Int>
) : RestRequest
