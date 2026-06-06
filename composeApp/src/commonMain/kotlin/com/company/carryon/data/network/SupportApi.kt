package com.company.carryon.data.network

import com.company.carryon.data.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class AiChatMessage(val role: String, val parts: String)

@Serializable
private data class AiChatRequest(val message: String, val history: List<AiChatMessage>)

@Serializable
data class AiChatResponse(val reply: String)

@Serializable
data class SupportIssueOption(
    val id: String = "",
    val label: String = "",
    val category: String = "",
    val priority: String = "MEDIUM",
    val requiresBooking: Boolean = false,
    val requiresDetails: Boolean = false,
    val allowsAttachments: Boolean = false,
    val emergency: Boolean = false,
    val children: List<SupportIssueOption> = emptyList()
)

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

@Serializable
private data class IntakeTicketRequest(
    val issueId: String,
    val bookingId: String? = null,
    val details: String = "",
    val answers: Map<String, String> = emptyMap(),
    val displayPath: List<String> = emptyList()
)

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

    suspend fun getIntakeOptions(): Result<ApiResponse<List<SupportIssueOption>>> = runCatching {
        client.get("/api/support/intake/options").body()
    }

    suspend fun createIntakeTicket(
        issueId: String,
        bookingId: String? = null,
        details: String = "",
        answers: Map<String, String> = emptyMap(),
        displayPath: List<String> = emptyList()
    ): Result<ApiResponse<SupportTicket>> = runCatching {
        client.post("/api/support/intake/tickets") {
            contentType(ContentType.Application.Json)
            setBody(IntakeTicketRequest(issueId, bookingId, details, answers, displayPath))
        }.body()
    }
}
