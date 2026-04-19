package com.company.carryon.data.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.createClientPlugin
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

class SessionExpiredException(message: String = "Session expired") : Exception(message)

private val AuthHeaderPlugin = createClientPlugin("AuthHeaderPlugin") {
    onRequest { request, _ ->
        request.headers.remove(HttpHeaders.Authorization)
        AuthStateManager.getValidAccessToken()?.let { token ->
            request.headers.append(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}

object HttpClientFactory {
    private fun buildClient(includeAuth: Boolean, notifyAuthExpiry: Boolean): HttpClient = HttpClient {
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
        }
        if (includeAuth) {
            install(AuthHeaderPlugin)
        }
        HttpResponseValidator {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    if (notifyAuthExpiry) {
                        AuthStateManager.onAuthExpired()
                    }
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
            }
        }
    }

    val client: HttpClient by lazy { buildClient(includeAuth = true, notifyAuthExpiry = true) }
    val publicClient: HttpClient by lazy { buildClient(includeAuth = false, notifyAuthExpiry = false) }

    suspend fun execute(request: HttpRequestBuilder.() -> Unit): HttpResponse {
        val response = client.request(request)
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
