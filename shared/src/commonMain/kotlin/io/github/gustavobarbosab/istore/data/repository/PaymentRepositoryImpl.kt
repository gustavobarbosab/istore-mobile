package io.github.gustavobarbosab.istore.data.repository

import arrow.core.Ior
import io.github.gustavobarbosab.istore.data.local.OrderLocalDataSource
import io.github.gustavobarbosab.istore.data.remote.PaymentRemoteDataSource
import io.github.gustavobarbosab.istore.domain.error.DataError
import io.github.gustavobarbosab.istore.domain.model.Payment
import io.github.gustavobarbosab.istore.domain.model.Product
import io.github.gustavobarbosab.istore.domain.repository.PaymentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PaymentRepositoryImpl(
    private val remoteDataSource: PaymentRemoteDataSource,
    private val orderLocalDataSource: OrderLocalDataSource,
) : PaymentRepository {

    // Own scope (not tied to any screen): the "worker" needs to keep running
    // in the background even if the user leaves the Confirmation screen,
    // since it doesn't wait for a response — it only updates the cache later.
    private val workerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override suspend fun checkout(product: Product): Ior<DataError, Payment> {
        val result = remoteDataSource.submitCheckout(product)
        result.getOrNull()?.let { payment -> simulateWorkerProcessing(paymentId = payment.paymentId) }
        return result
    }

    private fun simulateWorkerProcessing(paymentId: String) {
        workerScope.launch {
            val status = remoteDataSource.awaitWorkerResult()
            orderLocalDataSource.updateStatus(paymentId, status)
        }
    }
}
