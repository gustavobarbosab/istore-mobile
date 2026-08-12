package io.github.gustavobarbosab.istore.ui.screen.checkout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CheckoutScreen(
    productId: String,
    onBack: () -> Unit,
    onNavigateToConfirmation: (paymentId: String) -> Unit,
    viewModel: CheckoutViewModel = koinViewModel(parameters = { parametersOf(productId) }),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is CheckoutSideEffect.NavigateToConfirmation -> onNavigateToConfirmation(effect.paymentId)
            }
        }
    }

    CheckoutScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack,
    )
}
