package io.github.gustavobarbosab.istore.common

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@PublishedApi
internal val routeBase64 = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)

inline fun <reified T : Any> serializableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {

    override fun put(bundle: SavedState, key: String, value: T) {
        bundle.write { putString(key, json.encodeToString(value)) }
    }

    override fun get(bundle: SavedState, key: String): T? {
        return bundle.read { getStringOrNull(key) }?.let { json.decodeFromString(it) }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun parseValue(value: String): T {
        return json.decodeFromString(routeBase64.decode(value).decodeToString())
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun serializeAsValue(value: T): String {
        return routeBase64.encode(json.encodeToString(value).encodeToByteArray())
    }
}
