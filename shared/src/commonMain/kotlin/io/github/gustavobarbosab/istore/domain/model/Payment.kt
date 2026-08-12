package io.github.gustavobarbosab.istore.domain.model

/** Result of POST /checkout on the BFF: a "202 Accepted" with the payment id. */
data class Payment(
    val paymentId: String,
    val productId: String,
)
