package com.company.carryon.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import com.company.carryon.data.network.RealtimeSignalService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * Manages the load → subscribe → signal → refresh → scroll lifecycle
 * shared by ChatScreen and TicketDetailScreen.
 *
 * Usage:
 *   val state = rememberRealtimeMessaging(
 *       entityId = bookingId,
 *       signalFlow = RealtimeChatService.newMessageSignal,
 *       startListening = { id, scope -> RealtimeChatService.startListening(id, scope) },
 *       stopListening = { RealtimeChatService.stopListening() },
 *       load = { ChatApi.getMessages(it).getOrNull()?.data ?: emptyList() },
 *   )
 */
class RealtimeMessagingState<T>(
    val items: List<T>,
    val listState: LazyListState,
    val isLoading: Boolean,
) {
    internal var setItems: (List<T>) -> Unit = {}
}

@Composable
fun <T> rememberRealtimeMessaging(
    entityId: String,
    signalFlow: SharedFlow<String>,
    startListening: suspend (String, CoroutineScope) -> Unit,
    stopListening: suspend () -> Unit,
    load: suspend (String) -> List<T>,
): RealtimeMessagingState<T> {
    val scope = rememberCoroutineScope()
    var items by remember { mutableStateOf<List<T>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()

    // Initial load + subscribe
    LaunchedEffect(entityId) {
        isLoading = true
        items = load(entityId)
        isLoading = false
        startListening(entityId, this)
    }

    // Refresh on signal
    LaunchedEffect(entityId) {
        signalFlow.collect { signalId ->
            if (signalId == entityId) {
                items = load(entityId)
            }
        }
    }

    // Cleanup
    DisposableEffect(entityId) {
        onDispose { scope.launch { stopListening() } }
    }

    // Auto-scroll to bottom
    LaunchedEffect(items.size) {
        if (items.isNotEmpty()) {
            listState.animateScrollToItem(items.size - 1)
        }
    }

    return remember { RealtimeMessagingState<T>(emptyList(), listState, true) }.also {
        it.setItems = { newItems -> items = newItems }
    }.let {
        // Return a fresh state object each recomposition so callers see updates
        RealtimeMessagingState(items, listState, isLoading)
    }
}
