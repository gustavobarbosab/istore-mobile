package io.github.gustavobarbosab.istore.data.remote.network

/**
 * API Gateway connection info. Sourced from `local.properties` (see
 * `local.properties.example`) — never hardcode the api key here.
 *
 * [GeneratedGatewayConfig] is written into `commonMain` at build time by the
 * `generateGatewayConfig` Gradle task in `shared/build.gradle.kts`, reading
 * `local.properties` once so both the Android and iOS targets see the same
 * values without needing a separate secrets mechanism per platform.
 */
object GatewayConfig {
    val baseUrl: String = GeneratedGatewayConfig.BASE_URL
    val apiKey: String = GeneratedGatewayConfig.API_KEY
}
