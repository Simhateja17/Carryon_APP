package com.company.carryon.ui.screens.booking

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.model.Booking
import com.company.carryon.data.network.BookingApi
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.TextPrimary
import com.company.carryon.ui.theme.TextSecondary
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun DeliveryScheduledScreen(
    bookingId: String,
    onBack: () -> Unit,
    onViewOrder: () -> Unit,
    onModifySchedule: () -> Unit,
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

    val pickupAddress = booking?.pickupAddress?.label?.ifBlank { booking?.pickupAddress?.address.orEmpty() }.orEmpty()
    val dropAddress = booking?.deliveryAddress?.label?.ifBlank { booking?.deliveryAddress?.address.orEmpty() }.orEmpty()
    val scheduleLabel = booking?.scheduledTime?.let(::formatScheduledDateTime) ?: "Schedule pending"

    Scaffold(containerColor = Color(0xFFF7F8FA)) { paddingValues ->
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
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "‹",
                        color = Color.Black,
                        fontSize = 20.sp,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Text(
                        text = "  Delivery Scheduled",
                        color = Color(0xFF354164),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                    Text("On", color = Color(0xFF282B51), fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .size(126.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFFE7EEF9))
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF2F6FF))
                        .border(2.dp, Color(0xFFA7C2F6), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("", fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Your delivery is scheduled ",
                color = TextPrimary,
                fontSize = 38.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sit back and relax. We'll assign a delivery\npartner before your scheduled time.",
                color = TextSecondary,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFE7EEFF))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "AWAITING DRIVER ASSIGNMENT",
                            color = PrimaryBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("PICKUP", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        pickupAddress.ifBlank { "Pickup address unavailable" },
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("DROP-OFF", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        dropAddress.ifBlank { "Drop-off address unavailable" },
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF2F6FF))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("DATE & TIME", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(scheduleLabel, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text("", fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onViewOrder,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF4F7FEA), Color(0xFF88A4F6))
                            ),
                            RoundedCornerShape(28.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("View Order", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onModifySchedule,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8EDF6)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("Modify Schedule", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Cancel Delivery",
                color = PrimaryBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onCancelDelivery() }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFEFF3F8))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "You'll be notified once a driver is assigned",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

private fun formatScheduledDateTime(value: String): String {
    return runCatching {
        val dateTime = Instant.parse(value).toLocalDateTime(TimeZone.currentSystemDefault())
        val month = dateTime.month.name.lowercase().replaceFirstChar(Char::titlecase)
        val minute = dateTime.minute.toString().padStart(2, '0')
        val hour24 = dateTime.hour
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val meridiem = if (hour24 >= 12) "PM" else "AM"
        "$month ${dateTime.dayOfMonth}, $hour12:$minute $meridiem"
    }.getOrDefault(value)
}
