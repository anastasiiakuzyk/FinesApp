package ua.anastasiia.finesapp.ui.screens.fine_entry

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
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
import androidx.compose.ui.graphics.Color
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
import ua.anastasiia.finesapp.data.entity.Violation
import ua.anastasiia.finesapp.photo.camera.CameraCapture
import ua.anastasiia.finesapp.photo.gallery.GallerySelect
import ua.anastasiia.finesapp.ui.screens.CarViewModel
import ua.anastasiia.finesapp.ui.screens.FineDetails
import ua.anastasiia.finesapp.ui.screens.FineUiState
import ua.anastasiia.finesapp.ui.screens.LocationMap
import ua.anastasiia.finesapp.ui.screens.fine_details.DeleteConfirmationDialog
import ua.anastasiia.finesapp.util.getFileFromUri
import ua.anastasiia.finesapp.web.model.Results
import java.io.File

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
    onDelete: () -> Unit = {}
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
                LocationMap(viewModel = viewModel)
                val locationValue = if (isViewMode) viewModel.location else fineDetails.location
                OutlinedTextField(
                    value = locationValue,
                    onValueChange = { onValueChange(fineDetails.copy(location = it)) },
                    label = { Text(stringResource(R.string.location)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    singleLine = true
                )
                viewModel.updateDateTime()
                val dateValue = if (isViewMode) viewModel.date else fineDetails.date
                OutlinedTextField(
                    value = dateValue,
                    onValueChange = { onValueChange(fineDetails.copy(date = it)) },
                    label = { Text(stringResource(R.string.date)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    singleLine = true
                )
                OutlinedTextField(
                    value = fineDetails.plate,
                    onValueChange = { onValueChange(fineDetails.copy(plate = it)) },
                    label = { Text(stringResource(R.string.plate)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    singleLine = true
                )
                OutlinedTextField(
                    value = fineDetails.make,
                    onValueChange = { onValueChange(fineDetails.copy(make = it)) },
                    label = { Text(stringResource(R.string.make)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    singleLine = true
                )
                OutlinedTextField(
                    value = fineDetails.model,
                    onValueChange = { onValueChange(fineDetails.copy(model = it)) },
                    label = { Text(stringResource(R.string.model)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    singleLine = true
                )
                OutlinedTextField(
                    value = fineDetails.color,
                    onValueChange = { onValueChange(fineDetails.copy(color = it)) },
                    label = { Text(stringResource(R.string.color)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    singleLine = true
                )
                Image(
                    modifier = Modifier.size(400.dp),
                    painter = rememberImagePainter(if (isViewMode) imageUri else fineDetails.imageUri),
                    contentDescription = stringResource(R.string.captured_image),
                    contentScale = ContentScale.FillWidth
                )

                if (enabled) {
                    MultiComboBox(labelText = stringResource(R.string.violations),
                        onOptionsChosen = { violations ->
                            onValueChange(
                                fineDetails.copy(violations = violations,
                                    sum = violations.sumOf { it.price })
                            )
                        },
                        selectedIds = fineDetails.violations.map { violation ->
                            violation.violation_id
                        })

                    OutlinedTextField(
                        value = "${fineDetails.sum}${stringResource(R.string.currency)}",
                        onValueChange = {},
                        label = { Text(stringResource(R.string.sum)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        readOnly = true,
                        singleLine = true
                    )

                    Button(
                        onClick = onSaveClick,
                        enabled = fineUiState.isEntryValid,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.save_action))
                    }
                } else {

                    if (fineDetails.violations.isNotEmpty()) {
                        Card(
                            shape = RoundedCornerShape(5.dp),
                            border = BorderStroke(1.dp, Color.Gray),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(Modifier.padding(8.dp)) {
                                fineDetails.violations.forEachIndexed { i, it ->
                                    var description: String = stringResource(R.string.sel1)
                                    when (it.violation_id) {
                                        1 -> description = stringResource(R.string.sel1)
                                        2 -> description = stringResource(R.string.sel2)
                                        3 -> description = stringResource(R.string.sel3)
                                        4 -> description = stringResource(R.string.sel4)
                                        5 -> description = stringResource(R.string.sel5)
                                        6 -> description = stringResource(R.string.sel6)
                                        7 -> description = stringResource(R.string.sel7)
                                    }

                                    Text(text = "$description - ${it.price}", modifier.padding(8.dp))
                                    if (fineDetails.violations.size - 1 > i) Divider(
                                        modifier.padding(
                                            8.dp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = "${fineDetails.sum}${stringResource(R.string.currency)}",
                        onValueChange = {},
                        label = { Text(stringResource(R.string.sum)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        readOnly = true,
                        singleLine = true
                    )

                    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

                    OutlinedButton(
                        onClick = { deleteConfirmationRequired = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                    if (deleteConfirmationRequired) {
                        DeleteConfirmationDialog(onDeleteConfirm = {
                            deleteConfirmationRequired = false
                            onDelete()
                        }, onDeleteCancel = { deleteConfirmationRequired = false })
                    }
                }
            }
        }
    }
}