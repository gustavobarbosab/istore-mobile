package io.github.gustavobarbosab.istore.ui.screen.profile.di

import io.github.gustavobarbosab.istore.ui.screen.profile.ProfileMvi
import io.github.gustavobarbosab.istore.ui.screen.profile.ProfileViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object ProfileModule {
    val module = module {
        factoryOf(::ProfileMvi)
        viewModelOf(::ProfileViewModel)
    }
}
