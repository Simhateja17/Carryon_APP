package com.example.carryon.ui.screens.tracking

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.image_3
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import com.example.carryon.data.network.BookingApi
import com.example.carryon.data.model.Booking
import com.example.carryon.data.model.BookingStatus
import com.example.carryon.data.model.EtaResponse
import com.example.carryon.ui.components.MapViewComposable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDetailsScreen(
    orderId: String,
    onBack: () -> Unit = {},
    onDelivered: () -> Unit = {},
    onUnsuccessful: () -> Unit = {},
    onChatWithDriver: () -> Unit = {},
    onViewInvoice: () -> Unit = {}
) {
    val strings = LocalStrings.current
    var selectedTab by remember { mutableStateOf(0) }
    var booking by remember { mutableStateOf<Booking?>(null) }
    var etaResponse by remember { mutableStateOf<EtaResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load booking and ETA data
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
        
        // Also fetch ETA
        BookingApi.getEta(orderId)
            .onSuccess { response ->
                etaResponse = response.data
            }
        
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlueDark)
                    }
                },
                actions = { IconButton(onClick = { }) { Image(painter = painterResource(Res.drawable.bell_icon), contentDescription = "Notifications", modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
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
                val etaMinutes = etaResponse?.etaMinutes ?: currentBooking.eta ?: 0
                
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color.White).verticalScroll(rememberScrollState())
                ) {
                    // Delivery / Ride Tabs
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Column(modifier = Modifier.weight(1f).clickable { selectedTab = 0 }, horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(strings.delivery, fontSize = 15.sp, fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal, color = if (selectedTab == 0) TextPrimary else TextSecondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(if (selectedTab == 0) PrimaryBlue else Color.Transparent, RoundedCornerShape(2.dp)))
                        }
                        Column(modifier = Modifier.weight(1f).clickable { selectedTab = 1 }, horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Text(strings.ride, fontSize = 15.sp, fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal, color = if (selectedTab == 1) TextPrimary else TextSecondary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(modifier = Modifier.background(PrimaryBlue, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) { Text(strings.newLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(if (selectedTab == 1) PrimaryBlue else Color.Transparent, RoundedCornerShape(2.dp)))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                    // Track your shipment header
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text(strings.trackYourShipment, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        // Select dropdown
                        OutlinedTextField(
                            value = strings.select,
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { Text("v", fontSize = 12.sp, color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray, focusedContainerColor = Color(0xFFF8F8F8), unfocusedContainerColor = Color(0xFFF8F8F8), focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
                        )
                    }

                    // Map Section with live map
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                        MapViewComposable(
                            modifier = Modifier.fillMaxSize(),
                            centerLat = currentBooking.deliveryAddress.latitude,
                            centerLng = currentBooking.deliveryAddress.longitude,
                            zoom = 14.0,
                            markers = listOf(
                                com.example.carryon.ui.components.MapMarker(
                                    id = "pickup",
                                    lat = currentBooking.pickupAddress.latitude,
                                    lng = currentBooking.pickupAddress.longitude,
                                    title = "Pickup",
                                    color = com.example.carryon.ui.components.MarkerColor.GREEN
                                ),
                                com.example.carryon.ui.components.MapMarker(
                                    id = "delivery",
                                    lat = currentBooking.deliveryAddress.latitude,
                                    lng = currentBooking.deliveryAddress.longitude,
                                    title = "Delivery",
                                    color = com.example.carryon.ui.components.MarkerColor.RED
                                )
                            )
                        )
                        Box(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)) {
                            Box(modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text(
                                    if (etaMinutes > 0) "$etaMinutes min" else strings.calculating,
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.Medium, 
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    // "Your Package" section
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(Res.drawable.image_3),
                                contentDescription = "Package",
                                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(strings.yourPackage, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text("Order #${currentBooking.id}", fontSize = 12.sp, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Transit and Package Timeline with real data
                        DeliveryTimelineItem(
                            icon = "📊", 
                            label = strings.transit, 
                            location = currentBooking.deliveryAddress.address.ifBlank { currentBooking.deliveryAddress.label }, 
                            date = formatDeliveryDate(currentBooking.updatedAt), 
                            time = formatDeliveryTime(currentBooking.updatedAt)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        DeliveryTimelineItem(
                            icon = "📦", 
                            label = strings.sentPackage, 
                            location = currentBooking.pickupAddress.address.ifBlank { currentBooking.pickupAddress.label }, 
                            date = formatDeliveryDate(currentBooking.createdAt), 
                            time = formatDeliveryTime(currentBooking.createdAt)
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Sender / Receiver Grid with real data
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(strings.sendersName, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    currentBooking.pickupAddress.contactName.ifBlank { "—" }, 
                                    fontSize = 14.sp, 
                                    fontWeight = FontWeight.SemiBold, 
                                    color = TextPrimary
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(strings.sendersNumber, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    currentBooking.pickupAddress.contactPhone.ifBlank { "—" }, 
                                    fontSize = 14.sp, 
                                    fontWeight = FontWeight.SemiBold, 
                                    color = TextPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(strings.receiversName, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    currentBooking.deliveryAddress.contactName.ifBlank { "—" }, 
                                    fontSize = 14.sp, 
                                    fontWeight = FontWeight.SemiBold, 
                                    color = TextPrimary
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(strings.receiversNumber, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    currentBooking.deliveryAddress.contactPhone.ifBlank { "—" }, 
                                    fontSize = 14.sp, 
                                    fontWeight = FontWeight.SemiBold, 
                                    color = TextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Delivery Method & Fee with real data
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(strings.deliveryMethod, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                currentBooking.paymentMethod.name, 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.SemiBold, 
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(strings.deliveryFee, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "RM ${String.format("%.2f", if (currentBooking.finalPrice > 0) currentBooking.finalPrice else currentBooking.estimatedPrice)}", 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = PrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Chat & Invoice Buttons
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = onChatWithDriver,
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue),
                                enabled = currentBooking.driver != null
                            ) { Text(strings.chatWithDriver, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = if (currentBooking.driver != null) PrimaryBlue else TextSecondary) }

                            OutlinedButton(
                                onClick = onViewInvoice,
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
                            ) { Text(strings.viewInvoice, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue) }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = onUnsuccessful,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCCCCCC))
                            ) { Text(strings.unsuccessful, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary) }

                            Button(
                                onClick = onDelivered,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) { Text(strings.delivered, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White) }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

private fun formatDeliveryDate(isoDate: String): String {
    return try {
        val datePart = isoDate.split("T")[0].split("-")
        if (datePart.size < 3) return isoDate
        val month = when (datePart[1].toIntOrNull() ?: 0) {
            1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"
            5 -> "May"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Aug"
            9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
            else -> datePart[1]
        }
        "${datePart[2].toIntOrNull() ?: datePart[2]}$month,${datePart[0].takeLast(2)}"
    } catch (e: Exception) {
        isoDate
    }
}

private fun formatDeliveryTime(isoDate: String): String {
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

@Composable
private fun DeliveryTimelineItem(icon: String, label: String, location: String, date: String, time: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(44.dp).background(Color(0xFFF5F5F5), CircleShape), contentAlignment = Alignment.Center) { Text(icon, fontSize = 20.sp) }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(location.ifBlank { "—" }, fontSize = 12.sp, color = TextSecondary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(date, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(time, fontSize = 12.sp, color = TextSecondary)
        }
    }
}
