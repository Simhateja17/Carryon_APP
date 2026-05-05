package com.company.carryon.data.network

import com.company.carryon.data.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
private data class VerifyDeliveryRequest(val otp: String, val deliveryProofUrl: String? = null)

@Serializable
private data class CancelRequest(val reason: String? = null)

@Serializable
data class CreateBookingRequest(
    val pickupAddress: CreateAddressData,
    val deliveryAddress: CreateAddressData,
    val vehicleType: String,
    val paymentMethod: String = "CASH",
    val scheduledTime: String? = null,
    val promoCode: String? = null,
    val senderName: String,
    val senderPhone: String,
    val receiverName: String,
    val receiverPhone: String,
    val receiverEmail: String? = null,
    val notes: String? = null,
    val deliveryMode: String = "Regular",
    val offloading: Boolean = false
)

@Serializable
data class CreateAddressData(
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val contactName: String,
    val contactPhone: String,
    val contactEmail: String? = null,
    val landmark: String = ""
)

@Serializable
data class BookingQuoteRequest(
    val pickupAddress: CreateAddressData,
    val deliveryAddress: CreateAddressData,
    val vehicleType: String,
    val deliveryMode: String = "Regular",
    val offloading: Boolean = false
)

@Serializable
data class BookingQuoteBreakdown(
    val currency: String = "MYR",
    val vehicleType: String = "",
    val basePrice: Double = 0.0,
    val distance: Double = 0.0,
    val pricePerKm: Double = 0.0,
    val distanceFare: Double = 0.0,
    val offloadingFee: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0
)

@Serializable
data class BookingQuote(
    val estimatedPrice: Double = 0.0,
    val price: Double = 0.0,
    val distance: Double = 0.0,
    val duration: Int = 0,
    val isEstimated: Boolean = false,
    val breakdown: BookingQuoteBreakdown? = null
)

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

    suspend fun createBooking(request: CreateBookingRequest, idempotencyKey: String): Result<ApiResponse<Booking>> = runCatching {
        client.post("/api/bookings") {
            contentType(ContentType.Application.Json)
            header("Idempotency-Key", idempotencyKey)
            setBody(request)
        }.body()
    }

    suspend fun quoteBooking(request: BookingQuoteRequest): Result<ApiResponse<BookingQuote>> = runCatching {
        client.post("/api/bookings/quote") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getVehicles(): Result<ApiResponse<List<Vehicle>>> = runCatching {
        client.get("/api/vehicles").body()
    }
}
