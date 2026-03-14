package org.christophertwo.car.feature.onboarding.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.MapMarkerAlt
import compose.icons.fontawesomeicons.solid.Wifi
import kotlinx.coroutines.flow.distinctUntilChanged
import org.christophertwo.car.core.ui.CarLocateTheme
import org.christophertwo.car.feature.onboarding.presentation.components.OnboardingNavigationButtons
import org.christophertwo.car.feature.onboarding.presentation.components.OnboardingPageContent
import org.christophertwo.car.feature.onboarding.presentation.components.OnboardingPageIndicator

private data class OnboardingPage(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
)

private val pages = listOf(
    OnboardingPage(
        icon = FontAwesomeIcons.Solid.Car,
        title = "Bienvenido a ParkSpot",
        description = "Nunca más olvides dónde aparcaste tu carro. Guarda la ubicación exacta con un solo toque.",
    ),
    OnboardingPage(
        icon = FontAwesomeIcons.Solid.Wifi,
        title = "100% sin internet",
        description = "Tus datos nunca salen de tu dispositivo. ParkSpot funciona completamente sin conexión a internet y no comparte nada con la nube.",
    ),
    OnboardingPage(
        icon = FontAwesomeIcons.Solid.MapMarkerAlt,
        title = "Cómo funciona",
        description = "Cuando aparques, abre la app y guarda tu ubicación. Añade fotos y notas. Consulta tu historial de aparcamientos cuando lo necesites.",
    ),
)

@Composable
fun OnboardingRoot(
    viewModel: OnboardingViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    OnboardingScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = state.currentPage,
        pageCount = { state.totalPages }
    )

    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                onAction(OnboardingAction.OnPageChanged(page))
            }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { pageIndex ->
                val page = pages[pageIndex]
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    OnboardingPageContent(
                        icon = page.icon,
                        title = page.title,
                        description = page.description,
                    )
                }
            }

            OnboardingPageIndicator(
                totalPages = state.totalPages,
                currentPage = state.currentPage,
            )

            Spacer(modifier = Modifier.height(24.dp))

            OnboardingNavigationButtons(
                currentPage = state.currentPage,
                totalPages = state.totalPages,
                onNext = { onAction(OnboardingAction.OnNextPage) },
                onPrevious = { onAction(OnboardingAction.OnPreviousPage) },
                onComplete = { onAction(OnboardingAction.OnComplete) },
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CarLocateTheme {
        OnboardingScreen(
            state = OnboardingState(),
            onAction = {}
        )
    }
}
