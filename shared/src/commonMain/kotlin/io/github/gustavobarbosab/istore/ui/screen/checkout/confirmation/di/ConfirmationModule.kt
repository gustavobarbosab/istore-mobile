package io.github.gustavobarbosab.istore.ui.screen.checkout.confirmation.di

import io.github.gustavobarbosab.istore.ui.screen.checkout.confirmation.ConfirmationMvi
import io.github.gustavobarbosab.istore.ui.screen.checkout.confirmation.ConfirmationViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object ConfirmationModule {
    val module = module {
        factoryOf(::ConfirmationMvi)
        // paymentId arrives via parametersOf() in ConfirmationScreen's koinViewModel().
        viewModelOf(::ConfirmationViewModel)
    }
}
