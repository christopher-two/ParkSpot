package org.christophertwo.car.feature.onboarding.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun OnboardingNavigationButtons(
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLastPage = currentPage == totalPages - 1

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (currentPage > 0) {
            OutlinedButton(onClick = onPrevious) {
                Text(text = "Atrás")
            }
        } else {
            // Empty space to keep alignment
            OutlinedButton(onClick = {}, enabled = false) {
                Text(text = "Atrás")
            }
        }

        if (isLastPage) {
            Button(onClick = onComplete) {
                Text(text = "Comenzar")
            }
        } else {
            Button(onClick = onNext) {
                Text(text = "Siguiente")
            }
        }
    }
}

