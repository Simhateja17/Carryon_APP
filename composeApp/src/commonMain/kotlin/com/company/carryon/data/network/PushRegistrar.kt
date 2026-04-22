package com.company.carryon.data.network

import io.ktor.client.request.delete
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
private data class PushTokenRegistrationRequest(
    val token: String,
    val platform: String,
    val deviceId: String,
    val appVersion: String? = null,
)

@Serializable
private data class PushTokenDeleteRequest(
    val deviceId: String,
)

object PushRegistrar {
    suspend fun syncUserPushToken(): Result<Boolean> = runCatching {
        if (!AuthStateManager.ensureFreshToken()) return@runCatching false

        val token = currentPushToken()?.trim().orEmpty()
        if (token.isEmpty()) return@runCatching false

        HttpClientFactory.client.put("/api/users/me/push-token") {
            contentType(ContentType.Application.Json)
            setBody(
                PushTokenRegistrationRequest(
                    token = token,
                    platform = currentPushPlatform(),
                    deviceId = getOrCreateDeviceId(),
                )
            )
        }
        true
    }

    suspend fun unregisterUserPushToken(): Result<Boolean> = runCatching {
        if (!AuthStateManager.ensureFreshToken()) return@runCatching false

        HttpClientFactory.client.delete("/api/users/me/push-token") {
            contentType(ContentType.Application.Json)
            setBody(PushTokenDeleteRequest(deviceId = getOrCreateDeviceId()))
        }
        true
    }
}
