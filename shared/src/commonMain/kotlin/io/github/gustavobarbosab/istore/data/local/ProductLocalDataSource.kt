package io.github.gustavobarbosab.istore.data.local

import io.github.gustavobarbosab.istore.domain.model.Product

/**
 * Simple in-memory cache: lives as long as the app process is alive, gone
 * once the app is killed. Good enough for the skeleton — a real version
 * could swap this for a TTL-based cache, or disk persistence.
 */
class ProductLocalDataSource {
    private var cachedProducts: List<Product>? = null

    fun getCachedProducts(): List<Product>? = cachedProducts

    fun getCachedProduct(id: String): Product? = cachedProducts?.firstOrNull { it.id == id }

    fun cacheProducts(products: List<Product>) {
        cachedProducts = products
    }
}
