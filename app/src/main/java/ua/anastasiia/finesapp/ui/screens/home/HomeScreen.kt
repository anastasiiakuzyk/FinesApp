package ua.anastasiia.finesapp.ui.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.data.FineWithCarAndViolations
import ua.anastasiia.finesapp.ui.navigation.NavigationDestination
import ua.anastasiia.finesapp.ui.screens.FinesTopAppBar
import ua.anastasiia.finesapp.ui.theme.Teal100
import ua.anastasiia.finesapp.ui.theme.Teal200
import java.text.NumberFormat

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@Composable
fun HomeScreen(
    navigateToFineEntry: () -> Unit,
    navigateToMarkers: () -> Unit,
    navigateToFineUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    Log.d("homeUiState", homeUiState.toString())

    Scaffold(
        topBar = {
            FinesTopAppBar(
                title = stringResource(HomeDestination.titleRes), canNavigateBack = false,
                navigateToMarkers = navigateToMarkers
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToFineEntry, modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.fine_entry_title),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
    ) { innerPadding ->
        HomeBody(
            fineList = homeUiState.fineList,
            onFineClick = navigateToFineUpdate,
            modifier = modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun HomeBody(
    fineList: List<FineWithCarAndViolations>,
    onFineClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (fineList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_fine_description),
                style = MaterialTheme.typography.subtitle2
            )
        } else {
            FineList(fineList = fineList, onFineClick = { onFineClick(it.fine.fine_id) })
        }
    }
}

@Composable
private fun FineList(
    fineList: List<FineWithCarAndViolations>,
    onFineClick: (FineWithCarAndViolations) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = fineList, key = { it.fine.fine_id }) { fine ->
            Fine(fine = fine, onFineClick = onFineClick)
            Divider()
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun Fine(
    fine: FineWithCarAndViolations, onFineClick: (FineWithCarAndViolations) -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        backgroundColor = if (fine.fine.valid) Teal100 else Color.White
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onFineClick(fine) }
                .padding(8.dp)) {

            Image(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = Color.Black, shape = CircleShape),
                painter = rememberImagePainter(fine.fine.imageUri),
                contentDescription = stringResource(R.string.captured_image),
                contentScale = ContentScale.Crop
            )

            Text(
                text = fine.carInfo.plate, fontWeight = FontWeight.Bold
            )
            Text(
                text = "${fine.violations.sumOf { it.price }}${stringResource(R.string.currency)}"
            )
        }
    }
}
