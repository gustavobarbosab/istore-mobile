package io.github.gustavobarbosab.istore.ui.screen.history.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.gustavobarbosab.istore.ui.screen.history.model.OrderStatusUiModel

@Composable
fun StatusBadge(status: OrderStatusUiModel, modifier: Modifier = Modifier) {
    val (label, color) = when (status) {
        OrderStatusUiModel.APPROVED -> "Approved" to Color(0xFF2E7D32)
        OrderStatusUiModel.PROCESSING -> "Processing" to Color(0xFFF9A825)
        OrderStatusUiModel.DECLINED -> "Declined" to Color(0xFFC62828)
    }
    Text(
        text = label,
        color = color,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier.padding(top = 4.dp),
    )
}
