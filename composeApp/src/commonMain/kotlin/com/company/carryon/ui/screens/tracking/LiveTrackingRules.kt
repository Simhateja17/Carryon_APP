package com.company.carryon.ui.screens.tracking

import com.company.carryon.data.model.Address
import com.company.carryon.data.model.Booking
import com.company.carryon.data.model.BookingStatus
import com.company.carryon.data.model.LatLng
import com.company.carryon.ui.components.MapMarker
import com.company.carryon.ui.components.MarkerColor

fun BookingStatus.isLiveTrackable(): Boolean =
    this == BookingStatus.DRIVER_ASSIGNED ||
        this == BookingStatus.DRIVER_ARRIVED ||
        this == BookingStatus.PICKUP_DONE ||
        this == BookingStatus.IN_TRANSIT ||
        this == BookingStatus.ARRIVED_AT_DROP

enum class LiveJourneyPhase {
    HeadingToPickup,
    AtPickup,
    HeadingToDrop,
    AtDrop
}

data class LiveTrackingDisplay(
    val phase: LiveJourneyPhase,
    val title: String,
    val subtitle: String,
    val activeTarget: LatLng,
    val activeTargetLabel: String,
    val pickup: LatLng,
    val dropOff: LatLng,
    val markers: List<MapMarker>
)

fun buildLiveTrackingDisplay(
    booking: Booking,
    driverLat: Double,
    driverLng: Double
): LiveTrackingDisplay {
    val driverName = booking.driver?.name?.takeIf { it.isNotBlank() } ?: "Delivery partner"
    val pickup = booking.pickupAddress.toLatLng()
    val dropOff = booking.deliveryAddress.toLatLng()
    val phase = when (booking.status) {
        BookingStatus.DRIVER_ASSIGNED -> LiveJourneyPhase.HeadingToPickup
        BookingStatus.DRIVER_ARRIVED -> LiveJourneyPhase.AtPickup
        BookingStatus.PICKUP_DONE,
        BookingStatus.IN_TRANSIT -> LiveJourneyPhase.HeadingToDrop
        BookingStatus.ARRIVED_AT_DROP -> LiveJourneyPhase.AtDrop
        else -> LiveJourneyPhase.HeadingToPickup
    }
    val activeTarget = when (phase) {
        LiveJourneyPhase.HeadingToPickup,
        LiveJourneyPhase.AtPickup -> pickup
        LiveJourneyPhase.HeadingToDrop,
        LiveJourneyPhase.AtDrop -> dropOff
    }
    val markers = listOfNotNull(
        if (driverLat.isValidCoordinate() && driverLng.isValidCoordinate()) {
            MapMarker("driver", driverLat, driverLng, "Driver", MarkerColor.BLUE)
        } else {
            null
        },
        if (pickup.isValid()) MapMarker("pickup", pickup.lat, pickup.lng, "Pickup", MarkerColor.RED) else null,
        if (dropOff.isValid()) MapMarker("drop-off", dropOff.lat, dropOff.lng, "Drop-off", MarkerColor.GREEN) else null
    )

    return LiveTrackingDisplay(
        phase = phase,
        title = when (phase) {
            LiveJourneyPhase.HeadingToPickup -> "Heading to pickup"
            LiveJourneyPhase.AtPickup -> "At pickup"
            LiveJourneyPhase.HeadingToDrop -> "Out for delivery"
            LiveJourneyPhase.AtDrop -> "Arrived at drop-off"
        },
        subtitle = when (phase) {
            LiveJourneyPhase.HeadingToPickup -> "$driverName is driving to the pickup location"
            LiveJourneyPhase.AtPickup -> "$driverName has reached the pickup location"
            LiveJourneyPhase.HeadingToDrop -> "$driverName is driving safely to deliver your package"
            LiveJourneyPhase.AtDrop -> "$driverName has reached the drop-off location"
        },
        activeTarget = activeTarget,
        activeTargetLabel = when (phase) {
            LiveJourneyPhase.HeadingToPickup,
            LiveJourneyPhase.AtPickup -> "Pickup"
            LiveJourneyPhase.HeadingToDrop,
            LiveJourneyPhase.AtDrop -> "Drop-off"
        },
        pickup = pickup,
        dropOff = dropOff,
        markers = markers
    )
}

fun shouldRouteDriverToTarget(driverLat: Double, driverLng: Double, target: LatLng): Boolean =
    driverLat.isValidCoordinate() &&
        driverLng.isValidCoordinate() &&
        target.isValid()

private fun Address.toLatLng(): LatLng = LatLng(latitude, longitude)

private fun LatLng.isValid(): Boolean = lat.isValidCoordinate() && lng.isValidCoordinate()

private fun Double.isValidCoordinate(): Boolean = this != 0.0
