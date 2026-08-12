package io.github.gustavobarbosab.istore.data.remote

import arrow.core.Ior
import io.github.gustavobarbosab.istore.data.remote.dto.ProductDto
import io.github.gustavobarbosab.istore.data.remote.dto.toDomain
import io.github.gustavobarbosab.istore.data.remote.network.GatewayConfig
import io.github.gustavobarbosab.istore.data.remote.network.safeApiCall
import io.github.gustavobarbosab.istore.domain.error.DataError
import io.github.gustavobarbosab.istore.domain.model.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/** Talks to the BFF (via the API Gateway) for catalog data. */
class ProductRemoteDataSource(
    private val httpClient: HttpClient,
) {

    suspend fun fetchProducts(): Ior<DataError, List<Product>> = safeApiCall {
        httpClient.get("${GatewayConfig.baseUrl}/products")
            .body<List<ProductDto>>()
            .map { it.toDomain() }
    }
}
