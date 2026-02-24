package com.example.carryon.data.network

import com.example.carryon.data.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
private data class CreateTicketRequest(
    val subject: String,
    val category: String = "OTHER",
    val message: String,
    val bookingId: String? = null,
    val priority: String = "MEDIUM"
)

@Serializable
private data class ReplyRequest(val message: String, val imageUrl: String? = null)

object SupportApi {
    private val client get() = HttpClientFactory.client

    suspend fun createTicket(
        subject: String,
        category: String,
        message: String,
        bookingId: String? = null
    ): Result<ApiResponse<SupportTicket>> = runCatching {
        client.post("/api/support/tickets") {
            contentType(ContentType.Application.Json)
            setBody(CreateTicketRequest(subject, category, message, bookingId))
        }.body()
    }

    suspend fun getTickets(status: String? = null): Result<ApiResponse<List<SupportTicket>>> = runCatching {
        val url = if (status != null) "/api/support/tickets?status=$status" else "/api/support/tickets"
        client.get(url).body()
    }

    suspend fun getTicket(ticketId: String): Result<ApiResponse<SupportTicket>> = runCatching {
        client.get("/api/support/tickets/$ticketId").body()
    }

    suspend fun replyToTicket(ticketId: String, message: String): Result<ApiResponse<TicketMessage>> = runCatching {
        client.post("/api/support/tickets/$ticketId/reply") {
            contentType(ContentType.Application.Json)
            setBody(ReplyRequest(message))
        }.body()
    }

    suspend fun closeTicket(ticketId: String): Result<ApiResponse<SupportTicket>> = runCatching {
        client.post("/api/support/tickets/$ticketId/close").body()
    }
}
