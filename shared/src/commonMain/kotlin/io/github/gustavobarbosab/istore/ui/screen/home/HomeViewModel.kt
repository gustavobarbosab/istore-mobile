package io.github.gustavobarbosab.istore.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.gustavobarbosab.istore.common.MviDelegate
import io.github.gustavobarbosab.istore.common.MviEventHandler
import io.github.gustavobarbosab.istore.domain.usecase.GetProductsUseCase
import io.github.gustavobarbosab.istore.ui.screen.home.mapper.ProductUiModelMapper
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val productUiModelMapper: ProductUiModelMapper,
    private val mvi: HomeMvi,
) : ViewModel(),
    MviDelegate<HomeUiState, HomeSideEffect> by mvi,
    MviEventHandler<HomeEvent> {

    init {
        loadProducts()
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnProductClicked -> viewModelScope.launch {
                onSideEffect(HomeSideEffect.NavigateToDetail(event.productId))
            }

            HomeEvent.OnRetryClicked -> loadProducts()
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            onState(HomeUiState.Loading)
            // safeApiCall (see data/remote/network) already turned any Gateway
            // failure into Ior.Left — no try/catch needed here. UI only cares
            // about success/failure (not partial-success), so the Ior from the
            // use case is converted to a plain Either before folding it.
            getProductsUseCase().toEither().fold(
                ifLeft = { onState(HomeUiState.Error) },
                ifRight = { products -> onState(HomeUiState.Ready(productUiModelMapper.map(products))) },
            )
        }
    }
}
