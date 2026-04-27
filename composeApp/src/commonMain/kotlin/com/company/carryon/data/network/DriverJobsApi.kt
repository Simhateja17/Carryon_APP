package com.company.carryon.data.network

import com.company.carryon.data.model.ApiResponse
import com.company.carryon.data.model.DeliveryJob
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
private data class SubmitProofRequest(
    val photoUrl: String? = null,
    val recipientName: String? = null,
    val otpCode: String
)

object DriverJobsApi {
    private val client get() = HttpClientFactory.client

    suspend fun getJob(jobId: String): Result<ApiResponse<DeliveryJob>> = runCatching {
        client.get("/api/driver/jobs/$jobId").body()
    }

    suspend fun requestDeliveryOtp(jobId: String): Result<ApiResponse<DeliveryJob>> = runCatching {
        client.post("/api/driver/jobs/$jobId/request-delivery-otp").body()
    }

    suspend fun submitProof(
        jobId: String,
        otpCode: String,
        photoUrl: String?,
        recipientName: String? = null
    ): Result<ApiResponse<DeliveryJob>> = runCatching {
        client.post("/api/driver/jobs/$jobId/proof") {
            contentType(ContentType.Application.Json)
            setBody(
                SubmitProofRequest(
                    photoUrl = photoUrl,
                    recipientName = recipientName,
                    otpCode = otpCode
                )
            )
        }.body()
    }
}
