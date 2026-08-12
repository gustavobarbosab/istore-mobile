package io.github.gustavobarbosab.istore

import androidx.compose.runtime.Composable
import io.github.gustavobarbosab.istore.ui.IStoreApplication
import io.github.gustavobarbosab.istore.ui.navigation.AppNavigation
import io.github.gustavobarbosab.istore.ui.theme.IStoreTheme

/**
 * Entry point shared between Android and iOS. `MainActivity` (Android) and
 * `MainViewController` (iOS) just call this composable — all the real UI
 * (domain/data/ui) lives here in commonMain.
 */
@Composable
fun App() {
    IStoreApplication {
        IStoreTheme {
            AppNavigation()
        }
    }
}
