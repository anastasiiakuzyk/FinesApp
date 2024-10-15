package ua.anastasiia.finesapp.ui.screens.fine.entry

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import ua.anastasiia.finesapp.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChooseColor(
    recognizedColor: String,
    labelText: String,
    onColorChosen: (String) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    val colors = listOf("white", "black", "silver", "red", "blue", "green", "brown", "yellow")
    val colorsView = stringArrayResource(id = R.array.colors_array).toList()
    val colorsToView = remember { mutableStateListOf<String>() }

    colorsView.forEach {
        colorsToView.add(it)
    }

    var selectedOptionText by remember { mutableStateOf("") }
    selectedOptionText =
        colors.firstOrNull { color -> color.equals(recognizedColor, true) } ?: ""

    if (enabled) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                value = if (selectedOptionText == "") ""
                else colorsView[colors.indexOf(selectedOptionText)],
                onValueChange = { },
                label = { Text(labelText) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                    onColorChosen(selectedOptionText)
                }
            ) {
                colors.forEach { selectionOption ->
                    DropdownMenuItem(
                        onClick = {
                            selectedOptionText = selectionOption
                            expanded = false
                            onColorChosen(selectedOptionText)
                        }
                    ) {
                        Text(text = colorsView[colors.indexOf(selectionOption)])
                    }
                }
            }
        }
    } else {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            value = if (selectedOptionText == "") ""
            else colorsView[colors.indexOf(selectedOptionText)],
            onValueChange = { },
            label = { Text(labelText) }
        )
    }
}