package io.github.gustavobarbosab.istore.data.remote.dto

import io.github.gustavobarbosab.istore.domain.model.Payment
import kotlinx.serialization.Serializable

/** Request body for `POST /checkout`. */
@Serializable
data class CheckoutRequestDto(
    val productId: String,
)

/** Response body for `POST /checkout` — the Gateway's "202 Accepted". */
@Serializable
data class PaymentDto(
    val paymentId: String,
    val productId: String,
)

fun PaymentDto.toDomain(): Payment = Payment(
    paymentId = paymentId,
    productId = productId,
)
