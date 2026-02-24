package com.example.carryon.data.network

import com.example.carryon.data.model.ApiResponse
import com.example.carryon.data.model.User
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object UserApi {
    private val client get() = HttpClientFactory.client

    suspend fun getProfile(): Result<User> = runCatching {
        val response = client.get("/api/users/me")
            .body<ApiResponse<User>>()
        response.data ?: throw Exception("User not found")
    }

    suspend fun updateLanguage(language: String): Result<User> = runCatching {
        val response = client.put("/api/users/me") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("language" to language))
        }.body<ApiResponse<User>>()
        response.data ?: throw Exception("Update failed")
    }
}
