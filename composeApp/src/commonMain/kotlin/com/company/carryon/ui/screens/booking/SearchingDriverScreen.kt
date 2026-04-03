package com.company.carryon.ui.screens.booking

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.company.carryon.data.network.BookingApi
import com.company.carryon.data.model.BookingStatus
import com.company.carryon.i18n.LocalStrings
import com.company.carryon.ui.theme.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchingDriverScreen(
    bookingId: String,
    amount: Double,
    onDriverFound: () -> Unit,
    onCancel: () -> Unit
) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    var isCancelling by remember { mutableStateOf(false) }
    var driverFoundHandled by remember { mutableStateOf(false) }

    // Poll for driver assignment
    LaunchedEffect(bookingId) {
        if (bookingId.isBlank()) return@LaunchedEffect
        while (currentCoroutineContext().isActive && !driverFoundHandled) {
            delay(6000L)
            BookingApi.getBooking(bookingId).onSuccess { response ->
                val booking = response.data
                if (booking != null && booking.status == BookingStatus.DRIVER_ASSIGNED) {
                    driverFoundHandled = true
                    onDriverFound()
                }
            }
        }
    }

    // Pulsing animation
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Pulsing circle indicator
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(pulseScale)
                .background(PrimaryBlue.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(PrimaryBlue.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(PrimaryBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚗", fontSize = 24.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = strings.findingDriver,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = strings.searchingDriverSubtitle,
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 40.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Loading indicator
        CircularProgressIndicator(
            color = PrimaryBlue,
            modifier = Modifier.size(32.dp),
            strokeWidth = 3.dp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Cancel button
        OutlinedButton(
            onClick = {
                if (bookingId.isNotBlank() && !isCancelling) {
                    isCancelling = true
                    scope.launch {
                        BookingApi.cancelBooking(bookingId)
                        onCancel()
                    }
                } else {
                    onCancel()
                }
            },
            enabled = !isCancelling,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
        ) {
            if (isCancelling) {
                CircularProgressIndicator(
                    color = ErrorRed,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    strings.cancelBooking,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
