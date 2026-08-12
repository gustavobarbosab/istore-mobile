package io.github.gustavobarbosab.istore.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.gustavobarbosab.istore.common.MviDelegate
import io.github.gustavobarbosab.istore.common.MviEventHandler
import io.github.gustavobarbosab.istore.domain.model.Order
import io.github.gustavobarbosab.istore.domain.usecase.GetOrdersUseCase
import io.github.gustavobarbosab.istore.ui.screen.history.mapper.OrderUiModelMapper
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val orderUiModelMapper: OrderUiModelMapper,
    private val mvi: HistoryMvi,
) : ViewModel(),
    MviDelegate<HistoryUiState, HistorySideEffect> by mvi,
    MviEventHandler<HistoryEvent> {

    init {
        loadOrders()
    }

    override fun onEvent(event: HistoryEvent) {
        when (event) {
            HistoryEvent.OnRefresh -> loadOrders()
        }
    }

    private fun loadOrders() {
        // It's this query, made when the user opens this screen, that resolves
        // the payment's final status — no polling or WebSocket involved.
        viewModelScope.launch {
            onState(HistoryUiState.Loading)
            getOrdersUseCase().toEither().fold(
                ifLeft = { onState(HistoryUiState.Error) },
                ifRight = { orders -> onState(orders.toUiState()) },
            )
        }
    }

    private fun List<Order>.toUiState(): HistoryUiState {
        val uiOrders = orderUiModelMapper.map(this)
        return if (uiOrders.isEmpty()) HistoryUiState.Empty else HistoryUiState.Ready(uiOrders)
    }
}
