package io.github.gustavobarbosab.istore.domain.repository

import arrow.core.Ior
import io.github.gustavobarbosab.istore.domain.error.DataError
import io.github.gustavobarbosab.istore.domain.model.Order

interface OrderRepository {
    suspend fun getOrders(): Ior<DataError, List<Order>>
    suspend fun addOrder(order: Order): Ior<DataError, Unit>
}
