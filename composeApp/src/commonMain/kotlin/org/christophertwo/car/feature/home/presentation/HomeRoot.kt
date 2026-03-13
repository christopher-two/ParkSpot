package org.christophertwo.car.feature.home.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.christophertwo.car.core.common.AppTab
import org.christophertwo.car.core.ui.CarLocateTheme
import org.christophertwo.car.feature.navigation.wrappers.HomeNavigationWrapper

@Composable
fun HomeRoot(
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = state.selectedTab == tab,
                        onClick = { onAction(HomeAction.OnNavigateToTab(tab)) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text(text = tab.label) }
                    )
                }
            }
        },
        content = { innerPadding ->
            HomeNavigationWrapper(
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@Preview
@Composable
private fun Preview() {
    CarLocateTheme {
        HomeScreen(
            state = HomeState(),
            onAction = {}
        )
    }
}