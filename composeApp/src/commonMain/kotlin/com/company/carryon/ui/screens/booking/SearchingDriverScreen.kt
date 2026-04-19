package com.company.carryon.ui.screens.booking

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.vehicle_bike_icon
import com.company.carryon.data.model.Booking
import com.company.carryon.data.model.LatLng
import com.company.carryon.data.network.BookingApi
import com.company.carryon.data.model.BookingStatus
import com.company.carryon.data.network.LocationApi
import com.company.carryon.ui.components.MapMarker
import com.company.carryon.ui.components.MapViewComposable
import com.company.carryon.ui.components.MarkerColor
import com.company.carryon.ui.theme.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.painterResource

@Composable
fun SearchingDriverScreen(
    bookingId: String,
    amount: Double,
    onDriverFound: () -> Unit,
    onScheduled: () -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isCancelling by remember { mutableStateOf(false) }
    var driverFoundHandled by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }
    var booking by remember { mutableStateOf<Booking?>(null) }
    var routeGeometry by remember { mutableStateOf(emptyList<LatLng>()) }
    var bookingError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(bookingId) {
        if (bookingId.isBlank()) return@LaunchedEffect
        while (currentCoroutineContext().isActive && !driverFoundHandled && !hasNavigated) {
            BookingApi.getBooking(bookingId)
                .onSuccess { response ->
                    val updatedBooking = response.data
                    if (updatedBooking != null) {
                        booking = updatedBooking
                        bookingError = null
                        when (updatedBooking.status) {
                            BookingStatus.DRIVER_ASSIGNED,
                            BookingStatus.DRIVER_ARRIVED,
                            BookingStatus.PICKUP_DONE,
                            BookingStatus.IN_TRANSIT,
                            BookingStatus.DELIVERED -> {
                                driverFoundHandled = true
                                if (!hasNavigated) {
                                    hasNavigated = true
                                    onDriverFound()
                                }
                            }
                            BookingStatus.CANCELLED -> {
                                if (!hasNavigated) {
                                    hasNavigated = true
                                    onCancel()
                                }
                            }
                            else -> Unit
                        }
                    }
                }
                .onFailure { error ->
                    bookingError = error.message ?: "Failed to load booking"
                }
            delay(5000L)
        }
    }

    LaunchedEffect(
        booking?.pickupAddress?.latitude,
        booking?.pickupAddress?.longitude,
        booking?.deliveryAddress?.latitude,
        booking?.deliveryAddress?.longitude
    ) {
        val currentBooking = booking ?: return@LaunchedEffect
        val pickupLat = currentBooking.pickupAddress.latitude
        val pickupLng = currentBooking.pickupAddress.longitude
        val deliveryLat = currentBooking.deliveryAddress.latitude
        val deliveryLng = currentBooking.deliveryAddress.longitude
        if (!isValidCoordinate(pickupLat, pickupLng) || !isValidCoordinate(deliveryLat, deliveryLng)) {
            routeGeometry = emptyList()
            return@LaunchedEffect
        }
        LocationApi.calculateRoute(pickupLat, pickupLng, deliveryLat, deliveryLng)
            .onSuccess { routeGeometry = it.geometry }
            .onFailure { routeGeometry = emptyList() }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )
    val currentBooking = booking
    val estimatedPriceText = "RM ${((currentBooking?.estimatedPrice ?: amount).takeIf { it > 0.0 } ?: 150.0).roundToInt()}"
    val vehicleLabel = currentBooking?.vehicleType?.toDisplayVehicleLabel().orEmpty().ifBlank { "Vehicle" }
    val pickupAddressText = currentBooking?.pickupAddress?.address.orEmpty().ifBlank { "Loading pickup..." }
    val dropoffAddressText = currentBooking?.deliveryAddress?.address.orEmpty().ifBlank { "Loading drop-off..." }
    val etaText = currentBooking?.duration?.takeIf { it > 0 }?.let { "$it mins" } ?: "Searching"
    val pickupLat = currentBooking?.pickupAddress?.latitude
    val pickupLng = currentBooking?.pickupAddress?.longitude
    val deliveryLat = currentBooking?.deliveryAddress?.latitude
    val deliveryLng = currentBooking?.deliveryAddress?.longitude
    val markers = remember(currentBooking) {
        buildList {
            currentBooking?.pickupAddress?.takeIf { isValidCoordinate(it.latitude, it.longitude) }?.let {
                add(MapMarker("pickup", it.latitude, it.longitude, "Pickup", MarkerColor.BLUE))
            }
            currentBooking?.deliveryAddress?.takeIf { isValidCoordinate(it.latitude, it.longitude) }?.let {
                add(MapMarker("dropoff", it.latitude, it.longitude, "Drop-off", MarkerColor.GREEN))
            }
        }
    }
    val centerLat = remember(currentBooking) {
        when {
            pickupLat != null && pickupLng != null && deliveryLat != null && deliveryLng != null &&
                isValidCoordinate(pickupLat, pickupLng) && isValidCoordinate(deliveryLat, deliveryLng) -> (pickupLat + deliveryLat) / 2
            pickupLat != null && pickupLng != null && isValidCoordinate(pickupLat, pickupLng) -> pickupLat
            else -> 3.1390
        }
    }
    val centerLng = remember(currentBooking) {
        when {
            pickupLat != null && pickupLng != null && deliveryLat != null && deliveryLng != null &&
                isValidCoordinate(pickupLat, pickupLng) && isValidCoordinate(deliveryLat, deliveryLng) -> (pickupLng + deliveryLng) / 2
            pickupLat != null && pickupLng != null && isValidCoordinate(pickupLat, pickupLng) -> pickupLng
            else -> 101.6869
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapViewComposable(
            modifier = Modifier.fillMaxSize(),
            centerLat = centerLat,
            centerLng = centerLng,
            zoom = if (markers.size > 1) 11.0 else 14.0,
            markers = markers,
            routeGeometry = routeGeometry
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80F4F7FF))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    fontSize = 22.sp,
                    color = Color(0xFF1D2B53),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onCancel() }
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "Finding Your Driver",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D2B53),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "⋮",
                    fontSize = 22.sp,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(122.dp)
                        .scale(pulseRadius)
                        .background(Color.White, RoundedCornerShape(61.dp))
                        .border(3.dp, Color(0xFF2F66E7), RoundedCornerShape(61.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "◎",
                        fontSize = 42.sp,
                        color = Color(0xFF2F66E7),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Connecting you...",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF141414)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Connecting you to the best delivery\npartner for your bike request.",
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = Color(0xFF30354B),
                textAlign = TextAlign.Center
            )
            bookingError?.let { error ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = error,
                    fontSize = 13.sp,
                    color = ErrorRed,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(120.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.97f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE9F0FF), RoundedCornerShape(16.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(Res.drawable.vehicle_bike_icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(vehicleLabel, color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("ESTIMATED PRICE", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                            Text(estimatedPriceText, fontSize = 30.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFE7EAF0))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("PICKUP", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(pickupAddressText, fontSize = 16.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("DROP OFF", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(dropoffAddressText, fontSize = 16.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF1F3F7), RoundedCornerShape(14.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(etaText, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))
                    HorizontalDivider(color = Color(0xFFE7EAF0))
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isCancelling) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEAF2FF), RoundedCornerShape(18.dp))
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = PrimaryBlue,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEAF2FF), RoundedCornerShape(18.dp))
                                .clickable {
                                    if (bookingId.isNotBlank() && !isCancelling) {
                                        isCancelling = true
                                        scope.launch {
                                            BookingApi.cancelBooking(bookingId)
                                            hasNavigated = true
                                            onCancel()
                                        }
                                    } else {
                                        hasNavigated = true
                                        onCancel()
                                    }
                                }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cancel Request",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
        }
    }
}

private fun isValidCoordinate(lat: Double, lng: Double): Boolean {
    return lat in -90.0..90.0 && lng in -180.0..180.0 && !(lat == 0.0 && lng == 0.0)
}

private fun String.toDisplayVehicleLabel(): String {
    return when (this.uppercase()) {
        "BIKE", "2 WHEELER" -> "Bike"
        "CAR", "AUTO" -> "Car"
        "PICKUP", "4X4 PICKUP" -> "4x4 Pickup"
        "VAN_7FT" -> "Van 7ft"
        "VAN_9FT" -> "Van 9ft"
        "LORRY_10FT" -> "Small Lorry 10ft"
        "LORRY_14FT" -> "Medium Lorry 14ft"
        "LORRY_17FT" -> "Large Lorry 17ft"
        else -> replace('_', ' ').lowercase().split(' ').joinToString(" ") { part ->
            part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}
