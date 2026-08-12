package io.github.gustavobarbosab.istore.ui.screen.checkout.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.gustavobarbosab.istore.ui.screen.checkout.model.OrderSummaryUiModel

@Composable
fun OrderSummary(
    summary: OrderSummaryUiModel,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(text = "Order summary", style = MaterialTheme.typography.titleMedium)
                Text(text = summary.productName, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Total: ${summary.priceLabel}",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        Button(
            onClick = onConfirmClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Confirm payment")
        }
    }
}
