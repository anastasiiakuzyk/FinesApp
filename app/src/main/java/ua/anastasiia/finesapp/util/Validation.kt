package ua.anastasiia.finesapp.util

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import ua.anastasiia.finesapp.ui.screens.FineUiState
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Pattern


@SuppressLint("SimpleDateFormat")
val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")

var isLocationValid by mutableStateOf(false)

fun isDateValid(date: String) = try {
    val currentDate = Date()
    val dateValid = sdf.parse(date)
    currentDate.after(dateValid)
} catch (e: ParseException) {
    false
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
