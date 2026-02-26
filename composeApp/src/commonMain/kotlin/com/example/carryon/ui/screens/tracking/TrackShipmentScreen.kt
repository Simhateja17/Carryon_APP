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
import carryon.composeapp.generated.resources.carryon_logo
import carryon.composeapp.generated.resources.icon_documents
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
import kotlinx.coroutines.launch

private data class ShipmentTimelineItem(
    val iconRes: DrawableResource,
    val title: String,
    val location: String,
    val date: String,
    val time: String,
    val isCompleted: Boolean
)

private fun buildShipmentTimeline(
    booking: Booking,
    strings: com.example.carryon.i18n.AppStrings
): List<ShipmentTimelineItem> {
    val createdDate = formatShipmentDate(booking.createdAt)
    val createdTime = formatShipmentTime(booking.createdAt)
    val updatedDate = formatShipmentDate(booking.updatedAt)
    val updatedTime = formatShipmentTime(booking.updatedAt)

    val statusStep = when (booking.status) {
        BookingStatus.PENDING, BookingStatus.SEARCHING_DRIVER -> 0
        BookingStatus.DRIVER_ASSIGNED, BookingStatus.DRIVER_ARRIVED -> 1
        BookingStatus.PICKUP_DONE -> 2
        BookingStatus.IN_TRANSIT -> 3
        BookingStatus.DELIVERED -> 4
        BookingStatus.CANCELLED -> -1
    }

    return listOf(
        ShipmentTimelineItem(
            iconRes = Res.drawable.track_sent,
            title = strings.sentPackage,
            location = booking.pickupAddress.address.ifBlank { booking.pickupAddress.label },
            date = createdDate,
            time = createdTime,
            isCompleted = statusStep >= 0
        ),
        ShipmentTimelineItem(
            iconRes = Res.drawable.track_transit,
            title = strings.transit,
            location = booking.vehicleType,
            date = if (statusStep >= 1) updatedDate else "",
            time = if (statusStep >= 1) updatedTime else "",
            isCompleted = statusStep >= 1
        ),
        ShipmentTimelineItem(
            iconRes = Res.drawable.track_journey,
            title = strings.onAJourney,
            location = booking.deliveryAddress.address.ifBlank { booking.deliveryAddress.label },
            date = if (statusStep >= 3) updatedDate else "",
            time = if (statusStep >= 3) updatedTime else "",
            isCompleted = statusStep >= 3
        ),
        ShipmentTimelineItem(
            iconRes = Res.drawable.track_accepted,
            title = strings.accepted,
            location = booking.deliveryAddress.contactName.ifBlank { "—" },
            date = if (statusStep >= 4) updatedDate else "",
            time = if (statusStep >= 4) updatedTime else "",
            isCompleted = statusStep >= 4
        )
    )
}

private fun formatShipmentDate(isoDate: String): String {
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

private fun formatShipmentTime(isoDate: String): String {
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
fun TrackShipmentScreen(
    onSearch: (String) -> Unit,
    onViewDetails: (String) -> Unit
) {
    val strings = LocalStrings.current
    var trackingNumber by remember { mutableStateOf("") }
    var displayedBooking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Load most recent active booking on entry
    LaunchedEffect(Unit) {
        BookingApi.getBookings().onSuccess { response ->
            displayedBooking = response.data?.firstOrNull {
                it.status != BookingStatus.CANCELLED
            }
        }
        isLoading = false
    }

    val timelineItems = remember(displayedBooking, strings) {
        displayedBooking?.let { buildShipmentTimeline(it, strings) }
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
                            color = PrimaryBlue
                        )
                        Text(
                            text = " On",
                            fontSize = 22.sp,
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Header with avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.trackYourShipment,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.carryon_logo),
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tracking Number Input
            OutlinedTextField(
                value = trackingNumber,
                onValueChange = {
                    trackingNumber = it
                    searchError = null
                },
                placeholder = { Text(strings.enterTrackingNumber, color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color(0xFFF8F8F8),
                    unfocusedContainerColor = Color(0xFFF8F8F8),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )

            if (searchError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = searchError ?: "",
                    fontSize = 12.sp,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Button
            Button(
                onClick = {
                    if (trackingNumber.isNotBlank()) {
                        isSearching = true
                        searchError = null
                        scope.launch {
                            BookingApi.getBooking(trackingNumber)
                                .onSuccess { response ->
                                    displayedBooking = response.data
                                    if (response.data == null) {
                                        searchError = "No shipment found for this tracking number"
                                    }
                                }
                                .onFailure {
                                    searchError = "No shipment found for this tracking number"
                                }
                            isSearching = false
                            onSearch(trackingNumber)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                if (isSearching) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = strings.search,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else if (displayedBooking != null) {
                val booking = displayedBooking!!

                // Your Package Section
                Text(
                    text = strings.yourPackage,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Package Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                                Image(
                                    painter = painterResource(Res.drawable.icon_documents),
                                    contentDescription = "Documents",
                                    modifier = Modifier.size(22.dp),
                                    contentScale = ContentScale.Fit
                                )
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
                                    text = booking.id,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        HorizontalDivider(
                            color = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // Details Grid
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.from,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = booking.pickupAddress.address.ifBlank {
                                        booking.pickupAddress.label.ifBlank { "—" }
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
                                    text = booking.deliveryAddress.address.ifBlank {
                                        booking.deliveryAddress.label.ifBlank { "—" }
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.delivery,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = booking.vehicleType.ifBlank { "—" },
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
                                    text = if (booking.distance > 0) "${String.format("%.1f", booking.distance)} km" else "—",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Pagination dots
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(3) { i ->
                                Box(
                                    modifier = Modifier
                                        .size(if (i == 0) 8.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(if (i == 0) Color.White else Color.White.copy(alpha = 0.4f))
                                )
                                if (i < 2) Spacer(modifier = Modifier.width(6.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // View Details Button
                        Button(
                            onClick = { onViewDetails(booking.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF97CBF1),
                                            Color(0xFFB7DAF5)
                                        )
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                ),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
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

                Spacer(modifier = Modifier.height(32.dp))

                // Details Timeline
                Text(
                    text = strings.details,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(20.dp))

                timelineItems?.forEachIndexed { index, item ->
                    TrackingTimelineItem(
                        iconRes = item.iconRes,
                        title = item.title,
                        location = item.location,
                        date = item.date,
                        time = item.time,
                        isCompleted = item.isCompleted,
                        isLast = index == (timelineItems.size - 1)
                    )
                }
            } else {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📦", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Enter a tracking number to search",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TrackingTimelineItem(
    iconRes: DrawableResource,
    title: String,
    location: String,
    date: String,
    time: String,
    isCompleted: Boolean,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(70.dp)
        ) {
            // Circle with icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isCompleted) Color(0xFFB3D9F2) else Color(0xFFE0E0E0),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Dotted line connector
            if (!isLast) {
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
                        color = Color(0xFFB3D9F2),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(5f, 5f)
                            )
                        )
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, top = 4.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isCompleted) TextPrimary else TextSecondary
            )
            if (location.isNotBlank()) {
                Text(
                    text = location,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        if (date.isNotBlank() || time.isNotBlank()) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                if (date.isNotBlank()) {
                    Text(
                        text = date,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                if (time.isNotBlank()) {
                    Text(
                        text = time,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
