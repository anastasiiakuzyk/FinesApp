package ua.anastasiia.finesapp.rest.mapper

import ua.anastasiia.finesapp.entity.Fine
import ua.anastasiia.finesapp.rest.dto.response.ViolationResponse

fun ViolationResponse.toViolation() = Fine.TrafficTicket.Violation(
    description = description,
    price = price
)
