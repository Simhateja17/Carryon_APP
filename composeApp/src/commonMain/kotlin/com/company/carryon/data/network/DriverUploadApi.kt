package com.company.carryon.data.network

import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.Serializable

@Serializable
private data class DriverUploadResponse(
    val success: Boolean,
    val data: DriverUploadData? = null
)

@Serializable
private data class DriverUploadData(
    val url: String
)

object DriverUploadApi {
    private val client get() = HttpClientFactory.client

    suspend fun uploadProofImage(imageBytes: ByteArray): Result<String> = runCatching {
        val response = client.submitFormWithBinaryData(
            url = "/api/driver/upload/package-image",
            formData = formData {
                append("image", imageBytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"proof-of-delivery.jpg\"")
                })
            }
        ).body<DriverUploadResponse>()

        response.data?.url ?: throw Exception("Upload failed — no URL returned")
    }
}
