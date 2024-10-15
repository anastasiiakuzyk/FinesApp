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
import ua.anastasiia.finesapp.ui.screens.fine.entry.FineEntryDestination
import ua.anastasiia.finesapp.ui.screens.fine.details.FineDetailsDestination
import ua.anastasiia.finesapp.ui.screens.fine.details.FineDetailsScreen
import ua.anastasiia.finesapp.ui.screens.fine.edit.FineEditDestination
import ua.anastasiia.finesapp.ui.screens.fine.edit.FineEditScreen
import ua.anastasiia.finesapp.ui.screens.fine.entry.FineEntryScreen
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
                navigateToFineUpdate = { fineId, carPlate ->
                    navController.navigate("${FineDetailsDestination.route}/$carPlate/$fineId")
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
            arguments = listOf(
                navArgument(FineDetailsDestination.CAR_PLATE_ARG) {
                    type = NavType.StringType
                },
                navArgument(FineDetailsDestination.TRAFFIC_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) {
            FineDetailsScreen(
                navigateToEditFine = { fineId, carPlate ->
                    navController.navigate("${FineEditDestination.route}/$carPlate/$fineId")
                },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = FineEditDestination.routeWithArgs,
            arguments = listOf(
                navArgument(FineEditDestination.CAR_PLATE_ARG) {
                    type = NavType.StringType
                },
                navArgument(FineEditDestination.TRAFFIC_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) {
            FineEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
