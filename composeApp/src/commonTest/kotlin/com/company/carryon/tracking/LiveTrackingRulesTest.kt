package com.company.carryon.tracking

import com.company.carryon.data.model.Address
import com.company.carryon.data.model.Booking
import com.company.carryon.data.model.BookingStatus
import com.company.carryon.data.model.Driver
import com.company.carryon.ui.screens.tracking.LiveJourneyPhase
import com.company.carryon.ui.screens.tracking.buildLiveTrackingDisplay
import com.company.carryon.ui.screens.tracking.isLiveTrackable
import com.company.carryon.ui.screens.tracking.shouldRouteDriverToTarget
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LiveTrackingRulesTest {

    @Test
    fun onlyAssignedAndActiveDeliveryStatusesAreLiveTrackable() {
        val trackableStatuses = setOf(
            BookingStatus.DRIVER_ASSIGNED,
            BookingStatus.DRIVER_ARRIVED,
            BookingStatus.PICKUP_DONE,
            BookingStatus.IN_TRANSIT,
            BookingStatus.ARRIVED_AT_DROP
        )

        assertEquals(trackableStatuses, BookingStatus.entries.filter { it.isLiveTrackable() }.toSet())
        assertFalse(BookingStatus.PENDING.isLiveTrackable())
        assertFalse(BookingStatus.SEARCHING_DRIVER.isLiveTrackable())
        assertFalse(BookingStatus.DELIVERED.isLiveTrackable())
        assertFalse(BookingStatus.CANCELLED.isLiveTrackable())
    }

    @Test
    fun beforePickupRoutesDriverToPickup() {
        val display = buildLiveTrackingDisplay(
            booking = booking(status = BookingStatus.DRIVER_ASSIGNED),
            driverLat = 3.15,
            driverLng = 101.71
        )

        assertEquals(LiveJourneyPhase.HeadingToPickup, display.phase)
        assertEquals("Pickup", display.activeTargetLabel)
        assertEquals(3.1, display.activeTarget.lat)
        assertEquals(101.6, display.activeTarget.lng)
        assertEquals(listOf("driver", "pickup", "drop-off"), display.markers.map { it.id })
        assertTrue(shouldRouteDriverToTarget(3.15, 101.71, display.activeTarget))
    }

    @Test
    fun afterPickupRoutesDriverToDropOff() {
        val display = buildLiveTrackingDisplay(
            booking = booking(status = BookingStatus.IN_TRANSIT),
            driverLat = 3.15,
            driverLng = 101.71
        )

        assertEquals(LiveJourneyPhase.HeadingToDrop, display.phase)
        assertEquals("Drop-off", display.activeTargetLabel)
        assertEquals(3.2, display.activeTarget.lat)
        assertEquals(101.7, display.activeTarget.lng)
        assertTrue(display.subtitle.contains("Alex"))
    }

    private fun booking(status: BookingStatus): Booking =
        Booking(
            status = status,
            pickupAddress = Address(latitude = 3.1, longitude = 101.6),
            deliveryAddress = Address(latitude = 3.2, longitude = 101.7),
            driver = Driver(id = "driver-1", name = "Alex")
        )
}
