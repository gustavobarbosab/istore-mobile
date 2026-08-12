package io.github.gustavobarbosab.istore.data.local

import io.github.gustavobarbosab.istore.domain.model.Order
import io.github.gustavobarbosab.istore.domain.model.OrderStatus

/**
 * In-memory cache of orders. Since there's no real BFF yet, this is the only
 * "source of truth" for My Orders: it holds the pre-existing (seed) orders,
 * receives the new orders created at checkout, and applies the status updates
 * simulated by the "worker" (see PaymentRemoteDataSource).
 *
 * Not genuinely thread-safe (no Mutex) — fine for the skeleton, since
 * concurrent access here is minimal. A real implementation would want a
 * Mutex or a properly guarded MutableStateFlow.
 */
class OrderLocalDataSource {
    private val orders = mutableListOf(
        Order(
            id = "o1",
            productId = "p1",
            productName = "Bluetooth Headphones",
            date = "28/07/2026",
            price = 249.90,
            status = OrderStatus.APPROVED,
        ),
        Order(
            id = "o2",
            productId = "p2",
            productName = "Smartwatch",
            date = "30/07/2026",
            price = 599.00,
            status = OrderStatus.PROCESSING,
        ),
        Order(
            id = "o3",
            productId = "p4",
            productName = "Gaming Mouse",
            date = "15/07/2026",
            price = 189.90,
            status = OrderStatus.DECLINED,
        ),
    )

    fun getAll(): List<Order> = orders.toList()

    fun add(order: Order) {
        orders.add(0, order)
    }

    fun updateStatus(orderId: String, status: OrderStatus) {
        val index = orders.indexOfFirst { it.id == orderId }
        if (index != -1) {
            orders[index] = orders[index].copy(status = status)
        }
    }
}
