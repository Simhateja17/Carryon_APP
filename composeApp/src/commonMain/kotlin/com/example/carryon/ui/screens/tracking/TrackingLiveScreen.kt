package com.example.carryon.ui.screens.tracking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.icon_messages
import carryon.composeapp.generated.resources.icon_profile
import carryon.composeapp.generated.resources.icon_search
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.ui.components.MapViewComposable
import com.example.carryon.ui.components.MapMarker
import com.example.carryon.ui.components.MarkerColor
import com.example.carryon.data.model.LatLng
import com.example.carryon.data.model.MapConfig
import com.example.carryon.data.model.RouteResult
import com.example.carryon.data.network.LocationApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingLiveScreen(
    onBack: () -> Unit,
    onCallAgent: () -> Unit = {}
) {
    // Map state
    var mapConfig by remember { mutableStateOf(MapConfig()) }
    var driverLat by remember { mutableStateOf(17.400) }
    var driverLng by remember { mutableStateOf(78.470) }
    var routeResult by remember { mutableStateOf<RouteResult?>(null) }
    var snappedPath by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var etaMinutes by remember { mutableStateOf(12) }

    // Track raw GPS history for snap-to-roads
    val gpsHistory = remember { mutableListOf<LatLng>() }

    // Delivery destination (would come from booking in real app)
    val deliveryLat = 17.440
    val deliveryLng = 78.349

    val scope = rememberCoroutineScope()

    // Load map config
    LaunchedEffect(Unit) {
        LocationApi.getMapConfig().onSuccess { config ->
            mapConfig = config
        }
    }

    // Poll driver position every 8 seconds
    LaunchedEffect(Unit) {
        while (true) {
            // Try to get real position from tracker
            LocationApi.getPosition("driver-1").onSuccess { pos ->
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
            LocationApi.calculateRoute(driverLat, driverLng, deliveryLat, deliveryLng).onSuccess { route ->
                routeResult = route
                etaMinutes = route.duration
            }

            delay(8000)
        }
    }

    val markers = remember(driverLat, driverLng) {
        listOf(
            MapMarker("driver", driverLat, driverLng, "Driver", MarkerColor.BLUE),
            MapMarker("delivery", deliveryLat, deliveryLng, "Delivery", MarkerColor.GREEN)
        )
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
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("☰", fontSize = 22.sp, color = TextPrimary)
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
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                val items = listOf(
                    Pair(Res.drawable.icon_search, "Search"),
                    Pair(Res.drawable.icon_messages, "Messages"),
                    Pair(Res.drawable.icon_home, "Home"),
                    Pair(Res.drawable.icon_profile, "Profile")
                )
                items.forEachIndexed { index, (iconRes, label) ->
                    NavigationBarItem(
                        icon = {
                            Image(
                                painter = painterResource(iconRes),
                                contentDescription = label,
                                modifier = Modifier.size(24.dp),
                                contentScale = ContentScale.Fit
                            )
                        },
                        selected = index == 2,
                        onClick = {},
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = if (index == 2) PrimaryBlueSurface else Color.Transparent
                        )
                    )
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
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
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Track your shipment",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Interactive Map — fills remaining space
            Box(modifier = Modifier.weight(1f)) {
                // Use snapped path if available, otherwise fall back to route geometry
                val displayPath = snappedPath.ifEmpty { routeResult?.geometry }
                MapViewComposable(
                    modifier = Modifier.fillMaxSize(),
                    styleUrl = mapConfig.styleUrl,
                    centerLat = driverLat,
                    centerLng = driverLng,
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
                                "$etaMinutes",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "mins",
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            "Out for delivery",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF90CAF9)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Delivery partner is driving\nsafely to deliver your order",
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
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Image(painter = painterResource(Res.drawable.call_icon), contentDescription = null, modifier = Modifier.size(20.dp), contentScale = ContentScale.Fit)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Call Delivery Agent",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
