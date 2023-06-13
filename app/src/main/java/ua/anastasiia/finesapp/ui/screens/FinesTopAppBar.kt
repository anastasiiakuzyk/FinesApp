package ua.anastasiia.finesapp.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Language
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import ua.anastasiia.finesapp.R


/**
 * App bar to display title and conditionally display the back navigation.
 */
@Composable
fun FinesTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
    navigateToMarkers: () -> Unit = {}
) {
    if (canNavigateBack) {
        TopAppBar(
            title = { Text(title) },
            modifier = modifier,
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        )
    } else {
        TopAppBar(
            title = { Text(title) },
            modifier = modifier,
            navigationIcon = {
//                IconButton(
//                    onClick = {
//                        AppCompatDelegate.setApplicationLocales(
//                            LocaleListCompat.forLanguageTags("uk")
//                        )
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.Language,
//                        contentDescription = stringResource(R.string.changeToUk)
//                    )
//                }
                LocaleDropdownMenu()
            },
            actions = {
                IconButton(onClick = navigateToMarkers) {
                    Icon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = stringResource(R.string.markers)
                    )
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LocaleDropdownMenu() {
    val localeOptions = mapOf(
        R.string.en to "en",
        R.string.uk to "uk"
    ).mapKeys { stringResource(it.key) }

    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Language,
                    contentDescription = null,
                    tint = Color.White
                )
            },
            readOnly = true,
            value = stringResource(R.string.language),
            onValueChange = { },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            localeOptions.keys.forEach { selectionLocale ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        // set app locale given the user's selected locale
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(
                                localeOptions[selectionLocale]
                            )
                        )
                    },
                    content = { Text(selectionLocale) }
                )
            }
        }
    }
}