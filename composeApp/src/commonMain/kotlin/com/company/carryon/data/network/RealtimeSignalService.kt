package com.company.carryon.data.network

import io.github.jan.supabase.postgrest.query.filter.FilterOperator
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
 * Generic Supabase Realtime listener that watches one or more tables for
 * Postgres changes, then emits the subscribed entity ID as a signal so
 * the UI can refresh via the REST API.
 *
 * Usage:
 *   val service = RealtimeSignalService(
 *       channelPrefix = "chat",
 *       watches = listOf(
 *           TableWatch("ChatMessage", filterColumn = "bookingId", action = WatchAction.INSERT),
 *       ),
 *   )
 *   service.startListening("booking-123", scope)
 *   service.signal.collect { id -> refreshMessages(id) }
 *   service.stopListening()
 */
class RealtimeSignalService(
    private val channelPrefix: String,
    private val watches: List<TableWatch>,
) {
    private val _signal = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val signal: SharedFlow<String> = _signal

    private var channel: RealtimeChannel? = null
    private var collectJob: Job? = null
    private var currentId: String? = null

    suspend fun startListening(entityId: String, scope: CoroutineScope) {
        if (channel != null && currentId == entityId) return
        if (channel != null) stopListening()

        currentId = entityId
        val ch = SupabaseConfig.client.channel("$channelPrefix-$entityId")

        ch.subscribe()
        channel = ch

        collectJob = scope.launch {
            for (watch in watches) {
                launch {
                    when (watch.action) {
                        WatchAction.INSERT -> ch.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                            table = watch.table
                            filter(watch.filterColumn, FilterOperator.EQ, entityId)
                        }.collect { _signal.emit(entityId) }

                        WatchAction.UPDATE -> ch.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
                            table = watch.table
                            filter(watch.filterColumn, FilterOperator.EQ, entityId)
                        }.collect { _signal.emit(entityId) }
                    }
                }
            }
        }
    }

    suspend fun stopListening() {
        collectJob?.cancel()
        collectJob = null
        channel?.let { SupabaseConfig.client.realtime.removeChannel(it) }
        channel = null
        currentId = null
    }
}

enum class WatchAction { INSERT, UPDATE }

data class TableWatch(
    val table: String,
    val filterColumn: String,
    val action: WatchAction,
)
