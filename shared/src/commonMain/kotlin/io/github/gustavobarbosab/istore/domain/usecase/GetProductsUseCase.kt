package io.github.gustavobarbosab.istore.domain.usecase

import arrow.core.Ior
import io.github.gustavobarbosab.istore.domain.error.DataError
import io.github.gustavobarbosab.istore.domain.model.Product
import io.github.gustavobarbosab.istore.domain.repository.ProductRepository

class GetProductsUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(): Ior<DataError, List<Product>> = productRepository.getProducts()
}
