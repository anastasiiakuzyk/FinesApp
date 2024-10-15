package ua.anastasiia.finesapp.rest.mapper

import ua.anastasiia.finesapp.entity.Fine
import ua.anastasiia.finesapp.rest.dto.request.FineRequest

fun Fine.toRequest() = FineRequest(
    car = car.toRequest(),
    trafficTickets = listOf(trafficTicket.toRequest())
)
