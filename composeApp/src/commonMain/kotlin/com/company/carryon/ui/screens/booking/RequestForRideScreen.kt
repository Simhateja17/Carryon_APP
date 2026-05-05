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
import com.company.carryon.data.network.BookingQuoteRequest
import com.company.carryon.data.network.CreateBookingRequest
import com.company.carryon.data.network.CreateAddressData
import com.company.carryon.data.network.InsufficientBalanceException
import com.company.carryon.data.network.WalletApi
import com.company.carryon.data.network.newUuid
import com.company.carryon.data.payment.StripePaymentLauncher
import com.company.carryon.data.payment.StripePaymentResult
import com.company.carryon.ui.theme.*
import com.company.carryon.i18n.LocalStrings
import com.company.carryon.util.formatDecimal
import kotlinx.coroutines.launch
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
    onContinue: (bookingId: String, amount: Double, paymentMethod: String) -> Unit,
    onBack: () -> Unit
) {
    var selectedPayment by remember { mutableStateOf("wallet") }
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()

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
        "Car (2-Seat)"     -> "CAR"
        "Car (4-Seat)"     -> "CAR"
        "Mini Van"         -> "VAN_7FT"
        "Truck", "Open Truck" -> "PICKUP"
        else               -> "CAR"
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
    var taxAmount by remember { mutableStateOf(0.0) }
    var offloadingFee by remember { mutableStateOf(0.0) }
    var distanceKm by remember { mutableStateOf(0.0) }
    var isCalculating by remember { mutableStateOf(pickupAddress.isNotBlank() && deliveryAddress.isNotBlank()) }
    var isCreatingBooking by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var bookingAttemptKey by remember { mutableStateOf<String?>(null) }

    // Inline top-up state
    var showTopUpSheet by remember { mutableStateOf(false) }
    var topUpShortfall by remember { mutableStateOf(0.0) }
    var topUpCurrency by remember { mutableStateOf("MYR") }
    var walletTopUpMin by remember { mutableStateOf(10.0) }
    var isToppingUp by remember { mutableStateOf(false) }
    var topUpStatus by remember { mutableStateOf<String?>(null) }

    // Backend quote is the only fare authority. The app geocodes only to provide coordinates.
    LaunchedEffect(pickupAddress, deliveryAddress, offloading, vehicleTypeApi, deliveryMode) {
        bookingAttemptKey = null
        if (pickupAddress.isBlank() || deliveryAddress.isBlank()) {
            estimatedPrice = 0.0
            taxAmount = 0.0
            offloadingFee = 0.0
            distanceKm = 0.0
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

            val quoteRequest = BookingQuoteRequest(
                pickupAddress = CreateAddressData(
                    address = pickupAddress,
                    latitude = pickupGeo.lat,
                    longitude = pickupGeo.lng,
                    contactName = senderName,
                    contactPhone = senderPhone
                ),
                deliveryAddress = CreateAddressData(
                    address = deliveryAddress,
                    latitude = deliveryGeo.lat,
                    longitude = deliveryGeo.lng,
                    contactName = receiverName,
                    contactPhone = receiverPhone,
                    contactEmail = receiverEmail
                ),
                vehicleType = vehicleTypeApi,
                deliveryMode = deliveryMode,
                offloading = offloading
            )
            val quoteResult = BookingApi.quoteBooking(quoteRequest)
            val backendQuote = quoteResult.getOrNull()?.data
            if (backendQuote != null && backendQuote.estimatedPrice > 0) {
                val breakdown = backendQuote.breakdown
                taxAmount = breakdown?.tax ?: 0.0
                offloadingFee = breakdown?.offloadingFee ?: 0.0
                estimatedPrice = breakdown?.let {
                    kotlin.math.round((it.total - it.tax - it.offloadingFee) * 100).toDouble() / 100.0
                } ?: backendQuote.estimatedPrice
                distanceKm = backendQuote.distance
            } else {
                estimatedPrice = 0.0
                taxAmount = 0.0
                offloadingFee = 0.0
                distanceKm = 0.0
                geocodeError = quoteResult.exceptionOrNull()?.message ?: "Could not calculate an accurate route price. Please try again."
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
    val subtotal = estimatedPrice + offloadingFee
    val totalAmount = subtotal + taxAmount

    // Booking creation currently requires wallet payment on the backend.
    val paymentMethodApi = "WALLET"

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
                                offloading = offloading
                            )
                            val idempotencyKey = bookingAttemptKey ?: newUuid().also { bookingAttemptKey = it }
                            
                            BookingApi.createBooking(request, idempotencyKey)
                                .onSuccess { response ->
                                    val booking = response.data
                                    if (booking != null) {
                                        onContinue(booking.id, totalAmount, selectedPayment)
                                    } else {
                                        errorMessage = "Failed to create booking"
                                    }
                                }
                                .onFailure { e ->
                                    if (e is InsufficientBalanceException) {
                                        topUpShortfall = e.shortfall
                                        topUpCurrency = e.currency
                                        // Fetch actual minimum so we can show the real top-up amount
                                        scope.launch {
                                            WalletApi.getPaymentConfig().getOrNull()?.data?.let {
                                                walletTopUpMin = it.walletTopUpMin
                                            }
                                        }
                                        showTopUpSheet = true
                                    } else {
                                        errorMessage = e.message ?: "Failed to create booking"
                                    }
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
                actions = {
                    IconButton(onClick = {}) {
                        Image(
                            painter = painterResource(Res.drawable.bell_icon),
                            contentDescription = "Notifications",
                            modifier = Modifier.size(24.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                expandedHeight = 56.dp,
                windowInsets = WindowInsets(0, 0, 0, 0)
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
                    "‹",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
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
                            deliveryMode,
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
                        Text("RM ${offloadingFee.formatDecimal(2)}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
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

    // Inline top-up bottom sheet
    if (showTopUpSheet) {
        val actualTopUp = maxOf(topUpShortfall, walletTopUpMin)
        ModalBottomSheet(
            onDismissRequest = { showTopUpSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    "Insufficient Wallet Balance",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Add $topUpCurrency ${actualTopUp.formatDecimal(2)} to your wallet to confirm this delivery.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                if (actualTopUp > topUpShortfall) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "(RM ${topUpShortfall.formatDecimal(2)} for this delivery + RM ${(actualTopUp - topUpShortfall).formatDecimal(2)} minimum top-up)",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                topUpStatus?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        it,
                        fontSize = 13.sp,
                        color = if (it.contains("completed", ignoreCase = true)) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            isToppingUp = true
                            topUpStatus = "Starting payment..."
                            val config = WalletApi.getPaymentConfig().getOrNull()?.data
                            val topUpAmount = maxOf(topUpShortfall, config?.walletTopUpMin ?: 10.0)
                            val intent = WalletApi.createTopUpIntent(topUpAmount).getOrNull()?.data
                            if (config?.publishableKey.isNullOrBlank() || intent?.clientSecret.isNullOrBlank()) {
                                topUpStatus = "Payment setup unavailable. Please try again."
                                isToppingUp = false
                                return@launch
                            }

                            topUpStatus = "Complete payment in Stripe..."
                            val result = StripePaymentLauncher.presentWalletTopUp(
                                clientSecret = intent.clientSecret,
                                publishableKey = config.publishableKey
                            )

                            when (result) {
                                StripePaymentResult.COMPLETED -> {
                                    topUpStatus = "Top-up completed. Retrying booking..."
                                    // Retry booking automatically
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
                                        offloading = offloading
                                    )
                                    val idempotencyKey = bookingAttemptKey ?: newUuid().also { bookingAttemptKey = it }
                                    BookingApi.createBooking(request, idempotencyKey)
                                        .onSuccess { response ->
                                            val booking = response.data
                                            if (booking != null) {
                                                showTopUpSheet = false
                                                onContinue(booking.id, totalAmount, selectedPayment)
                                            } else {
                                                topUpStatus = "Booking failed after top-up. Please contact support."
                                            }
                                        }
                                        .onFailure { e ->
                                            topUpStatus = e.message ?: "Booking failed after top-up."
                                        }
                                }
                                StripePaymentResult.CANCELED -> {
                                    topUpStatus = "Payment canceled."
                                }
                                StripePaymentResult.FAILED -> {
                                    topUpStatus = "Payment failed. Please try another card."
                                }
                            }
                            isToppingUp = false
                        }
                    },
                    enabled = !isToppingUp,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    if (isToppingUp) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Top Up $topUpCurrency ${actualTopUp.formatDecimal(2)} & Confirm",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { showTopUpSheet = false },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
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
            ) { Text("", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold) }
        }
    }
}
