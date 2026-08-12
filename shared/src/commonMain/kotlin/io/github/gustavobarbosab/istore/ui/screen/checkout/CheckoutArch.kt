package io.github.gustavobarbosab.istore.ui.screen.checkout

import io.github.gustavobarbosab.istore.common.MviDelegateImpl
import io.github.gustavobarbosab.istore.ui.screen.checkout.model.OrderSummaryUiModel

class CheckoutMvi : MviDelegateImpl<CheckoutUiState, CheckoutSideEffect>(
    initialState = CheckoutUiState.Loading
)

sealed class CheckoutUiState {
    data object Loading : CheckoutUiState()
    data class Ready(val summary: OrderSummaryUiModel) : CheckoutUiState()

    /** Waiting for a response from POST /checkout (BFF via API Gateway). */
    data object Confirming : CheckoutUiState()

    /** Loading the summary or confirming payment failed (e.g. Gateway unreachable). */
    data object Error : CheckoutUiState()
}

sealed class CheckoutSideEffect {
    data class NavigateToConfirmation(val paymentId: String) : CheckoutSideEffect()
}

sealed class CheckoutEvent {
    data object OnConfirmClicked : CheckoutEvent()
    data object OnRetryClicked : CheckoutEvent()
}
