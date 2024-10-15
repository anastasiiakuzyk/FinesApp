package ua.anastasiia.finesapp.rest.mapper

import ua.anastasiia.finesapp.entity.Fine
import ua.anastasiia.finesapp.rest.dto.response.TrafficTicketResponse

fun TrafficTicketResponse.toTrafficTicket() = Fine.TrafficTicket(
    id = id,
    locationLat = latitude,
    locationLon = longitude,
    dateTime = dateTime,
    photoUrl = photoUrl,
    valid = valid,
    violations = violations.map { it.toViolation() }
)
