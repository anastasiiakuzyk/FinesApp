package ua.anastasiia.finesapp.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import ua.anastasiia.finesapp.entity.Fine
import ua.anastasiia.finesapp.ui.screens.fine.entry.EMPTY_IMAGE_URI
import ua.anastasiia.finesapp.util.GeocoderUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class FineUIState(
    val fineUIDetails: FineUIDetails = FineUIDetails(),
    val isEntryValid: Boolean = false
)

data class FineUIDetails(
    // Required id's
    val fineId: String = "",
    val carId: String = "",

    // Location details
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,

    // Date details
    val date: String = "",

    // Image details
    val imageUri: Uri = EMPTY_IMAGE_URI,

    // Validation status
    val valid: Boolean = false,

    // List of violations
    val violations: List<Fine.TrafficTicket.Violation> = listOf(),

    // Calculated sum of all violations
    val sum: Double = 0.0,

    // Car details
    val plate: String = "",
    val make: String = "",
    val model: String = "",
    val color: String = ""
) {

    companion object {
        const val DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm"
    }
}

@SuppressLint("NewApi")
fun FineUIDetails.toCreateFine(): Fine {
    val formatter = DateTimeFormatter.ofPattern(FineUIDetails.DATE_TIME_FORMAT)
    val parsedDate: String = LocalDateTime.parse(date, formatter).toString()

    return Fine(
        id = fineId,
        car = Fine.Car(
            plate = plate,
            make = make,
            model = model,
            color = color
        ),
        trafficTicket = Fine.TrafficTicket(
            id = fineId,
            locationLat = latitude,
            locationLon = longitude,
            dateTime = parsedDate,
            photoUrl = imageUri.toString().replace("10.0.2.2", "localhost"),
            violations = violations,
            valid = valid
        )
    )
}

fun FineUIDetails.toUpdateFine(): Fine {
    return Fine(
        id = fineId,
        car = Fine.Car(
            plate = plate,
            make = make,
            model = model,
            color = color
        ),
        trafficTicket = Fine.TrafficTicket(
            id = fineId,
            locationLat = latitude,
            locationLon = longitude,
            dateTime = date,
            photoUrl = imageUri.toString().replace("10.0.2.2", "localhost"),
            violations = violations,
            valid = valid
        )
    )
}

suspend fun Fine.toFineUIDetails(context: Context): FineUIDetails {
    return FineUIDetails(
        fineId = trafficTicket.id ?: throw IllegalArgumentException("Traffic ticket id is null"),
        carId = car.plate,
        location = GeocoderUtil.getAddressFromLatLng(
            context, trafficTicket.locationLat, trafficTicket.locationLon
        ) ?: "Not found",
        latitude = trafficTicket.locationLat,
        longitude = trafficTicket.locationLon,
        date = trafficTicket.dateTime,
        imageUri = Uri.parse(trafficTicket.photoUrl.replace("localhost", "10.0.2.2")),
        valid = trafficTicket.valid,
        violations = trafficTicket.violations,
        sum = trafficTicket.violations.sumOf { it.price },
        plate = car.plate,
        make = car.make,
        model = car.model,
        color = car.color
    )
}

suspend fun Fine.toFineUIState(isEntryValid: Boolean = false, context: Context): FineUIState =
    FineUIState(
        fineUIDetails = this.toFineUIDetails(context),
        isEntryValid = isEntryValid
    )


