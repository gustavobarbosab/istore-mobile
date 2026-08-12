package io.github.gustavobarbosab.istore.domain.error

/**
 * Technical failures the data layer can surface to domain/use cases/UI,
 * instead of throwing. Every repository method returns `Ior<DataError, T>`
 * (see [safeApiCall][io.github.gustavobarbosab.istore.data.remote.network.safeApiCall]
 * for where these get created) so callers pattern-match/fold instead of
 * wrapping every call in `try/catch`.
 */
sealed class DataError {

    /** The Gateway couldn't be reached at all (offline, DNS, timeout, connection refused). */
    data class Network(val message: String) : DataError()

    /** The Gateway responded, but with a 4xx/5xx status. */
    data class Http(val statusCode: Int, val message: String) : DataError()

    /** The response body didn't match the expected shape. */
    data class Serialization(val message: String) : DataError()

    /** Anything else not covered above (including domain-level failures, e.g. "not found"). */
    data class Unknown(val message: String) : DataError()
}
