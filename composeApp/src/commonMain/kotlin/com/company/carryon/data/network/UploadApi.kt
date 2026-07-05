package com.company.carryon.data.network

import com.company.carryon.data.model.ApiResponse
import io.ktor.client.call.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
private data class UploadResponse(
    val success: Boolean,
    val data: UploadData? = null
)

@Serializable
private data class UploadData(
    val url: String
)

@Serializable
data class ProfileImageUploadData(
    val profileImage: String,
    val profileImageUrl: String
)

object UploadApi {
    private val client get() = HttpClientFactory.client

    suspend fun uploadPackageImage(imageBytes: ByteArray): Result<String> = runCatching {
        val response = client.submitFormWithBinaryData(
            url = "/api/upload/package-image",
            formData = formData {
                append("image", imageBytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"package.jpg\"")
                })
            }
        ).body<UploadResponse>()

        response.data?.url ?: throw Exception("Upload failed — no URL returned")
    }

    suspend fun uploadProfileImage(imageBytes: ByteArray): Result<ProfileImageUploadData> = runCatching {
        val response = client.submitFormWithBinaryData(
            url = "/api/upload/profile-image",
            formData = formData {
                append("image", imageBytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                })
            }
        ).body<ApiResponse<ProfileImageUploadData>>()

        response.data ?: throw Exception("Upload failed — no profile image returned")
    }
}
