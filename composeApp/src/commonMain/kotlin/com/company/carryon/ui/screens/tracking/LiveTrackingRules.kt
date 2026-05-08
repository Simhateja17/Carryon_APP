package com.company.carryon.ui.screens.tracking

import com.company.carryon.data.model.BookingStatus

fun BookingStatus.isLiveTrackable(): Boolean =
    this == BookingStatus.DRIVER_ASSIGNED ||
        this == BookingStatus.DRIVER_ARRIVED ||
        this == BookingStatus.PICKUP_DONE ||
        this == BookingStatus.IN_TRANSIT ||
        this == BookingStatus.ARRIVED_AT_DROP
