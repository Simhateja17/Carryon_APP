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
import com.company.carryon.ui.theme.TextPrimary
import com.company.carryon.ui.theme.TextSecondary
import org.jetbrains.compose.resources.painterResource

@Composable
fun ScheduledOrderDetailsScreen(
    bookingId: String,
    onBack: () -> Unit,
    onModifySchedule: () -> Unit,
    onCancelDelivery: () -> Unit
) {
    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(bookingId) {
        if (bookingId.isBlank()) {
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        BookingApi.getBooking(bookingId)
            .onSuccess { response ->
                booking = response.data
                isLoading = false
            }
            .onFailure {
                isLoading = false
            }
    }

    Scaffold(containerColor = Color(0xFFF5F6F8)) { paddingValues ->
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

            else -> {
                val currentBooking = booking
                val pickupAddress =
                    currentBooking?.pickupAddress?.label
                        ?.ifBlank { currentBooking.pickupAddress.address }
                        .ifNullOrBlank("Jalan Tun Razak, Kuala Lumpur")
                val dropAddress =
                    currentBooking?.deliveryAddress?.label
                        ?.ifBlank { currentBooking.deliveryAddress.address }
                        .ifNullOrBlank("KL Sentral, Kuala Lumpur")
                        .ifNullOrBlank("")
                val dropAddress =
                    currentBooking?.deliveryAddress?.label
                        ?.ifBlank { currentBooking.deliveryAddress.address }
                        .ifNullOrBlank("")
                val paymentMethod = currentBooking?.paymentMethod?.name
                    ?.lowercase()
                    ?.replaceFirstChar { it.uppercase() }
                    ?: "Wallet"
                val totalPaid =
                    currentBooking?.let { if (it.finalPrice > 0) it.finalPrice else it.estimatedPrice } ?: 150.0

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "←",
                                color = PrimaryBlue,
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
                        Text("⋮", color = PrimaryBlue, fontSize = 20.sp)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
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
                        Text("Scheduled for Today", color = Color.Black, fontSize = 34.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("◷ 5:00 PM", color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Medium)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .padding(horizontal = 16.dp, vertical = 14.dp)
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
                            Text("➤", color = Color(0xFF2F80ED), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
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
                                    Text(pickupAddress, color = Color.Black, fontSize = 23.sp, fontWeight = FontWeight.Medium)
                                }
                                Column {
                                    Text("DROP-OFF ADDRESS", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Normal)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(dropAddress, color = Color.Black, fontSize = 23.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(Color(0xFFDCE6F1), RoundedCornerShape(20.dp))
                            .padding(20.dp)
                    ) {
                        SectionHeader(icon = "▣", title = "Package Details")
                        Spacer(modifier = Modifier.height(16.dp))
                        KeyValueRow("Type", currentBooking?.vehicleType?.ifBlank { "Package" } ?: "Package")
                        DividerSpacing()
                        KeyValueRow("Weight", "2.0 kg")
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("INSTRUCTIONS", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x80FFFFFF))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                "\"Leave at front desk\"",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(Color(0xFFDCE6F1), RoundedCornerShape(20.dp))
                            .padding(20.dp)
                    ) {
                        SectionHeader(icon = "RM", title = "Payment")
                        Spacer(modifier = Modifier.height(16.dp))
                        KeyValueRow(
                            "Method",
                            paymentMethod,
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
                                "RM ${totalPaid.toInt()}",
                                color = Color(0xFF2F80ED),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onModifySchedule,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED)),
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp)
                    ) {
                        Text("↻  Modify Schedule", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel Delivery",
                            color = Color(0xFF2F80ED),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onCancelDelivery() }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun DotMarker(isDropOff: Boolean) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(Color(0xFF2F80ED), CircleShape)
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
            Text(icon, color = Color(0xFF2F80ED), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
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

private fun String?.ifNullOrBlank(defaultValue: String): String {
    return if (this.isNullOrBlank()) defaultValue else this
}
