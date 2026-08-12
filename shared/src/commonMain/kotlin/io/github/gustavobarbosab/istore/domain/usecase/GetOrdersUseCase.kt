package io.github.gustavobarbosab.istore.domain.usecase

import arrow.core.Ior
import io.github.gustavobarbosab.istore.domain.error.DataError
import io.github.gustavobarbosab.istore.domain.model.Order
import io.github.gustavobarbosab.istore.domain.repository.OrderRepository

class GetOrdersUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(): Ior<DataError, List<Order>> = orderRepository.getOrders()
}
