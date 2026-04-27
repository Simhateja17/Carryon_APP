package com.company.carryon.data.payment

enum class StripePaymentResult {
    COMPLETED,
    CANCELED,
    FAILED
}

expect object StripePaymentLauncher {
    suspend fun presentWalletTopUp(
        clientSecret: String,
        publishableKey: String
    ): StripePaymentResult
}
