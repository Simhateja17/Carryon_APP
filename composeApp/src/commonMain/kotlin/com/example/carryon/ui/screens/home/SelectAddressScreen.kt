package com.example.carryon.ui.screens.home

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
import carryon.composeapp.generated.resources.location_pin
import carryon.composeapp.generated.resources.to_pin
import carryon.composeapp.generated.resources.ellipse_to
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.payment_icon
import carryon.composeapp.generated.resources.icon_people
import carryon.composeapp.generated.resources.icon_timer
import carryon.composeapp.generated.resources.bell_icon
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.ui.components.MapViewComposable
import com.example.carryon.ui.components.MapMarker
import com.example.carryon.ui.components.MarkerColor
import com.example.carryon.ui.components.rememberLocationRequester
import com.example.carryon.data.model.AutocompleteResult
import com.example.carryon.data.model.MapConfig
import com.example.carryon.data.model.NearbyPlace
import com.example.carryon.data.model.PlaceResult
import com.example.carryon.data.model.RouteResult
import com.example.carryon.data.network.LocationApi
import com.example.carryon.i18n.LocalStrings
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
    var nearbyPlaces by remember { mutableStateOf<List<NearbyPlace>>(emptyList()) }
    var isLoadingNearby by remember { mutableStateOf(false) }
    var fromPlace by remember { mutableStateOf<PlaceResult?>(null) }
    var toPlace by remember { mutableStateOf<PlaceResult?>(null) }
    var routeResult by remember { mutableStateOf<RouteResult?>(null) }
    var isLoadingRoute by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val strings = LocalStrings.current

    // Use device location to center map, pre-fill "from", and load real nearby places
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
                // Load nearby places around real location
                isLoadingNearby = true
                LocationApi.searchNearby(lat, lng, radius = 2000).onSuccess { places ->
                    nearbyPlaces = places
                }
                isLoadingNearby = false
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
                                        LocationApi.geocode(place.address.ifEmpty { selectedTitle }).onSuccess { geocoded ->
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
                                        LocationApi.geocode(place.address.ifEmpty { selectedTitle }).onSuccess { geocoded ->
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

                // Nearby places from API
                Text(strings.nearbyPlaces, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))

                if (isLoadingNearby) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PrimaryBlue, strokeWidth = 2.dp)
                    }
                } else if (nearbyPlaces.isEmpty()) {
                    Text(strings.noNearbyPlaces, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    nearbyPlaces.take(6).forEach { place ->
                        val distanceText = if (place.distance >= 1000) {
                            "${(place.distance / 1000).let { "%.1f".format(it) }}km"
                        } else {
                            "${place.distance.toInt()}m"
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                val placeResult = PlaceResult(
                                    placeId = place.placeId,
                                    label = place.title,
                                    address = place.address,
                                    latitude = place.lat,
                                    longitude = place.lng
                                )
                                if (isSearchingFrom) {
                                    from = place.title
                                    fromPlace = placeResult
                                } else {
                                    to = place.title
                                    toPlace = placeResult
                                }
                                centerLat = place.lat
                                centerLng = place.lng
                                mapZoom = 15.0
                            }.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Image(painter = painterResource(Res.drawable.to_pin), contentDescription = null, modifier = Modifier.size(20.dp).padding(top = 2.dp), contentScale = ContentScale.Fit)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(place.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text(place.address, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp, maxLines = 2)
                            }
                            Text(distanceText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                } // end inner Column
            } // end outer Column
        }
    }
}
