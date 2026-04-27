package com.company.carryon.ui.screens.booking

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
import com.company.carryon.data.network.LocationApi
import com.company.carryon.data.network.BookingApi
import com.company.carryon.data.network.CreateBookingRequest
import com.company.carryon.data.network.CreateAddressData
import com.company.carryon.ui.theme.*
import com.company.carryon.i18n.LocalStrings
import com.company.carryon.util.formatDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestForRideScreen(
    vehicleType: String = "",
    pickupAddress: String = "",
    deliveryAddress: String = "",
    senderName: String = "",
    senderPhone: String = "",
    receiverName: String = "",
    receiverPhone: String = "",
    receiverEmail: String = "",
    deliveryMode: String = "Regular",
    offloading: Boolean = false,
    scheduledTime: String? = null,
    onContinue: (bookingId: String, amount: Double) -> Unit,
    onBack: () -> Unit
) {
    var selectedPayment by remember { mutableStateOf("wallet") }
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()

    // Pricing is fully determined by VehiclePricing constants — no API fetch needed
    val pricePerKm by remember(vehicleType, deliveryMode) {
        mutableStateOf(com.company.carryon.data.model.VehiclePricing.ratePerKm(vehicleType, deliveryMode))
    }
    val basePrice = 0.0
    val isLoadingVehicles = false

    // Geocoded coordinates (null = not yet geocoded)
    var pickupLat by remember { mutableStateOf<Double?>(null) }
    var pickupLng by remember { mutableStateOf<Double?>(null) }
    var deliveryLat by remember { mutableStateOf<Double?>(null) }
    var deliveryLng by remember { mutableStateOf<Double?>(null) }
    var geocodeError by remember { mutableStateOf<String?>(null) }

    var estimatedPrice by remember { mutableStateOf(basePrice) }
    var taxAmount by remember { mutableStateOf(kotlin.math.round(basePrice * com.company.carryon.data.model.VehiclePricing.TAX_RATE * 100).toDouble() / 100.0) }
    var distanceKm by remember { mutableStateOf(0.0) }
    var isCalculating by remember { mutableStateOf(pickupAddress.isNotBlank() && deliveryAddress.isNotBlank()) }
    var isCreatingBooking by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Calculate price based on distance
    LaunchedEffect(pickupAddress, deliveryAddress, pricePerKm, offloading) {
        if (pickupAddress.isBlank() || deliveryAddress.isBlank()) {
            estimatedPrice = 0.0
            val subtotal = if (offloading) com.company.carryon.data.model.VehiclePricing.OFFLOADING_FEE else 0.0
            taxAmount = kotlin.math.round(subtotal * com.company.carryon.data.model.VehiclePricing.TAX_RATE * 100).toDouble() / 100.0
            isCalculating = false
            geocodeError = null
            return@LaunchedEffect
        }
        isCalculating = true
        geocodeError = null
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
                val fairPrice = com.company.carryon.data.model.VehiclePricing.calculateBaseFare(
                    vehicleType, deliveryMode, route.distance
                )
                val subtotal = fairPrice + if (offloading) com.company.carryon.data.model.VehiclePricing.OFFLOADING_FEE else 0.0
                estimatedPrice = kotlin.math.round(fairPrice * 100).toDouble() / 100.0
                taxAmount = kotlin.math.round(subtotal * com.company.carryon.data.model.VehiclePricing.TAX_RATE * 100).toDouble() / 100.0
            }
        } else {
            geocodeError = "Could not determine location coordinates. Please check the addresses."
            pickupLat = null
            pickupLng = null
            deliveryLat = null
            deliveryLng = null
        }
        isCalculating = false
    }

    val vehicleImageRes = when (vehicleType) {
        "2 Wheeler"                         -> Res.drawable.bike
        "Car"                               -> Res.drawable.car_4_seater
        "4x4 Pickup"                        -> Res.drawable.truck
        "Van 7ft", "Van 9ft"               -> Res.drawable.mini_van
        "Small Lorry 10ft",
        "Medium Lorry 14ft",
        "Large Lorry 17ft"                  -> Res.drawable.truck
        // Legacy names kept for compatibility
        "Bike"                              -> Res.drawable.bike
        "Car (2-Seat)"                      -> Res.drawable.car_two_seater
        "Car (4-Seat)"                      -> Res.drawable.car_4_seater
        "Mini Van"                          -> Res.drawable.mini_van
        "Truck", "Open Truck"              -> Res.drawable.truck
        else                               -> Res.drawable.car_mustang
    }
    val vehicleDisplayName = vehicleType.ifBlank { "Vehicle" }
    val offloadingFee = if (offloading) com.company.carryon.data.model.VehiclePricing.OFFLOADING_FEE else 0.0
    val subtotal = estimatedPrice + offloadingFee
    val totalAmount = subtotal + taxAmount

    // Map payment selection to API format
    val paymentMethodApi = "WALLET"

    // Map vehicle type to API format
    val vehicleTypeApi = when (vehicleType) {
        "2 Wheeler"        -> "BIKE"
        "Car"              -> "CAR"
        "4x4 Pickup"       -> "PICKUP"
        "Van 7ft"          -> "VAN_7FT"
        "Van 9ft"          -> "VAN_9FT"
        "Small Lorry 10ft" -> "LORRY_10FT"
        "Medium Lorry 14ft"-> "LORRY_14FT"
        "Large Lorry 17ft" -> "LORRY_17FT"
        // Legacy
        "Bike"             -> "BIKE"
        "Car (2-Seat)"     -> "AUTO"
        "Car (4-Seat)"     -> "CAR"
        "Mini Van"         -> "MINI_TRUCK"
        "Truck", "Open Truck" -> "TRUCK"
        else               -> "CAR"
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 12.dp)) {
                Button(
                    onClick = {
                        if (isCreatingBooking || isCalculating) return@Button
                        if (pickupLat == null || pickupLng == null || deliveryLat == null || deliveryLng == null) {
                            errorMessage = "Unable to determine location coordinates. Please go back and re-enter the addresses."
                            return@Button
                        }
                        scope.launch {
                            isCreatingBooking = true
                            errorMessage = null

                            val request = CreateBookingRequest(
                                pickupAddress = CreateAddressData(
                                    address = pickupAddress,
                                    latitude = pickupLat!!,
                                    longitude = pickupLng!!,
                                    contactName = senderName,
                                    contactPhone = senderPhone
                                ),
                                deliveryAddress = CreateAddressData(
                                    address = deliveryAddress,
                                    latitude = deliveryLat!!,
                                    longitude = deliveryLng!!,
                                    contactName = receiverName,
                                    contactPhone = receiverPhone,
                                    contactEmail = receiverEmail
                                ),
                                vehicleType = vehicleTypeApi,
                                paymentMethod = paymentMethodApi,
                                scheduledTime = scheduledTime,
                                senderName = senderName,
                                senderPhone = senderPhone,
                                receiverName = receiverName,
                                receiverPhone = receiverPhone,
                                receiverEmail = receiverEmail,
                                deliveryMode = deliveryMode,
                                estimatedPrice = totalAmount,
                                distance = distanceKm,
                                duration = 0
                            )
                            
                            BookingApi.createBooking(request)
                                .onSuccess { response ->
                                    val booking = response.data
                                    if (booking != null) {
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
                    enabled = !isCreatingBooking && !isCalculating && pickupAddress.isNotBlank() && deliveryAddress.isNotBlank() && pickupLat != null && geocodeError == null,
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
                        Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                        Text("On", color = PrimaryBlueDark, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
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
                .padding(horizontal = ScreenHorizontalPadding)
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

            // Error messages
            val displayError = errorMessage ?: geocodeError
            displayError?.let { error ->
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
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "$deliveryMode · RM ${pricePerKm.formatDecimal(2)}/km",
                            fontSize = 12.sp, color = PrimaryBlue
                        )
                        if (distanceKm > 0) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${(distanceKm * 10).toInt().toDouble() / 10.0} km", fontSize = 12.sp, color = TextSecondary)
                        }
                        if (offloading) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("+ Offloading service", fontSize = 12.sp, color = TextSecondary)
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
                    Text("RM ${estimatedPrice.formatDecimal(2)}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                }
                if (offloading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Offloading", fontSize = 14.sp, color = TextSecondary)
                        Text("RM ${com.company.carryon.data.model.VehiclePricing.OFFLOADING_FEE.formatDecimal(2)}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(strings.taxPercent, fontSize = 14.sp, color = TextSecondary)
                    Text("RM ${taxAmount.formatDecimal(2)}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFFE0E0E0))
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(strings.totalAmount, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text("RM ${totalAmount.formatDecimal(2)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Payment method
            Text(strings.selectPaymentMethod, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            PaymentMethodRow(
                iconRes = Res.drawable.payment_paypal,
                title = "CarryOn Wallet",
                subtitle = "Top up with Stripe before dispatch",
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
