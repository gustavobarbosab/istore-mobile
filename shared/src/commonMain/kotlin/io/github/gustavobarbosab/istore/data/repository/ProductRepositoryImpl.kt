package io.github.gustavobarbosab.istore.data.repository

import arrow.core.Ior
import arrow.core.rightIor
import io.github.gustavobarbosab.istore.data.local.ProductLocalDataSource
import io.github.gustavobarbosab.istore.data.remote.ProductRemoteDataSource
import io.github.gustavobarbosab.istore.domain.error.DataError
import io.github.gustavobarbosab.istore.domain.model.Product
import io.github.gustavobarbosab.istore.domain.repository.ProductRepository

/**
 * Simple cache strategy: cache-first. If products are already in memory,
 * use the cache; otherwise fetch from the "remote" source and store it for
 * the next call.
 */
class ProductRepositoryImpl(
    private val remoteDataSource: ProductRemoteDataSource,
    private val localDataSource: ProductLocalDataSource,
) : ProductRepository {

    override suspend fun getProducts(): Ior<DataError, List<Product>> {
        localDataSource.getCachedProducts()?.let { return it.rightIor() }

        val result = remoteDataSource.fetchProducts()
        result.getOrNull()?.let { products -> localDataSource.cacheProducts(products) }
        return result
    }

    override suspend fun getProductById(id: String): Ior<DataError, Product?> {
        localDataSource.getCachedProduct(id)?.let { return it.rightIor() }

        // Cache still empty (e.g. the user opened Detail directly, without
        // going through Home first) — populate the cache by fetching the full list.
        return getProducts().map { products -> products.firstOrNull { it.id == id } }
    }
}
