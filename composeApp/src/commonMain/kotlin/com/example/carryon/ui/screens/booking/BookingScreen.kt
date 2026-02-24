package com.example.carryon.ui.screens.booking

import kotlin.random.Random
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
import kotlinx.coroutines.launch

data class VehicleOption(
    val icon: String,
    val name: String,
    val description: String,
    val price: String,
    val eta: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    pickupAddress: String,
    deliveryAddress: String,
    packageType: String,
    onConfirmBooking: (String) -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalStrings.current

    // Vehicle options — prices update after route is calculated
    var vehicles by remember {
        mutableStateOf(
            listOf(
                VehicleOption("🏍️", "Bike", strings.upToKg(10), "—", "—"),
                VehicleOption("🛺", "Auto", strings.upToKg(50), "—", "—"),
                VehicleOption("🚚", "Mini Truck", strings.upToKg(500), "—", "—"),
                VehicleOption("🚛", "Truck", strings.upToKg(2000), "—", "—")
            )
        )
    }

    var selectedVehicle by remember { mutableStateOf(vehicles[2]) }
    var paymentType by remember { mutableStateOf("DuitNow") }
    var paidBy by remember { mutableStateOf("Me") }

    // Map & route state
    var mapConfig by remember { mutableStateOf(MapConfig()) }
    var routeResult by remember { mutableStateOf<RouteResult?>(null) }
    var isolineResult by remember { mutableStateOf<IsolineResult?>(null) }
    var isLoadingRoute by remember { mutableStateOf(false) }

    // Coordinates — populated by geocoding user's pickup/delivery addresses
    var pickupLat by remember { mutableStateOf(0.0) }
    var pickupLng by remember { mutableStateOf(0.0) }
    var deliveryLat by remember { mutableStateOf(0.0) }
    var deliveryLng by remember { mutableStateOf(0.0) }

    val scope = rememberCoroutineScope()

    // Load map config, geocode addresses, calculate route + isoline
    LaunchedEffect(Unit) {
        LocationApi.getMapConfig().onSuccess { config ->
            mapConfig = config
        }

        // Geocode pickup address if it's not already coordinates
        if (pickupAddress.isNotBlank()) {
            LocationApi.geocode(pickupAddress).onSuccess { place ->
                if (place != null) {
                    pickupLat = place.lat
                    pickupLng = place.lng
                }
            }
        }
        // Geocode delivery address
        if (deliveryAddress.isNotBlank()) {
            LocationApi.geocode(deliveryAddress).onSuccess { place ->
                if (place != null) {
                    deliveryLat = place.lat
                    deliveryLng = place.lng
                }
            }
        }

        isLoadingRoute = true
        LocationApi.calculateRoute(pickupLat, pickupLng, deliveryLat, deliveryLng).onSuccess { route ->
            routeResult = route
            // Update vehicle prices based on route distance
            val km = route.distance
            val mins = route.duration
            vehicles = listOf(
                VehicleOption("🏍️", "Bike", strings.upToKg(10), strings.rmAmount((5 + km * 1.5).toInt()), strings.minDuration(mins)),
                VehicleOption("🛺", "Auto", strings.upToKg(50), strings.rmAmount((10 + km * 2.5).toInt()), strings.minDuration(mins + 5)),
                VehicleOption("🚚", "Mini Truck", strings.upToKg(500), strings.rmAmount((30 + km * 4).toInt()), strings.minDuration(mins + 10)),
                VehicleOption("🚛", "Truck", strings.upToKg(2000), strings.rmAmount((60 + km * 7).toInt()), strings.minDuration(mins + 15))
            )
            selectedVehicle = vehicles[2]
        }
        // Fetch isoline: reachable within 15 min from pickup
        LocationApi.getIsoline(pickupLat, pickupLng, 15).onSuccess { iso ->
            isolineResult = iso
        }
        isLoadingRoute = false
    }

    val markers = remember {
        listOf(
            MapMarker("pickup", pickupLat, pickupLng, strings.pickup, MarkerColor.BLUE),
            MapMarker("delivery", deliveryLat, deliveryLng, strings.delivery, MarkerColor.GREEN)
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
                    centerLat = (pickupLat + deliveryLat) / 2,
                    centerLng = (pickupLng + deliveryLng) / 2,
                    zoom = 11.0,
                    markers = markers,
                    routeGeometry = routeResult?.geometry,
                    polygonGeometry = isolineResult?.geometry
                )

                if (isLoadingRoute) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center).size(32.dp),
                        color = PrimaryBlue,
                        strokeWidth = 3.dp
                    )
                }
            }

            // Vehicle Info Card
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
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
                                Text(selectedVehicle.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(selectedVehicle.description, fontSize = 13.sp, color = TextSecondary)
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
                            Text(selectedVehicle.price, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
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
                    onClick = { onConfirmBooking("BK${Random.nextInt(100000, 999999)}") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
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
