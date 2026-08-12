package io.github.gustavobarbosab.istore.ui.screen.history.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.gustavobarbosab.istore.ui.screen.history.model.OrderUiModel

@Composable
fun OrderCard(order: OrderUiModel, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = order.productName, style = MaterialTheme.typography.titleMedium)
                Text(text = order.priceLabel, style = MaterialTheme.typography.titleMedium)
            }
            Text(text = order.date, style = MaterialTheme.typography.bodySmall)
            StatusBadge(status = order.status)
        }
    }
}
