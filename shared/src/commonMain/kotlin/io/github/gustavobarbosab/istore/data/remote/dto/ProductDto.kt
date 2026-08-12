package io.github.gustavobarbosab.istore.data.remote.dto

import io.github.gustavobarbosab.istore.domain.model.Product
import kotlinx.serialization.Serializable

/** Wire format for `GET /products` (and each item in `GET /orders`). */
@Serializable
data class ProductDto(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val emoji: String,
)

fun ProductDto.toDomain(): Product = Product(
    id = id,
    name = name,
    description = description,
    price = price,
    emoji = emoji,
)
