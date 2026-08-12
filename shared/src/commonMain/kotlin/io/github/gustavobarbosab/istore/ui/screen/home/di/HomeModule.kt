package io.github.gustavobarbosab.istore.ui.screen.home.di

import io.github.gustavobarbosab.istore.ui.screen.home.HomeMvi
import io.github.gustavobarbosab.istore.ui.screen.home.HomeViewModel
import io.github.gustavobarbosab.istore.ui.screen.home.mapper.ProductUiModelMapper
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object HomeModule {
    val module = module {
        factoryOf(::HomeMvi)
        factoryOf(::ProductUiModelMapper)
        viewModelOf(::HomeViewModel)
    }
}
