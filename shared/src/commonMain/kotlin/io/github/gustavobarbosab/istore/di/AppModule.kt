package io.github.gustavobarbosab.istore.di

import io.github.gustavobarbosab.istore.data.remote.network.provideGatewayHttpClient
import io.ktor.client.HttpClient
import org.koin.dsl.module

/**
 * Cross-cutting app dependencies (HTTP client for the API Gateway, dispatchers, etc.).
 */
object AppModule {
    val module = module {
        // Single: one client/connection pool for the app's whole lifetime, shared
        // by every Remote Data Source.
        single<HttpClient> { provideGatewayHttpClient() }
    }
}
