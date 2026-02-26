package com.example.carryon.ui.screens.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.*
import com.example.carryon.data.network.LocationApi
import com.example.carryon.data.network.BookingApi
import com.example.carryon.data.network.CreateBookingRequest
import com.example.carryon.data.network.CreateAddressData
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestForRideScreen(
    vehicleType: String = "",
    pickupAddress: String = "",
    deliveryAddress: String = "",
    onContinue: (bookingId: String, amount: Double) -> Unit,
    onBack: () -> Unit
) {
    var selectedPayment by remember { mutableStateOf("cash") }
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()

    // Vehicle pricing from API
    var basePrice by remember { mutableStateOf(15.0) }
    var pricePerKm by remember { mutableStateOf(2.0) }
    var isLoadingVehicles by remember { mutableStateOf(true) }

    // Geocoded coordinates
    var pickupLat by remember { mutableStateOf(0.0) }
    var pickupLng by remember { mutableStateOf(0.0) }
    var deliveryLat by remember { mutableStateOf(0.0) }
    var deliveryLng by remember { mutableStateOf(0.0) }

    var estimatedPrice by remember { mutableStateOf(basePrice) }
    var taxAmount by remember { mutableStateOf(kotlin.math.round(basePrice * 0.06 * 100).toDouble() / 100.0) }
    var distanceKm by remember { mutableStateOf(0.0) }
    var isCalculating by remember { mutableStateOf(pickupAddress.isNotBlank() && deliveryAddress.isNotBlank()) }
    var isCreatingBooking by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch vehicle prices from API
    LaunchedEffect(vehicleType) {
        isLoadingVehicles = true
        BookingApi.getVehicles().onSuccess { response ->
            val vehicles = response.data ?: emptyList()
            // Map vehicle type to API vehicle
            val vehicleTypeMapping = mapOf(
                "Bike" to "bike",
                "Car (2-Seat)" to "auto",
                "Car (4-Seat)" to "car",
                "Mini Van" to "mini truck",
                "Truck" to "truck",
                "Open Truck" to "truck"
            )
            val apiType = vehicleTypeMapping[vehicleType] ?: vehicleType.lowercase()
            val vehicle = vehicles.find { it.type.lowercase() == apiType }
            if (vehicle != null) {
                basePrice = vehicle.basePrice
                pricePerKm = vehicle.pricePerKm
            }
        }
        isLoadingVehicles = false
    }

    // Calculate price based on distance
    LaunchedEffect(pickupAddress, deliveryAddress, basePrice, pricePerKm) {
        if (pickupAddress.isBlank() || deliveryAddress.isBlank()) {
            estimatedPrice = basePrice
            taxAmount = kotlin.math.round(basePrice * 0.06 * 100).toDouble() / 100.0
            isCalculating = false
            return@LaunchedEffect
        }
        isCalculating = true
        val pickupGeo = LocationApi.geocode(pickupAddress).getOrNull()
        val deliveryGeo = LocationApi.geocode(deliveryAddress).getOrNull()
        if (pickupGeo != null && deliveryGeo != null) {
            pickupLat = pickupGeo.lat
            pickupLng = pickupGeo.lng
            deliveryLat = deliveryGeo.lat
            deliveryLng = deliveryGeo.lng

            val route = LocationApi.calculateRoute(
                pickupGeo.lat, pickupGeo.lng,
                deliveryGeo.lat, deliveryGeo.lng
            ).getOrNull()
            if (route != null && route.distance > 0) {
                distanceKm = route.distance
                val calculatedPrice = basePrice + (route.distance * pricePerKm)
                estimatedPrice = kotlin.math.round(calculatedPrice * 100).toDouble() / 100.0
                taxAmount = kotlin.math.round(calculatedPrice * 0.06 * 100).toDouble() / 100.0
            }
        }
        isCalculating = false
    }

    val vehicleImageRes = when (vehicleType) {
        "Bike" -> Res.drawable.bike
        "Car (2-Seat)" -> Res.drawable.car_two_seater
        "Car (4-Seat)" -> Res.drawable.car_4_seater
        "Mini Van" -> Res.drawable.mini_van
        "Truck" -> Res.drawable.truck
        "Open Truck" -> Res.drawable.open_truck
        else -> Res.drawable.car_mustang
    }
    val vehicleDisplayName = vehicleType.ifBlank { "Vehicle" }

    // Map payment selection to API format
    val paymentMethodApi = when (selectedPayment) {
        "cash" -> "CASH"
        "card" -> "CARD"
        "wallet" -> "DUITNOW"
        else -> "CASH"
    }

    // Map vehicle type to API format
    val vehicleTypeApi = when (vehicleType) {
        "Bike" -> "BIKE"
        "Car (2-Seat)" -> "AUTO"
        "Car (4-Seat)" -> "CAR"
        "Mini Van" -> "MINI_TRUCK"
        "Truck" -> "TRUCK"
        "Open Truck" -> "TRUCK"
        else -> "CAR"
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 12.dp)) {
                Button(
                    onClick = {
                        if (isCreatingBooking || isCalculating) return@Button
                        scope.launch {
                            isCreatingBooking = true
                            errorMessage = null
                            
                            val request = CreateBookingRequest(
                                pickupAddress = CreateAddressData(
                                    address = pickupAddress,
                                    latitude = pickupLat,
                                    longitude = pickupLng,
                                    contactName = "Sender", // Default, can be updated later
                                    contactPhone = ""
                                ),
                                deliveryAddress = CreateAddressData(
                                    address = deliveryAddress,
                                    latitude = deliveryLat,
                                    longitude = deliveryLng,
                                    contactName = "Receiver",
                                    contactPhone = ""
                                ),
                                vehicleType = vehicleTypeApi,
                                paymentMethod = paymentMethodApi,
                                senderName = "Sender",
                                senderPhone = "",
                                receiverName = "Receiver",
                                receiverPhone = ""
                            )
                            
                            BookingApi.createBooking(request)
                                .onSuccess { response ->
                                    val booking = response.data
                                    if (booking != null) {
                                        val totalAmount = estimatedPrice + taxAmount
                                        onContinue(booking.id, totalAmount)
                                    } else {
                                        errorMessage = "Failed to create booking"
                                    }
                                }
                                .onFailure { e ->
                                    errorMessage = e.message ?: "Failed to create booking"
                                }
                            isCreatingBooking = false
                        }
                    },
                    enabled = !isCreatingBooking && !isCalculating && pickupAddress.isNotBlank() && deliveryAddress.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    if (isCreatingBooking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(strings.continueText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlueDark)
                    }
                },
                actions = { IconButton(onClick = {}) { Text("🔔", fontSize = 20.sp) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "<",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    modifier = Modifier.clickable { onBack() }.padding(end = 8.dp)
                )
                Text(strings.requestForRide, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Error message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        error,
                        modifier = Modifier.padding(12.dp),
                        color = Color(0xFFC62828),
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Route section
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(36.dp)) {
                    Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(Color(0xFFE53935)))
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        repeat(5) {
                            Box(modifier = Modifier.width(2.dp).height(5.dp).background(Color(0xFFB0BEC5)))
                            Spacer(modifier = Modifier.height(3.dp))
                        }
                    }
                    Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(Color(0xFF43A047)))
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Column {
                        Text(strings.pickup, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(
                            text = pickupAddress.ifBlank { strings.selectPickupLocation },
                            fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Column {
                        Text(strings.delivery, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(
                            text = deliveryAddress.ifBlank { strings.selectDeliveryLocation },
                            fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vehicle card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F9FA)),
                border = BorderStroke(1.dp, Color(0xFFDCE8E9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(vehicleDisplayName, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        if (distanceKm > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${(distanceKm * 10).toInt().toDouble() / 10.0} km", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                    Image(
                        painter = painterResource(vehicleImageRes),
                        contentDescription = vehicleDisplayName,
                        modifier = Modifier.width(110.dp).height(70.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Charge section
            Text(strings.charge, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(10.dp))
            if (isCalculating || isLoadingVehicles) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PrimaryBlue, strokeWidth = 2.dp)
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(strings.fairPrice, fontSize = 14.sp, color = TextSecondary)
                    Text("RM ${estimatedPrice.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(strings.taxPercent, fontSize = 14.sp, color = TextSecondary)
                    Text("RM ${taxAmount.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFFE0E0E0))
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(strings.totalAmount, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text("RM ${(estimatedPrice + taxAmount).toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Payment method
            Text(strings.selectPaymentMethod, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            PaymentMethodRow(
                iconRes = Res.drawable.payment_cash,
                title = strings.cash,
                subtitle = "",
                isSelected = selectedPayment == "cash"
            ) { selectedPayment = "cash" }
            Spacer(modifier = Modifier.height(10.dp))
            PaymentMethodRow(
                iconRes = Res.drawable.payment_visa,
                title = "Credit / Debit Card",
                subtitle = "",
                isSelected = selectedPayment == "card"
            ) { selectedPayment = "card" }
            Spacer(modifier = Modifier.height(10.dp))
            PaymentMethodRow(
                iconRes = Res.drawable.payment_paypal,
                title = "E-Wallet",
                subtitle = "",
                isSelected = selectedPayment == "wallet"
            ) { selectedPayment = "wallet" }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PaymentMethodRow(
    iconRes: org.jetbrains.compose.resources.DrawableResource,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(BorderStroke(1.dp, Color(0xFFDCE8E9)), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = title,
            modifier = Modifier.size(42.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, fontSize = 12.sp, color = TextSecondary)
            }
        }
        if (isSelected) {
            Box(
                modifier = Modifier.size(20.dp).clip(CircleShape).background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) { Text("✓", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold) }
        }
    }
}
