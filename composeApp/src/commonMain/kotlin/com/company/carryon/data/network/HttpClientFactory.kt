package com.company.carryon.data.network

import io.github.jan.supabase.auth.auth
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val networkJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

private fun getStoredToken(): String? {
    return try {
        val session = SupabaseConfig.client.auth.currentSessionOrNull()
        if (session != null) {
            saveToken(session.accessToken)
            session.accessToken
        } else {
            getToken()
        }
    } catch (_: Exception) {
        getToken()
    }
}

class SessionExpiredException(message: String = "Session expired") : Exception(message)

object HttpClientFactory {
    private val rawClient = HttpClient {
        install(ContentNegotiation) {
            json(networkJson)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 30_000
        }
        defaultRequest {
            url.takeFrom(apiBaseUrl())
            getStoredToken()?.let { headers.append("Authorization", "Bearer $it") }
        }
    }

    val client: HttpClient by lazy { rawClient }

    suspend fun execute(request: HttpRequestBuilder.() -> Unit): HttpResponse {
        val response = rawClient.request(request)
        if (response.status == HttpStatusCode.Unauthorized) {
            AuthStateManager.onAuthExpired()
            throw SessionExpiredException()
        }
        if (response.status.value >= 400) {
            val body = response.bodyAsText()
            val message = try {
                networkJson.parseToJsonElement(body).jsonObject["message"]?.jsonPrimitive?.content
                    ?: "Request failed"
            } catch (_: Exception) {
                "Request failed (${response.status.value})"
            }
            throw Exception(message)
        }
        return response
    }
}
