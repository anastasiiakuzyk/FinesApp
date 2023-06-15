package ua.anastasiia.finesapp.ui.screens.fine_entry

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.data.dao.Violations
import ua.anastasiia.finesapp.data.entity.Violation
import ua.anastasiia.finesapp.ui.screens.FineDetails

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SelectViolations(
    labelText: String,
    onViolationsChosen: (List<Violation>) -> Unit,
    modifier: Modifier = Modifier,
    selectedViolationIds: List<Int> = emptyList(),
) {
    var expanded by remember { mutableStateOf(false) }
    val violations: List<Violation> = Violations.violations

    val isEnabled by rememberUpdatedState { violations.isNotEmpty() }

    val selectedViolations = remember { mutableStateListOf<Int>() }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    selectedViolationIds.forEach {
        selectedViolations.add(it)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier
    ) {
        val selectedSummary = when (selectedViolations.distinct().size) {
            0 -> ""
            else -> "${stringResource(R.string.selected)}: ${selectedViolations.distinct().size}"
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
                onViolationsChosen(violations.filter { it.violation_id in selectedViolations.distinct() })
            }
        ) {
            for (violation in violations) {

                //use derivedStateOf to evaluate if it is checked
                val checked = remember {
                    derivedStateOf { violation.violation_id in selectedViolations.distinct() }
                }.value

                DropdownMenuItem(
                    onClick = {
                        if (!checked) {
                            selectedViolations.add(violation.violation_id)
                        } else {
                            selectedViolations.remove(violation.violation_id)
                        }
                        onViolationsChosen(violations.filter { it.violation_id in selectedViolations.distinct() })
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { newCheckedState ->
                                if (newCheckedState) {
                                    selectedViolations.add(violation.violation_id)
                                } else {
                                    selectedViolations.remove(violation.violation_id)
                                }
                                onViolationsChosen(violations.filter { it.violation_id in selectedViolations.distinct() })
                            },
                        )
                        Text(
                            text = stringArrayResource(id = R.array.violations_array)[violation.violation_id - 1] +
                                    " - ${violation.price}${stringResource(R.string.currency)}"
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SelectedViolations(
    fineDetails: FineDetails,
    modifier: Modifier
) {
    Card(
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.dp, Color.Gray),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(8.dp)) {
            fineDetails.violations.forEachIndexed { i, violation ->
                Text(
                    text = stringArrayResource(id = R.array.violations_array)[violation.violation_id - 1] +
                            " - ${violation.price}${stringResource(R.string.currency)}",
                    modifier = modifier.padding(8.dp)
                )
                if (fineDetails.violations.size - 1 > i)
                    Divider(modifier.padding(8.dp))
            }
        }
    }
}



