package com.example.carryon.ui.screens.tracking

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.track_sent
import carryon.composeapp.generated.resources.track_transit
import carryon.composeapp.generated.resources.track_journey
import carryon.composeapp.generated.resources.track_accepted
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import com.example.carryon.data.network.BookingApi
import com.example.carryon.data.model.Booking
import com.example.carryon.data.model.BookingStatus

private data class TrackingStep(
    val iconRes: DrawableResource,
    val title: String,
    val subtitle: String,
    val date: String,
    val time: String,
    val isCompleted: Boolean = true
)

private fun buildTrackingSteps(
    booking: Booking,
    strings: com.example.carryon.i18n.AppStrings
): List<TrackingStep> {
    val createdDate = formatDate(booking.createdAt)
    val createdTime = formatTime(booking.createdAt)
    val updatedDate = formatDate(booking.updatedAt)
    val updatedTime = formatTime(booking.updatedAt)

    val statusStep = when (booking.status) {
        BookingStatus.PENDING, BookingStatus.SEARCHING_DRIVER -> 0
        BookingStatus.DRIVER_ASSIGNED, BookingStatus.DRIVER_ARRIVED -> 1
        BookingStatus.PICKUP_DONE -> 2
        BookingStatus.IN_TRANSIT -> 3
        BookingStatus.DELIVERED -> 4
        BookingStatus.CANCELLED -> -1
    }

    return listOf(
        TrackingStep(
            iconRes = Res.drawable.track_sent,
            title = strings.sentPackage,
            subtitle = booking.pickupAddress.address.ifBlank { booking.pickupAddress.label },
            date = createdDate,
            time = createdTime,
            isCompleted = statusStep >= 0
        ),
        TrackingStep(
            iconRes = Res.drawable.track_transit,
            title = strings.transit,
            subtitle = booking.vehicleType,
            date = if (statusStep >= 1) updatedDate else "",
            time = if (statusStep >= 1) updatedTime else "",
            isCompleted = statusStep >= 1
        ),
        TrackingStep(
            iconRes = Res.drawable.track_journey,
            title = strings.onAJourney,
            subtitle = booking.deliveryAddress.address.ifBlank { booking.deliveryAddress.label },
            date = if (statusStep >= 3) updatedDate else "",
            time = if (statusStep >= 3) updatedTime else "",
            isCompleted = statusStep >= 3
        ),
        TrackingStep(
            iconRes = Res.drawable.track_accepted,
            title = strings.accepted,
            subtitle = booking.driver?.name ?: booking.deliveryAddress.contactName.ifBlank { "—" },
            date = if (statusStep >= 4) updatedDate else "",
            time = if (statusStep >= 4) updatedTime else "",
            isCompleted = statusStep >= 4
        )
    )
}

private fun formatDate(isoDate: String): String {
    return try {
        val datePart = isoDate.split("T")[0].split("-")
        if (datePart.size < 3) return isoDate
        val month = when (datePart[1].toIntOrNull() ?: 0) {
            1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"
            5 -> "May"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Aug"
            9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
            else -> datePart[1]
        }
        "${datePart[2].toIntOrNull() ?: datePart[2]} $month, ${datePart[0].takeLast(2)}"
    } catch (e: Exception) {
        isoDate
    }
}

private fun formatTime(isoDate: String): String {
    return try {
        val timePart = isoDate.split("T").getOrNull(1)?.take(5)?.split(":") ?: return ""
        if (timePart.size < 2) return ""
        val hour = timePart[0].toIntOrNull() ?: return ""
        val minute = timePart[1]
        val ampm = if (hour < 12) "am" else "pm"
        val hour12 = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        "$hour12:$minute$ampm"
    } catch (e: Exception) {
        ""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageDetailsScreen(
    orderId: String,
    onBack: () -> Unit,
    onRateDriver: (driverName: String) -> Unit
) {
    val strings = LocalStrings.current
    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load booking data
    LaunchedEffect(orderId) {
        isLoading = true
        errorMessage = null
        BookingApi.getBooking(orderId)
            .onSuccess { response ->
                booking = response.data
                if (response.data == null) {
                    errorMessage = "Booking not found"
                }
            }
            .onFailure { e ->
                errorMessage = "Failed to load booking: ${e.message}"
            }
        isLoading = false
    }

    val trackingSteps = remember(booking, strings) {
        booking?.let { buildTrackingSteps(it, strings) }
    }
    
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
                            color = PrimaryBlue,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                        Text(
                            text = " On",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
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
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(errorMessage ?: "", color = Color.Red)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBack) {
                            Text(strings.back)
                        }
                    }
                }
            }
            booking != null -> {
                val currentBooking = booking!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color.White)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    // Sub Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "<",
                            fontSize = 20.sp,
                            color = TextSecondary,
                            modifier = Modifier.clickable { onBack() }
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text(
                            text = strings.packageLabel,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text("", fontSize = 20.sp, color = TextSecondary)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Package Card with Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF2F80ED),
                                        Color(0xFF64B5F6)
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Package Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("📦", fontSize = 18.sp)
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column {
                                    Text(
                                        text = strings.documents,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = currentBooking.id,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            
                            HorizontalDivider(
                                color = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            
                            // Details Grid
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.from,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = currentBooking.pickupAddress.address.ifBlank { 
                                            currentBooking.pickupAddress.label.ifBlank { "—" } 
                                        },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.destination,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = currentBooking.deliveryAddress.address.ifBlank { 
                                            currentBooking.deliveryAddress.label.ifBlank { "—" } 
                                        },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.delivery,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = currentBooking.vehicleType.ifBlank { "—" },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.itemWeight,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = if (currentBooking.distance > 0) "${String.format("%.1f", currentBooking.distance)} km" else "—",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            // Rate Driver Button (only show if delivered and driver exists)
                            if (currentBooking.status == BookingStatus.DELIVERED && currentBooking.driver != null) {
                                Button(
                                    onClick = { onRateDriver(currentBooking.driver?.name ?: "") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryBlueLight.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Text(
                                        text = strings.viewDetails,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Details Timeline
                    Text(
                        text = strings.details,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Timeline
                    trackingSteps?.forEachIndexed { index, step ->
                        TimelineItem(
                            step = step,
                            isLast = index == trackingSteps.lastIndex
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun TimelineItem(
    step: TrackingStep,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Timeline indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (step.isCompleted) PrimaryBlueSurface else Color(0xFFE0E0E0),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(step.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
            }
            
            if (!isLast) {
                // Dotted line
                Canvas(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .padding(top = 4.dp)
                ) {
                    val dashPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width / 2, 0f)
                        lineTo(size.width / 2, size.height)
                    }
                    drawPath(
                        path = dashPath,
                        color = if (step.isCompleted) PrimaryBlue else Color(0xFFE0E0E0),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
                        )
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = step.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (step.isCompleted) TextPrimary else TextSecondary
            )
            if (step.subtitle.isNotBlank()) {
                Text(
                    text = step.subtitle,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
        
        // Date & Time
        if (step.date.isNotBlank() || step.time.isNotBlank()) {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (step.date.isNotBlank()) {
                    Text(
                        text = step.date,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (step.time.isNotBlank()) {
                    Text(
                        text = step.time,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
    
    if (!isLast) {
        Spacer(modifier = Modifier.height(8.dp))
    }
}
