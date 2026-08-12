package io.github.gustavobarbosab.istore.ui.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel<ProfileViewModel>(),
) {
    val state by viewModel.state.collectAsState()

    ProfileScreenContent(state = state)
}
