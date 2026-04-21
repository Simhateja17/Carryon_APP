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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.bike
import carryon.composeapp.generated.resources.car_4_seater
import carryon.composeapp.generated.resources.car_two_seater
import carryon.composeapp.generated.resources.home_delivery_progress_icon
import carryon.composeapp.generated.resources.home_estimated_logistics_icon
import carryon.composeapp.generated.resources.home_recent_delivery_icon
import carryon.composeapp.generated.resources.home_work_icon
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.icon_map
import carryon.composeapp.generated.resources.mini_van
import carryon.composeapp.generated.resources.truck
import com.company.carryon.data.network.BookingApi
import com.company.carryon.data.network.LocationApi
import com.company.carryon.data.network.UserApi
import com.company.carryon.data.network.getLanguage
import com.company.carryon.data.network.saveLanguage
import com.company.carryon.data.model.AutocompleteResult
import com.company.carryon.data.model.Booking
import com.company.carryon.data.model.BookingStatus
import com.company.carryon.data.model.GeocodedPlace
import com.company.carryon.i18n.LocalStrings
import com.company.carryon.ui.components.LanguageSelectionDialog
import com.company.carryon.ui.components.rememberLocationRequester
import com.company.carryon.ui.theme.BackgroundLight
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.PrimaryBlueDark
import com.company.carryon.util.formatDecimal
import com.company.carryon.ui.theme.PrimaryBlueSurface
import com.company.carryon.ui.theme.TextPrimary
import com.company.carryon.ui.theme.TextSecondary
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    onNavigateToBooking: (String, String) -> Unit,
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
    var isGettingLocation by remember { mutableStateOf(false) }
    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLng by remember { mutableStateOf<Double?>(null) }
    var pickupLat by remember { mutableStateOf<Double?>(null) }
    var pickupLng by remember { mutableStateOf<Double?>(null) }
    var deliveryLat by remember { mutableStateOf<Double?>(null) }
    var deliveryLng by remember { mutableStateOf<Double?>(null) }
    var estimatedRouteMinutes by remember { mutableStateOf<Int?>(null) }
    var showLanguageModal by remember { mutableStateOf(false) }

    var userName by remember { mutableStateOf<String?>(null) }
    var activeBooking by remember { mutableStateOf<Booking?>(null) }
    var recentDeliveredBookings by remember { mutableStateOf<List<Booking>>(emptyList()) }

    val scope = rememberCoroutineScope()

    var pickupSuggestions by remember { mutableStateOf<List<AutocompleteResult>>(emptyList()) }
    var deliverySuggestions by remember { mutableStateOf<List<AutocompleteResult>>(emptyList()) }
    var showPickupSuggestions by remember { mutableStateOf(false) }
    var showDeliverySuggestions by remember { mutableStateOf(false) }
    var deliveryLocationRecognized by remember { mutableStateOf(false) }
    var isSearchingPickup by remember { mutableStateOf(false) }
    var isSearchingDelivery by remember { mutableStateOf(false) }
    var pickupSearchJob by remember { mutableStateOf<Job?>(null) }
    var deliverySearchJob by remember { mutableStateOf<Job?>(null) }
    var showDeliveryRequiredError by remember { mutableStateOf(false) }

    fun proceedToBooking() {
        if (deliveryLocation.isBlank() || !deliveryLocationRecognized) {
            showDeliveryRequiredError = true
            return
        }
        val safePickup = pickupLocation.ifBlank { "Pickup Location" }
        onNavigateToBooking(safePickup, deliveryLocation.trim())
    }

    LaunchedEffect(Unit) {
        val savedLang = getLanguage()
        if (savedLang.isNullOrEmpty()) showLanguageModal = true
    }

    LaunchedEffect(Unit) {
        UserApi.getProfile().onSuccess { profile ->
            userName = profile.name.ifBlank { null }
        }

        BookingApi.getBookings().onSuccess { response ->
            val allBookings = response.data.orEmpty()
            activeBooking = allBookings.firstOrNull {
                it.status in setOf(
                    BookingStatus.PENDING,
                    BookingStatus.SEARCHING_DRIVER,
                    BookingStatus.DRIVER_ASSIGNED,
                    BookingStatus.DRIVER_ARRIVED,
                    BookingStatus.PICKUP_DONE,
                    BookingStatus.IN_TRANSIT
                )
            }
            recentDeliveredBookings = allBookings
                .asSequence()
                .filter { it.status == BookingStatus.DELIVERED }
                .sortedByDescending { it.updatedAt }
                .take(2)
                .toList()
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
                        pickupLat = lat
                        pickupLng = lng
                    }
                }
                isGettingLocation = false
            }
        },
        onDenied = { isGettingLocation = false }
    )

    LaunchedEffect(Unit) { requestLocation() }

    suspend fun geocodeSelection(
        place: AutocompleteResult,
        onResolved: (GeocodedPlace) -> Unit
    ) {
        LocationApi.geocode(placeId = place.placeId)
            .onSuccess { geocoded ->
                if (geocoded != null) onResolved(geocoded)
            }
    }

    LaunchedEffect(pickupLat, pickupLng, deliveryLat, deliveryLng) {
        val fromLat = pickupLat
        val fromLng = pickupLng
        val toLat = deliveryLat
        val toLng = deliveryLng
        if (fromLat == null || fromLng == null || toLat == null || toLng == null) {
            estimatedRouteMinutes = null
            return@LaunchedEffect
        }
        LocationApi.calculateRoute(fromLat, fromLng, toLat, toLng)
            .onSuccess { route -> estimatedRouteMinutes = route.duration.takeIf { it > 0 } }
            .onFailure { estimatedRouteMinutes = null }
    }

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
            Row(
                modifier = Modifier.clickable { onNavigateToProfile() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                Text("On", color = PrimaryBlueDark, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "WELCOME BACK",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Hello, ${userName ?: "—"}",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (activeBooking != null) {
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
                            Text("🚚", fontSize = 13.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Your delivery is in progress", color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Text(
                            activeBooking?.eta?.let { "Arriving in approx. $it mins" } ?: "Driver details updating",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    TextButton(
                        onClick = { activeBooking?.id?.let(onNavigateToTracking) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Track now  →", color = PrimaryBlue, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                    }
                }
            }
        } else {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFE8EEF7),
                tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No active deliveries right now",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
                )
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
                    pickupLat = null
                    pickupLng = null
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
                            scope.launch {
                                geocodeSelection(place) { geocoded ->
                                    pickupLocation = geocoded.title.ifBlank { place.title }
                                    pickupLat = geocoded.lat
                                    pickupLng = geocoded.lng
                                }
                            }
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
                    deliveryLocationRecognized = false
                    deliveryLat = null
                    deliveryLng = null
                    showDeliveryRequiredError = false
                    searchDelivery(it)
                    showPickupSuggestions = false
                },
                placeholder = { Text("Where should we deliver?", color = Color(0xFF000000)) },
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
                    focusedBorderColor = if (showDeliveryRequiredError) Color(0xFFEB5757) else Color.Transparent,
                    unfocusedBorderColor = if (showDeliveryRequiredError) Color(0xFFEB5757) else Color.Transparent,
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
                            deliveryLocationRecognized = true
                            showDeliveryRequiredError = false
                            showDeliverySuggestions = false
                            deliverySuggestions = emptyList()
                            scope.launch {
                                geocodeSelection(place) { geocoded ->
                                    deliveryLocation = geocoded.title.ifBlank { place.title }
                                    deliveryLat = geocoded.lat
                                    deliveryLng = geocoded.lng
                                }
                            }
                        },
                        colors = MenuDefaults.itemColors(textColor = TextPrimary)
                    )
                    HorizontalDivider(color = Color(0xFFECEFF3))
                }
            }
        }

        if (showDeliveryRequiredError) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Select a valid drop location from suggestions to continue",
                color = Color(0xFFEB5757),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { proceedToBooking() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Next", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text("Recent Deliveries", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(10.dp))

        if (recentDeliveredBookings.isEmpty()) {
            Surface(
                color = Color(0xFFEFF2F6),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No recent deliveries",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                )
            }
        } else {
            recentDeliveredBookings.forEachIndexed { index, booking ->
                val pickupTitle = booking.pickupAddress.label.ifBlank { booking.pickupAddress.address }.ifBlank { "Pickup" }
                val deliveryTitle = booking.deliveryAddress.label.ifBlank { booking.deliveryAddress.address }.ifBlank { "Delivery" }
                val deliveredDate = booking.updatedAt.take(10)
                RecentDeliveryCard(
                    title = "$pickupTitle to $deliveryTitle",
                    subtitle = if (deliveredDate.isNotBlank()) "Delivered on $deliveredDate" else "Delivered",
                    onRepeat = { onNavigateToOrders() }
                )
                if (index != recentDeliveredBookings.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
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
                text = "From",
                color = if (selected) Color.White.copy(alpha = 0.9f) else PrimaryBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = vehicle.price.removePrefix("RM "),
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
