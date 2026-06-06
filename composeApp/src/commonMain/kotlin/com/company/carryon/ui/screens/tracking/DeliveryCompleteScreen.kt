package com.company.carryon.ui.screens.tracking

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.model.Booking
import com.company.carryon.data.network.BookingApi
import com.company.carryon.i18n.LocalStrings
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.PrimaryBlueDark
import com.company.carryon.ui.theme.SuccessGreen
import com.company.carryon.ui.theme.TextPrimary

private val CompleteBg = Color(0xFFF7F8FC)
private val CompleteCard = Color(0xFFE3F2FD)
private val CompleteSoft = Color(0xFFBBDEFB)

@Composable
fun DeliveryCompleteScreen(
    bookingId: String,
    onBackToHome: () -> Unit,
    onRateDriver: (driverName: String) -> Unit,
    onViewReceipt: () -> Unit
) {
    val strings = LocalStrings.current
    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(bookingId) {
        BookingApi.getBooking(bookingId)
            .onSuccess { response ->
                booking = response.data
            }
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }

    val currentBooking = booking

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CompleteBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        // Success icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Title
        Text(
            strings.deliveryCompleteTitle,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(8.dp))

        // Subtitle with order code
        val orderCode = currentBooking?.orderCode
            ?: currentBooking?.id?.takeLast(8)?.uppercase()
            ?: bookingId.takeLast(8).uppercase()
        Text(
            strings.orderDeliveredMessage(orderCode),
            fontSize = 14.sp,
            color = TextPrimary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        // Summary card
        if (currentBooking != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CompleteCard)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Amount paid + order ID
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                strings.amountPaid,
                                color = PrimaryBlue.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                            val price = if (currentBooking.finalPrice > 0) currentBooking.finalPrice else currentBooking.estimatedPrice
                            Text(
                                "RM ${price.toInt()}",
                                color = PrimaryBlue,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 36.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(999.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                orderCode,
                                color = PrimaryBlue.copy(alpha = 0.9f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Time and Distance metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = strings.time,
                            value = if (currentBooking.duration > 0) currentBooking.duration.toString() else "--",
                            unit = strings.mins,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = strings.distanceLabel,
                            value = if (currentBooking.distance > 0) currentBooking.distance.toInt().toString() else "--",
                            unit = "km",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // Back to Home button
        Button(
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text(strings.backToHome, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(12.dp))

        // Rate Driver + View Receipt
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val driverName = currentBooking?.driver?.name ?: "Driver"
                    onRateDriver(driverName)
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryBlue)
            ) {
                Text(strings.rateDriver, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }

            OutlinedButton(
                onClick = onViewReceipt,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryBlue)
            ) {
                Text(strings.viewReceipt, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun MetricCard(title: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CompleteSoft)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title, color = PrimaryBlue.copy(alpha = 0.8f), fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 26.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(" $unit", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
    }
}
