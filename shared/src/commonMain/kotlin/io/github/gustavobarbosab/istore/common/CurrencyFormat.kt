package io.github.gustavobarbosab.istore.common

import kotlin.math.round

/**
 * Formats a price as a "R$ 0.00"-style label.
 *
 * `String.format`/`"%.2f"` are JVM-only and unavailable outside the JVM —
 * this lives in commonMain (shared with iOS), so the rounding/padding is
 * done by hand instead, mirroring the approach used for date formatting in
 * `CheckoutUseCase.today()`.
 */
fun Double.toPriceLabel(): String {
    val cents = round(this * 100).toLong()
    val negative = cents < 0
    val absCents = if (negative) -cents else cents
    val integerPart = absCents / 100
    val fractionPart = (absCents % 100).toString().padStart(2, '0')
    return "R$ ${if (negative) "-" else ""}$integerPart.$fractionPart"
}
