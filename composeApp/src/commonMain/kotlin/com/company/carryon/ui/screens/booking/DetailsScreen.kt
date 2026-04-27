package com.company.carryon.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.network.UserApi
import com.company.carryon.ui.components.ContactInfo
import com.company.carryon.ui.components.ContactPickerButton
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.ScreenHorizontalPadding
import com.company.carryon.ui.theme.TextPrimary
import com.company.carryon.ui.theme.TextSecondary
import com.company.carryon.util.formatDecimal
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

private val SectionTint20 = Color(0x33A6D2F3)
private const val DeliveryModePooling = "Pooling"
private const val DeliveryModePriority = "Priority"
private const val DeliveryModeRegular = "Regular"
private val RegularTimeSlots = listOf(
    TimeSlotOption("10 AM - 12 PM", 10),
    TimeSlotOption("12 PM - 2 PM", 12),
    TimeSlotOption("2 PM - 4 PM", 14),
    TimeSlotOption("4 PM - 6 PM", 16),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    vehicleType: String = "",
    pickup: String = "",
    delivery: String = "",
    onContinue: (vehicleType: String, pickup: String, delivery: String, senderName: String, senderPhone: String, receiverName: String, receiverPhone: String, deliveryMode: String, offloading: Boolean, scheduledTime: String?) -> Unit,
    onBack: () -> Unit,
    onContactSelected: (ContactInfo) -> Unit = {}
) {
    val now = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
    val defaultSchedule = remember(now) { defaultRegularSchedule(now) }
    val availableDates: List<LocalDate> = remember(now.date) {
        val startEpochDays = now.date.toEpochDays()
        List(7) { offset -> LocalDate.fromEpochDays(startEpochDays + offset) }
    }

    var deliveryMode by rememberSaveable { mutableStateOf(DeliveryModeRegular) }
    var offloading by rememberSaveable { mutableStateOf(false) }
    var selectedDateIndex by rememberSaveable { mutableStateOf(defaultSchedule.dateOffset.coerceIn(0, availableDates.lastIndex)) }
    var timeSlot by rememberSaveable { mutableStateOf(defaultSchedule.slot.label) }
    var sameDaySlot by rememberSaveable { mutableStateOf("Afternoon") }
    var parcelWeight by rememberSaveable { mutableStateOf("0.0") }
    var parcelType by rememberSaveable { mutableStateOf("Documents") }
    var instructions by rememberSaveable { mutableStateOf("") }
    var receiverName by rememberSaveable { mutableStateOf("") }
    var receiverPhone by rememberSaveable { mutableStateOf("") }
    var showValidationError by rememberSaveable { mutableStateOf(false) }

    var senderName by rememberSaveable { mutableStateOf("") }
    var senderPhone by rememberSaveable { mutableStateOf("") }

    var parcelDropdownExpanded by remember { mutableStateOf(false) }
    var dateDropdownExpanded by remember { mutableStateOf(false) }
    var timeSlotDropdownExpanded by remember { mutableStateOf(false) }
    val parcelTypeOptions = listOf("Documents", "Electronics", "Clothes", "Groceries", "Other")
    val selectedDate = availableDates.getOrElse(selectedDateIndex) { availableDates.first() }
    val selectedTimeSlot = RegularTimeSlots.firstOrNull { it.label == timeSlot } ?: defaultSchedule.slot
    val scheduledTime = remember(deliveryMode, selectedDate, selectedTimeSlot) {
        if (deliveryMode != DeliveryModeRegular) {
            null
        } else {
            selectedDate
                .atTime(selectedTimeSlot.hour, selectedTimeSlot.minute)
                .toInstant(TimeZone.currentSystemDefault())
                .toString()
        }
    }

    val normalizedReceiverName = receiverName.trim()
    val normalizedPhoneDigits = receiverPhone.filter { it.isDigit() }
    val parsedWeight = parcelWeight.toDoubleOrNull()
    val requiresWeight = deliveryMode == DeliveryModeRegular
    val hasValidWeight = !requiresWeight || (parsedWeight != null && parsedWeight > 0.0)
    val hasValidParcelType = parcelType.isNotBlank()
    val hasValidReceiverName = normalizedReceiverName.isNotBlank()
    val hasValidReceiverPhone = normalizedPhoneDigits.length >= 8
    val canContinue = hasValidWeight && hasValidParcelType && hasValidReceiverName && hasValidReceiverPhone
    val vehicleLabel = vehicleType.ifBlank { "Standard Delivery" }
    val vehicleEmoji = vehicleType.toVehicleEmoji()

    LaunchedEffect(Unit) {
        UserApi.getProfile().onSuccess { user ->
            if (senderName.isBlank()) senderName = user.name
            if (senderPhone.isBlank() && user.phone.isNotBlank()) senderPhone = user.phone
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Delivery Details",
                        color = Color(0xFF2563EB),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    Text(
                        text = "‹",
                        color = Color.Black,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable { onBack() }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                expandedHeight = 56.dp,
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 10.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 28.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = if (canContinue) {
                                        listOf(Color(0xFF2563EB), Color(0xFF60A5FA))
                                    } else {
                                        listOf(Color(0xFF9CB3E9), Color(0xFFB8C8EB))
                                    }
                                )
                            )
                            .clickable {
                                if (!canContinue) {
                                    showValidationError = true
                                    return@clickable
                                }
                                onContinue(
                                    vehicleType,
                                    pickup,
                                    delivery,
                                    senderName,
                                    senderPhone,
                                    receiverName,
                                    receiverPhone,
                                    deliveryMode,
                                    offloading,
                                    scheduledTime
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Confirm Booking",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.2.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "→",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    if (showValidationError && !canContinue) {
                        val message = when {
                            !hasValidWeight -> "Enter parcel weight greater than 0 kg."
                            !hasValidReceiverName -> "Enter receiver name."
                            !hasValidReceiverPhone -> "Enter a valid receiver phone number."
                            !hasValidParcelType -> "Select parcel type."
                            else -> "Fill all required details to continue."
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = message,
                            color = Color(0xFFEB5757),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenHorizontalPadding)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            SectionTitle("DELIVERY TIME")
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SectionTint20,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        DeliveryModeChip(DeliveryModePooling, deliveryMode == DeliveryModePooling, modifier = Modifier.weight(1f), selectedBold = true) { deliveryMode = DeliveryModePooling }
                        DeliveryModeChip(DeliveryModePriority, deliveryMode == DeliveryModePriority, modifier = Modifier.weight(1f)) { deliveryMode = DeliveryModePriority }
                        DeliveryModeChip(DeliveryModeRegular, deliveryMode == DeliveryModeRegular, modifier = Modifier.weight(1f)) { deliveryMode = DeliveryModeRegular }
                    }

                    if (deliveryMode == DeliveryModePooling) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = SectionTint20,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .padding(20.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Column {
                                            Text("Pooling Delivery", color = Color(0xFF2F80ED), fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp)
                                            Text("Grouped delivery with flexible timing", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Orders may be grouped to reduce delivery cost", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    } else if (deliveryMode == DeliveryModePriority) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = SectionTint20,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Priority Delivery", color = Color(0xFF2F80ED), fontSize = 20.sp, fontWeight = FontWeight.Medium)
                                        Text(
                                            "Faster dispatch for urgent\nshipments that need priority handling.",
                                            color = Color.Black,
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    } else if (deliveryMode == DeliveryModeRegular) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.weight(1f)) {
                                ScheduleSelectorCard(
                                    title = "SELECT DATE",
                                    value = selectedDate.toReadableLabel(),
                                    onClick = { dateDropdownExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = dateDropdownExpanded,
                                    onDismissRequest = { dateDropdownExpanded = false }
                                ) {
                                    availableDates.forEachIndexed { index: Int, date: LocalDate ->
                                        DropdownMenuItem(
                                            text = { Text(date.toReadableLabel()) },
                                            onClick = {
                                                selectedDateIndex = index
                                                dateDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                ScheduleSelectorCard(
                                    title = "TIME SLOT",
                                    value = timeSlot,
                                    onClick = { timeSlotDropdownExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = timeSlotDropdownExpanded,
                                    onDismissRequest = { timeSlotDropdownExpanded = false }
                                ) {
                                    RegularTimeSlots.forEach { slot ->
                                        DropdownMenuItem(
                                            text = { Text(slot.label) },
                                            onClick = {
                                                timeSlot = slot.label
                                                timeSlotDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (deliveryMode == DeliveryModePriority) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Select Slot", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Surface(shape = RoundedCornerShape(999.dp), color = Color(0xFF2F80ED)) {
                        Text("Today", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    SameDaySlotCard(
                        title = "Morning",
                        time = "10 AM – 12 PM",
                        selected = sameDaySlot == "Morning",
                        modifier = Modifier.weight(1f)
                    ) { sameDaySlot = "Morning" }
                    SameDaySlotCard(
                        title = "Afternoon",
                        time = "12 PM – 2 PM",
                        selected = sameDaySlot == "Afternoon",
                        modifier = Modifier.weight(1f)
                    ) { sameDaySlot = "Afternoon" }
                    SameDaySlotCard(
                        title = "Late Afr.",
                        time = "2 PM – 5 PM",
                        selected = sameDaySlot == "Late Afr.",
                        modifier = Modifier.weight(1f)
                    ) { sameDaySlot = "Late Afr." }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (false && deliveryMode == DeliveryModePooling) {
                SectionTitle("PARCEL DETAILS")
                Spacer(modifier = Modifier.height(8.dp))
                ExpressInputCard(
                    icon = "⚖",
                    label = "Weight (kg)",
                    valueText = parcelWeight
                )
                Spacer(modifier = Modifier.height(12.dp))
                ExpressDropdownCard(
                    icon = "",
                    label = "Parcel Type",
                    valueText = parcelType.ifBlank { "Package" },
                    expanded = parcelDropdownExpanded,
                    onToggle = { parcelDropdownExpanded = !parcelDropdownExpanded },
                    options = parcelTypeOptions,
                    onSelect = {
                        parcelType = it
                        parcelDropdownExpanded = false
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle("INSTRUCTIONS")
                Spacer(modifier = Modifier.height(8.dp))
                ExpressContainerCard {
                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        placeholder = { Text("Add delivery instructions...", color = Color(0x6671749E), fontSize = 16.sp, fontWeight = FontWeight.Medium) },
                        minLines = 4,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle("RECEIVER DETAILS")
                Spacer(modifier = Modifier.height(8.dp))
                ExpressLabeledInputCard(
                    icon = "",
                    placeholder = "Receiver Name",
                    value = receiverName,
                    onValueChange = { receiverName = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
                ExpressLabeledInputCard(
                    icon = "✆",
                    placeholder = "Phone Number",
                    value = receiverPhone,
                    onValueChange = { receiverPhone = it },
                    keyboardType = KeyboardType.Phone
                )
                Spacer(modifier = Modifier.height(12.dp))
                ContactPickerButton { contact ->
                    receiverName = contact.name
                    receiverPhone = contact.phone
                    onContactSelected(contact)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(32.dp),
                            ambientColor = Color(0x40000000),
                            spotColor = Color(0x40000000)
                        )
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF2F80ED))
                        .padding(24.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("STANDARD BIKE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                                Text("ETA: 25 mins", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 28.sp)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("COST", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                            Text("RM 150", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 32.sp)
                        }
                    }
                }
            } else if (false && deliveryMode == DeliveryModePriority) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Select Slot", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Surface(shape = RoundedCornerShape(999.dp), color = Color(0xFF2F80ED)) {
                        Text("Today", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    SameDaySlotCard(
                        title = "Morning",
                        time = "10 AM – 12 PM",
                        selected = sameDaySlot == "Morning",
                        modifier = Modifier.weight(1f)
                    ) { sameDaySlot = "Morning" }
                    SameDaySlotCard(
                        title = "Afternoon",
                        time = "12 PM – 2 PM",
                        selected = sameDaySlot == "Afternoon",
                        modifier = Modifier.weight(1f)
                    ) { sameDaySlot = "Afternoon" }
                    SameDaySlotCard(
                        title = "Late Afr.",
                        time = "2 PM – 5 PM",
                        selected = sameDaySlot == "Late Afr.",
                        modifier = Modifier.weight(1f)
                    ) { sameDaySlot = "Late Afr." }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text("Parcel Details", fontSize = 32.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                val parcelCardHeight = 116.dp
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SectionTint20,
                        modifier = Modifier.weight(1f).height(parcelCardHeight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("WEIGHT (KG)", fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.6.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(parcelWeight, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SectionTint20,
                        modifier = Modifier.weight(1f).height(parcelCardHeight)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = parcelDropdownExpanded,
                            onExpandedChange = { parcelDropdownExpanded = !parcelDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = parcelType.ifBlank { "Package" },
                                onValueChange = {},
                                readOnly = true,
                                singleLine = true,
                                label = { Text("TYPE", fontSize = 12.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = parcelDropdownExpanded) },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            DropdownMenu(expanded = parcelDropdownExpanded, onDismissRequest = { parcelDropdownExpanded = false }) {
                                parcelTypeOptions.forEach {
                                    DropdownMenuItem(text = { Text(it) }, onClick = {
                                        parcelType = it
                                        parcelDropdownExpanded = false
                                    })
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Surface(shape = RoundedCornerShape(16.dp), color = SectionTint20, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("INSTRUCTIONS", fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.6.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = instructions,
                            onValueChange = { instructions = it },
                            placeholder = { Text("Add delivery instructions...", color = Color.Black, fontSize = 14.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text("Receiver Details", fontSize = 32.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                SameDayReceiverRow(icon = "◉", placeholder = "Receiver's Name", value = receiverName, onValueChange = { receiverName = it })
                Spacer(modifier = Modifier.height(12.dp))
                SameDayReceiverRow(icon = "✆", placeholder = "Phone Number", value = receiverPhone, onValueChange = { receiverPhone = it }, keyboardType = KeyboardType.Phone)
                Spacer(modifier = Modifier.height(12.dp))
                ContactPickerButton { contact ->
                    receiverName = contact.name
                    receiverPhone = contact.phone
                    onContactSelected(contact)
                }

                Spacer(modifier = Modifier.height(20.dp))
                Surface(shape = RoundedCornerShape(32.dp), color = SectionTint20, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.size(64.dp)) {
                            Box(contentAlignment = Alignment.Center) { Text(vehicleEmoji, fontSize = 30.sp) }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(vehicleLabel, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                            Text("◕  3–5 hours ETA", fontSize = 12.sp, color = Color(0xFF2F80ED))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("RM 180", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                            Text("EST. TOTAL", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                        }
                    }
                }
            } else {
                SectionCard(title = "PARCEL DETAILS", icon = "◈") {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            SmallLabel("WEIGHT (KG)")
                            OutlinedTextField(
                                value = parcelWeight,
                                onValueChange = { parcelWeight = it },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            SmallLabel("PARCEL TYPE")
                            ExposedDropdownMenuBox(
                                expanded = parcelDropdownExpanded,
                                onExpandedChange = { parcelDropdownExpanded = !parcelDropdownExpanded }
                            ) {
                                OutlinedTextField(
                                    value = parcelType,
                                    onValueChange = {},
                                    readOnly = true,
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = parcelDropdownExpanded)
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black
                                    ),
                                    modifier = Modifier.fillMaxWidth().menuAnchor()
                                )
                                DropdownMenu(
                                    expanded = parcelDropdownExpanded,
                                    onDismissRequest = { parcelDropdownExpanded = false }
                                ) {
                                    parcelTypeOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                parcelType = option
                                                parcelDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                SectionCard(title = "INSTRUCTIONS", icon = "☰") {
                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        placeholder = { Text("Add delivery instructions...", color = Color(0x80000000), fontSize = 16.sp) },
                        minLines = 4,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth().height(102.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                SectionCard(title = "RECEIVER DETAILS", icon = "▣") {
                    OutlinedTextField(
                        value = receiverName,
                        onValueChange = { receiverName = it },
                        placeholder = { Text("Receiver Name", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = receiverPhone,
                        onValueChange = { receiverPhone = it },
                        placeholder = { Text("Phone Number", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium) },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ContactPickerButton { contact ->
                        receiverName = contact.name
                        receiverPhone = contact.phone
                        onContactSelected(contact)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Offloading add-on
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SectionTint20,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Offloading Service",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                            Text(
                                "+RM 30.00 per booking",
                                fontSize = 12.sp,
                                color = Color(0xFF2F80ED)
                            )
                        }
                        // Custom toggle with visible white knob
                        Box(
                            modifier = Modifier
                                .width(44.dp)
                                .height(24.dp)
                                .background(
                                    if (offloading) Color(0xFF2F80ED) else Color(0xFFA6D2F3),
                                    CircleShape
                                )
                                .clickable { offloading = !offloading }
                                .padding(horizontal = 2.dp),
                            contentAlignment = if (offloading) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color.White, CircleShape)
                                    .border(1.dp, Color.White, CircleShape)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = Color(0x330050D4),
                            spotColor = Color(0x330050D4)
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF2F80ED))
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("⚲", color = Color.White, fontSize = 10.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = vehicleType.ifBlank { "VEHICLE" }.uppercase(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Delivery Mode", color = Color(0xFFDBEAFE), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(deliveryMode, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Rate / km",
                                color = Color(0xFFDBEAFE),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            val rate = com.company.carryon.data.model.VehiclePricing.ratePerKm(vehicleType, deliveryMode)
                            Text(
                                "RM ${rate.formatDecimal(2)}/km",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.5).sp,
                                lineHeight = 30.sp
                            )
                            if (offloading) {
                                Text(
                                    "+RM 30 offloading",
                                    color = Color(0xFFDBEAFE),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

private fun String.toVehicleEmoji(): String {
    return when (trim().lowercase()) {
        "bike", "2 wheeler" -> "🚴"
        "car", "auto", "car (2-seat)", "car (4-seat)" -> "🚗"
        "van 7ft", "van 9ft", "van_7ft", "van_9ft", "mini van", "mini truck", "minitruck" -> "🚐"
        "4x4 pickup", "pickup", "small lorry 10ft", "medium lorry 14ft", "large lorry 17ft", "lorry_10ft", "lorry_14ft", "lorry_17ft", "truck", "open truck" -> "🚚"
        else -> "🚚"
    }
}

private data class TimeSlotOption(
    val label: String,
    val hour: Int,
    val minute: Int = 0
)

private data class DefaultRegularSchedule(
    val dateOffset: Int,
    val slot: TimeSlotOption
)

private fun defaultRegularSchedule(now: LocalDateTime): DefaultRegularSchedule {
    val currentMinutes = now.hour * 60 + now.minute
    val sameDaySlot = RegularTimeSlots.firstOrNull { slot ->
        (slot.hour * 60 + slot.minute) > currentMinutes
    }
    return if (sameDaySlot != null) {
        DefaultRegularSchedule(dateOffset = 0, slot = sameDaySlot)
    } else {
        DefaultRegularSchedule(dateOffset = 1, slot = RegularTimeSlots.first())
    }
}

private fun LocalDate.toReadableLabel(): String {
    val month = when (monthNumber) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        else -> "Dec"
    }
    return "$month $dayOfMonth, $year"
}

@Composable
private fun ScheduleSelectorCard(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(title, color = Color(0xFF2F80ED), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, color = Color(0xFF282B51), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color.Black,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.7.sp,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    )
}

@Composable
private fun SmallLabel(text: String) {
    Text(
        text = text,
        color = Color.Black,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun DeliveryModeChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    selectedBold: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (selected) Color(0xFF2F80ED) else Color.White,
        shadowElevation = if (selected) 3.dp else 0.dp,
        modifier = modifier.height(34.dp).clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = text,
                color = if (selected) Color.White else Color.Black,
                fontSize = 13.sp,
                fontWeight = if (selected && selectedBold) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = SectionTint20,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, color = PrimaryBlue, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    title,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.7.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SameDaySlotCard(
    title: String,
    time: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color(0xFF2F80ED) else SectionTint20,
        shadowElevation = if (selected) 6.dp else 0.dp,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
            Text(title, fontSize = 12.sp, color = if (selected) Color(0xFFDBEAFE) else Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(time, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (selected) Color.White else Color.Black)
        }
    }
}

@Composable
private fun SameDayReceiverRow(
    icon: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Surface(shape = RoundedCornerShape(16.dp), color = SectionTint20, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, fontSize = 14.sp, color = Color.Black) },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ExpressContainerCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SectionTint20,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(21.dp), content = content)
    }
}

@Composable
private fun ExpressInputCard(
    icon: String,
    label: String,
    valueText: String
) {
    ExpressContainerCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon.isNotBlank()) {
                Text(icon, color = Color.Black, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(label, color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            valueText,
            color = Color(0x6671749E),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpressDropdownCard(
    icon: String,
    label: String,
    valueText: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    ExpressContainerCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, color = Color.Black, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { onToggle() }) {
            OutlinedTextField(
                value = valueText,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onToggle,
                containerColor = SectionTint20
            ) {
                options.forEach {
                    DropdownMenuItem(
                        text = { Text(it, color = Color.Black) },
                        onClick = { onSelect(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpressLabeledInputCard(
    icon: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    ExpressContainerCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon.isNotBlank()) {
                Text(icon, color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(16.dp))
            }
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, color = Color(0xFF6B7280), fontSize = 16.sp, fontWeight = FontWeight.SemiBold) },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
