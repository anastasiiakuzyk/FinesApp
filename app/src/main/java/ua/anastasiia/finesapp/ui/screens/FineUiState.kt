package ua.anastasiia.finesapp.ui.screens

import android.net.Uri
import ua.anastasiia.finesapp.data.FineWithCarAndViolations
import ua.anastasiia.finesapp.data.entity.CarInfo
import ua.anastasiia.finesapp.data.entity.Fine
import ua.anastasiia.finesapp.data.entity.Violation
import ua.anastasiia.finesapp.ui.screens.fine_entry.EMPTY_IMAGE_URI

data class FineUiState(
    val fineDetails: FineDetails = FineDetails(),
    val isEntryValid: Boolean = false
)

data class FineDetails(
    val id: Int = 0,
    val car_id: Int = 0,
    val location: String = "",
    val date: String = "",

    val plate: String = "",
    val make: String = "",
    val model: String = "",
    val color: String = "",
    val imageUri: Uri = EMPTY_IMAGE_URI,

    val violations: List<Violation> = listOf(),
    val sum: Double = 0.0
)

fun FineDetails.toFineWithCarAndViolations(): FineWithCarAndViolations =
    FineWithCarAndViolations(
        carInfo = CarInfo(
            id = car_id,
            plate = plate,
            make = make,
            model = model,
            color = color
        ),
        fine = Fine(
            fine_id = id,
            location = location,
            date = date,
            imageUri = imageUri.toString(),
            car_id = car_id
        ),
        violations = violations
    )

fun FineWithCarAndViolations.toFineUiState(isEntryValid: Boolean = false): FineUiState =
    FineUiState(
        fineDetails = this.toFineDetails(),
        isEntryValid = isEntryValid
    )

fun FineWithCarAndViolations.toFineDetails(): FineDetails = FineDetails(
    id = this.fine.fine_id,
    car_id = this.carInfo.id,
    location = this.fine.location,
    date = this.fine.date,
    plate = this.carInfo.plate,
    make = this.carInfo.make,
    model = this.carInfo.model,
    color = this.carInfo.color,
    imageUri = Uri.parse(this.fine.imageUri),
    violations = this.violations,
    sum = this.violations.sumOf { violation -> violation.price }
)

