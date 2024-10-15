package ua.anastasiia.finesapp.rest.dto.request

data class FineRequest(
    val car: CarRequest,
    val trafficTickets: List<TrafficTicketRequest>
) : RestRequest
