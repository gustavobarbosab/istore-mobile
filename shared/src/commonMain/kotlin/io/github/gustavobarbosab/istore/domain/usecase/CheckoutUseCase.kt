package io.github.gustavobarbosab.istore.domain.usecase

import arrow.core.Ior
import arrow.core.leftIor
import io.github.gustavobarbosab.istore.domain.error.DataError
import io.github.gustavobarbosab.istore.domain.model.Order
import io.github.gustavobarbosab.istore.domain.model.OrderStatus
import io.github.gustavobarbosab.istore.domain.model.Payment
import io.github.gustavobarbosab.istore.domain.repository.OrderRepository
import io.github.gustavobarbosab.istore.domain.repository.PaymentRepository
import io.github.gustavobarbosab.istore.domain.repository.ProductRepository
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

/**
 * Confirms the checkout of a product.
 *
 * Mirrors the real flow designed in the architecture: submits the payment
 * (BFF via API Gateway), receives a "202 Accepted" with the paymentId, and
 * immediately creates the order locally as PROCESSING — without waiting for
 * the final result. The result (approved/declined) only shows up later, when
 * the user opens My Orders.
 */
class CheckoutUseCase(
    private val productRepository: ProductRepository,
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(productId: String): Ior<DataError, Payment> {
        val productResult = productRepository.getProductById(productId)
        productResult.leftOrNull()?.let { return it.leftIor() }
        val product = productResult.getOrNull()
            ?: return DataError.Unknown("Product $productId not found").leftIor()

        val paymentResult = paymentRepository.checkout(product)
        val payment = paymentResult.getOrNull() ?: return paymentResult

        orderRepository.addOrder(
            Order(
                id = payment.paymentId,
                productId = product.id,
                productName = product.name,
                date = today(),
                price = product.price,
                status = OrderStatus.PROCESSING,
            )
        )

        return paymentResult
    }

    // java.text.SimpleDateFormat doesn't exist outside the JVM — domain is
    // commonMain (shared with iOS), so formatting relies on plain kotlinx-datetime.
    private fun today(): String {
        val date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return "${date.day.pad2()}/${date.month.number.pad2()}/${date.year}"
    }

    private fun Int.pad2(): String = if (this < 10) "0$this" else "$this"
}
