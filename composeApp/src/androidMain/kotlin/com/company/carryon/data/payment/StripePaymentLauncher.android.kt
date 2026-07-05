package com.company.carryon.data.payment

import androidx.activity.ComponentActivity
import com.company.carryon.data.network.CustomPaymentMethodConfig
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentelement.CustomPaymentMethodResult
import com.stripe.android.paymentelement.CustomPaymentMethodResultHandler
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
        paymentSheet = PaymentSheet.Builder { result ->
            val mapped = when (result) {
                is PaymentSheetResult.Completed -> StripePaymentResult.COMPLETED
                is PaymentSheetResult.Canceled -> StripePaymentResult.CANCELED
                is PaymentSheetResult.Failed -> StripePaymentResult.FAILED
            }
            continuation?.resume(mapped)
            continuation = null
        }.confirmCustomPaymentMethodCallback { customPaymentMethod, _ ->
            CustomPaymentMethodResultHandler.handleCustomPaymentMethodResult(
                activity,
                CustomPaymentMethodResult.failed(
                    "${customPaymentMethod.id} is visible in Stripe, but Touch 'n Go processing is not connected yet."
                )
            )
        }.build(activity)
    }

    actual suspend fun presentPaymentSheet(
        clientSecret: String,
        publishableKey: String,
        customPaymentMethods: List<CustomPaymentMethodConfig>
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
        val sheetCustomPaymentMethods = customPaymentMethods
            .filter { it.id.startsWith("cpmt_") }
            .map {
                PaymentSheet.CustomPaymentMethod(
                    id = it.id,
                    subtitle = it.subtitle.ifBlank { it.label }.ifBlank { null },
                    disableBillingDetailCollection = true
                )
            }
        val paymentMethodOrder = sheetCustomPaymentMethods.map { it.id } + listOf("grabpay", "fpx", "card", "link")
        val configuration = PaymentSheet.Configuration.Builder("CarryOn")
            .defaultBillingDetails(
                PaymentSheet.BillingDetails(
                    address = PaymentSheet.Address(country = "MY")
                )
            )
            .allowsDelayedPaymentMethods(true)
            .customPaymentMethods(sheetCustomPaymentMethods)
            .paymentMethodOrder(paymentMethodOrder)
            .userOverrideCountry("MY")
            .build()
        currentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret = clientSecret,
            configuration = configuration
        )
    }
}
