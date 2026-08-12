package io.github.gustavobarbosab.istore.ui.screen.detail.di

import io.github.gustavobarbosab.istore.ui.screen.detail.DetailMvi
import io.github.gustavobarbosab.istore.ui.screen.detail.DetailViewModel
import io.github.gustavobarbosab.istore.ui.screen.detail.mapper.ProductDetailUiModelMapper
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object DetailModule {
    val module = module {
        factoryOf(::DetailMvi)
        factoryOf(::ProductDetailUiModelMapper)
        // productId arrives via parametersOf() in DetailScreen's koinViewModel().
        viewModelOf(::DetailViewModel)
    }
}
