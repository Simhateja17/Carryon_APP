package com.company.carryon.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.bike
import carryon.composeapp.generated.resources.car_4_seater
import carryon.composeapp.generated.resources.car_two_seater
import carryon.composeapp.generated.resources.ellipse_4
import carryon.composeapp.generated.resources.home_current_location_icon
import carryon.composeapp.generated.resources.home_delivery_progress_icon
import carryon.composeapp.generated.resources.home_estimated_logistics_icon
import carryon.composeapp.generated.resources.home_recent_delivery_icon
import carryon.composeapp.generated.resources.home_work_icon
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.icon_map
import carryon.composeapp.generated.resources.mini_van
import carryon.composeapp.generated.resources.open_truck
import carryon.composeapp.generated.resources.truck
import com.company.carryon.data.network.BookingApi
import com.company.carryon.data.network.LocationApi
import com.company.carryon.data.network.UserApi
import com.company.carryon.data.network.getLanguage
import com.company.carryon.data.network.saveLanguage
import com.company.carryon.data.model.AutocompleteResult
import com.company.carryon.i18n.LocalStrings
import com.company.carryon.ui.components.LanguageSelectionDialog
import com.company.carryon.ui.components.rememberLocationRequester
import com.company.carryon.ui.theme.BackgroundLight
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.PrimaryBlueDark
import com.company.carryon.ui.theme.PrimaryBlueSurface
import com.company.carryon.ui.theme.TextPrimary
import com.company.carryon.ui.theme.TextSecondary
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private data class VehicleOption(
    val iconRes: DrawableResource,
    val name: String,
    val price: String
)

private val HomeCardBackground = Color(0x33A6D2F3)

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

    var vehicleOptions by remember { mutableStateOf<List<VehicleOption>>(emptyList()) }
    var isLoadingVehicles by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    var pickupSuggestions by remember { mutableStateOf<List<AutocompleteResult>>(emptyList()) }
    var deliverySuggestions by remember { mutableStateOf<List<AutocompleteResult>>(emptyList()) }
    var showPickupSuggestions by remember { mutableStateOf(false) }
    var showDeliverySuggestions by remember { mutableStateOf(false) }
    var isSearchingPickup by remember { mutableStateOf(false) }
    var isSearchingDelivery by remember { mutableStateOf(false) }
    var pickupSearchJob by remember { mutableStateOf<Job?>(null) }
    var deliverySearchJob by remember { mutableStateOf<Job?>(null) }

    fun proceedToBooking() {
        val selectedVehicleName = vehicleOptions.getOrNull(selectedVehicle)?.name ?: "Car (4-Seat)"
        val safePickup = pickupLocation.ifBlank { "Pickup Location" }
        val safeDelivery = deliveryLocation.ifBlank { "Delivery Location" }
        onNavigateToBooking(safePickup, safeDelivery, selectedVehicleName)
    }

    LaunchedEffect(Unit) {
        val savedLang = getLanguage()
        if (savedLang.isNullOrEmpty()) showLanguageModal = true
    }

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

        try {
            withContext(Dispatchers.Default) { BookingApi.getVehicles() }
                .onSuccess { response ->
                    val apiVehicles = response.data.orEmpty()
                    vehicleOptions = if (apiVehicles.isNotEmpty()) {
                        apiVehicles.map { vehicle ->
                            val type = vehicle.type.lowercase()
                            val icon = iconMap[type] ?: Res.drawable.car_4_seater
                            val displayName = when (type) {
                                "bike" -> "Bike"
                                "auto" -> "Car (2-Seat)"
                                "car" -> "Car (4-Seat)"
                                "mini truck", "minitruck" -> "Mini Van"
                                "truck" -> "Truck"
                                else -> vehicle.type
                            }
                            VehicleOption(icon, displayName, "RM ${vehicle.basePrice.toInt()}")
                        }
                    } else defaultVehicles
                }
                .onFailure { vehicleOptions = defaultVehicles }
        } catch (_: Throwable) {
            vehicleOptions = defaultVehicles
        } finally {
            isLoadingVehicles = false
        }
    }

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

    LaunchedEffect(Unit) { requestLocation() }

    fun searchPickup(query: String) {
        pickupSearchJob?.cancel()
        if (query.length < 2) {
            pickupSuggestions = emptyList()
            showPickupSuggestions = false
            isSearchingPickup = false
            return
        }
        pickupSearchJob = scope.launch {
            isSearchingPickup = true
            delay(250)
            LocationApi.autocomplete(query, userLat, userLng)
                .onSuccess { results ->
                    pickupSuggestions = results
                    showPickupSuggestions = results.isNotEmpty()
                }
                .onFailure {
                    pickupSuggestions = emptyList()
                    showPickupSuggestions = false
                }
            isSearchingPickup = false
        }
    }

    fun searchDelivery(query: String) {
        deliverySearchJob?.cancel()
        if (query.length < 2) {
            deliverySuggestions = emptyList()
            showDeliverySuggestions = false
            isSearchingDelivery = false
            return
        }
        deliverySearchJob = scope.launch {
            isSearchingDelivery = true
            delay(250)
            LocationApi.autocomplete(query, userLat, userLng)
                .onSuccess { results ->
                    deliverySuggestions = results
                    showDeliverySuggestions = results.isNotEmpty()
                }
                .onFailure {
                    deliverySuggestions = emptyList()
                    showDeliverySuggestions = false
                }
            isSearchingDelivery = false
        }
    }

    if (showLanguageModal) {
        LanguageSelectionDialog(
            onDismiss = {},
            onLanguageSelected = { langCode ->
                saveLanguage(langCode)
                showLanguageModal = false
                onLanguageChanged(langCode)
                scope.launch { UserApi.updateLanguage(langCode) }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.ellipse_4),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable { onNavigateToProfile() },
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "WELCOME BACK",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Hello, Devansh",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                Text("On", color = PrimaryBlueDark, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { }) {
                    Image(
                        painter = painterResource(Res.drawable.bell_icon),
                        contentDescription = "Notifications",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFE8EEF7),
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                                painter = painterResource(Res.drawable.home_delivery_progress_icon),
                            contentDescription = "Delivery in progress",
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Your delivery is in progress", color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Text("Arriving in approx. 12 mins", color = TextSecondary, fontSize = 11.sp)
                }
                TextButton(onClick = { onNavigateToTracking("active") }, contentPadding = PaddingValues(0.dp)) {
                    Text("Track now  →", color = PrimaryBlue, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text("Send a Package", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.SemiBold, lineHeight = 30.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = pickupLocation,
                onValueChange = {
                    pickupLocation = it
                    searchPickup(it)
                    showDeliverySuggestions = false
                },
                placeholder = {
                    Text(
                        if (isGettingLocation) strings.detectingLocation else "Enter pickup location",
                        color = Color(0xFF90A0B7)
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(Res.drawable.icon_map),
                        contentDescription = "Enter pickup location",
                        modifier = Modifier.size(14.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                trailingIcon = if (isGettingLocation || isSearchingPickup) {
                    {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = PrimaryBlue
                        )
                    }
                } else null,
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE7ECF4),
                    unfocusedContainerColor = Color(0xFFE7ECF4),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = showPickupSuggestions && pickupSuggestions.isNotEmpty(),
                onDismissRequest = { showPickupSuggestions = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                pickupSuggestions.take(6).forEach { place ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(place.title, color = TextPrimary, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                if (place.address.isNotEmpty()) {
                                    Text(place.address, color = TextSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        },
                        onClick = {
                            pickupLocation = place.title
                            showPickupSuggestions = false
                            pickupSuggestions = emptyList()
                        },
                        colors = MenuDefaults.itemColors(textColor = TextPrimary)
                    )
                    HorizontalDivider(color = Color(0xFFECEFF3))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = deliveryLocation,
                onValueChange = {
                    deliveryLocation = it
                    searchDelivery(it)
                    showPickupSuggestions = false
                },
                placeholder = { Text("Where should we deliver?", color = Color(0xFF90A0B7)) },
                leadingIcon = {
                    Image(
                        painter = painterResource(Res.drawable.icon_home),
                        contentDescription = "Where should we deliver",
                        modifier = Modifier.size(14.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                trailingIcon = if (isSearchingDelivery) {
                    {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = PrimaryBlue
                        )
                    }
                } else null,
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE7ECF4),
                    unfocusedContainerColor = Color(0xFFE7ECF4),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = showDeliverySuggestions && deliverySuggestions.isNotEmpty(),
                onDismissRequest = { showDeliverySuggestions = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                deliverySuggestions.take(6).forEach { place ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(place.title, color = TextPrimary, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                if (place.address.isNotEmpty()) {
                                    Text(place.address, color = TextSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        },
                        onClick = {
                            deliveryLocation = place.title
                            showDeliverySuggestions = false
                            deliverySuggestions = emptyList()
                        },
                        colors = MenuDefaults.itemColors(textColor = TextPrimary)
                    )
                    HorizontalDivider(color = Color(0xFFECEFF3))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickLocationChip(
                title = "Current Location",
                selected = true,
                iconRes = Res.drawable.home_current_location_icon,
                onClick = {
                    isGettingLocation = true
                    requestLocation()
                }
            )
            QuickLocationChip(
                title = "Home",
                selected = false,
                iconRes = Res.drawable.icon_home,
                onClick = { pickupLocation = "Home" }
            )
            QuickLocationChip(
                title = "Work",
                selected = false,
                iconRes = Res.drawable.home_work_icon,
                onClick = { deliveryLocation = "Work" }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Vehicle Type", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            TextButton(onClick = { onNavigateToCalculate() }, contentPadding = PaddingValues(0.dp)) {
                Text("View capacity", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isLoadingVehicles) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                vehicleOptions.forEachIndexed { index, vehicle ->
                    VehicleCard(
                        vehicle = vehicle,
                        selected = selectedVehicle == index,
                        onClick = { selectedVehicle = index }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { proceedToBooking() },
            shape = RoundedCornerShape(16.dp),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF6BA2FF), PrimaryBlue)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.23f), modifier = Modifier.size(30.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(Res.drawable.home_estimated_logistics_icon),
                                contentDescription = "Estimated logistics",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ESTIMATED LOGISTICS", color = Color.White.copy(alpha = 0.72f), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                        Text("25 mins · ${vehicleOptions.getOrNull(selectedVehicle)?.price ?: "RM 15"} - RM 150", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Text("→", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text("Recent Deliveries", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(10.dp))

        RecentDeliveryCard(
            title = "Office to Home",
            subtitle = "Last delivered 2 days ago",
            onRepeat = { onNavigateToOrders() }
        )
        Spacer(modifier = Modifier.height(8.dp))
        RecentDeliveryCard(
            title = "Warehouse to Shop",
            subtitle = "Last delivered 5 days ago",
            onRepeat = { onNavigateToOrders() }
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Saved Addresses", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text("⊕", color = PrimaryBlue, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SavedAddressCard(
                title = "Home",
                subtitle = "Bukit Jalil, Kuala Lumpur...",
                iconRes = Res.drawable.icon_home,
                modifier = Modifier.weight(1f)
            )
            SavedAddressCard(
                title = "Work",
                subtitle = "KL Sentral, Kuala Lumpur...",
                iconRes = Res.drawable.home_work_icon,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
private fun QuickLocationChip(
    title: String,
    selected: Boolean,
    iconRes: DrawableResource,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = if (selected) PrimaryBlue else Color(0xFFF0F2F5),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(14.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = title,
                color = if (selected) Color.White else TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun VehicleCard(
    vehicle: VehicleOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) PrimaryBlue else Color.White,
        tonalElevation = if (selected) 2.dp else 0.dp,
        modifier = Modifier
            .width(122.dp)
            .clickable(onClick = onClick)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) PrimaryBlue else Color(0xFFE4E8EE),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selected) Color(0xFF1F3F78) else HomeCardBackground),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(vehicle.iconRes),
                    contentDescription = vehicle.name,
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = vehicle.name,
                color = if (selected) Color.White else TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "From ${vehicle.price.removePrefix("RM ")}",
                color = if (selected) Color.White.copy(alpha = 0.9f) else PrimaryBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun RecentDeliveryCard(title: String, subtitle: String, onRepeat: () -> Unit) {
    Surface(
        color = HomeCardBackground,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(26.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(Res.drawable.home_recent_delivery_icon),
                        contentDescription = "Recent delivery",
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, color = TextSecondary, fontSize = 10.sp)
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFDDE8FF),
                modifier = Modifier.clickable(onClick = onRepeat)
            ) {
                Text(
                    "Repeat",
                    color = PrimaryBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun SavedAddressCard(
    title: String,
    subtitle: String,
    iconRes: DrawableResource,
    modifier: Modifier = Modifier
) {
    Surface(
        color = HomeCardBackground,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, Color(0xFFD8DEE8), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(14.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = TextSecondary, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
