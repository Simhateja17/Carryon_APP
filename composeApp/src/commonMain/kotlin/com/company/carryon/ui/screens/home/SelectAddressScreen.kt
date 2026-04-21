package com.company.carryon.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
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
import carryon.composeapp.generated.resources.location_pin
import carryon.composeapp.generated.resources.to_pin
import carryon.composeapp.generated.resources.ellipse_to
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.payment_icon
import carryon.composeapp.generated.resources.icon_people
import carryon.composeapp.generated.resources.icon_timer
import carryon.composeapp.generated.resources.bike
import carryon.composeapp.generated.resources.car_4_seater
import carryon.composeapp.generated.resources.mini_van
import carryon.composeapp.generated.resources.truck
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.DrawableResource
import com.company.carryon.ui.theme.*
import com.company.carryon.ui.components.MapViewComposable
import com.company.carryon.ui.components.MapMarker
import com.company.carryon.ui.components.MarkerColor
import com.company.carryon.ui.components.rememberLocationRequester
import com.company.carryon.data.model.AutocompleteResult
import com.company.carryon.data.model.MapConfig
import com.company.carryon.data.model.PlaceResult
import com.company.carryon.data.model.RouteResult
import com.company.carryon.data.network.LocationApi
import com.company.carryon.data.network.BookingApi
import com.company.carryon.i18n.LocalStrings
import com.company.carryon.util.formatDecimal
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Place

private data class VehicleChoice(
    val iconRes: DrawableResource,
    val name: String,
    val price: String,
    val description: String,
    val specs: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAddressScreen(
    initialFrom: String = "",
    initialTo: String = "",
    vehicleType: String = "",
    onNext: (vehicleType: String, pickup: String, delivery: String) -> Unit,
    onBack: () -> Unit
) {
    var from by remember { mutableStateOf(initialFrom) }
    var to by remember { mutableStateOf(initialTo) }

    // Map state — centered on user's real location once obtained
    var mapConfig by remember { mutableStateOf(MapConfig()) }
    var centerLat by remember { mutableStateOf(0.0) }
    var centerLng by remember { mutableStateOf(0.0) }
    var mapZoom by remember { mutableStateOf(12.0) }

    // Search state
    var searchResults by remember { mutableStateOf<List<AutocompleteResult>>(emptyList()) }
    var isSearchingFrom by remember { mutableStateOf(true) }
    var showSearchResults by remember { mutableStateOf(false) }
    var fromPlace by remember { mutableStateOf<PlaceResult?>(null) }
    var toPlace by remember { mutableStateOf<PlaceResult?>(null) }
    var routeResult by remember { mutableStateOf<RouteResult?>(null) }
    var isLoadingRoute by remember { mutableStateOf(false) }
    var vehicleOptions by remember { mutableStateOf<List<VehicleChoice>>(emptyList()) }
    var selectedVehicleIndex by remember { mutableStateOf(0) }
    var isLoadingVehicles by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val strings = LocalStrings.current

    fun loadVehicles() {
        scope.launch {
            isLoadingVehicles = true
            val iconMap = mapOf(
                "2 wheeler" to Res.drawable.bike,
                "bike" to Res.drawable.bike,
                "car" to Res.drawable.car_4_seater,
                "auto" to Res.drawable.car_4_seater,
                "4x4 pickup" to Res.drawable.truck,
                "van 7ft" to Res.drawable.mini_van,
                "van 9ft" to Res.drawable.mini_van,
                "small lorry 10ft" to Res.drawable.truck,
                "medium lorry 14ft" to Res.drawable.truck,
                "large lorry 17ft" to Res.drawable.truck,
                "mini truck" to Res.drawable.mini_van,
                "minitruck" to Res.drawable.mini_van,
                "truck" to Res.drawable.truck
            )
            val defaultVehicles = listOf(
                VehicleChoice(Res.drawable.bike, "2 Wheeler", "RM 0.90/km", "Ideal for groceries, food, documents, small parcels", "0.3 x 0.3 x 0.3 Meter · Up to 10 kg"),
                VehicleChoice(Res.drawable.car_4_seater, "Car", "RM 1.17/km", "Ideal for groceries, food, flowers, parcels, fragile goods", "0.5 x 0.5 x 0.5 Meter · Up to 40 kg"),
                VehicleChoice(Res.drawable.truck, "4x4 Pickup", "RM 3.40/km", "Small boxes, small furniture, bicycle", "1.2 x 0.9 x 0.9 Meter · Up to 250 kg"),
                VehicleChoice(Res.drawable.mini_van, "Van 7ft", "RM 5.40/km", "Vanette / GranMax sized loads", "1.7 x 1 x 1.2 Meter · Up to 500 kg"),
                VehicleChoice(Res.drawable.mini_van, "Van 9ft", "RM 6.40/km", "Hiace sized loads", "2.7 x 1.3 x 1.2 Meter · Up to 800 kg"),
                VehicleChoice(Res.drawable.truck, "Small Lorry 10ft", "RM 8.23/km", "Medium household or office move", "3.0 x 1.5 x 1.7 Meter · Up to 1000 kg"),
                VehicleChoice(Res.drawable.truck, "Medium Lorry 14ft", "RM 11.60/km", "Larger household or retail loads", "4.2 x 1.8 x 2.0 Meter · Up to 2000 kg"),
                VehicleChoice(Res.drawable.truck, "Large Lorry 17ft", "RM 15.60/km", "Bulk and heavy transport", "5.2 x 2.0 x 2.2 Meter · Up to 3000 kg")
            )
            BookingApi.getVehicles()
                .onSuccess { response ->
                    vehicleOptions = response.data.orEmpty().map { vehicle ->
                        val key = vehicle.type.lowercase()
                        val displayName = when (key) {
                            "2 wheeler", "bike" -> "2 Wheeler"
                            "car", "auto" -> "Car"
                            "4x4 pickup" -> "4x4 Pickup"
                            "van 7ft" -> "Van 7ft"
                            "van 9ft" -> "Van 9ft"
                            "small lorry 10ft" -> "Small Lorry 10ft"
                            "medium lorry 14ft" -> "Medium Lorry 14ft"
                            "large lorry 17ft" -> "Large Lorry 17ft"
                            "mini truck", "minitruck" -> "Van 7ft"
                            "truck" -> "Small Lorry 10ft"
                            else -> vehicle.type
                        }
                        VehicleChoice(
                            iconRes = iconMap[key] ?: Res.drawable.car_4_seater,
                            name = displayName,
                            price = "RM ${vehicle.pricePerKm.formatDecimal(2)}/km",
                            description = vehicle.description.ifBlank { "Suitable for everyday parcel delivery" },
                            specs = when (displayName) {
                                "2 Wheeler" -> "0.3 x 0.3 x 0.3 Meter · Up to 10 kg"
                                "Car" -> "0.5 x 0.5 x 0.5 Meter · Up to 40 kg"
                                "4x4 Pickup" -> "1.2 x 0.9 x 0.9 Meter · Up to 250 kg"
                                "Van 7ft" -> "1.7 x 1 x 1.2 Meter · Up to 500 kg"
                                "Van 9ft" -> "2.7 x 1.3 x 1.2 Meter · Up to 800 kg"
                                "Small Lorry 10ft" -> "3.0 x 1.5 x 1.7 Meter · Up to 1000 kg"
                                "Medium Lorry 14ft" -> "4.2 x 1.8 x 2.0 Meter · Up to 2000 kg"
                                "Large Lorry 17ft" -> "5.2 x 2.0 x 2.2 Meter · Up to 3000 kg"
                                else -> "Size varies · Capacity based on model"
                            }
                        )
                    }
                    if (vehicleOptions.isEmpty()) vehicleOptions = defaultVehicles
                }
                .onFailure {
                    vehicleOptions = defaultVehicles
                }
            if (vehicleType.isNotBlank()) {
                selectedVehicleIndex = vehicleOptions.indexOfFirst { it.name.equals(vehicleType, ignoreCase = true) }
                    .takeIf { it >= 0 } ?: 0
            } else if (selectedVehicleIndex !in vehicleOptions.indices) {
                selectedVehicleIndex = 0
            }
            isLoadingVehicles = false
        }
    }

    // Use device location to center map and pre-fill "from"
    val requestLocation = rememberLocationRequester(
        onLocation = { lat, lng ->
            centerLat = lat
            centerLng = lng
            mapZoom = 14.0
            scope.launch {
                // Reverse geocode → pre-fill the "from" field
                LocationApi.reverseGeocode(lat, lng).onSuccess { place ->
                    if (place != null && from.isBlank()) {
                        from = place.label.ifEmpty { place.address }
                        fromPlace = PlaceResult(
                            placeId = place.placeId,
                            label = place.label,
                            address = place.address,
                            latitude = lat,
                            longitude = lng
                        )
                    }
                }
            }
        }
    )

    // Load map config, then request location (triggers permission + GPS)
    LaunchedEffect(Unit) {
        loadVehicles()
        LocationApi.getMapConfig().onSuccess { config ->
            mapConfig = config
        }
        requestLocation()
        // Geocode initial addresses passed from HomeScreen
        if (initialFrom.isNotBlank() && fromPlace == null) {
            LocationApi.geocode(initialFrom).onSuccess { geocoded ->
                if (geocoded != null) {
                    fromPlace = PlaceResult(
                        placeId = geocoded.placeId,
                        label = geocoded.title,
                        address = geocoded.address,
                        latitude = geocoded.lat,
                        longitude = geocoded.lng
                    )
                    centerLat = geocoded.lat
                    centerLng = geocoded.lng
                    mapZoom = 14.0
                }
            }
        }
        if (initialTo.isNotBlank() && toPlace == null) {
            LocationApi.geocode(initialTo).onSuccess { geocoded ->
                if (geocoded != null) {
                    toPlace = PlaceResult(
                        placeId = geocoded.placeId,
                        label = geocoded.title,
                        address = geocoded.address,
                        latitude = geocoded.lat,
                        longitude = geocoded.lng
                    )
                }
            }
        }
    }

    // Calculate route whenever both places are set
    LaunchedEffect(fromPlace, toPlace) {
        val from = fromPlace
        val to = toPlace
        if (from != null && to != null) {
            isLoadingRoute = true
            LocationApi.calculateRoute(
                originLat = from.latitude, originLng = from.longitude,
                destLat = to.latitude, destLng = to.longitude
            ).onSuccess { route ->
                routeResult = route
            }.onFailure {
                routeResult = null
            }
            isLoadingRoute = false
        } else {
            routeResult = null
        }
    }

    // Build markers list
    val markers = remember(fromPlace, toPlace) {
        buildList {
            fromPlace?.let {
                add(MapMarker("from", it.latitude, it.longitude, "Pickup", MarkerColor.BLUE))
            }
            toPlace?.let {
                add(MapMarker("to", it.latitude, it.longitude, "Delivery", MarkerColor.GREEN))
            }
        }
    }

    // Debounced autocomplete function
    fun performSearch(query: String) {
        searchJob?.cancel()
        if (query.length < 2) {
            searchResults = emptyList()
            showSearchResults = false
            return
        }
        searchJob = scope.launch {
            delay(300)
            LocationApi.autocomplete(query, centerLat, centerLng)
                .onSuccess { results ->
                    searchResults = results
                    showSearchResults = results.isNotEmpty()
                }
                .onFailure {
                    println("[Autocomplete] SelectAddress error: ${it.message}")
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                        Text("On", color = PrimaryBlueDark, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                    }
                },
                actions = { IconButton(onClick = {}) { Icon(imageVector = Icons.Outlined.NotificationsNone, contentDescription = "Notifications", tint = PrimaryBlue, modifier = Modifier.size(24.dp)) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = Color.White) {
                Column {
                    Button(
                        onClick = {
                            val selectedVehicle = vehicleOptions.getOrNull(selectedVehicleIndex)?.name
                                ?: vehicleType.ifBlank { "Car" }
                            onNext(selectedVehicle, from, to)
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp).height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) { Text(strings.next, fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color.White)) {
            // Bottom sheet
            Column(
                modifier = Modifier.fillMaxWidth().offset(y = (-16).dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color.White)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                // Handle bar
                Box(modifier = Modifier.width(120.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0x802F80ED)).align(Alignment.CenterHorizontally))
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(strings.selectAddress, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(16.dp))

                // From field
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = from,
                        onValueChange = {
                            from = it
                            isSearchingFrom = true
                            performSearch(it)
                        },
                        placeholder = { Text(strings.from, color = PrimaryBlue) },
                        leadingIcon = { Icon(imageVector = Icons.Outlined.Place, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(22.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = PrimaryBlue, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black),
                        singleLine = true
                    )
                    DropdownMenu(
                        expanded = showSearchResults && isSearchingFrom && searchResults.isNotEmpty(),
                        onDismissRequest = { showSearchResults = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        searchResults.take(5).forEach { place ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(place.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary, maxLines = 1)
                                        if (place.address.isNotEmpty()) {
                                            Text(place.address, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                                        }
                                    }
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Outlined.Place, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                                },
                                onClick = {
                                    val selectedTitle = place.title
                                    from = selectedTitle
                                    searchResults = emptyList()
                                    showSearchResults = false
                                    scope.launch {
                                        LocationApi.geocode(placeId = place.placeId).onSuccess { geocoded ->
                                            if (geocoded != null) {
                                                fromPlace = PlaceResult(
                                                    placeId = geocoded.placeId,
                                                    label = geocoded.title,
                                                    address = geocoded.address,
                                                    latitude = geocoded.lat,
                                                    longitude = geocoded.lng
                                                )
                                                centerLat = geocoded.lat
                                                centerLng = geocoded.lng
                                                mapZoom = 15.0
                                            }
                                        }
                                    }
                                }
                            )
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // To field
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = to,
                        onValueChange = {
                            to = it
                            isSearchingFrom = false
                            performSearch(it)
                        },
                        placeholder = { Text(strings.to, color = PrimaryBlue) },
                        leadingIcon = { Image(painter = painterResource(Res.drawable.ellipse_to), contentDescription = null, modifier = Modifier.size(22.dp), contentScale = ContentScale.Fit) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = PrimaryBlue, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black),
                        singleLine = true
                    )
                    DropdownMenu(
                        expanded = showSearchResults && !isSearchingFrom && searchResults.isNotEmpty(),
                        onDismissRequest = { showSearchResults = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        searchResults.take(5).forEach { place ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(place.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary, maxLines = 1)
                                        if (place.address.isNotEmpty()) {
                                            Text(place.address, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                                        }
                                    }
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Outlined.Place, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                                },
                                onClick = {
                                    val selectedTitle = place.title
                                    to = selectedTitle
                                    searchResults = emptyList()
                                    showSearchResults = false
                                    scope.launch {
                                        LocationApi.geocode(placeId = place.placeId).onSuccess { geocoded ->
                                            if (geocoded != null) {
                                                toPlace = PlaceResult(
                                                    placeId = geocoded.placeId,
                                                    label = geocoded.title,
                                                    address = geocoded.address,
                                                    latitude = geocoded.lat,
                                                    longitude = geocoded.lng
                                                )
                                                centerLat = geocoded.lat
                                                centerLng = geocoded.lng
                                                mapZoom = 15.0
                                            }
                                        }
                                    }
                                }
                            )
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text("Vehicle Type", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                if (isLoadingVehicles) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        vehicleOptions.forEachIndexed { index, option ->
                            VehicleTypeCard(
                                vehicle = option,
                                selected = index == selectedVehicleIndex,
                                onClick = { selectedVehicleIndex = index }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                } // end inner Column
            } // end outer Column
        }
    }
}

@Composable
private fun VehicleTypeCard(
    vehicle: VehicleChoice,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) Color(0xFFEAF2FF) else Color(0xFFF7F9FC),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) PrimaryBlue else Color(0xFFE3E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 14.dp)
                .heightIn(min = 148.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 106.dp, height = 88.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) Color(0xFF355D9E) else Color(0xFFEAF1FB)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(vehicle.iconRes),
                    contentDescription = vehicle.name,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(vehicle.name, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Spacer(modifier = Modifier.height(5.dp))
                Text(vehicle.description, color = TextSecondary, fontSize = 14.sp, lineHeight = 19.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(vehicle.specs, color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("From ${vehicle.price.removePrefix("RM ")}", color = PrimaryBlue, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
