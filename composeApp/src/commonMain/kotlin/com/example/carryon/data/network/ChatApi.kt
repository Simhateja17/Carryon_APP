package com.example.carryon.data.network

import com.example.carryon.data.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
private data class SendMessageRequest(val message: String, val imageUrl: String? = null)

object ChatApi {
    private val client get() = HttpClientFactory.client

    suspend fun getMessages(bookingId: String): Result<ApiResponse<List<ChatMessage>>> = runCatching {
        client.get("/api/chat/$bookingId").body()
    }

    suspend fun sendMessage(bookingId: String, message: String, imageUrl: String? = null): Result<ApiResponse<ChatMessage>> = runCatching {
        client.post("/api/chat/$bookingId") {
            contentType(ContentType.Application.Json)
            setBody(SendMessageRequest(message, imageUrl))
        }.body()
    }

    suspend fun getUnreadCount(bookingId: String): Result<ApiResponse<UnreadCount>> = runCatching {
        client.get("/api/chat/$bookingId/unread").body()
    }

    suspend fun getQuickMessages(bookingId: String): Result<ApiResponse<List<String>>> = runCatching {
        client.get("/api/chat/$bookingId/quick-messages").body()
    }
}

@Serializable
data class UnreadCount(val unreadCount: Int = 0)
