package com.example.carryon.ui.screens.tracking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import carryon.composeapp.generated.resources.call_icon
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import com.example.carryon.ui.components.MapViewComposable
import com.example.carryon.ui.components.MapMarker
import com.example.carryon.ui.components.MarkerColor
import com.example.carryon.data.model.LatLng
import com.example.carryon.data.model.MapConfig
import com.example.carryon.data.model.RouteResult
import com.example.carryon.data.model.Booking
import com.example.carryon.data.network.LocationApi
import com.example.carryon.data.network.BookingApi
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingLiveScreen(
    bookingId: String,
    onBack: () -> Unit,
    onCallAgent: () -> Unit = {}
) {
    val strings = LocalStrings.current
    
    // Booking state
    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Map state
    var mapConfig by remember { mutableStateOf(MapConfig()) }
    var driverLat by remember { mutableStateOf(0.0) }
    var driverLng by remember { mutableStateOf(0.0) }
    var deliveryLat by remember { mutableStateOf(0.0) }
    var deliveryLng by remember { mutableStateOf(0.0) }
    var routeResult by remember { mutableStateOf<RouteResult?>(null) }
    var snappedPath by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var etaMinutes by remember { mutableStateOf(0) }
    var driverId by remember { mutableStateOf("") }

    // Track raw GPS history for snap-to-roads
    val gpsHistory = remember { mutableListOf<LatLng>() }

    // Load booking data
    LaunchedEffect(bookingId) {
        isLoading = true
        errorMessage = null
        
        BookingApi.getBooking(bookingId)
            .onSuccess { response ->
                val loadedBooking = response.data
                booking = loadedBooking
                if (loadedBooking != null) {
                    // Set delivery destination from booking
                    deliveryLat = loadedBooking.deliveryAddress.latitude
                    deliveryLng = loadedBooking.deliveryAddress.longitude
                    
                    // Set initial driver position from booking (or pickup as fallback)
                    driverLat = loadedBooking.driver?.currentLatitude 
                        ?: loadedBooking.pickupAddress.latitude
                    driverLng = loadedBooking.driver?.currentLongitude 
                        ?: loadedBooking.pickupAddress.longitude
                    
                    // Get driver ID for position tracking
                    driverId = loadedBooking.driver?.id ?: ""
                    
                    // Set initial ETA from booking
                    etaMinutes = loadedBooking.eta ?: 0
                } else {
                    errorMessage = "Booking not found"
                }
            }
            .onFailure { e ->
                errorMessage = "Failed to load booking: ${e.message}"
            }
        
        isLoading = false
    }

    // Load map config
    LaunchedEffect(Unit) {
        LocationApi.getMapConfig().onSuccess { config ->
            mapConfig = config
        }
    }

    // Poll driver position every 8 seconds (only if we have a driver)
    LaunchedEffect(driverId, deliveryLat, deliveryLng) {
        if (driverId.isBlank() || deliveryLat == 0.0) return@LaunchedEffect
        
        while (true) {
            // Try to get real position from tracker
            LocationApi.getPosition(driverId).onSuccess { pos ->
                if (pos.latitude != 0.0 && pos.longitude != 0.0) {
                    driverLat = pos.latitude
                    driverLng = pos.longitude
                    gpsHistory.add(LatLng(pos.latitude, pos.longitude))
                }
            }

            // Snap GPS trail to roads for smooth display
            if (gpsHistory.size >= 2) {
                LocationApi.snapToRoads(gpsHistory.toList()).onSuccess { snapped ->
                    if (snapped.isNotEmpty()) {
                        snappedPath = snapped
                    }
                }
            }

            // Recalculate route from driver to delivery
            if (driverLat != 0.0 && deliveryLat != 0.0) {
                LocationApi.calculateRoute(driverLat, driverLng, deliveryLat, deliveryLng).onSuccess { route ->
                    routeResult = route
                    etaMinutes = route.duration
                }
            }

            delay(8000)
        }
    }

    // Also poll ETA from booking API
    LaunchedEffect(bookingId) {
        while (true) {
            delay(15000) // Every 15 seconds
            BookingApi.getEta(bookingId).onSuccess { response ->
                response.data?.let { eta ->
                    etaMinutes = eta.etaMinutes
                }
            }
        }
    }

    val markers = remember(driverLat, driverLng, deliveryLat, deliveryLng) {
        if (driverLat == 0.0 && deliveryLat == 0.0) {
            emptyList()
        } else {
            listOfNotNull(
                if (driverLat != 0.0) MapMarker("driver", driverLat, driverLng, "Driver", MarkerColor.BLUE) else null,
                if (deliveryLat != 0.0) MapMarker("delivery", deliveryLat, deliveryLng, "Delivery", MarkerColor.GREEN) else null
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Carry", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlueDark)
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Title row with back arrow
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "<",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.clickable { onBack() }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            strings.trackYourShipment,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    // Interactive Map — fills remaining space
                    Box(modifier = Modifier.weight(1f)) {
                        // Use snapped path if available, otherwise fall back to route geometry
                        val displayPath = snappedPath.ifEmpty { routeResult?.geometry }
                        val centerLat = if (driverLat != 0.0) driverLat else deliveryLat
                        val centerLng = if (driverLng != 0.0) driverLng else deliveryLng
                        
                        MapViewComposable(
                            modifier = Modifier.fillMaxSize(),
                            styleUrl = mapConfig.styleUrl,
                            centerLat = centerLat,
                            centerLng = centerLng,
                            zoom = 13.0,
                            markers = markers,
                            routeGeometry = displayPath
                        )
                    }

                    // Bottom sheet area
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        // Out for delivery card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(PrimaryBlue)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mins box
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        if (etaMinutes > 0) "$etaMinutes" else "--",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        strings.mins,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    strings.outForDelivery,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF90CAF9)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    if (currentBooking.driver != null) 
                                        "${currentBooking.driver?.name ?: ""} ${strings.deliveryPartnerDriving}"
                                    else 
                                        strings.deliveryPartnerDriving,
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    lineHeight = 19.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Call Delivery Agent button
                        Button(
                            onClick = onCallAgent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            enabled = currentBooking.driver != null
                        ) {
                            Image(painter = painterResource(Res.drawable.call_icon), contentDescription = null, modifier = Modifier.size(20.dp), contentScale = ContentScale.Fit)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                strings.callDeliveryAgent,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
