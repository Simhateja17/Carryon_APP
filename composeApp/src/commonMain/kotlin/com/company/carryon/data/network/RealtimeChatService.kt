package com.company.carryon.data.network

import com.company.carryon.data.model.ChatMessage
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * Listens to Supabase Realtime Postgres changes on the ChatMessage table.
 * When a new message is inserted for the subscribed booking, emits a signal
 * so the UI can refresh messages via the REST API.
 */
object RealtimeChatService {

    private val _newMessageSignal = MutableSharedFlow<String>(extraBufferCapacity = 1)
    /** Emits the bookingId whenever a new message arrives */
    val newMessageSignal: SharedFlow<String> = _newMessageSignal

    private var channel: RealtimeChannel? = null
    private var collectJob: Job? = null
    private var currentBookingId: String? = null

    suspend fun startListening(bookingId: String, scope: CoroutineScope) {
        // Already listening to this booking
        if (channel != null && currentBookingId == bookingId) return
        // Switch to a different booking
        if (channel != null) stopListening()

        currentBookingId = bookingId
        val supabase = SupabaseConfig.client
        val ch = supabase.channel("chat-$bookingId")

        val inserts = ch.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "ChatMessage"
        }

        ch.subscribe()
        channel = ch

        collectJob = scope.launch {
            inserts.collect {
                _newMessageSignal.emit(bookingId)
            }
        }
    }

    suspend fun stopListening() {
        collectJob?.cancel()
        collectJob = null
        channel?.let {
            SupabaseConfig.client.realtime.removeChannel(it)
        }
        channel = null
        currentBookingId = null
    }
}
