package io.github.gustavobarbosab.istore.data.remote.network

import arrow.core.Ior
import arrow.core.leftIor
import arrow.core.rightIor
import io.github.gustavobarbosab.istore.domain.error.DataError
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

/**
 * Runs a single Ktor call, converting known failure modes into [DataError]
 * instead of letting them propagate as exceptions. Every `*RemoteDataSource`
 * method that hits the network goes through this — it's the one place that
 * knows about Ktor's exception types, so repositories and use cases never
 * need to repeat this try/catch themselves.
 */
suspend fun <T> safeApiCall(block: suspend () -> T): Ior<DataError, T> {
    return try {
        block().rightIor()
    } catch (e: CancellationException) {
        // Never swallow cancellation — that would break structured concurrency.
        throw e
    } catch (e: ResponseException) {
        // The Gateway responded, but with a 4xx/5xx status.
        DataError.Http(
            statusCode = e.response.status.value,
            message = e.message ?: "Gateway returned ${e.response.status.value}",
        ).leftIor()
    } catch (e: SerializationException) {
        DataError.Serialization(e.message ?: "Failed to parse the Gateway's response").leftIor()
    } catch (e: Exception) {
        // Anything else (unreachable host, timeout, connection refused, ...).
        DataError.Network(e.message ?: "Couldn't reach the Gateway").leftIor()
    }
}
