package com.example.carryon.ui.screens.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import com.example.carryon.data.network.BookingApi
import com.example.carryon.data.network.RatingApi
import com.example.carryon.data.model.Booking
import com.example.carryon.data.model.BookingStatus
import kotlinx.coroutines.launch

data class TrackingStatus(
    val title: String,
    val subtitle: String,
    val time: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean = false
)

private fun bookingStatusToStep(status: BookingStatus): Int = when (status) {
    BookingStatus.PENDING, BookingStatus.SEARCHING_DRIVER -> 0
    BookingStatus.DRIVER_ASSIGNED, BookingStatus.DRIVER_ARRIVED -> 1
    BookingStatus.PICKUP_DONE -> 2
    BookingStatus.IN_TRANSIT -> 3
    BookingStatus.DELIVERED -> 5
    BookingStatus.CANCELLED -> -1
}

private fun progressFor(status: BookingStatus): Float = when (status) {
    BookingStatus.PENDING, BookingStatus.SEARCHING_DRIVER -> 0.1f
    BookingStatus.DRIVER_ASSIGNED -> 0.3f
    BookingStatus.DRIVER_ARRIVED -> 0.45f
    BookingStatus.PICKUP_DONE -> 0.55f
    BookingStatus.IN_TRANSIT -> 0.7f
    BookingStatus.DELIVERED -> 1.0f
    BookingStatus.CANCELLED -> 0.0f
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    bookingId: String,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val strings = LocalStrings.current
    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showRatingDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(bookingId) {
        BookingApi.getBooking(bookingId).onSuccess { response ->
            booking = response.data
        }
        isLoading = false
    }

    val currentStatus = booking?.status ?: BookingStatus.PENDING
    val step = bookingStatusToStep(currentStatus)
    val progress = progressFor(currentStatus)

    val trackingStatuses = remember(booking, strings) {
        val s = booking?.status ?: BookingStatus.PENDING
        val currentStep = bookingStatusToStep(s)
        listOf(
            TrackingStatus(strings.orderConfirmed, strings.orderPlaced, "", currentStep >= 0, currentStep == 0),
            TrackingStatus(strings.driverAssigned, strings.driverOnTheWay, "", currentStep >= 1, currentStep == 1),
            TrackingStatus(strings.pickedUp, strings.packageCollected, "", currentStep >= 2, currentStep == 2),
            TrackingStatus(strings.inTransit, strings.onTheWayToDestination, "", currentStep >= 3, currentStep == 3),
            TrackingStatus(strings.outForDelivery, strings.nearYourLocation, "", currentStep >= 4, currentStep == 4),
            TrackingStatus(strings.delivered, strings.packageDeliveredSuccessfully, "", currentStep >= 5, currentStep == 5)
        )
    }

    val driverName = booking?.driver?.name?.ifBlank { "—" } ?: "—"
    val driverRating = booking?.driver?.rating?.let { String.format("%.1f", it) } ?: "—"
    val etaMinutes = booking?.eta?.let { "$it min" } ?: strings.estimatedDelivery
    val vehicleType = booking?.vehicleType ?: "—"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Carry",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = " On",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BackgroundLight)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = strings.trackYourShipment,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = strings.orderIdLabel(bookingId),
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = PrimaryBlue,
                            trackColor = PrimaryBlueSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = etaMinutes,
                            fontSize = 12.sp,
                            color = PrimaryBlue
                        )
                    }
                }

                // Your Package Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = strings.yourPackage,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(PrimaryBlueSurface, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📄", fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.documents,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row {
                                    StatusChip(strings.processing, PrimaryBlue)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    StatusChip(strings.delivering, SuccessGreen)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Package Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailItem("🚗", strings.parcels, vehicleType)
                            DetailItem("⏱️", strings.estExpress, etaMinutes)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // View Details Button
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    )
                ) {
                    Text(
                        text = strings.viewDetails,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Details Section (Timeline)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = strings.details,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        trackingStatuses.forEachIndexed { index, status ->
                            TimelineItem(
                                status = status,
                                isLast = index == trackingStatuses.lastIndex
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Driver Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(PrimaryBlueSurface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = driverName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⭐", fontSize = 14.sp)
                                Text(
                                    text = " $driverRating",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Call button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(PrimaryBlueSurface, CircleShape)
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📞", fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Message button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(PrimaryBlueSurface, CircleShape)
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💬", fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rate Driver Button (shown when delivery is complete)
                if (currentStatus == BookingStatus.DELIVERED) {
                    OutlinedButton(
                        onClick = { showRatingDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PrimaryBlue
                        )
                    ) {
                        Text(
                            text = strings.rateDriver,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Rating Dialog
    if (showRatingDialog) {
        RatingDialog(
            driverName = driverName,
            driverRating = booking?.driver?.rating ?: 0.0,
            bookingId = bookingId,
            onDismiss = { showRatingDialog = false },
            onSubmit = {
                showRatingDialog = false
                onNavigateToHome()
            }
        )
    }
}

@Composable
private fun StatusChip(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DetailItem(
    icon: String,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun TimelineItem(
    status: TrackingStatus,
    isLast: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        when {
                            status.isCurrent -> PrimaryBlue
                            status.isCompleted -> SuccessGreen
                            else -> Color.LightGray
                        },
                        CircleShape
                    )
            )

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(
                            if (status.isCompleted) SuccessGreen.copy(alpha = 0.5f) else Color.LightGray
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = status.title,
                    fontSize = 13.sp,
                    fontWeight = if (status.isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (status.isCompleted || status.isCurrent) TextPrimary else TextSecondary
                )
                if (status.time.isNotBlank()) {
                    Text(
                        text = status.time,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
            Text(
                text = status.subtitle,
                fontSize = 11.sp,
                color = TextSecondary
            )

            if (!isLast) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun RatingDialog(
    driverName: String,
    driverRating: Double,
    bookingId: String,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    var rating by remember { mutableStateOf(5) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var selectedTip by remember { mutableStateOf<Int?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val tags = listOf(strings.goodCommunication, strings.excellentService, strings.cleanAndComfy)
    val tips = listOf(10, 50, 80, 100, 200)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    isSubmitting = true
                    scope.launch {
                        RatingApi.submitRating(
                            bookingId = bookingId,
                            rating = rating,
                            tags = selectedTags.toList(),
                            tipAmount = (selectedTip ?: 0).toDouble()
                        )
                        isSubmitting = false
                        onSubmit()
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(strings.submit, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.giveRatingForDriver,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(strings.howWasTheDriver, fontSize = 14.sp, color = TextSecondary)

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(PrimaryBlueSurface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = driverName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("⭐ ${String.format("%.1f", driverRating)}", fontSize = 14.sp, color = StarYellow)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Star rating
                Row {
                    (1..5).forEach { star ->
                        Text(
                            text = if (star <= rating) "⭐" else "☆",
                            fontSize = 32.sp,
                            modifier = Modifier
                                .clickable { rating = star }
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(strings.whatImpressedYou, fontSize = 13.sp, color = TextSecondary)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    tags.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) PrimaryBlue else Color.Transparent
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) PrimaryBlue else Color.LightGray,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    selectedTags = if (isSelected) {
                                        selectedTags - tag
                                    } else {
                                        selectedTags + tag
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = strings.tipsForDriver,
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tips.forEach { tip ->
                        val isSelected = selectedTip == tip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) PrimaryBlue else Color(0xFFF5F5F5)
                                )
                                .clickable { selectedTip = tip }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "RM $tip",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}
