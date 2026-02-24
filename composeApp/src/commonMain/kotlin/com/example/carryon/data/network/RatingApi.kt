package com.example.carryon.data.network

import com.example.carryon.data.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object RatingApi {
    private val client get() = HttpClientFactory.client

    suspend fun submitRating(
        bookingId: String,
        rating: Int,
        review: String? = null,
        tags: List<String> = emptyList(),
        tipAmount: Double = 0.0
    ): Result<ApiResponse<Order>> = runCatching {
        client.post("/api/ratings/$bookingId") {
            contentType(ContentType.Application.Json)
            setBody(RatingRequest(rating, review, tags, tipAmount))
        }.body()
    }

    suspend fun getRating(bookingId: String): Result<ApiResponse<Order>> = runCatching {
        client.get("/api/ratings/$bookingId").body()
    }
}
