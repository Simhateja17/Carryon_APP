package com.company.carryon.data.network

import com.company.carryon.data.model.AuthResponse
import com.company.carryon.data.model.OtpResponse
import com.company.carryon.data.model.OtpSendRequest
import com.company.carryon.data.model.OtpVerifyRequest
import com.company.carryon.data.model.RefreshResponse
import com.company.carryon.data.model.RefreshTokenRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object AuthApi {
    private val client get() = HttpClientFactory.publicClient

    suspend fun sendOtp(email: String, mode: String = "login"): Result<OtpResponse> = runCatching {
        client.post("/api/auth/send-otp") {
            contentType(ContentType.Application.Json)
            setBody(OtpSendRequest(email = email, mode = mode))
        }.body<OtpResponse>()
    }

    suspend fun verifyOtp(email: String, otp: String, mode: String = "login", name: String = ""): Result<AuthResponse> = runCatching {
        client.post("/api/auth/verify-otp") {
            contentType(ContentType.Application.Json)
            setBody(OtpVerifyRequest(email = email, otp = otp, mode = mode, name = name))
        }.body<AuthResponse>()
    }

    suspend fun refresh(refreshToken: String): Result<RefreshResponse> = runCatching {
        client.post("/api/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenRequest(refreshToken = refreshToken))
        }.body<RefreshResponse>()
    }
}
