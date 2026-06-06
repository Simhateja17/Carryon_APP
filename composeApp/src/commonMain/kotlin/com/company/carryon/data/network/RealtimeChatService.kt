package com.company.carryon.data.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

/**
 * Listens to Supabase Realtime Postgres changes on the ChatMessage table.
 * When a new message is inserted for the subscribed booking, emits a signal
 * so the UI can refresh messages via the REST API.
 */
object RealtimeChatService {

    private val delegate = RealtimeSignalService(
        channelPrefix = "chat",
        watches = listOf(
            TableWatch("ChatMessage", filterColumn = "bookingId", action = WatchAction.INSERT),
        ),
    )

    /** Emits the bookingId whenever a new message arrives */
    val newMessageSignal: SharedFlow<String> = delegate.signal

    suspend fun startListening(bookingId: String, scope: CoroutineScope) =
        delegate.startListening(bookingId, scope)

    suspend fun stopListening() = delegate.stopListening()
}
