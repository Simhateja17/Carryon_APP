package com.example.carryon.ui.screens.tracking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.map_background
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.data.network.BookingApi
import com.example.carryon.data.model.Booking
import com.example.carryon.data.model.BookingStatus
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings

private fun formatISODate(isoDate: String): String {
    return try {
        val parts = isoDate.split("T")
        if (parts.size < 2) return isoDate
        val datePart = parts[0].split("-")
        val timePart = parts[1].take(5).split(":")
        if (datePart.size < 3 || timePart.size < 2) return isoDate
        val year = datePart[0].takeLast(2)
        val month = when (datePart[1].toIntOrNull() ?: 0) {
            1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"
            5 -> "May"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Aug"
            9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
            else -> datePart[1]
        }
        val day = datePart[2].toIntOrNull()?.toString() ?: datePart[2]
        val hour = timePart[0].toIntOrNull() ?: 0
        val minute = timePart[1]
        val ampm = if (hour < 12) "am" else "pm"
        val hour12 = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        "$hour12:$minute $ampm | $day $month $year"
    } catch (e: Exception) {
        isoDate
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveShipmentScreen(
    onTrackShipments: () -> Unit,
    onChatWithDriver: (String, String) -> Unit = { _, _ -> }
) {
    val strings = LocalStrings.current
    var activeBooking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var shareWithNeighbors by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        BookingApi.getBookings().onSuccess { response ->
            activeBooking = response.data?.firstOrNull {
                it.status != BookingStatus.DELIVERED && it.status != BookingStatus.CANCELLED
            }
        }
        isLoading = false
    }

    val etaText = activeBooking?.eta?.let { "$it min  ●●●" } ?: "—  ●●●"
    val orderId = activeBooking?.id ?: "—"
    val pickupAddress = activeBooking?.pickupAddress?.address ?: "—"
    val recipientName = activeBooking?.deliveryAddress?.contactName?.ifBlank {
        activeBooking?.deliveryAddress?.label
    } ?: "—"
    val dispatchedText = activeBooking?.createdAt?.let { formatISODate(it) } ?: "—"
    val deliverByText = activeBooking?.scheduledTime?.let { formatISODate(it) }
        ?: activeBooking?.let {
            if (it.duration > 0) {
                // Approximate: createdAt + duration minutes
                "~${it.duration} min from dispatch"
            } else "—"
        } ?: "—"
    val driverName = activeBooking?.driver?.name ?: "Driver"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Carry",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = " On",
                            fontSize = 24.sp,
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
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Map Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.map_background),
                    contentDescription = "Route Map",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x33000000))
                )

                // ETA label
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = etaText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Share with neighbors checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = shareWithNeighbors,
                        onCheckedChange = { shareWithNeighbors = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryBlue,
                            uncheckedColor = TextSecondary
                        ),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = strings.shareWithNeighbors,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = strings.forExtraDiscount,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                } else {
                    // Recipient name
                    Text(
                        text = recipientName,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Order Info
                    Text(
                        text = "order: $orderId | $pickupAddress",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Dispatched / Deliver by Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = strings.dispatched,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dispatchedText,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = strings.deliverBy,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = deliverByText,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Chat with Driver Button
                    OutlinedButton(
                        onClick = { onChatWithDriver(orderId, driverName) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
                    ) {
                        Text(
                            text = strings.chatWithDriver,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Track Shipments Button
                Button(
                    onClick = onTrackShipments,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    )
                ) {
                    Text(
                        text = strings.trackShipments,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
