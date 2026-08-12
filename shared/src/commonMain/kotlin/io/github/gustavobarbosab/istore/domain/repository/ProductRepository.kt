package io.github.gustavobarbosab.istore.domain.repository

import arrow.core.Ior
import io.github.gustavobarbosab.istore.domain.error.DataError
import io.github.gustavobarbosab.istore.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(): Ior<DataError, List<Product>>
    suspend fun getProductById(id: String): Ior<DataError, Product?>
}
