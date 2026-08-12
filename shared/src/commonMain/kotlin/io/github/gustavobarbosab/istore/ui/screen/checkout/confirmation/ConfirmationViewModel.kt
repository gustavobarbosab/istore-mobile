package io.github.gustavobarbosab.istore.ui.screen.checkout.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.gustavobarbosab.istore.common.MviDelegate
import io.github.gustavobarbosab.istore.common.MviEventHandler
import kotlinx.coroutines.launch

class ConfirmationViewModel(
    private val paymentId: String,
    private val mvi: ConfirmationMvi,
) : ViewModel(),
    MviDelegate<ConfirmationUiState, ConfirmationSideEffect> by mvi,
    MviEventHandler<ConfirmationEvent> {

    init {
        onState(ConfirmationUiState.Processing(paymentId))
    }

    override fun onEvent(event: ConfirmationEvent) {
        when (event) {
            ConfirmationEvent.OnViewOrdersClicked -> viewModelScope.launch {
                onSideEffect(ConfirmationSideEffect.NavigateToHistory)
            }

            ConfirmationEvent.OnBackToHomeClicked -> viewModelScope.launch {
                onSideEffect(ConfirmationSideEffect.NavigateToHome)
            }
        }
    }
}
