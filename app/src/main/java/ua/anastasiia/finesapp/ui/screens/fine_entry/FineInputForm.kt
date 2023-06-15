package ua.anastasiia.finesapp.ui.screens.fine_entry

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
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ua.anastasiia.finesapp.LoadingUI
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.photo.camera.CameraCapture
import ua.anastasiia.finesapp.photo.gallery.GallerySelect
import ua.anastasiia.finesapp.ui.screens.CarViewModel
import ua.anastasiia.finesapp.ui.screens.FineDetails
import ua.anastasiia.finesapp.ui.screens.FineUiState
import ua.anastasiia.finesapp.ui.screens.LocationMap
import ua.anastasiia.finesapp.ui.screens.fine_details.DeleteConfirmationDialog
import ua.anastasiia.finesapp.util.getFileFromUri
import ua.anastasiia.finesapp.util.isDateValid
import ua.anastasiia.finesapp.util.isLocationValid
import ua.anastasiia.finesapp.util.isMakeModelValid
import ua.anastasiia.finesapp.util.isPlateValid
import ua.anastasiia.finesapp.web.model.Results

@OptIn(
    ExperimentalCoilApi::class, ExperimentalPermissionsApi::class, ExperimentalCoroutinesApi::class
)
@Composable
fun FineInputForm(
    fineUiState: FineUiState,
    fineDetails: FineDetails,
    modifier: Modifier = Modifier,
    onValueChange: (FineDetails) -> Unit = {},
    enabled: Boolean = true,
    isViewMode: Boolean = true,
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
            if (isViewMode) {
                if (imageUri != EMPTY_IMAGE_URI) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            if (result != null) {
                                val modelMake = result.model_make.find { true }
                                val color = result.color.find { true }
                                onValueChange(
                                    fineDetails.copy(
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
                            Text(stringResource(R.string.fill))
                        }
                        Button(onClick = {
                            imageUri = EMPTY_IMAGE_URI
                            onValueChange(
                                fineDetails.copy(
                                    plate = "", make = "", model = "", color = ""
                                )
                            )
                        }) {
                            Text(stringResource(R.string.remove_image))
                        }
                        Button(onClick = {
                            onValueChange(
                                fineDetails.copy(
                                    plate = "", make = "", model = "", color = ""
                                )
                            )
                        }) {
                            Text(stringResource(R.string.remove))
                        }
                    }

                } else {
                    var showGallerySelect by remember { mutableStateOf(false) }
                    if (showGallerySelect) {
                        GallerySelect(modifier = modifier, onImageUri = { uri ->
                            showGallerySelect = false
                            imageUri = uri
                            Log.d("imageUriG", imageUri.toString())
                            carViewModel.getResultsFromFile(context.getFileFromUri(imageUri))
                        })
                    } else {
                        Box(modifier = modifier) {
                            CameraCapture(
                                modifier = modifier,
                                onImageFile = { file ->
//                                Log.e("file.str", file.toString())
//                                imageUri = file
                                    imageUri = file.toUri()
//                                carViewModel.getResultsFromFile(file)
                                    carViewModel.getResultsFromFile(context.getFileFromUri(imageUri))
                                })
                            Button(modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp),
                                onClick = {
                                    showGallerySelect = true
                                }) {
                                Text(stringResource(R.string.select_from_gallery))
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
                    location = fineDetails.location
                )
                val locationValue = if (isViewMode) viewModel.location else fineDetails.location
                var locationError by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = locationValue,
                    onValueChange = {
                        onValueChange(fineDetails.copy(location = it))
                        locationError = !isLocationValid
                    },
                    isError = locationError,
                    label = { Text(stringResource(R.string.location)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !enabled,
                    singleLine = true
                )

                viewModel.updateDateTime()
                val dateValue = if (isViewMode) viewModel.date else fineDetails.date
                var dateError by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = dateValue,
                    onValueChange = {
                        onValueChange(fineDetails.copy(date = it))
                        dateError = !isDateValid(it)
                    },
                    isError = dateError,
                    label = { Text(stringResource(R.string.date)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !enabled,
                    singleLine = true
                )

                var plateError by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = fineDetails.plate,
                    onValueChange = {
                        onValueChange(fineDetails.copy(plate = it))
                        plateError = !isPlateValid(it)
                    },
                    isError = plateError,
                    label = { Text(stringResource(R.string.plate)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !enabled,
                    singleLine = true
                )

                var makeError by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = fineDetails.make,
                    onValueChange = {
                        onValueChange(fineDetails.copy(make = it))
                        makeError = !isMakeModelValid(it)
                    },
                    isError = makeError,
                    label = { Text(stringResource(R.string.make)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !enabled,
                    singleLine = true
                )

                var modelError by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = fineDetails.model,
                    onValueChange = {
                        onValueChange(fineDetails.copy(model = it))
                        modelError = !isMakeModelValid(it)
                    },
                    isError = modelError,
                    label = { Text(stringResource(R.string.model)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !enabled,
                    singleLine = true
                )

                ChooseColor(
                    labelText = stringResource(R.string.color),
                    onColorChosen = {
                        onValueChange(fineDetails.copy(color = it))
                    },
                    recognizedColor = fineDetails.color,
                    enabled = enabled
                )

                Image(
                    modifier = Modifier.size(400.dp),
                    painter = rememberImagePainter(if (isViewMode) imageUri else fineDetails.imageUri),
                    contentDescription = stringResource(R.string.captured_image),
                    contentScale = ContentScale.FillWidth
                )
                if (enabled) {
                    SelectViolations(
                        labelText = stringResource(R.string.violations),
                        onViolationsChosen = { violations ->
                            onValueChange(
                                fineDetails.copy(violations = violations,
                                    sum = violations.sumOf { it.price })
                            )
                        },
                        selectedViolationIds = fineDetails.violations.map { violation ->
                            violation.violation_id
                        }
                    )
                } else {
                    SelectedViolations(fineDetails, modifier)
                }

                OutlinedTextField(
                    value = "${fineDetails.sum}${stringResource(R.string.currency)}",
                    onValueChange = {},
                    label = { Text(stringResource(R.string.sum)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    singleLine = true
                )

                if (enabled) {
                    Button(
                        onClick = onSaveClick,
                        enabled = fineUiState.isEntryValid,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.save_action))
                    }
                } else {
                    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        OutlinedButton(
                            onClick = { deleteConfirmationRequired = true }
                        ) {
                            Text(stringResource(R.string.delete))
                        }
                        if (deleteConfirmationRequired) {
                            DeleteConfirmationDialog(onDeleteConfirm = {
                                deleteConfirmationRequired = false
                                onDelete()
                            }, onDeleteCancel = { deleteConfirmationRequired = false })
                        }
                        OutlinedButton(
                            onClick = { onValidate(!fineDetails.valid) }
                        ) {
                            Text(stringResource(if (!fineDetails.valid) R.string.validate else R.string.invalidate))
                        }
                    }
                }
            }
        }
    }
}

