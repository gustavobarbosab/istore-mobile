package io.github.gustavobarbosab.istore.ui.screen.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DetailScreen(
    productId: String,
    onBack: () -> Unit,
    onNavigateToCheckout: (productId: String) -> Unit,
    viewModel: DetailViewModel = koinViewModel(parameters = { parametersOf(productId) }),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is DetailSideEffect.NavigateToCheckout -> onNavigateToCheckout(effect.productId)
            }
        }
    }

    DetailScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack,
    )
}
