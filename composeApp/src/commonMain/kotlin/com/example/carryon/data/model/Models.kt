package com.example.carryon.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImage: String? = null,
    val language: String = "",
    val isVerified: Boolean = false,
    val referralCode: String? = null,
    val createdAt: String = ""
)

@Serializable
data class Address(
    val id: String = "",
    val label: String = "",
    val address: String = "",
    val landmark: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val contactName: String = "",
    val contactPhone: String = "",
    val type: AddressType = AddressType.OTHER
)

@Serializable
enum class AddressType {
    HOME, OFFICE, OTHER
}

@Serializable
data class Vehicle(
    val id: String,
    val type: String = "",
    val name: String,
    val description: String,
    val capacity: String,
    val basePrice: Double,
    val pricePerKm: Double,
    val iconName: String,
    val isAvailable: Boolean = true
)

@Serializable
data class Booking(
    val id: String = "",
    val userId: String = "",
    val pickupAddress: Address = Address(),
    val deliveryAddress: Address = Address(),
    val vehicleType: String = "",
    val scheduledTime: String? = null,
    val estimatedPrice: Double = 0.0,
    val finalPrice: Double = 0.0,
    val distance: Double = 0.0,
    val duration: Int = 0,
    val status: BookingStatus = BookingStatus.PENDING,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val driver: Driver? = null,
    val otp: String = "",
    val promoCode: String? = null,
    val discountAmount: Double = 0.0,
    val deliveryProofUrl: String? = null,
    val deliveredAt: String? = null,
    val eta: Int? = null,
    val senderName: String = "",
    val senderPhone: String = "",
    val receiverName: String = "",
    val receiverPhone: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
enum class BookingStatus {
    PENDING,
    SEARCHING_DRIVER,
    DRIVER_ASSIGNED,
    DRIVER_ARRIVED,
    PICKUP_DONE,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED
}

@Serializable
enum class PaymentMethod {
    CASH, DUITNOW, CARD, WALLET
}

@Serializable
enum class PaymentStatus {
    PENDING, COMPLETED, FAILED, REFUNDED
}

@Serializable
data class Driver(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val photo: String? = null,
    val rating: Double = 0.0,
    val totalTrips: Int = 0,
    val vehicleNumber: String = "",
    val vehicleModel: String = "",
    val currentLatitude: Double = 0.0,
    val currentLongitude: Double = 0.0
)

@Serializable
data class Order(
    val id: String = "",
    val bookingId: String = "",
    val rating: Int? = null,
    val review: String? = null,
    val tags: List<String> = emptyList(),
    val tipAmount: Double = 0.0,
    val completedAt: String = ""
)

@Serializable
data class PriceEstimate(
    val vehicleId: String,
    val vehicleName: String,
    val estimatedPrice: Double,
    val distance: Double,
    val duration: Int,
    val surge: Double = 1.0
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String = "",
    val data: T? = null
)

@Serializable
data class OtpSendRequest(
    val email: String,
    val mode: String = "login"
)

@Serializable
data class OtpVerifyRequest(
    val email: String,
    val otp: String,
    val mode: String = "login",
    val name: String = ""
)

@Serializable
data class OtpResponse(
    val success: Boolean,
    val message: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: User,
    val isNewUser: Boolean
)

@Serializable
data class LocationUpdate(
    val driverId: String,
    val latitude: Double,
    val longitude: Double,
    val heading: Double = 0.0
)

// AWS Location Services models

@Serializable
data class PlaceResult(
    val placeId: String = "",
    val label: String = "",
    val address: String = "",
    val city: String = "",
    val region: String = "",
    val country: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

@Serializable
data class RouteResult(
    val distance: Double = 0.0,
    val duration: Int = 0,
    val geometry: List<LatLng> = emptyList()
)

@Serializable
data class LatLng(
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

@Serializable
data class MapConfig(
    val apiKey: String = "",
    val styleUrl: String = "",
    val region: String = ""
)

@Serializable
data class DevicePosition(
    val deviceId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: String = ""
)

@Serializable
data class CalculateRouteRequest(
    val originLat: Double,
    val originLng: Double,
    val destLat: Double,
    val destLng: Double
)

@Serializable
data class UpdatePositionRequest(
    val deviceId: String,
    val latitude: Double,
    val longitude: Double
)

// AWS Location Services v2 models

@Serializable
data class AutocompleteHighlight(
    val start: Int = 0,
    val end: Int = 0
)

@Serializable
data class AutocompleteResult(
    val placeId: String = "",
    val title: String = "",
    val address: String = "",
    val highlights: List<AutocompleteHighlight> = emptyList()
)

@Serializable
data class NearbyPlace(
    val placeId: String = "",
    val title: String = "",
    val address: String = "",
    val categories: List<String> = emptyList(),
    val distance: Double = 0.0,
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

@Serializable
data class GeocodedPlace(
    val placeId: String = "",
    val title: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val address: String = ""
)

@Serializable
data class IsolineResult(
    val geometry: List<LatLng> = emptyList(),
    val distanceMeters: Double = 0.0,
    val durationSeconds: Double = 0.0
)

@Serializable
data class SnapResult(
    val snappedPoints: List<LatLng> = emptyList()
)

@Serializable
data class StaticMapResponse(
    val url: String = ""
)

@Serializable
data class GeocodeRequest(
    val address: String
)

@Serializable
data class SnapToRoadsRequest(
    val points: List<LatLng>
)

@Serializable
data class IsolineRequest(
    val lat: Double,
    val lng: Double,
    val minutes: Int
)

// ── Promo & Referral Models ─────────────────────────────

@Serializable
data class Coupon(
    val id: String = "",
    val code: String = "",
    val description: String = "",
    val discountType: String = "PERCENTAGE",
    val discountValue: Double = 0.0,
    val maxDiscount: Double? = null,
    val minOrderValue: Double = 0.0,
    val expiresAt: String? = null
)

@Serializable
data class PromoValidateResponse(
    val couponId: String = "",
    val code: String = "",
    val description: String = "",
    val discountType: String = "",
    val discountValue: Double = 0.0,
    val calculatedDiscount: Double = 0.0
)

@Serializable
data class PromoApplyResponse(
    val discount: Double = 0.0,
    val finalPrice: Double = 0.0
)

@Serializable
data class ReferralInfo(
    val referralCode: String = "",
    val totalReferrals: Int = 0,
    val completedReferrals: Int = 0,
    val totalEarned: Double = 0.0
)

// ── Chat Models ─────────────────────────────────────────

@Serializable
data class ChatMessage(
    val id: String = "",
    val bookingId: String = "",
    val senderId: String = "",
    val senderType: String = "USER",
    val message: String = "",
    val imageUrl: String? = null,
    val isRead: Boolean = false,
    val createdAt: String = ""
)

// ── Wallet Models ───────────────────────────────────────

@Serializable
data class Wallet(
    val id: String = "",
    val userId: String = "",
    val balance: Double = 0.0,
    val transactions: List<WalletTransaction> = emptyList()
)

@Serializable
data class WalletTransaction(
    val id: String = "",
    val walletId: String = "",
    val type: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val referenceId: String? = null,
    val createdAt: String = ""
)

@Serializable
data class WalletPayResponse(
    val balance: Double = 0.0,
    val amountPaid: Double = 0.0
)

@Serializable
data class WalletTopUpResponse(
    val balance: Double = 0.0
)

// ── Support Ticket Models ───────────────────────────────

@Serializable
data class SupportTicket(
    val id: String = "",
    val userId: String = "",
    val bookingId: String? = null,
    val subject: String = "",
    val category: String = "OTHER",
    val status: String = "OPEN",
    val priority: String = "MEDIUM",
    val messages: List<TicketMessage> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class TicketMessage(
    val id: String = "",
    val ticketId: String = "",
    val senderId: String = "",
    val isStaff: Boolean = false,
    val message: String = "",
    val imageUrl: String? = null,
    val createdAt: String = ""
)

// ── ETA Models ──────────────────────────────────────────

@Serializable
data class EtaResponse(
    val status: String = "",
    val etaMinutes: Int = 0,
    val statusMessage: String = "",
    val driverName: String? = null,
    val driverPhone: String? = null,
    val driverLocation: LatLng? = null
)

// ── Invoice Models ──────────────────────────────────────

@Serializable
data class Invoice(
    val id: String = "",
    val bookingId: String = "",
    val userId: String = "",
    val invoiceNumber: String = "",
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val taxRate: Double = 0.06,
    val currency: String = "MYR",
    val issuedAt: String = ""
)

@Serializable
data class InvoiceDetail(
    val invoice: Invoice = Invoice(),
    val booking: InvoiceBooking = InvoiceBooking(),
    val customer: InvoiceCustomer = InvoiceCustomer(),
    val company: InvoiceCompany = InvoiceCompany()
)

@Serializable
data class InvoiceBooking(
    val id: String = "",
    val vehicleType: String = "",
    val distance: Double = 0.0,
    val duration: Int = 0,
    val status: String = "",
    val paymentMethod: String = "",
    val createdAt: String = "",
    val deliveredAt: String? = null,
    val pickupAddress: Address = Address(),
    val deliveryAddress: Address = Address(),
    val driver: InvoiceDriver? = null
)

@Serializable
data class InvoiceDriver(
    val name: String = "",
    val phone: String = "",
    val vehicleNumber: String = ""
)

@Serializable
data class InvoiceCustomer(
    val name: String = "",
    val email: String = "",
    val phone: String = ""
)

@Serializable
data class InvoiceCompany(
    val name: String = "",
    val registration: String = "",
    val sstNo: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = ""
)

// ── Rating Request ──────────────────────────────────────

@Serializable
data class RatingRequest(
    val rating: Int,
    val review: String? = null,
    val tags: List<String> = emptyList(),
    val tipAmount: Double = 0.0
)
