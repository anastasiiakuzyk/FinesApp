package ua.anastasiia.finesapp.ui.screens.fine_entry

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.toSize
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.data.dao.Violations
import ua.anastasiia.finesapp.data.entity.Violation

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MultiComboBox(
    labelText: String,
    onOptionsChosen: (List<Violation>) -> Unit,
    modifier: Modifier = Modifier,
    selectedIds: List<Int> = emptyList(),
) {
    var expanded by remember { mutableStateOf(false) }
    val options: List<Violation> = Violations.violations

    val optionsLocal: List<Violation> = listOf(
        Violation(options[0].violation_id, stringResource(R.string.sel1), options[0].price),
        Violation(options[1].violation_id, stringResource(R.string.sel2), options[1].price),
        Violation(options[2].violation_id, stringResource(R.string.sel3), options[2].price),
        Violation(options[3].violation_id, stringResource(R.string.sel4), options[3].price),
        Violation(options[4].violation_id, stringResource(R.string.sel5), options[4].price),
        Violation(options[5].violation_id, stringResource(R.string.sel6), options[5].price),
        Violation(options[6].violation_id, stringResource(R.string.sel7), options[6].price),
    )

    val isEnabled by rememberUpdatedState { options.isNotEmpty() }

    val selectedOptionsList = remember { mutableStateListOf<Int>() }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    selectedIds.forEach {
        selectedOptionsList.add(it)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier
    ) {
        val selectedSummary = when (selectedOptionsList.distinct().size) {
            0 -> ""
            else -> "${stringResource(R.string.selected)}: ${selectedOptionsList.distinct().size}"
        }
        OutlinedTextField(
            enabled = isEnabled(),
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    textFieldSize = coordinates.size.toSize()
                },
            readOnly = true,
            value = selectedSummary,
            onValueChange = {},
            label = { Text(text = labelText) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                onOptionsChosen(options.filter { it.violation_id in selectedOptionsList.distinct() })
            }
        ) {
            for (option in options) {

                //use derivedStateOf to evaluate if it is checked
                val checked = remember {
                    derivedStateOf { option.violation_id in selectedOptionsList.distinct() }
                }.value

                DropdownMenuItem(
                    onClick = {
                        if (!checked) {
                            selectedOptionsList.add(option.violation_id)
                        } else {
                            selectedOptionsList.remove(option.violation_id)
                        }
                        onOptionsChosen(options.filter { it.violation_id in selectedOptionsList.distinct() })
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { newCheckedState ->
                                if (newCheckedState) {
                                    selectedOptionsList.add(option.violation_id)
                                } else {
                                    selectedOptionsList.remove(option.violation_id)
                                }
                                onOptionsChosen(options.filter { it.violation_id in selectedOptionsList.distinct() })
                            },
                        )
                        var description: String = stringResource(R.string.sel1)
                        when (option.violation_id) {
                            1 -> description = stringResource(R.string.sel1)
                            2 -> description = stringResource(R.string.sel2)
                            3 -> description = stringResource(R.string.sel3)
                            4 -> description = stringResource(R.string.sel4)
                            5 -> description = stringResource(R.string.sel5)
                            6 -> description = stringResource(R.string.sel6)
                            7 -> description = stringResource(R.string.sel7)
                        }
                        Text(text = "$description - ${option.price}${stringResource(R.string.currency)}")
                    }
                }
            }
        }
    }
}
