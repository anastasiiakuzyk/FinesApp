package ua.anastasiia.finesapp.rest.dto.response

data class FineResponse(
    val id: String?,
    val car: CarResponse,
    val trafficTickets: List<TrafficTicketResponse>
)
