package io.github.gustavobarbosab.istore.domain.usecase

import arrow.core.Ior
import io.github.gustavobarbosab.istore.domain.error.DataError
import io.github.gustavobarbosab.istore.domain.model.Product
import io.github.gustavobarbosab.istore.domain.repository.ProductRepository

class GetProductByIdUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(productId: String): Ior<DataError, Product?> =
        productRepository.getProductById(productId)
}
