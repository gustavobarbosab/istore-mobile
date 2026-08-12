package io.github.gustavobarbosab.istore.ui.screen.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.gustavobarbosab.istore.ui.screen.detail.model.ProductDetailUiModel

@Composable
fun ProductDetail(
    product: ProductDetailUiModel,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = product.emoji, fontSize = 96.sp)
        Text(text = product.name, style = MaterialTheme.typography.headlineSmall)
        Text(text = product.description, style = MaterialTheme.typography.bodyMedium)
        Text(text = product.priceLabel, style = MaterialTheme.typography.titleLarge)
        Button(
            onClick = onBuyClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Buy")
        }
    }
}
