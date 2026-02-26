package com.example.carryon.ui.screens.booking

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.mask_group
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import com.example.carryon.ui.components.MapViewComposable
import com.example.carryon.ui.components.MapMarker
import com.example.carryon.ui.components.MarkerColor
import com.example.carryon.data.model.IsolineResult
import com.example.carryon.data.model.MapConfig
import com.example.carryon.data.model.RouteResult
import com.example.carryon.data.model.LatLng
import com.example.carryon.data.network.LocationApi
import com.example.carryon.data.network.BookingApi
import com.example.carryon.data.model.Vehicle
import kotlinx.coroutines.launch

data class VehicleOption(
    val id: String,
    val icon: String,
    val name: String,
    val description: String,
    val price: String,
    val priceValue: Double,
    val eta: String,
    val basePrice: Double,
    val pricePerKm: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    pickupAddress: String,
    deliveryAddress: String,
    packageType: String,
    pickupLat: Double = 0.0,
    pickupLng: Double = 0.0,
    deliveryLat: Double = 0.0,
    deliveryLng: Double = 0.0,
    onConfirmBooking: (vehicleType: String, price: Double, paymentMethod: String) -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalStrings.current

    // Vehicle options — populated from API
    var vehicles by remember { mutableStateOf<List<VehicleOption>>(emptyList()) }
    var isLoadingVehicles by remember { mutableStateOf(true) }

    var selectedVehicle by remember { mutableStateOf<VehicleOption?>(null) }
    var paymentType by remember { mutableStateOf("DuitNow") }
    var paidBy by remember { mutableStateOf("Me") }

    // Map & route state
    var mapConfig by remember { mutableStateOf(MapConfig()) }
    var routeResult by remember { mutableStateOf<RouteResult?>(null) }
    var isolineResult by remember { mutableStateOf<IsolineResult?>(null) }
    var isLoadingRoute by remember { mutableStateOf(false) }

    // Coordinates — can be passed in or geocoded
    var currentPickupLat by remember { mutableStateOf(pickupLat) }
    var currentPickupLng by remember { mutableStateOf(pickupLng) }
    var currentDeliveryLat by remember { mutableStateOf(deliveryLat) }
    var currentDeliveryLng by remember { mutableStateOf(deliveryLng) }

    val scope = rememberCoroutineScope()

    // Fetch vehicles from API
    LaunchedEffect(Unit) {
        isLoadingVehicles = true
        BookingApi.getVehicles().onSuccess { response ->
            val apiVehicles = response.data ?: emptyList()
            // Map API vehicles to VehicleOption with icons
            val iconMap = mapOf(
                "bike" to "🏍️",
                "auto" to "🛺",
                "mini truck" to "🚚",
                "minitruck" to "🚚",
                "truck" to "🚛",
                "car" to "🚗"
            )
            vehicles = apiVehicles.map { v ->
                VehicleOption(
                    id = v.id,
                    icon = iconMap[v.name.lowercase()] ?: "📦",
                    name = v.name,
                    description = v.description.ifBlank { v.capacity },
                    price = "—",
                    priceValue = 0.0,
                    eta = "—",
                    basePrice = v.basePrice,
                    pricePerKm = v.pricePerKm
                )
            }
            // Fallback to defaults if API returns empty
            if (vehicles.isEmpty()) {
                vehicles = listOf(
                    VehicleOption("bike", "🏍️", "Bike", strings.upToKg(10), "—", 0.0, "—", 5.0, 1.5),
                    VehicleOption("auto", "🛺", "Auto", strings.upToKg(50), "—", 0.0, "—", 10.0, 2.5),
                    VehicleOption("minitruck", "🚚", "Mini Truck", strings.upToKg(500), "—", 0.0, "—", 30.0, 4.0),
                    VehicleOption("truck", "🚛", "Truck", strings.upToKg(2000), "—", 0.0, "—", 60.0, 7.0)
                )
            }
            selectedVehicle = vehicles.getOrNull(2) ?: vehicles.firstOrNull()
        }.onFailure {
            // Fallback defaults
            vehicles = listOf(
                VehicleOption("bike", "🏍️", "Bike", strings.upToKg(10), "—", 0.0, "—", 5.0, 1.5),
                VehicleOption("auto", "🛺", "Auto", strings.upToKg(50), "—", 0.0, "—", 10.0, 2.5),
                VehicleOption("minitruck", "🚚", "Mini Truck", strings.upToKg(500), "—", 0.0, "—", 30.0, 4.0),
                VehicleOption("truck", "🚛", "Truck", strings.upToKg(2000), "—", 0.0, "—", 60.0, 7.0)
            )
            selectedVehicle = vehicles[2]
        }
        isLoadingVehicles = false
    }

    // Load map config, geocode addresses, calculate route + isoline
    LaunchedEffect(Unit) {
        LocationApi.getMapConfig().onSuccess { config ->
            mapConfig = config
        }

        // Geocode pickup address if coordinates not provided
        if (currentPickupLat == 0.0 && pickupAddress.isNotBlank()) {
            LocationApi.geocode(pickupAddress).onSuccess { place ->
                if (place != null) {
                    currentPickupLat = place.lat
                    currentPickupLng = place.lng
                }
            }
        }
        // Geocode delivery address if coordinates not provided
        if (currentDeliveryLat == 0.0 && deliveryAddress.isNotBlank()) {
            LocationApi.geocode(deliveryAddress).onSuccess { place ->
                if (place != null) {
                    currentDeliveryLat = place.lat
                    currentDeliveryLng = place.lng
                }
            }
        }
    }

    // Calculate route when coordinates are ready
    LaunchedEffect(currentPickupLat, currentPickupLng, currentDeliveryLat, currentDeliveryLng, vehicles) {
        if (currentPickupLat == 0.0 || currentDeliveryLat == 0.0 || vehicles.isEmpty()) return@LaunchedEffect
        
        isLoadingRoute = true
        LocationApi.calculateRoute(currentPickupLat, currentPickupLng, currentDeliveryLat, currentDeliveryLng).onSuccess { route ->
            routeResult = route
            // Update vehicle prices based on route distance using API-provided pricing
            val km = route.distance
            val mins = route.duration
            vehicles = vehicles.mapIndexed { index, v ->
                val calculatedPrice = v.basePrice + (km * v.pricePerKm)
                v.copy(
                    price = strings.rmAmount(calculatedPrice.toInt()),
                    priceValue = calculatedPrice,
                    eta = strings.minDuration(mins + (index * 5))
                )
            }
            selectedVehicle = vehicles.find { it.id == selectedVehicle?.id } ?: vehicles.getOrNull(2) ?: vehicles.firstOrNull()
        }
        // Fetch isoline: reachable within 15 min from pickup
        LocationApi.getIsoline(currentPickupLat, currentPickupLng, 15).onSuccess { iso ->
            isolineResult = iso
        }
        isLoadingRoute = false
    }

    val markers = remember(currentPickupLat, currentPickupLng, currentDeliveryLat, currentDeliveryLng) {
        listOf(
            MapMarker("pickup", currentPickupLat, currentPickupLng, strings.pickup, MarkerColor.BLUE),
            MapMarker("delivery", currentDeliveryLat, currentDeliveryLng, strings.delivery, MarkerColor.GREEN)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("←", fontSize = 24.sp, color = TextPrimary) }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 12.dp).background(SuccessGreen.copy(alpha = 0.15f), RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(SuccessGreen, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.ride, fontSize = 12.sp, color = SuccessGreen)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(BackgroundLight)
        ) {
            // Interactive Map with route and isoline
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                MapViewComposable(
                    modifier = Modifier.fillMaxSize(),
                    styleUrl = mapConfig.styleUrl,
                    centerLat = (currentPickupLat + currentDeliveryLat) / 2,
                    centerLng = (currentPickupLng + currentDeliveryLng) / 2,
                    zoom = 11.0,
                    markers = markers,
                    routeGeometry = routeResult?.geometry,
                    polygonGeometry = isolineResult?.geometry
                )

                if (isLoadingRoute || isLoadingVehicles) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center).size(32.dp),
                        color = PrimaryBlue,
                        strokeWidth = 3.dp
                    )
                }
            }

            // Vehicle Info Card
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                if (selectedVehicle != null) {
                    val currentVehicle = selectedVehicle!!
                    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Vehicle image and name
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(Res.drawable.mask_group),
                                    contentDescription = "Vehicle",
                                    modifier = Modifier.size(64.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(currentVehicle.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(currentVehicle.description, fontSize = 13.sp, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Route info from API
                            if (routeResult != null) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text(strings.distance, fontSize = 12.sp, color = TextSecondary)
                                        Text("${routeResult!!.distance} km", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(strings.duration, fontSize = 12.sp, color = TextSecondary)
                                        Text(strings.minDuration(routeResult!!.duration), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Charge
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(strings.charge, fontSize = 14.sp, color = TextSecondary)
                                Text(currentVehicle.price, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            }
                        }
                    }
                }

                // Address Summary
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(PrimaryBlue, CircleShape))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Pickup", fontSize = 12.sp, color = TextSecondary)
                                Text(pickupAddress.ifBlank { "Pickup Location" }, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            }
                        }
                        Box(modifier = Modifier.padding(start = 4.dp).width(2.dp).height(20.dp).background(Color.LightGray))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(SuccessGreen, CircleShape))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Delivery", fontSize = 12.sp, color = TextSecondary)
                                Text(deliveryAddress.ifBlank { "Delivery Location" }, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Section
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Who pays?
                        Text("Select who pays", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            listOf("Me", "Recipient").forEach { option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { paidBy = option }.padding(end = 20.dp)
                                ) {
                                    RadioButton(
                                        selected = paidBy == option,
                                        onClick = { paidBy = option },
                                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                                    )
                                    Text(option, fontSize = 14.sp, color = TextPrimary)
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))

                        // Payment method chips
                        Text("Payment type", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(10.dp))
                        val paymentMethods = listOf(
                            Pair("💵", "Cash"),
                            Pair("🇲🇾", "DuitNow"),
                            Pair("🟢", "Touch 'n Go"),
                            Pair("🚗", "GrabPay"),
                            Pair("🏦", "FPX"),
                            Pair("💳", "Card")
                        )
                        val rows = paymentMethods.chunked(3)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            rows.forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowItems.forEach { (icon, method) ->
                                        val isSelected = paymentType == method
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) PrimaryBlue else Color(0xFFE0E0E0),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .background(
                                                    color = if (isSelected) PrimaryBlue.copy(alpha = 0.08f) else Color(0xFFF8F8F8),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable { paymentType = method }
                                                .padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(icon, fontSize = 20.sp)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    method,
                                                    fontSize = 10.sp,
                                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                    color = if (isSelected) PrimaryBlue else TextSecondary,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vehicle Selection
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Select Vehicle", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            vehicles.forEach { vehicle ->
                                VehicleChip(vehicle = vehicle, isSelected = selectedVehicle == vehicle, onClick = { selectedVehicle = vehicle })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { 
                        selectedVehicle?.let { vehicle ->
                            // Map UI payment type to API payment method
                            val apiPaymentMethod = when (paymentType) {
                                "Cash" -> "CASH"
                                "DuitNow" -> "DUITNOW"
                                "Card" -> "CARD"
                                else -> "WALLET"
                            }
                            onConfirmBooking(vehicle.name, vehicle.priceValue, apiPaymentMethod)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    enabled = selectedVehicle != null && selectedVehicle!!.priceValue > 0
                ) { Text("Next", fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun VehicleChip(vehicle: VehicleOption, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable { onClick() }.then(if (isSelected) Modifier.background(PrimaryBlueSurface, RoundedCornerShape(12.dp)) else Modifier).padding(8.dp)) {
        Box(modifier = Modifier.size(48.dp).background(if (isSelected) PrimaryBlue else Color(0xFFF5F5F5), CircleShape), contentAlignment = Alignment.Center) {
            Text(vehicle.icon, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(vehicle.name, fontSize = 11.sp, color = if (isSelected) PrimaryBlue else TextSecondary)
        Text(vehicle.price, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (isSelected) PrimaryBlue else TextPrimary)
    }
}
