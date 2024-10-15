package ua.anastasiia.finesapp.rest.mapper

import android.annotation.SuppressLint
import ua.anastasiia.finesapp.entity.Fine
import ua.anastasiia.finesapp.rest.dto.response.FineResponse

@SuppressLint("NewApi")
fun FineResponse.toFineList(): List<Fine> = trafficTickets.map {
    Fine(
        id = id ?: "",
        car = car.toCar(),
        trafficTicket = it.toTrafficTicket()
    )
}

fun FineResponse.toFineWithSpecificTrafficTicket(trafficTicketId: String): Fine = Fine(
    id = id ?: "",
    car = car.toCar(),
    trafficTicket = trafficTickets.find { it.id == trafficTicketId }
        ?.toTrafficTicket()
        ?: throw IllegalArgumentException("Traffic ticket with id $trafficTicketId not found")
)
