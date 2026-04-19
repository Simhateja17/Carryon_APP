package com.company.carryon.ui.screens.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.company.carryon.util.formatDecimal
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.icon_settings_menu
import carryon.composeapp.generated.resources.location_pin
import carryon.composeapp.generated.resources.to_pin
import com.company.carryon.data.model.Booking
import com.company.carryon.data.model.BookingStatus
import com.company.carryon.data.model.LatLng
import com.company.carryon.data.network.BookingApi
import com.company.carryon.data.network.LocationApi
import com.company.carryon.ui.components.MapMarker
import com.company.carryon.ui.components.MapViewComposable
import com.company.carryon.ui.components.MarkerColor
import org.jetbrains.compose.resources.painterResource

private val OrdersBlue = Color(0xFF2F80ED)
private val OrdersBg = Color(0xFFF4F5F8)
private val OrdersText = Color(0xFF202124)
private val OrdersMuted = Color(0xFF7A8499)
private val OrdersDivider = Color(0x1A000000)
private val OrdersCardBg = Color.White
private val OrdersAmountGreen = Color(0xFF27AE60)

@Composable
fun OrdersScreen(
    onBack: () -> Unit,
    onOrderClick: (orderId: String) -> Unit,
    onTrackOrder: (orderId: String) -> Unit = onOrderClick
) {
    var pastOrders by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        BookingApi.getBookings()
            .onSuccess { response ->
                pastOrders = response.data
                    .orEmpty()
                    .filter { it.status == BookingStatus.DELIVERED }
                    .sortedByDescending { booking ->
                        booking.deliveredAt?.ifBlank { null }
                            ?: booking.updatedAt.ifBlank { booking.createdAt }
                    }
                errorMessage = null
            }
            .onFailure { error ->
                errorMessage = error.message ?: "Failed to load past orders"
            }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OrdersBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.icon_settings_menu),
                contentDescription = "Menu",
                colorFilter = ColorFilter.tint(Color(0xFF6F7480)),
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onBack() },
                contentScale = ContentScale.Fit
            )
            Text(
                text = "Carry On",
                color = OrdersBlue,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(Res.drawable.bell_icon),
                contentDescription = "Notifications",
                colorFilter = ColorFilter.tint(Color(0xFF6F7480)),
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
        }

        Text(
            text = "Past Orders",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = OrdersText,
            modifier = Modifier.padding(start = 24.dp, bottom = 14.dp)
        )

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 27.dp, topEnd = 27.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Loading...", color = OrdersMuted, fontSize = 15.sp)
                    }
                }

                errorMessage != null -> {
                    val message = errorMessage ?: "Failed to load past orders"
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = message,
                            color = Color(0xFFEB5757),
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }

                pastOrders.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No past orders yet", color = OrdersMuted, fontSize = 15.sp)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item { Spacer(Modifier.height(8.dp)) }
                        items(
                            items = pastOrders,
                            key = { booking -> booking.id }
                        ) { booking ->
                            PastOrderCard(
                                booking = booking,
                                onClick = { onOrderClick(booking.id) }
                            )
                        }
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PastOrderCard(
    booking: Booking,
    onClick: () -> Unit
) {
    val pickupLat = booking.pickupAddress.latitude
    val pickupLng = booking.pickupAddress.longitude
    val dropLat = booking.deliveryAddress.latitude
    val dropLng = booking.deliveryAddress.longitude
    val hasCoords = pickupLat != 0.0 || pickupLng != 0.0 || dropLat != 0.0 || dropLng != 0.0

    val centerLat = when {
        pickupLat != 0.0 && dropLat != 0.0 -> (pickupLat + dropLat) / 2
        pickupLat != 0.0 -> pickupLat
        else -> dropLat
    }
    val centerLng = when {
        pickupLng != 0.0 && dropLng != 0.0 -> (pickupLng + dropLng) / 2
        pickupLng != 0.0 -> pickupLng
        else -> dropLng
    }

    val markers = buildList {
        if (pickupLat != 0.0 || pickupLng != 0.0) {
            add(MapMarker("pickup-${booking.id}", pickupLat, pickupLng, "Pickup", MarkerColor.GREEN))
        }
        if (dropLat != 0.0 || dropLng != 0.0) {
            add(MapMarker("drop-${booking.id}", dropLat, dropLng, "Dropoff", MarkerColor.RED))
        }
    }

    var routeGeometry by remember(booking.id) { mutableStateOf<List<LatLng>?>(null) }
    LaunchedEffect(booking.id, pickupLat, pickupLng, dropLat, dropLng) {
        if (pickupLat != 0.0 && pickupLng != 0.0 && dropLat != 0.0 && dropLng != 0.0) {
            LocationApi.calculateRoute(pickupLat, pickupLng, dropLat, dropLng)
                .onSuccess { result ->
                    if (result.geometry.isNotEmpty()) {
                        routeGeometry = result.geometry
                    }
                }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OrdersCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            if (hasCoords) {
                MapViewComposable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    centerLat = centerLat,
                    centerLng = centerLng,
                    zoom = 11.0,
                    markers = markers,
                    routeGeometry = routeGeometry
                )
            }

            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = bookingPastOrderId(booking),
                            color = OrdersBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = booking.deliveredAt?.ifBlank { null }
                                ?: booking.updatedAt.ifBlank { booking.createdAt },
                            color = OrdersMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = bookingAmountText(booking),
                        color = OrdersAmountGreen,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }

                HorizontalDivider(color = OrdersDivider)

                RouteAddressRow(
                    isPickup = true,
                    address = booking.pickupAddress.address.ifBlank {
                        booking.pickupAddress.label.ifBlank { "—" }
                    }
                )
                RouteAddressRow(
                    isPickup = false,
                    address = booking.deliveryAddress.address.ifBlank {
                        booking.deliveryAddress.label.ifBlank { "—" }
                    }
                )

                HorizontalDivider(color = OrdersDivider)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    StatChip("Distance", "${booking.distance.toInt()} km")
                    StatChip("Duration", "${booking.duration} min")
                    StatChip("Package", booking.vehicleType.ifBlank { "CAR" }.uppercase())
                }
            }
        }
    }
}

@Composable
private fun RouteAddressRow(
    isPickup: Boolean,
    address: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(if (isPickup) Res.drawable.location_pin else Res.drawable.to_pin),
            contentDescription = null,
            colorFilter = ColorFilter.tint(if (isPickup) Color(0xFF27AE60) else Color(0xFFEB5757)),
            modifier = Modifier
                .size(16.dp)
                .padding(top = 1.dp),
            contentScale = ContentScale.Fit
        )
        Column {
            Text(
                text = if (isPickup) "Pickup" else "Dropoff",
                color = OrdersMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = address,
                color = OrdersText,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StatChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(text = label, color = OrdersMuted, fontSize = 10.sp)
        Text(
            text = value,
            color = OrdersText,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun bookingPastOrderId(booking: Booking): String {
    val orderCodeDigits = booking.orderCode
        ?.uppercase()
        ?.let { Regex("""ORD[-_\s]*(\d+)""").find(it)?.groupValues?.getOrNull(1) }

    return if (!orderCodeDigits.isNullOrBlank()) {
        "#ORD-${orderCodeDigits.padStart(6, '0')}"
    } else {
        val fallbackDigits = booking.id.filter { it.isDigit() }.takeLast(6).padStart(6, '0')
        "#ORD-$fallbackDigits"
    }
}

private fun bookingAmountText(booking: Booking): String {
    val amount = if (booking.finalPrice > 0.0) booking.finalPrice else booking.estimatedPrice
    val wholeAmount = if (amount % 1.0 == 0.0) amount.toInt().toString() else amount.formatDecimal(2)
    return "RM $wholeAmount"
}
