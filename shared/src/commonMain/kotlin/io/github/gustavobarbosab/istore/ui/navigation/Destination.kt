package io.github.gustavobarbosab.istore.ui.navigation

import kotlinx.serialization.Serializable

/**
 * All of the app's routes. Type-safe navigation via Navigation-Compose (2.8+).
 *
 * Top-level screens (shown in the bottom bar): Home, History, Profile.
 * Flow screens (stacked on top of the bottom bar): Detail, Checkout, Confirmation.
 */
@Serializable
sealed interface Destination

@Serializable
data object HomeDestination : Destination

@Serializable
data object HistoryDestination : Destination

@Serializable
data object ProfileDestination : Destination

@Serializable
data class DetailDestination(val productId: String) : Destination

@Serializable
data class CheckoutDestination(val productId: String) : Destination

@Serializable
data class ConfirmationDestination(val paymentId: String) : Destination
