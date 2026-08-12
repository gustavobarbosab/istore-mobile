package io.github.gustavobarbosab.istore.ui.screen.history.di

import io.github.gustavobarbosab.istore.ui.screen.history.HistoryMvi
import io.github.gustavobarbosab.istore.ui.screen.history.HistoryViewModel
import io.github.gustavobarbosab.istore.ui.screen.history.mapper.OrderUiModelMapper
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object HistoryModule {
    val module = module {
        factoryOf(::HistoryMvi)
        factoryOf(::OrderUiModelMapper)
        viewModelOf(::HistoryViewModel)
    }
}
