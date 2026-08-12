package io.github.gustavobarbosab.istore.ui.screen.detail.mapper

import io.github.gustavobarbosab.istore.common.toPriceLabel
import io.github.gustavobarbosab.istore.domain.model.Product
import io.github.gustavobarbosab.istore.ui.screen.detail.model.ProductDetailUiModel

class ProductDetailUiModelMapper {

    fun map(product: Product): ProductDetailUiModel = ProductDetailUiModel(
        id = product.id,
        name = product.name,
        description = product.description,
        priceLabel = product.price.toPriceLabel(),
        emoji = product.emoji,
    )
}
