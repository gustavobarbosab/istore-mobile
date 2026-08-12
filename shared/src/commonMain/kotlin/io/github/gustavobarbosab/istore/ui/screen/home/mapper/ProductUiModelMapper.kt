package io.github.gustavobarbosab.istore.ui.screen.home.mapper

import io.github.gustavobarbosab.istore.common.toPriceLabel
import io.github.gustavobarbosab.istore.domain.model.Product
import io.github.gustavobarbosab.istore.ui.screen.home.model.ProductUiModel

class ProductUiModelMapper {

    fun map(product: Product): ProductUiModel = ProductUiModel(
        id = product.id,
        name = product.name,
        description = product.description,
        priceLabel = product.price.toPriceLabel(),
        emoji = product.emoji,
    )

    fun map(products: List<Product>): List<ProductUiModel> = products.map(::map)
}
