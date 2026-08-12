package io.github.gustavobarbosab.istore.ui.screen.checkout.confirmation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ConfirmationScreen(
    paymentId: String,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: ConfirmationViewModel = koinViewModel(parameters = { parametersOf(paymentId) }),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                ConfirmationSideEffect.NavigateToHome -> onNavigateToHome()
                ConfirmationSideEffect.NavigateToHistory -> onNavigateToHistory()
            }
        }
    }

    ConfirmationScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
    )
}
