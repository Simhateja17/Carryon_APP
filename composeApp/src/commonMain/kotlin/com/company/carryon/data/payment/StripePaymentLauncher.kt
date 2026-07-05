package com.company.carryon.data.payment

import com.company.carryon.data.network.CustomPaymentMethodConfig

enum class StripePaymentResult {
    COMPLETED,
    CANCELED,
    FAILED
}

expect object StripePaymentLauncher {
    suspend fun presentPaymentSheet(
        clientSecret: String,
        publishableKey: String,
        customPaymentMethods: List<CustomPaymentMethodConfig>
    ): StripePaymentResult
}
