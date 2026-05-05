package com.company.carryon.data.network

import com.company.carryon.data.model.ApiResponse
import com.company.carryon.data.model.AccountDeleteResponse
import com.company.carryon.data.model.PrivacyConsentResponse
import com.company.carryon.data.model.User
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
private data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null
)

@Serializable
private data class PrivacyConsentRequest(val policyVersion: String)

object UserApi {
    private val client get() = HttpClientFactory.client

    suspend fun getProfile(): Result<User> = runCatching {
        val response = client.get("/api/users/me")
            .body<ApiResponse<User>>()
        response.data ?: throw Exception("User not found")
    }

    suspend fun updateProfile(name: String, email: String): Result<User> = runCatching {
        val response = client.put("/api/users/me") {
            contentType(ContentType.Application.Json)
            setBody(UpdateProfileRequest(name = name, email = email))
        }.body<ApiResponse<User>>()
        response.data ?: throw Exception("Update failed")
    }

    suspend fun updateLanguage(language: String): Result<User> = runCatching {
        val response = client.put("/api/users/me") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("language" to language))
        }.body<ApiResponse<User>>()
        response.data ?: throw Exception("Update failed")
    }

    @Serializable
    data class UserStats(val totalShipments: Int = 0, val userRating: Double = 0.0, val ratingCount: Int = 0)

    suspend fun getUserStats(): Result<UserStats> = runCatching {
        val response = client.get("/api/users/me/stats")
            .body<ApiResponse<UserStats>>()
        response.data ?: throw Exception("Stats not found")
    }

    suspend fun exportAccount(): Result<JsonObject> = runCatching {
        val response = client.get("/api/v1/users/me/export")
            .body<ApiResponse<JsonObject>>()
        response.data ?: throw Exception("Export not found")
    }

    suspend fun deleteAccount(): Result<AccountDeleteResponse> = runCatching {
        val response = client.delete("/api/v1/users/me")
            .body<ApiResponse<AccountDeleteResponse>>()
        response.data ?: throw Exception("Delete failed")
    }

    suspend fun recordPrivacyConsent(policyVersion: String): Result<PrivacyConsentResponse> = runCatching {
        val response = client.post("/api/v1/users/me/privacy-consent") {
            contentType(ContentType.Application.Json)
            setBody(PrivacyConsentRequest(policyVersion))
        }.body<ApiResponse<PrivacyConsentResponse>>()
        response.data ?: throw Exception("Consent update failed")
    }
}
