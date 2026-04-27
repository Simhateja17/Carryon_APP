package com.company.carryon.data.payment

actual object StripePaymentLauncher {
    actual suspend fun presentWalletTopUp(
        clientSecret: String,
        publishableKey: String
    ): StripePaymentResult = StripePaymentResult.FAILED
}
