package com.company.carryon.data.payment

import com.company.carryon.data.network.CustomPaymentMethodConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

interface StripePresenter {
    fun present(
        clientSecret: String,
        publishableKey: String,
        onCompleted: () -> Unit,
        onCanceled: () -> Unit,
        onFailed: () -> Unit
    )
}

actual object StripePaymentLauncher {
    var presenter: StripePresenter? = null

    actual suspend fun presentPaymentSheet(
        clientSecret: String,
        publishableKey: String,
        customPaymentMethods: List<CustomPaymentMethodConfig>
    ): StripePaymentResult = suspendCancellableCoroutine { cont ->
        val currentPresenter = presenter
        if (currentPresenter == null) {
            cont.resume(StripePaymentResult.FAILED)
            return@suspendCancellableCoroutine
        }
        currentPresenter.present(
            clientSecret = clientSecret,
            publishableKey = publishableKey,
            onCompleted = { if (cont.isActive) cont.resume(StripePaymentResult.COMPLETED) },
            onCanceled = { if (cont.isActive) cont.resume(StripePaymentResult.CANCELED) },
            onFailed = { if (cont.isActive) cont.resume(StripePaymentResult.FAILED) }
        )
    }
}
