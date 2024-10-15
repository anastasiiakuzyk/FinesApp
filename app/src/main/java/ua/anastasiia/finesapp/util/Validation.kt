package ua.anastasiia.finesapp.util

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ua.anastasiia.finesapp.ui.screens.FineUIDetails
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Pattern


@SuppressLint("SimpleDateFormat")
val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")

var isLocationValid by mutableStateOf(false)

fun isDateValid(date: String): Pair<Boolean, String> {

    return true to "future_date"
    try {
        val currentDate = Date()
        val dateValid = sdf.parse(date)
        return Pair(currentDate.after(dateValid), "future_date")
    } catch (e: ParseException) {
        return Pair(false, "invalid_format")
    }
}

fun isPlateValid(plate: String?): Boolean {
    if (plate.isNullOrEmpty()) {
        return false
    }
    return Pattern
        .compile(
            "^(?=(.*[A-ZА-ЯІЇҐЄ]){2,})([A-ZА-ЯІЇҐЄ0-9]{3,8})\$"
        ).matcher(plate).find()
}


fun isMakeModelValid(makeModel: String?): Boolean {
    if (makeModel.isNullOrEmpty()) {
        return false
    }
    return Pattern
        .compile(
            "^.{1,50}\$"
        ).matcher(makeModel).find()
}

fun validateInput(fineUIDetails: FineUIDetails): Boolean =
    with(fineUIDetails) {
        val isLocationValid = location.isNotBlank() && isLocationValid
        Log.d("Validation", "Location valid: $isLocationValid")

        val isDateValid = date.isNotBlank() && isDateValid(date).first
        Log.d("Validation", "Date valid: $isDateValid")

        val isPlateValid = plate.isNotBlank() && isPlateValid(plate)
        Log.d("Validation", "Plate valid: $isPlateValid")

        val isMakeValid = make.isNotBlank() && isMakeModelValid(make)
        Log.d("Validation", "Make valid: $isMakeValid")

        val isModelValid = model.isNotBlank() && isMakeModelValid(model)
        Log.d("Validation", "Model valid: $isModelValid")

        val isColorValid = color.isNotBlank()
        Log.d("Validation", "Color valid: $isColorValid")

        val areViolationsValid = violations.isNotEmpty()
        Log.d("Validation", "Violations valid: $areViolationsValid")

        isLocationValid && isDateValid && isPlateValid && isMakeValid && isModelValid && isColorValid && areViolationsValid
    }