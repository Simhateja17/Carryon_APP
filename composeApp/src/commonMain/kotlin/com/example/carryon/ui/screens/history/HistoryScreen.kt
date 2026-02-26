package com.example.carryon.ui.screens.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.icon_spark
import carryon.composeapp.generated.resources.icon_timer
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import com.example.carryon.data.network.BookingApi
import com.example.carryon.data.model.Booking
import com.example.carryon.data.model.BookingStatus

data class OrderHistory(
    val orderId: String,
    val recipient: String,
    val location: String,
    val date: String,
    val status: String,
    val vehicleType: String = ""
)

private fun Booking.toOrderHistory() = OrderHistory(
    orderId = id,
    recipient = deliveryAddress.contactName.ifBlank { deliveryAddress.label.ifBlank { "—" } },
    location = deliveryAddress.address,
    date = createdAt,
    status = when (status) {
        BookingStatus.DELIVERED -> "Completed"
        BookingStatus.CANCELLED -> "Cancelled"
        BookingStatus.IN_TRANSIT -> "In Transit"
        BookingStatus.PICKUP_DONE -> "Picked Up"
        BookingStatus.DRIVER_ASSIGNED, BookingStatus.DRIVER_ARRIVED -> "Driver Assigned"
        BookingStatus.SEARCHING_DRIVER -> "Searching Driver"
        BookingStatus.PENDING -> "Pending"
    },
    vehicleType = vehicleType
)

private fun vehicleEmoji(vehicleType: String) = when (vehicleType.lowercase()) {
    "auto" -> "🛺"
    "mini truck" -> "🚚"
    "truck" -> "🚛"
    else -> "🏍️"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onInstantDelivery: () -> Unit,
    onScheduleDelivery: () -> Unit,
    onOrderClick: (String) -> Unit,
    onViewAll: () -> Unit
) {
    val strings = LocalStrings.current
    var allBookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        BookingApi.getBookings().onSuccess { response ->
            allBookings = response.data ?: emptyList()
        }
        isLoading = false
    }

    val activeBooking = allBookings.firstOrNull {
        it.status != BookingStatus.DELIVERED && it.status != BookingStatus.CANCELLED
    }
    val orderHistory = allBookings
        .filter { it.status == BookingStatus.DELIVERED || it.status == BookingStatus.CANCELLED }
        .map { it.toOrderHistory() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Carry",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = " On",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlueDark
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Image(
                            painter = painterResource(Res.drawable.bell_icon),
                            contentDescription = "Notifications",
                            modifier = Modifier.size(24.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                // Active Delivery Banner (only if there's an active booking)
                if (activeBooking != null) {
                    Text(
                        text = strings.deliveryInProgress,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Active Delivery Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOrderClick(activeBooking.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeBooking.id,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                                Text(
                                    text = strings.recipientLabel(
                                        activeBooking.deliveryAddress.contactName.ifBlank {
                                            activeBooking.deliveryAddress.label.ifBlank { "—" }
                                        }
                                    ),
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                Row {
                                    val etaText = activeBooking.eta?.let { "$it mins" } ?: "—"
                                    Text(
                                        text = etaText,
                                        fontSize = 13.sp,
                                        color = PrimaryBlue,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = " ${strings.toDeliveryLocation}",
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = strings.inProgress,
                                    fontSize = 12.sp,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // What would you like to do?
                Text(
                    text = strings.whatWouldYouLikeToDo,
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Instant Delivery Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onInstantDelivery() },
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Image(painter = painterResource(Res.drawable.icon_spark), contentDescription = null, modifier = Modifier.size(28.dp), contentScale = ContentScale.Fit)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = strings.instantDelivery,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = strings.instantDeliveryDesc,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Image(
                            painter = painterResource(Res.drawable.icon_spark),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).align(Alignment.CenterEnd).padding(end = 16.dp),
                            alpha = 0.2f,
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Schedule Delivery Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onScheduleDelivery() },
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Image(painter = painterResource(Res.drawable.icon_timer), contentDescription = null, modifier = Modifier.size(28.dp), contentScale = ContentScale.Fit)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = strings.scheduleDelivery,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Text(
                                text = strings.scheduleDeliveryDesc,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }

                        Image(
                            painter = painterResource(Res.drawable.icon_timer),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).align(Alignment.CenterEnd).padding(end = 16.dp),
                            alpha = 0.15f,
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // History Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.history,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(onClick = onViewAll) {
                        Text(
                            text = strings.viewAll,
                            fontSize = 13.sp,
                            color = PrimaryBlue
                        )
                    }
                }

                if (orderHistory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No delivery history yet",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                } else {
                    orderHistory.forEach { order ->
                        HistoryItem(
                            order = order,
                            onClick = { onOrderClick(order.orderId) }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HistoryItem(
    order: OrderHistory,
    onClick: () -> Unit
) {
    val strings = LocalStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.orderId,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )

                Box(
                    modifier = Modifier
                        .background(PrimaryBlue, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.status,
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                text = strings.recipientLabel(order.recipient),
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(PrimaryBlueSurface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(vehicleEmoji(order.vehicleType), fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row {
                        Text("📍 ", fontSize = 12.sp, color = PrimaryBlue)
                        Text(
                            text = strings.dropOff,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = order.location,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = order.date,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
