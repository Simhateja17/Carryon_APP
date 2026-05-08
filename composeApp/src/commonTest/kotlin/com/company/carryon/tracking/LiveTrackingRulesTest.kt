package com.company.carryon.tracking

import com.company.carryon.data.model.BookingStatus
import com.company.carryon.ui.screens.tracking.isLiveTrackable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

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
}
