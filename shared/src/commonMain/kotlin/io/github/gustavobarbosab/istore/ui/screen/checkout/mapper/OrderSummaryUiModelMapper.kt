package io.github.gustavobarbosab.istore.ui.screen.checkout.mapper

import io.github.gustavobarbosab.istore.common.toPriceLabel
import io.github.gustavobarbosab.istore.domain.model.Product
import io.github.gustavobarbosab.istore.ui.screen.checkout.model.OrderSummaryUiModel

class OrderSummaryUiModelMapper {

    fun map(product: Product): OrderSummaryUiModel = OrderSummaryUiModel(
        productName = product.name,
        priceLabel = product.price.toPriceLabel(),
    )
}
