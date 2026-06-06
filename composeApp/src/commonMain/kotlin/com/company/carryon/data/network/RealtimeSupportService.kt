package com.company.carryon.data.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

/**
 * Listens to Supabase Realtime Postgres changes on the SupportTicket and
 * TicketMessage tables. Emits a signal so the UI can refresh the ticket
 * detail via the REST API.
 */
object RealtimeSupportService {

    private val delegate = RealtimeSignalService(
        channelPrefix = "support-ticket",
        watches = listOf(
            TableWatch("TicketMessage", filterColumn = "ticketId", action = WatchAction.INSERT),
            TableWatch("SupportTicket", filterColumn = "id", action = WatchAction.UPDATE),
        ),
    )

    val ticketSignal: SharedFlow<String> = delegate.signal

    suspend fun startListening(ticketId: String, scope: CoroutineScope) =
        delegate.startListening(ticketId, scope)

    suspend fun stopListening() = delegate.stopListening()
}
