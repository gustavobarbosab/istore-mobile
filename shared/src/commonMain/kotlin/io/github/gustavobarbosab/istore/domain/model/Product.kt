package io.github.gustavobarbosab.istore.domain.model

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val emoji: String,
)
