package com.example.carryon.data.network

import com.example.carryon.data.model.AuthResponse
import com.example.carryon.data.model.OtpResponse
import com.example.carryon.data.model.OtpSendRequest
import com.example.carryon.data.model.OtpVerifyRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object AuthApi {
    private val client get() = HttpClientFactory.client

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
}
