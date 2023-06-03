package ua.anastasiia.finesapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import ua.anastasiia.finesapp.ui.screens.home.HomeDestination
import ua.anastasiia.finesapp.ui.screens.home.HomeScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ua.anastasiia.finesapp.ui.screens.fine_entry.FineEntryDestination
import ua.anastasiia.finesapp.ui.screens.fine_details.FineDetailsDestination
import ua.anastasiia.finesapp.ui.screens.fine_details.FineDetailsScreen
import ua.anastasiia.finesapp.ui.screens.fine_edit.FineEditDestination
import ua.anastasiia.finesapp.ui.screens.fine_edit.FineEditScreen
import ua.anastasiia.finesapp.ui.screens.fine_entry.FineEntryScreen
import ua.anastasiia.finesapp.ui.screens.markers.MarkersDestination
import ua.anastasiia.finesapp.ui.screens.markers.MarkersScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun FineNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToFineEntry = { navController.navigate(FineEntryDestination.route) },
                navigateToFineUpdate = {
                    navController.navigate("${FineDetailsDestination.route}/${it}")
                },
                navigateToMarkers = {
                    navController.navigate(MarkersDestination.route)
                }
            )
        }
        composable(
            route = MarkersDestination.route
        ) {
            MarkersScreen(
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(route = FineEntryDestination.route) {
            FineEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = FineDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(FineDetailsDestination.fineIdArg) {
                type = NavType.IntType
            })
        ) {
            FineDetailsScreen(
                navigateToEditFine = { navController.navigate("${FineEditDestination.route}/$it") },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = FineEditDestination.routeWithArgs,
            arguments = listOf(navArgument(FineEditDestination.fineIdArg) {
                type = NavType.IntType
            })
        ) {
            FineEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
