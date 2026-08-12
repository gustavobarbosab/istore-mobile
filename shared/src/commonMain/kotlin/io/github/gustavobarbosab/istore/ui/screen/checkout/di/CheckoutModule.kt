package io.github.gustavobarbosab.istore.ui.screen.checkout.di

import io.github.gustavobarbosab.istore.ui.screen.checkout.CheckoutMvi
import io.github.gustavobarbosab.istore.ui.screen.checkout.CheckoutViewModel
import io.github.gustavobarbosab.istore.ui.screen.checkout.mapper.OrderSummaryUiModelMapper
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object CheckoutModule {
    val module = module {
        factoryOf(::CheckoutMvi)
        factoryOf(::OrderSummaryUiModelMapper)
        // productId arrives via parametersOf() in CheckoutScreen's koinViewModel().
        viewModelOf(::CheckoutViewModel)
    }
}
