package com.company.carryon.ui.screens.booking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.map_route
import com.company.carryon.data.model.Booking
import com.company.carryon.data.network.BookingApi
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.util.formatDecimal
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource

@Composable
fun ModifyScheduleScreen(
    bookingId: String,
    onBack: () -> Unit,
    onUpdateSchedule: () -> Unit,
    onCancelDelivery: () -> Unit
) {
    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(bookingId.isNotBlank()) }

    LaunchedEffect(bookingId) {
        if (bookingId.isBlank()) {
            isLoading = false
            return@LaunchedEffect
        }
        BookingApi.getBooking(bookingId)
            .onSuccess { response -> booking = response.data }
            .onFailure { booking = null }
        isLoading = false
    }

    Scaffold(
        containerColor = Color(0xFFF5F6F8)
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderRow(onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                StatusCard(booking)
                RoutePreviewCard()
                AddressCard(booking)
                PackageCard(booking)
                PaymentCard(booking)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onUpdateSchedule,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp)
            ) {
                Text("↻  Modify Schedule", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { onCancelDelivery() },
                contentAlignment = Alignment.Center
            ) {
                Text("Cancel Delivery", color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            BottomNavigationStub()
        }
    }
}

@Composable
private fun HeaderRow(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "‹",
                color = Color.Black,
                fontSize = 18.sp,
                modifier = Modifier.clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Order Details",
                color = Color(0xFF282B51),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
            Text("On", color = Color(0xFF282B51), fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
        }
    }
}

@Composable
private fun StatusCard(booking: Booking?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDCE6F1), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFE1ECF8))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⏺", color = PrimaryBlue, fontSize = 10.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "AWAITING DRIVER ASSIGNMENT",
                color = PrimaryBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            booking?.scheduledTime?.let(::formatScheduledHeading) ?: "Scheduled delivery",
            color = Color.Black,
            fontSize = 34.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "◷ ${booking?.scheduledTime?.let(::formatScheduledTimeOnly) ?: "Time pending"}",
            color = Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RoutePreviewCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Image(
            painter = painterResource(Res.drawable.map_route),
            contentDescription = "Route map",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0x33000000))
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(40.dp)
                .background(Color(0x66A6D2F3), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("", color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AddressCard(booking: Booking?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDCE6F1), RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .width(22.dp)
                    .height(92.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(92.dp)
                        .background(Color(0x664C7EDB))
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DotMarker(isDropOff = false)
                    DotMarker(isDropOff = true)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Column {
                    Text("PICKUP ADDRESS", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Normal)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        booking?.pickupAddress?.label?.ifBlank { booking.pickupAddress.address }.orEmpty().ifBlank { "Pickup address unavailable" },
                        color = Color.Black,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column {
                    Text("DROP-OFF ADDRESS", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Normal)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        booking?.deliveryAddress?.label?.ifBlank { booking.deliveryAddress.address }.orEmpty().ifBlank { "Drop-off address unavailable" },
                        color = Color.Black,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun PackageCard(booking: Booking?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDCE6F1), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        SectionHeader(icon = "▣", title = "Package Details")
        Spacer(modifier = Modifier.height(16.dp))
        KeyValueRow("Type", booking?.vehicleType?.ifBlank { "Package" } ?: "Package")
        DividerSpacing()
        KeyValueRow("Sender", booking?.senderName?.ifBlank { "Not provided" } ?: "Not provided")
        Spacer(modifier = Modifier.height(14.dp))
        Text("RECEIVER", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x80FFFFFF))
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(
                listOfNotNull(
                    booking?.receiverName?.takeIf { it.isNotBlank() },
                    booking?.receiverPhone?.takeIf { it.isNotBlank() }
                ).joinToString(" • ").ifBlank { "Not provided" },
                color = Color(0xFF64748B),
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
private fun PaymentCard(booking: Booking?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDCE6F1), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        SectionHeader(icon = "RM", title = "Payment")
        Spacer(modifier = Modifier.height(16.dp))
        KeyValueRow(
            "Method",
            booking?.paymentMethod?.name?.lowercase()?.replaceFirstChar { it.titlecase() } ?: "Unavailable",
            trailingDot = true
        )
        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = Color(0x330050D4), thickness = 1.dp)
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text("Total Paid", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(
                "RM ${((booking?.finalPrice ?: 0.0).takeIf { it > 0 } ?: booking?.estimatedPrice ?: 0.0).formatDecimal(2)}",
                color = PrimaryBlue,
                fontSize = 42.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatScheduledHeading(value: String): String {
    return runCatching {
        val dateTime = Instant.parse(value).toLocalDateTime(TimeZone.currentSystemDefault())
        val month = dateTime.month.name.lowercase().replaceFirstChar { it.titlecase() }
        "Scheduled for $month ${dateTime.dayOfMonth}"
    }.getOrDefault("Scheduled delivery")
}

private fun formatScheduledTimeOnly(value: String): String {
    return runCatching {
        val dateTime = Instant.parse(value).toLocalDateTime(TimeZone.currentSystemDefault())
        val minute = dateTime.minute.toString().padStart(2, '0')
        val hour24 = dateTime.hour
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val meridiem = if (hour24 >= 12) "PM" else "AM"
        "$hour12:$minute $meridiem"
    }.getOrDefault("Time pending")
}

@Composable
private fun BottomNavigationStub() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(label = "Deliveries", selected = false)
        BottomNavItem(label = "Schedule", selected = true)
        BottomNavItem(label = "History", selected = false)
        BottomNavItem(label = "Profile", selected = false)
    }
}

@Composable
private fun BottomNavItem(label: String, selected: Boolean) {
    val containerColor = if (selected) Color(0xFFD9E8FF) else Color.Transparent
    val textColor = if (selected) PrimaryBlue else Color(0xFF94A3B8)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DotMarker(isDropOff: Boolean) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(PrimaryBlue, CircleShape)
            .border(
                width = 3.dp,
                color = if (isDropOff) Color(0x80D8E3FB) else Color(0x1A0050D4),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isDropOff) {
            Text("⌖", color = Color.White, fontSize = 10.sp)
        } else {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(Color(0xFFA6D2F3), CircleShape)
            )
        }
    }
}

@Composable
private fun SectionHeader(icon: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = PrimaryBlue, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun KeyValueRow(
    key: String,
    value: String,
    trailingDot: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(key, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Normal)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Normal)
            if (trailingDot) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(PrimaryBlue, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun DividerSpacing() {
    Spacer(modifier = Modifier.height(10.dp))
    HorizontalDivider(color = Color(0x1AA7AAD7), thickness = 1.dp)
    Spacer(modifier = Modifier.height(10.dp))
}
