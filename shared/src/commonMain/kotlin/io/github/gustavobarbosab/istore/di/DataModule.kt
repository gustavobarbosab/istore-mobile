package io.github.gustavobarbosab.istore.di

import io.github.gustavobarbosab.istore.data.local.OrderLocalDataSource
import io.github.gustavobarbosab.istore.data.local.ProductLocalDataSource
import io.github.gustavobarbosab.istore.data.remote.PaymentRemoteDataSource
import io.github.gustavobarbosab.istore.data.remote.ProductRemoteDataSource
import io.github.gustavobarbosab.istore.data.repository.OrderRepositoryImpl
import io.github.gustavobarbosab.istore.data.repository.PaymentRepositoryImpl
import io.github.gustavobarbosab.istore.data.repository.ProductRepositoryImpl
import io.github.gustavobarbosab.istore.domain.repository.OrderRepository
import io.github.gustavobarbosab.istore.domain.repository.PaymentRepository
import io.github.gustavobarbosab.istore.domain.repository.ProductRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

object DataModule {
    val module = module {
        // Local data sources are singletons: the in-memory cache needs to
        // survive across screens (otherwise it "forgets" on every navigation).
        singleOf(::ProductLocalDataSource)
        singleOf(::OrderLocalDataSource)

        // Remote data sources are stateless, so they can be factory.
        factoryOf(::ProductRemoteDataSource)
        factoryOf(::PaymentRemoteDataSource)

        singleOf(::ProductRepositoryImpl) bind ProductRepository::class
        singleOf(::OrderRepositoryImpl) bind OrderRepository::class
        singleOf(::PaymentRepositoryImpl) bind PaymentRepository::class
    }
}