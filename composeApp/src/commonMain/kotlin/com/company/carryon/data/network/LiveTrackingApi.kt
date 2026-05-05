package com.company.carryon.data.network

import io.ktor.client.plugins.websocket.*
import io.ktor.http.encodeURLParameter
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class LiveDriverLocation(
    val latitude: Double,
    val longitude: Double,
    val timestamp: String = ""
)

object LiveTrackingApi {
    private val json = Json { ignoreUnknownKeys = true }

    private fun liveTrackingUrl(bookingId: String, token: String): String {
        val base = apiBaseUrl()
            .removeSuffix("/")
            .replaceFirst("https://", "wss://")
            .replaceFirst("http://", "ws://")
        return "$base/api/tracking/live?bookingId=${bookingId.encodeURLParameter()}&token=${token.encodeURLParameter()}"
    }

    suspend fun subscribeToBooking(
        bookingId: String,
        onConnected: () -> Unit = {},
        onLocation: (LiveDriverLocation) -> Unit
    ): Result<Unit> = runCatching {
        val token = AuthStateManager.getValidAccessToken()
            ?: throw SessionExpiredException()

        HttpClientFactory.client.webSocket(urlString = liveTrackingUrl(bookingId, token)) {
            for (frame in incoming) {
                if (frame !is Frame.Text) continue
                val payload = json.parseToJsonElement(frame.readText()).jsonObject
                when (payload["type"]?.jsonPrimitive?.content) {
                    "connected" -> onConnected()
                    "driver_location" -> {
                        val latitude = payload["latitude"]?.jsonPrimitive?.doubleOrNull
                        val longitude = payload["longitude"]?.jsonPrimitive?.doubleOrNull
                        if (latitude != null && longitude != null) {
                            onLocation(
                                LiveDriverLocation(
                                    latitude = latitude,
                                    longitude = longitude,
                                    timestamp = payload["timestamp"]?.jsonPrimitive?.content ?: ""
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
