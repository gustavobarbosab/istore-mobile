package io.github.gustavobarbosab.istore.ui.screen.checkout.confirmation

import io.github.gustavobarbosab.istore.common.MviDelegateImpl

class ConfirmationMvi : MviDelegateImpl<ConfirmationUiState, ConfirmationSideEffect>(
    initialState = ConfirmationUiState.Processing(paymentId = "")
)

sealed class ConfirmationUiState {
    /**
     * The only state this screen has: no polling or WebSocket, the app just
     * informs the user the payment is being processed. The final result
     * (approved/declined) shows up later on the My Orders screen.
     */
    data class Processing(val paymentId: String) : ConfirmationUiState()
}

sealed class ConfirmationSideEffect {
    data object NavigateToHome : ConfirmationSideEffect()
    data object NavigateToHistory : ConfirmationSideEffect()
}

sealed class ConfirmationEvent {
    data object OnViewOrdersClicked : ConfirmationEvent()
    data object OnBackToHomeClicked : ConfirmationEvent()
}
