package io.github.gustavobarbosab.istore.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.gustavobarbosab.istore.common.MviDelegate
import io.github.gustavobarbosab.istore.common.MviEventHandler
import io.github.gustavobarbosab.istore.domain.model.Product
import io.github.gustavobarbosab.istore.domain.usecase.GetProductByIdUseCase
import io.github.gustavobarbosab.istore.ui.screen.detail.mapper.ProductDetailUiModelMapper
import kotlinx.coroutines.launch

class DetailViewModel(
    private val productId: String,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val productDetailUiModelMapper: ProductDetailUiModelMapper,
    private val mvi: DetailMvi,
) : ViewModel(),
    MviDelegate<DetailUiState, DetailSideEffect> by mvi,
    MviEventHandler<DetailEvent> {

    init {
        loadProduct()
    }

    override fun onEvent(event: DetailEvent) {
        when (event) {
            DetailEvent.OnBuyClicked -> viewModelScope.launch {
                onSideEffect(DetailSideEffect.NavigateToCheckout(productId))
            }

            DetailEvent.OnRetryClicked -> loadProduct()
        }
    }

    private fun loadProduct() {
        viewModelScope.launch {
            onState(DetailUiState.Loading)
            getProductByIdUseCase(productId).toEither().fold(
                ifLeft = { onState(DetailUiState.Error) },
                ifRight = { product -> onState(product.toUiState()) },
            )
        }
    }

    private fun Product?.toUiState(): DetailUiState =
        if (this == null) DetailUiState.NotFound else DetailUiState.Ready(productDetailUiModelMapper.map(this))
}
