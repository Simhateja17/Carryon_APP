package com.company.carryon.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DriverJobStop(
    val address: String = "",
    val shortAddress: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val contactName: String = "",
    val contactPhone: String = "",
    val contactEmail: String = "",
    val instructions: String = ""
)

@Serializable
data class DeliveryProof(
    val photoUrl: String? = null,
    val signatureUrl: String? = null,
    val otpCode: String? = null,
    val deliveredAt: String? = null,
    val recipientName: String? = null
)

@Serializable
data class DeliveryJob(
    val id: String = "",
    val displayOrderId: String = "",
    val status: String = "",
    val pickup: DriverJobStop = DriverJobStop(),
    val dropoff: DriverJobStop = DriverJobStop(),
    val customerName: String = "",
    val customerEmail: String = "",
    val customerPhone: String = "",
    val packageType: String = "",
    val packageSize: String = "",
    val estimatedEarnings: Double = 0.0,
    val distance: Double = 0.0,
    val estimatedDuration: Int = 0,
    val createdAt: String = "",
    val expiresAt: String = "",
    val scheduledAt: String? = null,
    val acceptedAt: String? = null,
    val pickedUpAt: String? = null,
    val deliveredAt: String? = null,
    val completedAt: String? = null,
    val notes: String = "",
    val proofOfDelivery: DeliveryProof? = null
)
