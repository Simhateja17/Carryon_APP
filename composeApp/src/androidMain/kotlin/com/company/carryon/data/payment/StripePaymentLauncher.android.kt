package com.company.carryon.data.payment

import androidx.activity.ComponentActivity
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual object StripePaymentLauncher {
    private var activity: ComponentActivity? = null
    private var paymentSheet: PaymentSheet? = null
    private var continuation: CancellableContinuation<StripePaymentResult>? = null

    fun init(activity: ComponentActivity) {
        this.activity = activity
        paymentSheet = PaymentSheet(activity) { result ->
            val mapped = when (result) {
                is PaymentSheetResult.Completed -> StripePaymentResult.COMPLETED
                is PaymentSheetResult.Canceled -> StripePaymentResult.CANCELED
                is PaymentSheetResult.Failed -> StripePaymentResult.FAILED
            }
            continuation?.resume(mapped)
            continuation = null
        }
    }

    actual suspend fun presentWalletTopUp(
        clientSecret: String,
        publishableKey: String
    ): StripePaymentResult = suspendCancellableCoroutine { cont ->
        val currentActivity = activity
        val currentSheet = paymentSheet
        if (currentActivity == null || currentSheet == null || publishableKey.isBlank()) {
            cont.resume(StripePaymentResult.FAILED)
            return@suspendCancellableCoroutine
        }

        PaymentConfiguration.init(currentActivity, publishableKey)
        continuation = cont
        cont.invokeOnCancellation { continuation = null }
        currentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret = clientSecret,
            configuration = PaymentSheet.Configuration(
                merchantDisplayName = "CarryOn"
            )
        )
    }
}
