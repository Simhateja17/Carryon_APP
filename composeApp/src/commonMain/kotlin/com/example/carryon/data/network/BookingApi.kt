package com.example.carryon.data.network

import com.example.carryon.data.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
private data class VerifyDeliveryRequest(val otp: String, val deliveryProofUrl: String? = null)

@Serializable
private data class CancelRequest(val reason: String? = null)

object BookingApi {
    private val client get() = HttpClientFactory.client

    suspend fun getBookings(status: String? = null): Result<ApiResponse<List<Booking>>> = runCatching {
        val url = if (status != null) "/api/bookings?status=$status" else "/api/bookings"
        client.get(url).body()
    }

    suspend fun getBooking(bookingId: String): Result<ApiResponse<Booking>> = runCatching {
        client.get("/api/bookings/$bookingId").body()
    }

    suspend fun verifyDelivery(bookingId: String, otp: String, deliveryProofUrl: String? = null): Result<ApiResponse<Booking>> = runCatching {
        client.post("/api/bookings/$bookingId/verify-delivery") {
            contentType(ContentType.Application.Json)
            setBody(VerifyDeliveryRequest(otp, deliveryProofUrl))
        }.body()
    }

    suspend fun getEta(bookingId: String): Result<ApiResponse<EtaResponse>> = runCatching {
        client.get("/api/bookings/$bookingId/eta").body()
    }

    suspend fun cancelBooking(bookingId: String, reason: String? = null): Result<ApiResponse<Booking>> = runCatching {
        client.post("/api/bookings/$bookingId/cancel") {
            contentType(ContentType.Application.Json)
            setBody(CancelRequest(reason))
        }.body()
    }
}
