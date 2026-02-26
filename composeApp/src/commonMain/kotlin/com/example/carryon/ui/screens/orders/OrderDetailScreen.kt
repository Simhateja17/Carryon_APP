package com.example.carryon.ui.screens.orders

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carryon.data.model.Booking
import com.example.carryon.data.model.BookingStatus
import com.example.carryon.data.model.PaymentMethod
import com.example.carryon.data.network.BookingApi
import com.example.carryon.data.network.RatingApi
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    onBack: () -> Unit
) {
    val strings = LocalStrings.current
    var rating by remember { mutableStateOf(0) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var ratingSubmitted by remember { mutableStateOf(false) }

    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(orderId) {
        BookingApi.getBooking(orderId)
            .onSuccess { response ->
                booking = response.data
                isLoading = false
            }
            .onFailure {
                error = it.message
                isLoading = false
            }
    }

    val orderDate       = booking?.createdAt?.take(10) ?: ""
    val pickupAddress   = booking?.let { it.pickupAddress.label.ifEmpty { it.pickupAddress.address } } ?: "—"
    val deliveryAddress = booking?.let { it.deliveryAddress.label.ifEmpty { it.deliveryAddress.address } } ?: "—"
    val driverName      = booking?.driver?.name ?: "—"
    val driverRating    = booking?.driver?.rating ?: 0.0
    val vehicleNumber   = booking?.driver?.vehicleNumber ?: "—"
    val vehicleType     = booking?.vehicleType ?: "—"
    val distance        = booking?.distance ?: 0.0
    val durationMin     = booking?.duration ?: 0
    val totalPrice      = booking?.let { if (it.finalPrice > 0) it.finalPrice else it.estimatedPrice } ?: 0.0
    val paymentMethodText = when (booking?.paymentMethod) {
        PaymentMethod.WALLET -> "Wallet"
        PaymentMethod.CARD   -> "Card"
        PaymentMethod.UPI    -> "UPI"
        else                 -> "Cash"
    }
    val bookingStatus = booking?.status ?: BookingStatus.DELIVERED
    val statusText = when (bookingStatus) {
        BookingStatus.DELIVERED        -> "✅ ${strings.delivered}"
        BookingStatus.CANCELLED        -> "❌ ${strings.cancelled}"
        BookingStatus.IN_TRANSIT       -> "🚚 ${strings.inTransit}"
        BookingStatus.SEARCHING_DRIVER -> "🔍 ${strings.findingDriver}"
        else                           -> "⏳ ${strings.processing}"
    }
    val statusColor = when (bookingStatus) {
        BookingStatus.DELIVERED        -> SuccessGreen
        BookingStatus.CANCELLED        -> ErrorRed
        BookingStatus.IN_TRANSIT       -> InfoBlue
        BookingStatus.SEARCHING_DRIVER -> WarningYellow
        else                           -> Color.Gray
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", fontSize = 24.sp) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(error ?: "Error", color = ErrorRed, fontSize = 14.sp)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(BackgroundLight)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Order ID and Status
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Order #$orderId", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text(text = orderDate, fontSize = 13.sp, color = Color.Gray)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(text = statusText, fontSize = 13.sp, color = statusColor, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    // Route Details
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Route Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

                            Row(verticalAlignment = Alignment.Top) {
                                Box(modifier = Modifier.size(12.dp).offset(y = 4.dp).background(SuccessGreen, CircleShape))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = strings.pickup, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                    Text(text = pickupAddress, fontSize = 14.sp)
                                }
                            }

                            Box(modifier = Modifier.padding(start = 5.dp, top = 4.dp, bottom = 4.dp).width(2.dp).height(20.dp).background(Color.LightGray))

                            Row(verticalAlignment = Alignment.Top) {
                                Box(modifier = Modifier.size(12.dp).offset(y = 4.dp).background(ErrorRed, CircleShape))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = strings.delivery, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                    Text(text = deliveryAddress, fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth().background(BackgroundLight, RoundedCornerShape(8.dp)).padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "$distance km", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text(text = strings.distance, fontSize = 12.sp, color = Color.Gray)
                                }
                                Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.LightGray))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "$durationMin min", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text(text = strings.duration, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Driver Details
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Driver Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) { Text("👤", fontSize = 28.sp) }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = driverName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "$vehicleType • $vehicleNumber", fontSize = 13.sp, color = Color.Gray)
                                }
                                if (driverRating > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(WarningYellow.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("⭐", fontSize = 14.sp)
                                        Text(text = " $driverRating", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Payment Details
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Payment Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(text = "RM ${totalPrice.toInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryOrange)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.background(SuccessGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(8.dp)
                            ) {
                                Text("💵", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Paid via $paymentMethodText", fontSize = 13.sp, color = SuccessGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rate Driver (only if delivered and not yet rated)
                    if (bookingStatus == BookingStatus.DELIVERED && !ratingSubmitted) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "Rate your experience", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row {
                                    repeat(5) { index ->
                                        IconButton(onClick = { rating = index + 1 }) {
                                            Text(text = if (index < rating) "⭐" else "☆", fontSize = 28.sp)
                                        }
                                    }
                                }
                                if (rating > 0) {
                                    Button(
                                        onClick = { showRatingDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                                        shape = RoundedCornerShape(8.dp)
                                    ) { Text(strings.submit) }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("📤", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Invoice")
                        }
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                        ) {
                            Text("🔄", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Rebook")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text("Thank you!") },
            text = { Text("Your rating of $rating stars has been submitted.") },
            confirmButton = {
                Button(
                    onClick = {
                        showRatingDialog = false
                        scope.launch {
                            RatingApi.submitRating(bookingId = orderId, rating = rating)
                            ratingSubmitted = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) { Text(strings.ok) }
            }
        )
    }
}
