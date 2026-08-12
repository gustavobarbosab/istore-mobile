package io.github.gustavobarbosab.istore.ui.screen.home

import io.github.gustavobarbosab.istore.common.MviDelegateImpl
import io.github.gustavobarbosab.istore.ui.screen.home.model.ProductUiModel

class HomeMvi : MviDelegateImpl<HomeUiState, HomeSideEffect>(
    initialState = HomeUiState.Loading
)

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Ready(val products: List<ProductUiModel>) : HomeUiState()

    /** Fetching the catalog failed (e.g. the Gateway is unreachable). */
    data object Error : HomeUiState()
}

sealed class HomeSideEffect {
    data class NavigateToDetail(val productId: String) : HomeSideEffect()
}

sealed class HomeEvent {
    data class OnProductClicked(val productId: String) : HomeEvent()
    data object OnRetryClicked : HomeEvent()
}
