package io.github.gustavobarbosab.istore.domain.repository

import arrow.core.Ior
import io.github.gustavobarbosab.istore.domain.error.DataError
import io.github.gustavobarbosab.istore.domain.model.Payment
import io.github.gustavobarbosab.istore.domain.model.Product

interface PaymentRepository {
    /**
     * Submits the payment request (equivalent to POST /checkout on the BFF
     * via the API Gateway). Returns as soon as the payment is accepted for
     * processing — it does not wait for the final result (approved/declined).
     */
    suspend fun checkout(product: Product): Ior<DataError, Payment>
}
