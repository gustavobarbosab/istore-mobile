package io.github.gustavobarbosab.istore.ui.screen.checkout.confirmation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaymentProcessing(
    paymentId: String,
    onViewOrdersClick: () -> Unit,
    onBackToHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "⏳", fontSize = 64.sp)
        Text(
            text = "Payment processing",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = "Once it's approved, you'll see the result in My Orders.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
        )
        Text(
            text = "Order: $paymentId",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 32.dp),
        )
        Button(
            onClick = onViewOrdersClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("View My Orders")
        }
        OutlinedButton(
            onClick = onBackToHomeClick,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("Back to Home")
        }
    }
}
