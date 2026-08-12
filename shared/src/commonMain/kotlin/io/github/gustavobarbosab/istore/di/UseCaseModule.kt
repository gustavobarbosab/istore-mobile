package io.github.gustavobarbosab.istore.di

import io.github.gustavobarbosab.istore.domain.usecase.CheckoutUseCase
import io.github.gustavobarbosab.istore.domain.usecase.GetOrdersUseCase
import io.github.gustavobarbosab.istore.domain.usecase.GetProductByIdUseCase
import io.github.gustavobarbosab.istore.domain.usecase.GetProductsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

object UseCaseModule {
    val module = module {
        factoryOf(::GetProductsUseCase)
        factoryOf(::GetProductByIdUseCase)
        factoryOf(::GetOrdersUseCase)
        factoryOf(::CheckoutUseCase)
    }
}