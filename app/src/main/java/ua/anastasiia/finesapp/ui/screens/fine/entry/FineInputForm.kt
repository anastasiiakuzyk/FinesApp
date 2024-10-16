package ua.anastasiia.finesapp.ui.screens.fine.entry

import android.content.Context
import android.net.Uri
import java.io.File
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ua.anastasiia.finesapp.LoadingUI
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.photo.camera.CameraCapture
import ua.anastasiia.finesapp.photo.gallery.GallerySelect
import ua.anastasiia.finesapp.rest.mapper.toViolationType
import ua.anastasiia.finesapp.ui.screens.CarViewModel
import ua.anastasiia.finesapp.ui.screens.FineUIDetails
import ua.anastasiia.finesapp.ui.screens.FineUIState
import ua.anastasiia.finesapp.ui.screens.LocationMap
import ua.anastasiia.finesapp.ui.screens.fine.details.DeleteConfirmationDialog
import ua.anastasiia.finesapp.util.getFileFromUri
import ua.anastasiia.finesapp.util.isDateValid
import ua.anastasiia.finesapp.util.isLocationValid
import ua.anastasiia.finesapp.util.isMakeModelValid
import ua.anastasiia.finesapp.util.isPlateValid
import ua.anastasiia.finesapp.web.model.Results

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(
    ExperimentalCoilApi::class, ExperimentalPermissionsApi::class, ExperimentalCoroutinesApi::class
)
@Composable
fun FineInputForm(
    fineUiState: FineUIState,
    fineUIDetails: FineUIDetails,
    modifier: Modifier = Modifier,
    onValueChange: (FineUIDetails) -> Unit = {},
    isViewMode: Boolean = false,
    isEditMode: Boolean = false,
    isCreateMode: Boolean = false,
    isPhotoTaking: Boolean = false,
    viewModel: FineViewModel,
    carViewModel: CarViewModel,
    isLoading: MutableState<Boolean>,
    onSaveClick: () -> Unit,
    onDelete: () -> Unit = {},
    onValidate: (Boolean) -> Unit = {}
) {

    var imageUri by remember { mutableStateOf(EMPTY_IMAGE_URI) }
    val context = LocalContext.current
    val result: Results? = carViewModel.response.collectAsState().value.results.find { true }

    when {
        isLoading.value -> {
            LoadingUI()
        }

        else -> {
            if (isPhotoTaking) {
                if (imageUri != EMPTY_IMAGE_URI) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            if (result != null) {
                                val modelMake = result.model_make.find { true }
                                val color = result.color.find { true }
                                onValueChange(
                                    fineUIDetails.copy(
                                        plate = result.plate?.uppercase().toString(),
                                        model = modelMake?.model ?: "",
                                        make = modelMake?.make ?: "",
                                        color = color?.color ?: "",
                                        imageUri = imageUri
                                    )
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    context.resources.getString(R.string.noResults),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Text(stringResource(R.string.fill), color = Color.White)
                        }
                        Button(onClick = {
                            imageUri = EMPTY_IMAGE_URI
                            onValueChange(
                                fineUIDetails.copy(
                                    plate = "", make = "", model = "", color = ""
                                )
                            )
                        }) {
                            Text(stringResource(R.string.remove_image), color = Color.White)
                        }
                        Button(onClick = {
                            onValueChange(
                                fineUIDetails.copy(
                                    plate = "", make = "", model = "", color = ""
                                )
                            )
                        }) {
                            Text(stringResource(R.string.remove), color = Color.White)
                        }
                    }

                } else {
                    var showGallerySelect by remember { mutableStateOf(false) }
                    if (showGallerySelect) {
                        GallerySelect(
                            modifier = modifier,
                            onImageUri = { uri ->
                                val savedFile: File? = saveUriContentToFile(context, uri)
                                if (savedFile != null) {

                                    imageUri = savedFile.toUri()
                                    carViewModel.getResultsFromFile(savedFile)

                                    onValueChange(fineUIDetails.copy(imageUri = imageUri))
                                }
                            }
                        )
                    } else {
                        Box(modifier = modifier) {
                            CameraCapture(
                                modifier = modifier,
                                onImageFile = { file ->
                                    imageUri = file.toUri()
                                    carViewModel.getResultsFromFile(context.getFileFromUri(imageUri))
                                    onValueChange(fineUIDetails.copy(imageUri = imageUri))
                                })
                            Button(modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp),
                                onClick = {
                                    showGallerySelect = true
                                }) {
                                Text(
                                    stringResource(R.string.select_from_gallery),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LocationMap(
                    viewModel = viewModel,
                    location = fineUIDetails.location
                )
                val locationValue = if (isViewMode) viewModel.uiState.value.fineUIDetails.location else fineUIDetails.location
                var locationError by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = locationValue,
                    onValueChange = {
                        onValueChange(fineUIDetails.copy(location = it))
                        locationError = !isLocationValid
                    },
                    isError = locationError,
                    supportingText = {
                        if (locationError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.no_location),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    label = { Text(stringResource(R.string.location)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isViewMode,
                    singleLine = true
                )

                viewModel.updateDateTime()

                val dateValue = if (isCreateMode) viewModel.uiState.value.fineUIDetails.date else fineUIDetails.date
                var dateError by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = dateValue,
                    onValueChange = {
                        onValueChange(fineUIDetails.copy(date = it))
                        dateError = !isDateValid(it).first
                    },
                    isError = dateError,
                    supportingText = {
                        if (dateError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = if (isDateValid(dateValue).second.equals("future_date"))
                                    stringResource(R.string.future_date)
                                else {
                                    stringResource(R.string.invalid_format)

                                },
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    label = { Text(stringResource(R.string.date)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isViewMode,
                    singleLine = true
                )

                var plateError by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = fineUIDetails.plate,
                    onValueChange = {
                        onValueChange(fineUIDetails.copy(plate = it))
                        plateError = !isPlateValid(it)
                    },
                    isError = plateError,
                    supportingText = {
                        if (plateError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.no_plate),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    label = { Text(stringResource(R.string.plate)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isViewMode || isEditMode,
                    singleLine = true
                )

                var makeError by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = fineUIDetails.make,
                    onValueChange = {
                        onValueChange(fineUIDetails.copy(make = it))
                        makeError = !isMakeModelValid(it)
                    },
                    isError = makeError,
                    supportingText = {
                        if (makeError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.no_make),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    label = { Text(stringResource(R.string.make)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isViewMode || isEditMode,
                    singleLine = true
                )

                var modelError by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = fineUIDetails.model,
                    onValueChange = {
                        onValueChange(fineUIDetails.copy(model = it))
                        modelError = !isMakeModelValid(it)
                    },
                    isError = modelError,
                    supportingText = {
                        if (modelError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.no_model),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }, label = { Text(stringResource(R.string.model)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isViewMode || isEditMode,
                    singleLine = true
                )

                ChooseColor(
                    labelText = stringResource(R.string.color),
                    onColorChosen = {
                        onValueChange(fineUIDetails.copy(color = it))
                    },
                    recognizedColor = fineUIDetails.color,
                    enabled = isCreateMode
                )

                Image(
                    modifier = Modifier.size(400.dp),
                    painter = rememberAsyncImagePainter(if (isCreateMode) imageUri else fineUIDetails.imageUri),
                    contentDescription = stringResource(R.string.captured_image),
                    contentScale = ContentScale.FillWidth
                )

                if (isEditMode || isCreateMode) {
                    SelectViolations(
                        labelText = stringResource(R.string.violations),
                        onViolationsChosen = { violations ->
                            onValueChange(
                                fineUIDetails.copy(violations = violations,
                                    sum = violations.sumOf { it.price })
                            )
                        },
                        selectedViolationIds = fineUIDetails.violations.map { violation ->
                            violation.toViolationType().ordinal
                        }
                    )
                } else {
                    SelectedViolations(fineUIDetails, modifier)
                }

                OutlinedTextField(
                    value = "${fineUIDetails.sum}${stringResource(R.string.currency)}",
                    onValueChange = {},
                    label = { Text(stringResource(R.string.sum)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    singleLine = true
                )

                if (isEditMode || isCreateMode) {
                    Button(
                        onClick = onSaveClick,
                        enabled = fineUiState.isEntryValid,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.save_action), color = Color.White)
                    }
                } else {
                    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            onClick = { deleteConfirmationRequired = true }
                        ) {
                            Text(stringResource(R.string.delete), color = Color.White)
                        }
                        if (deleteConfirmationRequired) {
                            DeleteConfirmationDialog(onDeleteConfirm = {
                                deleteConfirmationRequired = false
                                onDelete()
                            }, onDeleteCancel = { deleteConfirmationRequired = false })
                        }
                        Button(
                            onClick = { onValidate(!fineUIDetails.valid) }
                        ) {
                            Text(
                                stringResource(if (!fineUIDetails.valid) R.string.validate else R.string.invalidate),
                                color = Color.White
                            )
                        }
                        Text("")
                    }
                }
            }
        }
    }
}


fun saveUriContentToFile(context: Context, contentUri: Uri): File? {
    val contentResolver = context.contentResolver
    val fileName = "${System.currentTimeMillis()}.jpg"
    val file = File(context.cacheDir, fileName)
    file.createNewFile()

    try {
        contentResolver.openInputStream(contentUri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    } catch (e: Exception) {
        Log.e("saveUriContentToFile", "Error copying content to file", e)
        return null
    }
}
