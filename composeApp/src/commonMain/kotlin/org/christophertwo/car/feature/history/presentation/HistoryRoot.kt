package org.christophertwo.car.feature.history.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.christophertwo.car.core.ui.CarLocateTheme
import org.christophertwo.car.feature.history.presentation.components.EmptyHistoryPlaceholder
import org.christophertwo.car.feature.history.presentation.components.HistorySearchBar
import org.christophertwo.car.feature.history.presentation.components.ParkingSpotCard

@Composable
fun HistoryRoot(
    viewModel: HistoryViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HistoryScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun HistoryScreen(
    state: HistoryState,
    onAction: (HistoryAction) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            HistorySearchBar(
                query = state.searchQuery,
                onQueryChange = { onAction(HistoryAction.OnSearchQueryChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
        }

        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (state.filteredSpots.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    EmptyHistoryPlaceholder()
                }
            }
        } else {
            items(
                items = state.filteredSpots,
                key = { it.id },
            ) { spot ->
                ParkingSpotCard(
                    spot = spot,
                    onClick = { onAction(HistoryAction.OnParkingSpotClicked(spot.id)) },
                    onDelete = { onAction(HistoryAction.OnDeleteParkingSpot(spot.id)) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CarLocateTheme {
        HistoryScreen(
            state = HistoryState(),
            onAction = {}
        )
    }
}

