package com.company.carryon.data.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class PendingPushNavigation(
    val type: String,
    val bookingId: String? = null,
    val targetScreen: String? = null,
)

object PushNavigationSignal {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun signalPendingNavigation() {
        _events.tryEmit(Unit)
    }
}
