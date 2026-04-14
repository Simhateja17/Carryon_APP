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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.map_background
import carryon.composeapp.generated.resources.vehicle_bike_icon
import com.company.carryon.data.network.BookingApi
import com.company.carryon.data.model.BookingStatus
import com.company.carryon.i18n.LocalStrings
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
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    var isCancelling by remember { mutableStateOf(false) }
    var driverFoundHandled by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }
    val estimatedPriceText = "₹${(if (amount > 0.0) amount else 150.0).roundToInt()}"

    // Auto-advance to scheduled state after a short wait (5-10 sec expected)
    LaunchedEffect(Unit) {
        delay(7000L)
        if (!hasNavigated && !isCancelling) {
            hasNavigated = true
            onScheduled()
        }
    }

    // Poll for driver assignment
    LaunchedEffect(bookingId) {
        if (bookingId.isBlank()) return@LaunchedEffect
        while (currentCoroutineContext().isActive && !driverFoundHandled && !hasNavigated) {
            delay(6000L)
            BookingApi.getBooking(bookingId).onSuccess { response ->
                val booking = response.data
                if (booking != null && booking.status == BookingStatus.DRIVER_ASSIGNED) {
                    driverFoundHandled = true
                    if (!hasNavigated) {
                        hasNavigated = true
                        onDriverFound()
                    }
                }
            }
        }
    }

    // Pulsing animation for search target
    val infiniteTransition = rememberInfiniteTransition()
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.map_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xB3F4F7FF))
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
                modifier = Modifier.size((130 * pulseRadius).dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(122.dp)
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

            Spacer(modifier = Modifier.weight(1f))

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
                                Text("Bike", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
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
                    Text("32nd Avenue, Sector 15", fontSize = 21.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("DROP OFF", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("DLF Cyber City, Phase 2", fontSize = 21.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF1F3F7), RoundedCornerShape(14.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("25 mins", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isCancelling) {
                CircularProgressIndicator(
                    color = PrimaryBlue,
                    modifier = Modifier.size(26.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Cancel Request",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue,
                    modifier = Modifier.clickable {
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
                )
            }

            Spacer(modifier = Modifier.height(22.dp))
        }
    }
}
