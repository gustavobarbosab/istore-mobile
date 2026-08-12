package io.github.gustavobarbosab.istore.data.remote.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** Header the Gateway expects for authentication (single static key, for now). */
private const val API_KEY_HEADER = "X-API-Key"

/**
 * Single [HttpClient] shared by every `*RemoteDataSource` to talk to the API
 * Gateway. The api key is attached here, once, as a default header — data
 * sources never see or handle it directly.
 *
 * No explicit engine is passed in: this lives in `commonMain`, so Ktor picks
 * whichever engine artifact is on the classpath for the target platform
 * (`ktor-client-okhttp` on Android, `ktor-client-darwin` on iOS — wired in
 * `shared/build.gradle.kts`).
 */
fun provideGatewayHttpClient(): HttpClient = HttpClient {
    expectSuccess = true

    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        )
    }

    install(Logging) {
        // Ktor's default Logger probes for SLF4J and silently falls back to a
        // no-op logger when it isn't found (that's the "SLF4J ... Defaulting
        // to no-operation (NOP) logger" warning you'd see in Logcat) — this
        // app never sets up SLF4J, so nothing was actually printing. Logger.SIMPLE
        // is a plain println-based logger from Ktor itself: multiplatform-safe
        // (works the same on Android/iOS) and guaranteed to actually log.
        logger = Logger.SIMPLE
        level = LogLevel.ALL

        // LogLevel.ALL includes request/response headers, which includes the
        // X-API-Key header in plaintext. Fine for local development; dial this
        // back to LogLevel.INFO (or gate it behind a debug build check) before
        // this ever ships anywhere real logs could leak.
    }

    defaultRequest {
        header(API_KEY_HEADER, GatewayConfig.apiKey)
    }
}
