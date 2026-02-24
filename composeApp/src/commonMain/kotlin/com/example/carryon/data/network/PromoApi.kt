package com.example.carryon.data.network

import com.example.carryon.data.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
private data class ValidateRequest(val code: String, val orderAmount: Double)

@Serializable
private data class ApplyRequest(val code: String, val bookingId: String)

@Serializable
private data class ReferralApplyRequest(val referralCode: String)

object PromoApi {
    private val client get() = HttpClientFactory.client

    suspend fun validatePromo(code: String, orderAmount: Double): Result<ApiResponse<PromoValidateResponse>> = runCatching {
        client.post("/api/promo/validate") {
            contentType(ContentType.Application.Json)
            setBody(ValidateRequest(code, orderAmount))
        }.body()
    }

    suspend fun applyPromo(code: String, bookingId: String): Result<ApiResponse<PromoApplyResponse>> = runCatching {
        client.post("/api/promo/apply") {
            contentType(ContentType.Application.Json)
            setBody(ApplyRequest(code, bookingId))
        }.body()
    }

    suspend fun getAvailableCoupons(): Result<ApiResponse<List<Coupon>>> = runCatching {
        client.get("/api/promo/coupons").body()
    }

    suspend fun getReferralInfo(): Result<ApiResponse<ReferralInfo>> = runCatching {
        client.get("/api/promo/referral").body()
    }

    suspend fun applyReferralCode(referralCode: String): Result<ApiResponse<Unit>> = runCatching {
        client.post("/api/promo/referral/apply") {
            contentType(ContentType.Application.Json)
            setBody(ReferralApplyRequest(referralCode))
        }.body()
    }
}
