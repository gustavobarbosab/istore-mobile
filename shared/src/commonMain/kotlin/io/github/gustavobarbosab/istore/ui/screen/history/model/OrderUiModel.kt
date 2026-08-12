package io.github.gustavobarbosab.istore.ui.screen.history.model

enum class OrderStatusUiModel { APPROVED, PROCESSING, DECLINED }

data class OrderUiModel(
    val id: String,
    val productName: String,
    val date: String,
    val priceLabel: String,
    val status: OrderStatusUiModel,
)
