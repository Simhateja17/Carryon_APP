package com.company.carryon.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.model.Booking
import com.company.carryon.data.network.BookingApi
import com.company.carryon.data.network.SupportApi
import com.company.carryon.data.network.SupportIssueOption
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.ScreenHorizontalPadding
import com.company.carryon.util.formatOrderDisplayId
import kotlinx.coroutines.launch

@Composable
fun SupportChatScreen(
    onBack: () -> Unit,
    onTicketCreated: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var options by remember { mutableStateOf<List<SupportIssueOption>>(emptyList()) }
    var selectedGroup by remember { mutableStateOf<SupportIssueOption?>(null) }
    var selectedIssue by remember { mutableStateOf<SupportIssueOption?>(null) }
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }
    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var showOrderPicker by remember { mutableStateOf(false) }
    var promptedOrderIssueId by remember { mutableStateOf<String?>(null) }
    var orderSearch by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        SupportApi.getIntakeOptions()
            .onSuccess { options = it.data ?: emptyList() }
            .onFailure { error = "Unable to load support options." }
        BookingApi.getBookings()
            .onSuccess { bookings = it.data ?: emptyList() }
    }

    LaunchedEffect(selectedIssue?.id, bookings.size) {
        val issue = selectedIssue
        if (
            issue?.requiresBooking == true &&
            selectedBooking == null &&
            promptedOrderIssueId != issue.id &&
            bookings.isNotEmpty()
        ) {
            promptedOrderIssueId = issue.id
            showOrderPicker = true
        }
    }

    if (showOrderPicker) {
        OrderPickerDialog(
            bookings = bookings,
            query = orderSearch,
            onQueryChange = { orderSearch = it },
            onDismiss = { showOrderPicker = false },
            onSelect = {
                selectedBooking = it
                showOrderPicker = false
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF5F6F8),
        topBar = {
            SupportBotHeader(onBack = onBack)
        },
        bottomBar = {
            if (selectedIssue != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = ScreenHorizontalPadding, vertical = 14.dp)
                ) {
                    Button(
                        enabled = !isSubmitting &&
                            (!selectedIssue!!.requiresDetails || details.trim().length >= 3) &&
                            (!selectedIssue!!.requiresBooking || selectedBooking != null),
                        onClick = {
                            val issue = selectedIssue ?: return@Button
                            isSubmitting = true
                            error = null
                            scope.launch {
                                SupportApi.createIntakeTicket(
                                    issueId = issue.id,
                                    bookingId = selectedBooking?.id,
                                    details = details.trim(),
                                    displayPath = listOfNotNull(selectedGroup?.label, issue.label)
                                ).onSuccess { response ->
                                    val ticketId = response.data?.id
                                    if (ticketId != null) {
                                        onTicketCreated(ticketId)
                                    } else {
                                        error = "Ticket was not created."
                                    }
                                }.onFailure {
                                    error = it.message ?: "Could not raise ticket."
                                }
                                isSubmitting = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text(if (isSubmitting) "Raising ticket..." else "Raise Support Ticket", color = Color.White)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = ScreenHorizontalPadding),
            contentPadding = PaddingValues(vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                BotBubble("I can help raise a support ticket. Select the issue that best matches what is happening.")
            }

            error?.let {
                item { Text(it, color = Color(0xFFB91C1C), fontSize = 13.sp) }
            }

            if (selectedGroup == null) {
                items(options) { option ->
                    OptionRow(option.label) {
                        selectedGroup = option
                        selectedIssue = null
                        selectedBooking = null
                        details = ""
                    }
                }
            } else if (selectedIssue == null) {
                item {
                    SelectedRow(selectedGroup!!.label) {
                        selectedGroup = null
                        selectedIssue = null
                    }
                }
                items(selectedGroup!!.children) { child ->
                    OptionRow(child.label) {
                        selectedIssue = child
                        selectedBooking = null
                        promptedOrderIssueId = null
                        details = ""
                    }
                }
            } else {
                item {
                    SelectedRow(selectedGroup!!.label) {
                        selectedGroup = null
                        selectedIssue = null
                    }
                }
                item {
                    SelectedRow(selectedIssue!!.label) {
                        selectedIssue = null
                    }
                }
                if (selectedIssue!!.requiresBooking) {
                    item {
                        OrderSelectorCard(
                            booking = selectedBooking,
                            hasOrders = bookings.isNotEmpty(),
                            onClick = { showOrderPicker = true }
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text(if (selectedIssue!!.requiresDetails) "Add details required for support..." else "Add optional details...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderPickerDialog(
    bookings: List<Booking>,
    query: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSelect: (Booking) -> Unit
) {
    val normalized = query.trim().lowercase()
    val filtered = remember(bookings, normalized) {
        if (normalized.isBlank()) {
            bookings
        } else {
            bookings
                .map { booking ->
                    val haystack = listOf(
                        booking.id,
                        booking.orderCode.orEmpty(),
                        booking.pickupAddress.address,
                        booking.pickupAddress.label,
                        booking.deliveryAddress.address,
                        booking.deliveryAddress.label,
                        booking.status.name
                    ).joinToString(" ").lowercase()
                    val score = when {
                        haystack.contains(normalized) -> 0
                        booking.orderCode.orEmpty().lowercase().startsWith(normalized) -> 1
                        else -> 2
                    }
                    score to booking
                }
                .filter { it.first < 2 }
                .sortedBy { it.first }
                .map { it.second }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = PrimaryBlue) }
        },
        title = { Text("Select your order", fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Search order ID or location") }
                )
                if (filtered.isEmpty()) {
                    Text("No matching orders found.", color = Color(0xFF64748B), fontSize = 13.sp)
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 360.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filtered.take(20)) { booking ->
                            OrderPickRow(booking = booking, onClick = { onSelect(booking) })
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun OrderSelectorCard(booking: Booking?, hasOrders: Boolean, onClick: () -> Unit) {
    val accent = if (booking == null) PrimaryBlue else Color(0xFF10B981)
    val title = if (booking == null) "Required: select your order" else "Selected order"
    val actionLabel = when {
        !hasOrders -> "No recent orders found"
        booking == null -> "Tap to select order"
        else -> "Change order"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.5.dp, accent, RoundedCornerShape(14.dp))
            .clickable(enabled = hasOrders) { onClick() }
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(accent, CircleShape))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, color = accent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        if (booking == null) {
            Text(
                if (hasOrders) "Choose the related shipment before raising this support ticket." else "We could not find a recent shipment on this account.",
                color = Color(0xFF111827),
                fontSize = 15.sp,
                lineHeight = 21.sp
            )
        } else {
            Text(formatOrderDisplayId(booking.id, booking.orderCode), color = Color(0xFF111827), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text("From: ${booking.pickupAddress.address.ifBlank { booking.pickupAddress.label }}", color = Color(0xFF475569), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("To: ${booking.deliveryAddress.address.ifBlank { booking.deliveryAddress.label }}", color = Color(0xFF475569), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Surface(
            color = if (booking == null) PrimaryBlue else Color(0xFFEFF6FF),
            shape = RoundedCornerShape(999.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    actionLabel,
                    color = if (booking == null) Color.White else PrimaryBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (hasOrders) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("›", color = if (booking == null) Color.White else PrimaryBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun OrderPickRow(booking: Booking, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(formatOrderDisplayId(booking.id, booking.orderCode), color = Color(0xFF111827), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Text("From: ${booking.pickupAddress.address.ifBlank { booking.pickupAddress.label }}", color = Color(0xFF475569), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("To: ${booking.deliveryAddress.address.ifBlank { booking.deliveryAddress.label }}", color = Color(0xFF475569), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(booking.status.name.replace("_", " "), color = PrimaryBlue, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SupportBotHeader(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("‹", color = PrimaryBlue, fontSize = 26.sp, modifier = Modifier.padding(end = 12.dp).clickable { onBack() })
                Column {
                    Text("Support", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AUTOMATION BOT", color = Color(0xB3555881), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                com.company.carryon.ui.components.CarryOnWordmark()
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE7E7EE)))
    }
}

@Composable
private fun BotBubble(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE6F0FA), RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Text(text, color = Color(0xFF111827), fontSize = 16.sp, lineHeight = 24.sp)
    }
}

@Composable
private fun OptionRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), color = Color(0xFF111827), fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text("›", color = PrimaryBlue, fontSize = 24.sp)
    }
}

@Composable
private fun SelectedRow(label: String, onChange: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .background(PrimaryBlue, RoundedCornerShape(18.dp))
                .clickable { onChange() }
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(label, color = Color.White, fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}
