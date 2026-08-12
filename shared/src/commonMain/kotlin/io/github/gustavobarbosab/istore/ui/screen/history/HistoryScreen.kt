package io.github.gustavobarbosab.istore.ui.screen.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel<HistoryViewModel>(),
) {
    val state by viewModel.state.collectAsState()

    HistoryScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
    )
}
