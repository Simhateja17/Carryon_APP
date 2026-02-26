package com.example.carryon.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
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
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.payment_icon
import carryon.composeapp.generated.resources.icon_people
import carryon.composeapp.generated.resources.icon_timer
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.clip_path_group
import carryon.composeapp.generated.resources.clip_path_group_1
import carryon.composeapp.generated.resources.mask_group
import carryon.composeapp.generated.resources.rectangle_22
import carryon.composeapp.generated.resources.ellipse_4
import carryon.composeapp.generated.resources.bike
import carryon.composeapp.generated.resources.car_4_seater
import carryon.composeapp.generated.resources.car_two_seater
import carryon.composeapp.generated.resources.truck
import carryon.composeapp.generated.resources.mini_van
import carryon.composeapp.generated.resources.open_truck
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.ui.components.rememberLocationRequester
import com.example.carryon.ui.components.LanguageSelectionDialog
import com.example.carryon.i18n.LocalStrings
import com.example.carryon.data.model.AutocompleteResult
import com.example.carryon.data.network.LocationApi
import com.example.carryon.data.network.UserApi
import com.example.carryon.data.network.BookingApi
import com.example.carryon.data.network.getLanguage
import com.example.carryon.data.network.saveLanguage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToBooking: (String, String, String) -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToTracking: (String) -> Unit,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToCalculate: () -> Unit = {},
    onLanguageChanged: (String) -> Unit = {}
) {
    val strings = LocalStrings.current
    var pickupLocation by remember { mutableStateOf("") }
    var deliveryLocation by remember { mutableStateOf("") }
    var selectedVehicle by remember { mutableStateOf(0) }
    var isGettingLocation by remember { mutableStateOf(false) }
    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLng by remember { mutableStateOf<Double?>(null) }
    var showLanguageModal by remember { mutableStateOf(false) }

    // Vehicle pricing from API
    data class VehicleOption(val iconRes: org.jetbrains.compose.resources.DrawableResource, val name: String, val price: String)
    var vehicleOptions by remember { mutableStateOf<List<VehicleOption>>(emptyList()) }
    var isLoadingVehicles by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    // Show language modal if user hasn't selected one yet
    LaunchedEffect(Unit) {
        val savedLang = getLanguage()
        if (savedLang.isNullOrEmpty()) {
            showLanguageModal = true
        }
    }

    // Fetch vehicle pricing from API
    LaunchedEffect(Unit) {
        isLoadingVehicles = true
        val iconMap = mapOf(
            "bike" to Res.drawable.bike,
            "auto" to Res.drawable.car_two_seater,
            "car" to Res.drawable.car_4_seater,
            "mini truck" to Res.drawable.mini_van,
            "minitruck" to Res.drawable.mini_van,
            "truck" to Res.drawable.truck
        )
        val defaultVehicles = listOf(
            VehicleOption(Res.drawable.bike, "Bike", "RM 8"),
            VehicleOption(Res.drawable.car_two_seater, "Car (2-Seat)", "RM 15"),
            VehicleOption(Res.drawable.car_4_seater, "Car (4-Seat)", "RM 20"),
            VehicleOption(Res.drawable.mini_van, "Mini Van", "RM 30"),
            VehicleOption(Res.drawable.truck, "Truck", "RM 45"),
            VehicleOption(Res.drawable.open_truck, "Open Truck", "RM 40")
        )
        BookingApi.getVehicles()
            .onSuccess { response ->
                val apiVehicles = response.data ?: emptyList()
                if (apiVehicles.isNotEmpty()) {
                    vehicleOptions = apiVehicles.map { vehicle ->
                        val icon = iconMap[vehicle.type.lowercase()] ?: Res.drawable.car_4_seater
                        val displayName = when (vehicle.type.lowercase()) {
                            "bike" -> "Bike"
                            "auto" -> "Car (2-Seat)"
                            "car" -> "Car (4-Seat)"
                            "mini truck", "minitruck" -> "Mini Van"
                            "truck" -> "Truck"
                            else -> vehicle.type
                        }
                        VehicleOption(icon, displayName, "RM ${vehicle.basePrice.toInt()}")
                    }
                } else {
                    vehicleOptions = defaultVehicles
                }
            }
            .onFailure {
                vehicleOptions = defaultVehicles
            }
        isLoadingVehicles = false
    }

    // Location permission + GPS fetch → reverse geocode → fill pickup
    val requestLocation = rememberLocationRequester(
        onLocation = { lat, lng ->
            userLat = lat
            userLng = lng
            scope.launch {
                isGettingLocation = true
                LocationApi.reverseGeocode(lat, lng).onSuccess { place ->
                    if (place != null) {
                        pickupLocation = place.label.ifEmpty { place.address }
                    }
                }
                isGettingLocation = false
            }
        },
        onDenied = { isGettingLocation = false }
    )

    // Auto-request location on first open
    LaunchedEffect(Unit) {
        requestLocation()
    }

    // Autocomplete state
    var pickupSuggestions by remember { mutableStateOf<List<AutocompleteResult>>(emptyList()) }
    var deliverySuggestions by remember { mutableStateOf<List<AutocompleteResult>>(emptyList()) }
    var showPickupSuggestions by remember { mutableStateOf(false) }
    var showDeliverySuggestions by remember { mutableStateOf(false) }

    var pickupSearchJob by remember { mutableStateOf<Job?>(null) }
    var deliverySearchJob by remember { mutableStateOf<Job?>(null) }

    fun searchPickup(query: String) {
        pickupSearchJob?.cancel()
        if (query.length < 2) {
            pickupSuggestions = emptyList()
            showPickupSuggestions = false
            return
        }
        pickupSearchJob = scope.launch {
            delay(300)
            val result = LocationApi.autocomplete(query, userLat, userLng)
            result.onSuccess { results ->
                pickupSuggestions = results
                showPickupSuggestions = results.isNotEmpty()
            }.onFailure {
                // API error — keep showing any existing suggestions rather than crashing
                println("[Autocomplete] pickup error: ${it.message}")
            }
        }
    }

    fun searchDelivery(query: String) {
        deliverySearchJob?.cancel()
        if (query.length < 2) {
            deliverySuggestions = emptyList()
            showDeliverySuggestions = false
            return
        }
        deliverySearchJob = scope.launch {
            delay(300)
            val result = LocationApi.autocomplete(query, userLat, userLng)
            result.onSuccess { results ->
                deliverySuggestions = results
                showDeliverySuggestions = results.isNotEmpty()
            }.onFailure {
                println("[Autocomplete] delivery error: ${it.message}")
            }
        }
    }

    if (showLanguageModal) {
        LanguageSelectionDialog(
            onDismiss = { /* don't allow dismiss without selecting */ },
            onLanguageSelected = { langCode ->
                saveLanguage(langCode)
                showLanguageModal = false
                onLanguageChanged(langCode)
                scope.launch {
                    UserApi.updateLanguage(langCode)
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Carry", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlueDark)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Image(painter = painterResource(Res.drawable.bell_icon), contentDescription = "Notifications", modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = Color.White) {
                Column {
                    Button(
                        onClick = {
                            if (pickupLocation.isNotBlank() && deliveryLocation.isNotBlank() && vehicleOptions.isNotEmpty()) {
                                val selectedVehicleName = vehicleOptions.getOrNull(selectedVehicle)?.name ?: "Car (4-Seat)"
                                onNavigateToBooking(pickupLocation, deliveryLocation, selectedVehicleName)
                            }
                        },
                        enabled = pickupLocation.isNotBlank() && deliveryLocation.isNotBlank() && vehicleOptions.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) { Text(strings.next, fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Banner: "We are Ready to Serve" with background image
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(Res.drawable.rectangle_22),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(130.dp),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier.fillMaxWidth().height(130.dp).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(strings.welcome, fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(strings.weAreReadyToServe, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, lineHeight = 26.sp)
                    }
                    Image(
                        painter = painterResource(Res.drawable.ellipse_4),
                        contentDescription = "Profile",
                        modifier = Modifier.size(64.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pickup Location with autocomplete + "use my location"
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(strings.pickupLocation, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                if (isGettingLocation) {
                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = PrimaryBlue)
                } else {
                    TextButton(
                        onClick = {
                            isGettingLocation = true
                            requestLocation()
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("📍 ${strings.useMyLocation}", fontSize = 11.sp, color = PrimaryBlue)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Pickup field + floating dropdown
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = pickupLocation,
                    onValueChange = {
                        pickupLocation = it
                        searchPickup(it)
                        showDeliverySuggestions = false
                    },
                    placeholder = { Text(if (isGettingLocation) strings.detectingLocation else strings.enterPickupAddress, color = Color.Gray) },
                    leadingIcon = { Text("📍", fontSize = 14.sp) },
                    trailingIcon = if (isGettingLocation) {
                        { CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = PrimaryBlue) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Color.LightGray, focusedContainerColor = Color.White, unfocusedContainerColor = Color(0xFFF8F8F8), focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    singleLine = true
                )
                DropdownMenu(
                    expanded = showPickupSuggestions && pickupSuggestions.isNotEmpty(),
                    onDismissRequest = { showPickupSuggestions = false },
                    modifier = Modifier.fillMaxWidth().background(Color.White)
                ) {
                    pickupSuggestions.take(5).forEach { place ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(place.title, fontSize = 13.sp, color = TextPrimary, maxLines = 1)
                                    if (place.address.isNotEmpty()) {
                                        Text(place.address, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                                    }
                                }
                            },
                            leadingIcon = { Text("📍", fontSize = 12.sp) },
                            onClick = {
                                pickupLocation = place.title
                                showPickupSuggestions = false
                                pickupSuggestions = emptyList()
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = TextPrimary,
                                leadingIconColor = TextPrimary
                            )
                        )
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delivery Location with floating dropdown
            Text(strings.deliveryLocation, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = deliveryLocation,
                    onValueChange = {
                        deliveryLocation = it
                        searchDelivery(it)
                        showPickupSuggestions = false
                    },
                    placeholder = { Text(strings.enterDeliveryAddress, color = Color.Gray) },
                    leadingIcon = { Text("○", fontSize = 14.sp, color = SuccessGreen) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Color.LightGray, focusedContainerColor = Color.White, unfocusedContainerColor = Color(0xFFF8F8F8), focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    singleLine = true
                )
                DropdownMenu(
                    expanded = showDeliverySuggestions && deliverySuggestions.isNotEmpty(),
                    onDismissRequest = { showDeliverySuggestions = false },
                    modifier = Modifier.fillMaxWidth().background(Color.White)
                ) {
                    deliverySuggestions.take(5).forEach { place ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(place.title, fontSize = 13.sp, color = TextPrimary, maxLines = 1)
                                    if (place.address.isNotEmpty()) {
                                        Text(place.address, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                                    }
                                }
                            },
                            leadingIcon = { Text("📍", fontSize = 12.sp) },
                            onClick = {
                                deliveryLocation = place.title
                                showDeliverySuggestions = false
                                deliverySuggestions = emptyList()
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = TextPrimary,
                                leadingIconColor = TextPrimary
                            )
                        )
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Vehicle Type
            Text(strings.vehicleType, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(10.dp))
            if (isLoadingVehicles) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp), color = PrimaryBlue, strokeWidth = 3.dp)
                }
            } else {
                Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                     vehicleOptions.forEachIndexed { index, vehicle ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selectedVehicle = index },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedVehicle == index) PrimaryBlueSurface else Color.White
                        ),
                        border = if (selectedVehicle == index) androidx.compose.foundation.BorderStroke(2.dp, PrimaryBlue) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(vehicle.iconRes),
                                contentDescription = vehicle.name,
                                modifier = Modifier.size(72.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(vehicle.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(strings.fastAndReliable, fontSize = 11.sp, color = TextSecondary)
                            }
                            Text(vehicle.price, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Our services
            Text(strings.ourServices, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ServiceCard(imageRes = Res.drawable.clip_path_group_1, title = strings.sameDayDelivery, modifier = Modifier.weight(1f))
                ServiceCard(imageRes = Res.drawable.clip_path_group, title = strings.overnightDelivery, modifier = Modifier.weight(1f))
                ServiceCard(imageRes = Res.drawable.mask_group, title = strings.expressDelivery, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ServiceCard(imageRes: org.jetbrains.compose.resources.DrawableResource, title: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x332F80ED)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = title,
                modifier = Modifier.size(90.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary, lineHeight = 15.sp, modifier = Modifier.padding(horizontal = 2.dp))
    }
}
