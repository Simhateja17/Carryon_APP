package com.example.carryon.data.network

import com.example.carryon.data.model.Address
import com.example.carryon.data.model.ApiResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
private data class CreateAddressRequest(
    val label: String,
    val address: String,
    val landmark: String,
    val latitude: Double,
    val longitude: Double,
    val contactName: String,
    val contactPhone: String,
    val type: String
)

object AddressApi {
    private val client get() = HttpClientFactory.client

    suspend fun getAddresses(): Result<List<Address>> = runCatching {
        val response = client.get("/api/addresses")
            .body<ApiResponse<List<Address>>>()
        response.data ?: emptyList()
    }

    suspend fun createAddress(address: Address): Result<Address> = runCatching {
        val response = client.post("/api/addresses") {
            contentType(ContentType.Application.Json)
            setBody(
                CreateAddressRequest(
                    label = address.label,
                    address = address.address,
                    landmark = address.landmark,
                    latitude = address.latitude,
                    longitude = address.longitude,
                    contactName = address.contactName,
                    contactPhone = address.contactPhone,
                    type = address.type.name
                )
            )
        }.body<ApiResponse<Address>>()
        response.data ?: throw Exception("Failed to create address")
    }

    suspend fun updateAddress(id: String, address: Address): Result<Address> = runCatching {
        val response = client.put("/api/addresses/$id") {
            contentType(ContentType.Application.Json)
            setBody(
                CreateAddressRequest(
                    label = address.label,
                    address = address.address,
                    landmark = address.landmark,
                    latitude = address.latitude,
                    longitude = address.longitude,
                    contactName = address.contactName,
                    contactPhone = address.contactPhone,
                    type = address.type.name
                )
            )
        }.body<ApiResponse<Address>>()
        response.data ?: throw Exception("Failed to update address")
    }

    suspend fun deleteAddress(id: String): Result<Unit> = runCatching {
        client.delete("/api/addresses/$id")
        Unit
    }
}
