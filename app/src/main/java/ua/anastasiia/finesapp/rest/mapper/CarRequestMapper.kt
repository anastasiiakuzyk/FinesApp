package ua.anastasiia.finesapp.rest.mapper

import ua.anastasiia.finesapp.entity.Fine
import ua.anastasiia.finesapp.rest.dto.request.CarRequest

fun Fine.Car.toRequest() = CarRequest(
    plate = plate,
    make = make,
    model = model,
    color = color
)
