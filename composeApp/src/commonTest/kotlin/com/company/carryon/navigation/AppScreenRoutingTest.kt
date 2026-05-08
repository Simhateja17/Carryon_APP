package com.company.carryon.navigation

import com.company.carryon.AppScreen
import com.company.carryon.AppScreenDestination
import com.company.carryon.contentDestination
import kotlin.test.Test
import kotlin.test.assertEquals

class AppScreenRoutingTest {

    @Test
    fun trackingLiveRouteResolvesToLiveTrackingDestination() {
        assertEquals(
            AppScreenDestination.TrackingLive,
            AppScreen.TrackingLive("booking-123").contentDestination()
        )
    }

    @Test
    fun trackingEntryScreensResolveToActiveShipmentDestination() {
        listOf(
            AppScreen.ActiveShipment,
            AppScreen.TrackShipment,
            AppScreen.TrackOrder("booking-123"),
            AppScreen.DeliveryDetails("booking-123")
        ).forEach { screen ->
            assertEquals(AppScreenDestination.ActiveShipment, screen.contentDestination())
        }
    }
}
