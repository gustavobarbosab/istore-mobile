package io.github.gustavobarbosab.istore.data.remote

import arrow.core.Ior
import io.github.gustavobarbosab.istore.data.remote.dto.CheckoutRequestDto
import io.github.gustavobarbosab.istore.data.remote.dto.PaymentDto
import io.github.gustavobarbosab.istore.data.remote.dto.toDomain
import io.github.gustavobarbosab.istore.data.remote.network.GatewayConfig
import io.github.gustavobarbosab.istore.data.remote.network.safeApiCall
import io.github.gustavobarbosab.istore.domain.error.DataError
import io.github.gustavobarbosab.istore.domain.model.OrderStatus
import io.github.gustavobarbosab.istore.domain.model.Payment
import io.github.gustavobarbosab.istore.domain.model.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlin.random.Random

/** Talks to the BFF (via the API Gateway) to submit a checkout. */
class PaymentRemoteDataSource(
    private val httpClient: HttpClient,
) {

    /** Equivalent to a 202 Accepted: accepts the order, doesn't wait for the result. */
    suspend fun submitCheckout(product: Product): Ior<DataError, Payment> = safeApiCall {
        httpClient.post("${GatewayConfig.baseUrl}/checkout") {
            contentType(ContentType.Application.Json)
            setBody(CheckoutRequestDto(productId = product.id))
        }.body<PaymentDto>().toDomain()
    }

    /**
     * Doesn't exist on a real client — there's no Gateway endpoint for this
     * (the architecture is "no polling": the worker processes via the queue
     * and the result only shows up later in My Orders). This simulates that
     * async processing time just to produce a final result without needing
     * a real backend/worker. Not wrapped in [safeApiCall]/`Ior`: it never
     * touches the network, so it has no failure mode to report.
     */
    suspend fun awaitWorkerResult(): OrderStatus {
        delay(5_000)
        return if (Random.nextInt(100) < 80) OrderStatus.APPROVED else OrderStatus.DECLINED
    }
}
