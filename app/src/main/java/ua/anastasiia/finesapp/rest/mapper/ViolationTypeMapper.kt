package ua.anastasiia.finesapp.rest.mapper

import ua.anastasiia.finesapp.entity.Fine
import ua.anastasiia.finesapp.infrastructure.mapper.ViolationType

fun Fine.TrafficTicket.Violation.toId(): Int = toViolationType().ordinal

fun Fine.TrafficTicket.Violation.toViolationType() =
    ViolationType.valueOf(description.replace(" ", "_"))
