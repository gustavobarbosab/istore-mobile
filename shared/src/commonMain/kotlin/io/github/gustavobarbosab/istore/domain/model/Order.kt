package io.github.gustavobarbosab.istore.domain.model

data class Order(
    val id: String,
    val productId: String,
    val productName: String,
    val date: String,
    val price: Double,
    val status: OrderStatus,
)
