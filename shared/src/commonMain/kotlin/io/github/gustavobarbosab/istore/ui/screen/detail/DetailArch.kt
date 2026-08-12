package io.github.gustavobarbosab.istore.ui.screen.detail

import io.github.gustavobarbosab.istore.common.MviDelegateImpl
import io.github.gustavobarbosab.istore.ui.screen.detail.model.ProductDetailUiModel

class DetailMvi : MviDelegateImpl<DetailUiState, DetailSideEffect>(
    initialState = DetailUiState.Loading
)

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Ready(val product: ProductDetailUiModel) : DetailUiState()

    /** The product genuinely doesn't exist in the catalog. */
    data object NotFound : DetailUiState()

    /** Fetching the product failed (e.g. the Gateway is unreachable). */
    data object Error : DetailUiState()
}

sealed class DetailSideEffect {
    data class NavigateToCheckout(val productId: String) : DetailSideEffect()
}

sealed class DetailEvent {
    data object OnBuyClicked : DetailEvent()
    data object OnRetryClicked : DetailEvent()
}
