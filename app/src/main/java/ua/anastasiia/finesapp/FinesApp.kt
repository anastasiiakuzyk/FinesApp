package ua.anastasiia.finesapp

import ua.anastasiia.finesapp.ui.navigation.FineNavHost
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * Top level composable that represents screens for the application.
 */
@Composable
fun FinesApp(navController: NavHostController = rememberNavController()) {
    FineNavHost(navController = navController)
}
