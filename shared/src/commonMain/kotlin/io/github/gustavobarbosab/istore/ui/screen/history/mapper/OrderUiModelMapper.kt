package io.github.gustavobarbosab.istore.ui.screen.history.mapper

import io.github.gustavobarbosab.istore.common.toPriceLabel
import io.github.gustavobarbosab.istore.domain.model.Order
import io.github.gustavobarbosab.istore.domain.model.OrderStatus
import io.github.gustavobarbosab.istore.ui.screen.history.model.OrderStatusUiModel
import io.github.gustavobarbosab.istore.ui.screen.history.model.OrderUiModel

class OrderUiModelMapper {

    fun map(order: Order): OrderUiModel = OrderUiModel(
        id = order.id,
        productName = order.productName,
        date = order.date,
        priceLabel = order.price.toPriceLabel(),
        status = mapStatus(order.status),
    )

    fun map(orders: List<Order>): List<OrderUiModel> = orders.map(::map)

    private fun mapStatus(status: OrderStatus): OrderStatusUiModel = when (status) {
        OrderStatus.APPROVED -> OrderStatusUiModel.APPROVED
        OrderStatus.PROCESSING -> OrderStatusUiModel.PROCESSING
        OrderStatus.DECLINED -> OrderStatusUiModel.DECLINED
    }
}
