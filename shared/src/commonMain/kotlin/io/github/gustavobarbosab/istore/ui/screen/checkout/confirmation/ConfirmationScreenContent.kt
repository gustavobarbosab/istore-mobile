package io.github.gustavobarbosab.istore.ui.screen.checkout.confirmation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.gustavobarbosab.istore.ui.screen.checkout.confirmation.component.PaymentProcessing

@Composable
fun ConfirmationScreenContent(
    state: ConfirmationUiState,
    onEvent: (ConfirmationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is ConfirmationUiState.Processing -> PaymentProcessing(
            paymentId = state.paymentId,
            onViewOrdersClick = { onEvent(ConfirmationEvent.OnViewOrdersClicked) },
            onBackToHomeClick = { onEvent(ConfirmationEvent.OnBackToHomeClicked) },
            modifier = modifier,
        )
    }
}
