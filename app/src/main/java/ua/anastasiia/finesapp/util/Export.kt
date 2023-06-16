package ua.anastasiia.finesapp.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import java.io.File
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.data.FineWithCarAndViolations

fun exportDatabaseToCSVFile(
    context: Context,
    fines: List<FineWithCarAndViolations>
) {
    val csvFile = generateFile(context)
    if (csvFile != null) {
        exportFinesToCSVFile(csvFile, fines)
        Toast.makeText(
            context, context.resources.getString(R.string.file_generated),
            Toast.LENGTH_LONG
        ).show()
        val intent = goToFileIntent(context, csvFile)
        startActivity(context, intent, null)
    } else {
        Toast.makeText(
            context,
            context.resources.getString(R.string.file_not_generated),
            Toast.LENGTH_LONG
        ).show()
    }
}

private fun generateFile(context: Context): File? {
    val csvFile = File(context.filesDir, "ValidatedFines.csv")
    csvFile.createNewFile()

    return if (csvFile.exists()) {
        csvFile
    } else {
        null
    }
}

private fun goToFileIntent(context: Context, file: File): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    val contentUri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val mimeType = context.contentResolver.getType(contentUri)
    intent.setDataAndType(contentUri, mimeType)
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    return intent
}

private fun exportFinesToCSVFile(csvFile: File, fines: List<FineWithCarAndViolations>) {
    csvWriter().open(csvFile, append = false) {
        // Header
        writeRow(
            listOf(
                "ID",
                "Date",
                "Location",
                "Plate",
                "Make",
                "Model",
                "Color",
                "Violations",
                "Price"
            )
        )
        fines.forEachIndexed { index, fine ->
            writeRow(
                listOf(
                    index,
                    fine.fine.date,
                    fine.fine.location,
                    fine.carInfo.plate,
                    fine.carInfo.make,
                    fine.carInfo.model,
                    fine.carInfo.color,
                    fine.violations.joinToString(", ", transform = { it.description }),
                    fine.violations.sumOf { it.price }
                )
            )
        }
    }
}