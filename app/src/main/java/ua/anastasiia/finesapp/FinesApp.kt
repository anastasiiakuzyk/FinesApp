package ua.anastasiia.finesapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ua.anastasiia.finesapp.ui.navigation.FineNavHost

/**
 * Top level composable that represents screens for the application.
 */
@Composable
fun FinesApp(navController: NavHostController = rememberNavController()) {
    FineNavHost(navController = navController)
}
