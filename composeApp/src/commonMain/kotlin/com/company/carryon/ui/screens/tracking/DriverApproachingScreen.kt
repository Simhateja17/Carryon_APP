package com.company.carryon.ui.screens.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.model.Booking
import com.company.carryon.data.model.BookingStatus
import com.company.carryon.data.model.LatLng
import com.company.carryon.data.model.MapConfig
import com.company.carryon.data.model.RouteResult
import com.company.carryon.data.network.BookingApi
import com.company.carryon.data.network.LocationApi
import com.company.carryon.i18n.LocalStrings
import com.company.carryon.ui.components.MapMarker
import com.company.carryon.ui.components.MapViewComposable
import com.company.carryon.ui.components.MarkerColor
import com.company.carryon.ui.theme.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverApproachingScreen(
    bookingId: String,
    onPickupDone: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalStrings.current

    // Booking state
    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Map state
    var mapConfig by remember { mutableStateOf(MapConfig()) }
    var driverLat by remember { mutableStateOf(0.0) }
    var driverLng by remember { mutableStateOf(0.0) }
    var pickupLat by remember { mutableStateOf(0.0) }
    var pickupLng by remember { mutableStateOf(0.0) }
    var routeResult by remember { mutableStateOf<RouteResult?>(null) }
    var etaMinutes by remember { mutableStateOf(0) }
    var driverId by remember { mutableStateOf("") }
    var pickupDoneHandled by remember { mutableStateOf(false) }

    // Load booking data
    LaunchedEffect(bookingId) {
        isLoading = true
        errorMessage = null

        BookingApi.getBooking(bookingId)
            .onSuccess { response ->
                val loadedBooking = response.data
                booking = loadedBooking
                if (loadedBooking != null) {
                    pickupLat = loadedBooking.pickupAddress.latitude
                    pickupLng = loadedBooking.pickupAddress.longitude

                    driverLat = loadedBooking.driver?.currentLatitude
                        ?: loadedBooking.pickupAddress.latitude
                    driverLng = loadedBooking.driver?.currentLongitude
                        ?: loadedBooking.pickupAddress.longitude

                    driverId = loadedBooking.driver?.id ?: ""
                    etaMinutes = when {
                        (loadedBooking.eta ?: 0) > 0 -> loadedBooking.eta ?: 0
                        loadedBooking.duration > 0 -> loadedBooking.duration
                        loadedBooking.distance > 0.0 -> estimateMinutesFromDistance(loadedBooking.distance)
                        else -> 0
                    }
                } else {
                    errorMessage = "Booking not found"
                }
            }
            .onFailure { e ->
                errorMessage = "Failed to load booking: ${e.message}"
            }

        isLoading = false
    }

    // Load map config
    LaunchedEffect(Unit) {
        LocationApi.getMapConfig().onSuccess { config ->
            mapConfig = config
        }
    }

    // Poll booking status every 5s for status changes
    LaunchedEffect(bookingId) {
        if (bookingId.isBlank()) return@LaunchedEffect
        while (currentCoroutineContext().isActive && !pickupDoneHandled) {
            delay(5000L)
            BookingApi.getBooking(bookingId).onSuccess { response ->
                val updated = response.data
                if (updated != null) {
                    booking = updated
                    if (updated.status == BookingStatus.PICKUP_DONE ||
                        updated.status == BookingStatus.IN_TRANSIT ||
                        updated.status == BookingStatus.DELIVERED
                    ) {
                        pickupDoneHandled = true
                        onPickupDone()
                    }
                }
            }
        }
    }

    // Poll driver position every 8s
    LaunchedEffect(driverId, pickupLat, pickupLng) {
        if (driverId.isBlank() || pickupLat == 0.0) return@LaunchedEffect

        while (currentCoroutineContext().isActive && !pickupDoneHandled) {
            LocationApi.getPosition(driverId).onSuccess { pos ->
                if (pos.latitude != 0.0 && pos.longitude != 0.0) {
                    driverLat = pos.latitude
                    driverLng = pos.longitude
                }
            }

            // Recalculate route from driver to pickup
            if (driverLat != 0.0 && pickupLat != 0.0) {
                LocationApi.calculateRoute(driverLat, driverLng, pickupLat, pickupLng).onSuccess { route ->
                    routeResult = route
                    etaMinutes = if (route.duration > 0) route.duration else estimateMinutesFromDistance(route.distance)
                }
            }

            delay(8000)
        }
    }

    val markers = remember(driverLat, driverLng, pickupLat, pickupLng) {
        listOfNotNull(
            if (driverLat != 0.0) MapMarker("driver", driverLat, driverLng, "Driver", MarkerColor.BLUE) else null,
            if (pickupLat != 0.0) MapMarker("pickup", pickupLat, pickupLng, "Pickup", MarkerColor.GREEN) else null
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Carry", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlueDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(errorMessage ?: "", color = Color.Red)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBack) {
                            Text(strings.back)
                        }
                    }
                }
            }
            booking != null -> {
                val currentBooking = booking!!
                val isDriverArrived = currentBooking.status == BookingStatus.DRIVER_ARRIVED

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Back arrow + title
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "<",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.clickable { onBack() }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            if (isDriverArrived) strings.driverArrivedStatus else strings.driverOnTheWayStatus,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    // Map
                    Box(modifier = Modifier.weight(1f)) {
                        val centerLat = if (driverLat != 0.0) driverLat else pickupLat
                        val centerLng = if (driverLng != 0.0) driverLng else pickupLng

                        MapViewComposable(
                            modifier = Modifier.fillMaxSize(),
                            styleUrl = mapConfig.styleUrl,
                            centerLat = centerLat,
                            centerLng = centerLng,
                            zoom = 14.0,
                            markers = markers,
                            routeGeometry = routeResult?.geometry
                        )
                    }

                    // Bottom panel
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        // Driver info + ETA card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(PrimaryBlue)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // ETA box
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        if (etaMinutes > 0) "$etaMinutes" else "--",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        strings.mins,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    if (isDriverArrived) strings.driverArrivedStatus else strings.driverOnTheWayStatus,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                currentBooking.driver?.let { driver ->
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "${driver.name} - ${driver.vehicleModel}",
                                        fontSize = 13.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // OTP Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = strings.yourDeliveryCode,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Large OTP display
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val otp = currentBooking.otp
                                    otp.forEach { digit ->
                                        Box(
                                            modifier = Modifier
                                                .size(52.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(PrimaryBlue),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = digit.toString(),
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isDriverArrived)
                                        strings.shareOtpWithDriver
                                    else
                                        strings.shareOtpWithDriver,
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

private fun estimateMinutesFromDistance(distanceKm: Double): Int {
    if (distanceKm <= 0.0) return 0
    // Fallback ETA for cases where backend route duration is unavailable.
    return ceil((distanceKm / 30.0) * 60.0).toInt().coerceAtLeast(1)
}
