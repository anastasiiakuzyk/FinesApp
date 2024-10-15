package ua.anastasiia.finesapp.rest.mapper

import ua.anastasiia.finesapp.entity.Fine
import ua.anastasiia.finesapp.rest.dto.response.CarResponse

fun CarResponse.toCar() = Fine.Car(
    plate = plate,
    make = make,
    model = model,
    color = color
)
