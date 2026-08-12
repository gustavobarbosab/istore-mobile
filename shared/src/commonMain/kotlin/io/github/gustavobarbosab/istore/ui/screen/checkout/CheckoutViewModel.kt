package io.github.gustavobarbosab.istore.ui.screen.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.gustavobarbosab.istore.common.MviDelegate
import io.github.gustavobarbosab.istore.common.MviEventHandler
import io.github.gustavobarbosab.istore.domain.model.Product
import io.github.gustavobarbosab.istore.domain.usecase.CheckoutUseCase
import io.github.gustavobarbosab.istore.domain.usecase.GetProductByIdUseCase
import io.github.gustavobarbosab.istore.ui.screen.checkout.mapper.OrderSummaryUiModelMapper
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val productId: String,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val checkoutUseCase: CheckoutUseCase,
    private val orderSummaryUiModelMapper: OrderSummaryUiModelMapper,
    private val mvi: CheckoutMvi,
) : ViewModel(),
    MviDelegate<CheckoutUiState, CheckoutSideEffect> by mvi,
    MviEventHandler<CheckoutEvent> {

    init {
        loadOrderSummary()
    }

    override fun onEvent(event: CheckoutEvent) {
        when (event) {
            CheckoutEvent.OnConfirmClicked -> confirmPayment()
            CheckoutEvent.OnRetryClicked -> loadOrderSummary()
        }
    }

    private fun loadOrderSummary() {
        viewModelScope.launch {
            onState(CheckoutUiState.Loading)
            getProductByIdUseCase(productId).toEither().fold(
                ifLeft = { onState(CheckoutUiState.Error) },
                ifRight = { product -> onState(product.toReadyOrError()) },
            )
        }
    }

    private fun Product?.toReadyOrError(): CheckoutUiState =
        if (this == null) CheckoutUiState.Error else CheckoutUiState.Ready(orderSummaryUiModelMapper.map(this))

    private fun confirmPayment() {
        viewModelScope.launch {
            onState(CheckoutUiState.Confirming)

            // Calls the BFF via the API Gateway (mocked): receives the "202
            // Accepted" with the paymentId and already creates the order in
            // My Orders as PROCESSING.
            checkoutUseCase(productId).toEither().fold(
                ifLeft = { onState(CheckoutUiState.Error) },
                ifRight = { payment -> onSideEffect(CheckoutSideEffect.NavigateToConfirmation(payment.paymentId)) },
            )
        }
    }
}
