package ua.anastasiia.finesapp.rest.mapper

import ua.anastasiia.finesapp.entity.Fine
import ua.anastasiia.finesapp.rest.dto.request.TrafficTicketRequest

fun Fine.TrafficTicket.toRequest() = TrafficTicketRequest(
    longitude = locationLon,
    latitude = locationLat,
    dateTime = dateTime,
    photoUrl = photoUrl,
    valid = valid,
    violationIds = violations.map { violation ->
        violation.toId()
    }
)
