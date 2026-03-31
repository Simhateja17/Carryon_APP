package com.company.carryon.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import carryon.composeapp.generated.resources.location_pin
import carryon.composeapp.generated.resources.to_pin
import carryon.composeapp.generated.resources.ellipse_to
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.payment_icon
import carryon.composeapp.generated.resources.icon_people
import carryon.composeapp.generated.resources.icon_timer
import carryon.composeapp.generated.resources.bell_icon
import org.jetbrains.compose.resources.painterResource
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
import com.company.carryon.i18n.LocalStrings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val strings = LocalStrings.current

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
                        Text("Carry", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlueDark)
                    }
                },
                actions = { IconButton(onClick = {}) { Image(painter = painterResource(Res.drawable.bell_icon), contentDescription = "Notifications", modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = Color.White) {
                Column {
                    Button(
                        onClick = { onNext(vehicleType, from, to) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp).height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) { Text(strings.next, fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color.White)) {
            // Interactive Map with route
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                MapViewComposable(
                    modifier = Modifier.fillMaxSize(),
                    styleUrl = mapConfig.styleUrl,
                    centerLat = centerLat,
                    centerLng = centerLng,
                    zoom = mapZoom,
                    markers = markers,
                    routeGeometry = routeResult?.geometry,
                    onMapClick = { lat, lng ->
                        // Reverse geocode the tapped location
                        scope.launch {
                            LocationApi.reverseGeocode(lat, lng).onSuccess { place ->
                                if (place != null) {
                                    if (isSearchingFrom) {
                                        from = place.label
                                        fromPlace = place
                                        centerLat = place.latitude
                                        centerLng = place.longitude
                                    } else {
                                        to = place.label
                                        toPlace = place
                                        centerLat = place.latitude
                                        centerLng = place.longitude
                                    }
                                    mapZoom = 15.0
                                }
                            }
                        }
                    }
                )
                if (isLoadingRoute) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center).size(32.dp),
                        color = PrimaryBlue,
                        strokeWidth = 3.dp
                    )
                }
            }

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
                        leadingIcon = { Image(painter = painterResource(Res.drawable.location_pin), contentDescription = null, modifier = Modifier.size(22.dp), contentScale = ContentScale.Fit) },
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
                                    Image(painter = painterResource(Res.drawable.to_pin), contentDescription = null, modifier = Modifier.size(18.dp), contentScale = ContentScale.Fit)
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
                                    Image(painter = painterResource(Res.drawable.to_pin), contentDescription = null, modifier = Modifier.size(18.dp), contentScale = ContentScale.Fit)
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
                Spacer(modifier = Modifier.height(8.dp))
                } // end inner Column
            } // end outer Column
        }
    }
}
