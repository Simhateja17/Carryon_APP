package com.company.carryon.data.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class PendingPushNavigation(
    val type: String,
    val bookingId: String? = null,
    val targetScreen: String? = null,
)

fun PendingPushNavigation.toDeepLinkTarget(): DeepLinkTarget? {
    return when (type.uppercase()) {
        "DEEP_LINK_TRACK" -> bookingId?.let { DeepLinkTarget.TrackBooking(it) }
        "DEEP_LINK_REFERRAL" -> targetScreen?.let { DeepLinkTarget.Referral(it.uppercase()) }
        else -> null
    }
}

object PushNavigationSignal {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun signalPendingNavigation() {
        _events.tryEmit(Unit)
    }
}
