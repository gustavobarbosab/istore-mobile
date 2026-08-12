package io.github.gustavobarbosab.istore.data.repository

import arrow.core.Ior
import arrow.core.rightIor
import io.github.gustavobarbosab.istore.data.local.OrderLocalDataSource
import io.github.gustavobarbosab.istore.domain.error.DataError
import io.github.gustavobarbosab.istore.domain.model.Order
import io.github.gustavobarbosab.istore.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val localDataSource: OrderLocalDataSource,
) : OrderRepository {

    // Local-only, in-memory — can't actually fail, but the interface still
    // returns Ior for consistency with every other repository.
    override suspend fun getOrders(): Ior<DataError, List<Order>> = localDataSource.getAll().rightIor()

    override suspend fun addOrder(order: Order): Ior<DataError, Unit> {
        localDataSource.add(order)
        return Unit.rightIor()
    }
}
