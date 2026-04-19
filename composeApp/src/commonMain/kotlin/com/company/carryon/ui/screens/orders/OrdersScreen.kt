package com.company.carryon.ui.screens.orders

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.rectangle_2544
import carryon.composeapp.generated.resources.ellipse_4
import carryon.composeapp.generated.resources.icon_help
import carryon.composeapp.generated.resources.icon_spark
import carryon.composeapp.generated.resources.icon_timer
import carryon.composeapp.generated.resources.icon_profile
import carryon.composeapp.generated.resources.location_pin
import carryon.composeapp.generated.resources.to_pin
import carryon.composeapp.generated.resources.vector_truck
import com.company.carryon.data.model.Booking
import com.company.carryon.data.model.BookingStatus
import com.company.carryon.data.network.BookingApi
import com.company.carryon.ui.theme.ErrorRed
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.PrimaryBlueDark
import com.company.carryon.util.formatOrderDisplayId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource

data class OrderItem(
    val id: String,
    val orderCode: String? = null,
    val date: String,
    val pickup: String,
    val delivery: String,
    val vehicleType: String,
    val price: Double,
    val status: BookingStatus,
    val estimatedMinutes: Int? = null,
    val driverName: String? = null
)

private fun Booking.toOrderItem() = OrderItem(
    id          = id,
    orderCode   = orderCode,
    date        = createdAt.take(10),
    pickup      = pickupAddress.label.ifEmpty { pickupAddress.address },
    delivery    = deliveryAddress.label.ifEmpty { deliveryAddress.address },
    vehicleType = vehicleType,
    price       = if (finalPrice > 0) finalPrice else estimatedPrice,
    status      = status,
    estimatedMinutes = eta,
    driverName  = driver?.name
)

private enum class OrdersTab(val label: String) {
    ALL("All"),
    ONGOING("Ongoing"),
    SCHEDULED("Scheduled"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

private val OrderCardCornerRadius = 20.dp
private val OrderCardHeadingFontSize = 22.sp
private val OrderCardValueFontSize = 18.sp
private val OrderCardSubValueFontSize = 14.sp
private val OrderCardLabelFontSize = 10.sp
private val OrderCardMetaFontSize = 12.sp
private val OrderCardActionFontSize = 14.sp
private val OrderCardStatusFontSize = 12.sp

@Composable
fun OrdersScreen(
    onBack: () -> Unit,
    onOrderClick: (orderId: String) -> Unit,
    onTrackOrder: (orderId: String) -> Unit = onOrderClick
) {
    var selectedTab by remember { mutableStateOf(OrdersTab.ALL) }

    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    suspend fun loadBookings() {
        val result = withContext(Dispatchers.Default) { BookingApi.getBookings() }
        result
            .onSuccess { response ->
                bookings = response.data ?: emptyList()
                error = null
                isLoading = false
            }
            .onFailure {
                error = it.message ?: "Failed to load orders"
                isLoading = false
            }
    }

    LaunchedEffect(Unit) {
        loadBookings()
    }

    val orders = bookings.map { it.toOrderItem() }

    val ongoingOrder = orders.firstOrNull {
        it.status in setOf(
            BookingStatus.SEARCHING_DRIVER,
            BookingStatus.DRIVER_ASSIGNED,
            BookingStatus.DRIVER_ARRIVED,
            BookingStatus.PICKUP_DONE,
            BookingStatus.IN_TRANSIT
        )
    }

    val scheduledOrders = orders.filter {
        it.status == BookingStatus.PENDING || it.status == BookingStatus.SEARCHING_DRIVER
    }

    val completedFromApi = orders.filter { it.status == BookingStatus.DELIVERED }
    val completedCards = completedFromApi.map { order ->
        CompletedOrderPreview(
            order = order,
            dateTime = order.date.ifBlank { "—" },
            amountText = "RM ${order.price.toInt()}",
            statusText = "DELIVERED"
        )
    }

    val cancelledFromApi = orders.filter { it.status == BookingStatus.CANCELLED }
    val cancelledCards = cancelledFromApi.map { order ->
        CancelledOrderPreview(
            order = order,
            dateTime = order.date.ifBlank { "—" },
            reasonTitle = "Cancellation Reason",
            reasonText = "Cancelled",
            statusColor = Color(0xFF64748B),
            statusBgColor = Color(0xFFF1F5F9),
            reasonLeftBorder = false,
            reasonTitleBlue = true,
            actionLabel = "Re-book",
            footerText = "",
            dropIconBg = Color(0xFFA6D2F3),
            dropIconTint = Color(0xFF2F80ED)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FA))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        OrdersHeader()

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x33A6D2F3), RoundedCornerShape(26.dp))
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OrdersTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (isSelected) Color(0xFF2F80ED) else Color.Transparent)
                        .clickable { selectedTab = tab }
                ) {
                    Text(
                        text = tab.label,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        color = if (isSelected) Color.White else Color(0xFF111827),
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }

        if (error != null) {
            RetryBlock(
                message = error ?: "Failed to load orders",
                onRetry = {
                    isLoading = true
                    error = null
                    scope.launch {
                        loadBookings()
                    }
                }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        val showOngoing = selectedTab == OrdersTab.ALL || selectedTab == OrdersTab.ONGOING
        val showScheduled = selectedTab == OrdersTab.ALL || selectedTab == OrdersTab.SCHEDULED
        val showCompleted = selectedTab == OrdersTab.ALL || selectedTab == OrdersTab.COMPLETED
        val showCancelled = selectedTab == OrdersTab.ALL || selectedTab == OrdersTab.CANCELLED

        if (showOngoing) {
            if (ongoingOrder != null) {
                OngoingOrderCard(order = ongoingOrder, onTrack = { onOrderClick(ongoingOrder.id) })
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                EmptyOrdersState("No ongoing deliveries")
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (showScheduled) {
            if (scheduledOrders.isEmpty()) {
                EmptyOrdersState("No scheduled deliveries")
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                scheduledOrders.forEach { scheduled ->
                    ScheduledOrderCard(
                        order = scheduled,
                        onModify = { onOrderClick(scheduled.id) },
                        onCancel = { onOrderClick(scheduled.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        if (showCompleted) {
            if (completedCards.isEmpty()) {
                EmptyOrdersState("No completed deliveries")
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                completedCards.forEach { completed ->
                    CompletedOrderCard(
                        card = completed,
                        onViewDetails = { onOrderClick(completed.order.id) },
                        onRepeat = { onOrderClick(completed.order.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        if (showCancelled) {
            if (cancelledCards.isEmpty()) {
                EmptyOrdersState("No cancelled deliveries")
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                cancelledCards.forEach { cancelled ->
                    CancelledOrderCard(
                        card = cancelled,
                        onAction = { onOrderClick(cancelled.order.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        if (selectedTab != OrdersTab.CANCELLED) {
            ProMembershipBanner()
            Spacer(modifier = Modifier.height(18.dp))
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private data class CancelledOrderPreview(
    val order: OrderItem,
    val dateTime: String,
    val reasonTitle: String,
    val reasonText: String,
    val statusColor: Color,
    val statusBgColor: Color,
    val reasonLeftBorder: Boolean,
    val reasonTitleBlue: Boolean,
    val actionLabel: String,
    val footerText: String,
    val dropIconBg: Color,
    val dropIconTint: Color
)

private data class CompletedOrderPreview(
    val order: OrderItem,
    val dateTime: String,
    val amountText: String,
    val statusText: String
)

private data class OngoingDeliveryPreview(
    val orderId: String,
    val orderCode: String? = null,
    val statusLabel: String,
    val etaMinutes: Int,
    val courierName: String,
    val primaryValue: String,
    val secondaryValue: String,
    val buttonText: String
)

@Composable
private fun CompletedOrdersScreen(
    completedCards: List<CompletedOrderPreview>,
    onViewDetails: (String) -> Unit,
    onRepeat: (String) -> Unit,
    onScheduledClick: () -> Unit,
    onActiveClick: () -> Unit,
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FA))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                color = Color(0xFF2F80ED),
                fontSize = 22.sp,
                modifier = Modifier.clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Orders",
                color = Color(0xFF1D254B),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // 3-tab header: Scheduled | Active | Completed (selected)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x33A6D2F3), RoundedCornerShape(26.dp))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.Transparent)
                    .clickable { onScheduledClick() }
            ) {
                Text(
                    text = "Scheduled",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.Transparent)
                    .clickable { onActiveClick() }
            ) {
                Text(
                    text = "Active",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFF2F80ED))
            ) {
                Text(
                    text = "Completed",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Order History",
            color = Color(0xFF28345E),
            fontWeight = FontWeight.Medium,
            fontSize = 28.sp,
            lineHeight = 34.sp
        )
        Text(
            text = "Reviewing your past deliveries",
            color = Color(0xFF555881),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        completedCards.forEach { completed ->
            CompletedOrderCard(
                card = completed,
                onViewDetails = { onViewDetails(completed.order.id) },
                onRepeat = { onRepeat(completed.order.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        ProMembershipBanner()
        Spacer(modifier = Modifier.height(12.dp))
        UtilitySectionCards()
        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
private fun OngoingDeliveriesScreen(
    ongoingOrders: List<OrderItem>,
    onBack: () -> Unit,
    onOrderClick: (String) -> Unit,
    onHistoryClick: () -> Unit,
    onDraftsClick: () -> Unit
) {
    val cards = ongoingOrders.take(2).mapIndexed { idx, order ->
        OngoingDeliveryPreview(
            orderId = order.id,
            orderCode = order.orderCode,
            statusLabel = if (idx == 0) "IN TRANSIT" else "OUT FOR PICKUP",
            etaMinutes = order.estimatedMinutes ?: 0,
            courierName = order.driverName ?: "",
            primaryValue = order.pickup,
            secondaryValue = order.delivery,
            buttonText = if (idx == 0) "Track Order" else "View Details"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                modifier = Modifier
                    .size(30.dp)
                    .clickable { onBack() },
                color = PrimaryBlue,
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Deliveries",
                color = PrimaryBlue,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(Res.drawable.ellipse_4),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = Color(0xFFE7ECF3))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Scheduled",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.clickable { onDraftsClick() }
            )
            Text(
                text = "Active",
                color = Color(0xFF64748B),
                fontSize = 12.sp,
                modifier = Modifier.clickable { }
            )
            Text(
                text = "Completed",
                color = Color(0xFF64748B),
                fontSize = 12.sp,
                modifier = Modifier.clickable { onHistoryClick() }
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Ongoing",
                    color = PrimaryBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .height(2.dp)
                        .background(PrimaryBlue)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        cards.forEach { card ->
            OngoingDeliveryCard(
                card = card,
                onClick = { onOrderClick(card.orderId) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun OngoingDeliveryCard(
    card: OngoingDeliveryPreview,
    onClick: () -> Unit
) {
    val hasDrop = card.secondaryValue.isNotBlank()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(OrderCardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color(0x33A6D2F3)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0x10A7AAD7))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ORDER ID",
                        color = Color.Black,
                        fontSize = 10.sp,
                        letterSpacing = 0.6.sp
                    )
                    Text(
                        text = formatOrderDisplayId(card.orderId, card.orderCode),
                        color = Color.Black,
                        fontSize = OrderCardHeadingFontSize,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(PrimaryBlue, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = card.statusLabel,
                            color = PrimaryBlue,
                            fontSize = OrderCardLabelFontSize,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.25.sp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (hasDrop) 172.dp else 128.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.rectangle_2544),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .background(Color(0xE6FFFFFF), RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ellipse_4),
                        contentDescription = null,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("COURIER", fontSize = OrderCardLabelFontSize, color = Color(0xFF71749E), fontWeight = FontWeight.Normal)
                        Text(card.courierName, fontSize = OrderCardSubValueFontSize, fontWeight = FontWeight.Medium, color = Color.Black)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("ETA", fontSize = OrderCardLabelFontSize, color = Color(0xFF71749E), fontWeight = FontWeight.Normal)
                        Text("${card.etaMinutes} MINS", fontSize = OrderCardSubValueFontSize, fontWeight = FontWeight.Medium, color = PrimaryBlue)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(if (hasDrop) 82.dp else 38.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .align(Alignment.TopCenter)
                                .background(Color(0x332F80ED), CircleShape)
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(PrimaryBlue, CircleShape)
                            )
                        }

                        if (hasDrop) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(40.dp)
                                    .align(Alignment.Center)
                                    .background(Color(0x667B9CFF))
                            )
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(Color(0x332F80ED), CircleShape)
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF74A3F2), CircleShape)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        if (hasDrop) {
                            Text("PICKUP", color = Color.Black, fontSize = OrderCardLabelFontSize, fontWeight = FontWeight.Medium)
                            Text(card.primaryValue, color = Color.Black, fontSize = OrderCardSubValueFontSize, fontWeight = FontWeight.Normal)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("DROPOFF", color = Color.Black, fontSize = OrderCardLabelFontSize, fontWeight = FontWeight.Medium)
                            Text(card.secondaryValue, color = Color.Black, fontSize = OrderCardSubValueFontSize, fontWeight = FontWeight.Normal)
                        } else {
                            Text("CURRENT GOAL", color = Color.Black, fontSize = OrderCardLabelFontSize, fontWeight = FontWeight.Medium)
                            Text(card.primaryValue, color = Color.Black, fontSize = OrderCardSubValueFontSize, fontWeight = FontWeight.Normal)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(999.dp))
                        .height(48.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(card.buttonText, color = Color.White, fontWeight = FontWeight.Medium, fontSize = OrderCardActionFontSize)
                }
            }
        }
    }
}
@Composable
private fun OrdersHeader(
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Orders",
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1D254B)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.Medium, fontSize = 22.sp)
        Text("On", color = PrimaryBlueDark, fontWeight = FontWeight.Medium, fontSize = 22.sp)
    }
    HorizontalDivider(color = Color(0xFFE9ECF2))
}

@Composable
private fun OngoingOrderCard(order: OrderItem, onTrack: () -> Unit) {
    val statusText = when (order.status) {
        BookingStatus.SEARCHING_DRIVER -> "SEARCHING DRIVER"
        BookingStatus.DRIVER_ASSIGNED -> "DRIVER ASSIGNED"
        BookingStatus.DRIVER_ARRIVED -> "DRIVER ARRIVED"
        BookingStatus.PICKUP_DONE -> "PICKUP DONE"
        BookingStatus.IN_TRANSIT -> "IN TRANSIT"
        else -> order.status.name.replace('_', ' ')
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(OrderCardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF2FC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color(0xFFAED0F3), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("🚚", fontSize = 22.sp) }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("ID: ${formatOrderDisplayId(order.id, order.orderCode)}", color = Color(0xFF8F9BB3), fontSize = OrderCardMetaFontSize, fontWeight = FontWeight.Normal)
                    Text("Ongoing Delivery", color = Color(0xFF28345E), fontWeight = FontWeight.Medium, fontSize = OrderCardHeadingFontSize, maxLines = 1)
                }

                StatusPill(text = statusText, color = PrimaryBlue)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Pickup: ${order.pickup}", color = Color(0xFF4E5B7C), fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Drop: ${order.delivery}", color = Color(0xFF4E5B7C), fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFD7E3F4))
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("ETA", color = Color(0xFF7A88A7), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(if (order.date.isBlank()) "—" else order.date, color = PrimaryBlue, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onTrack,
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text("Track Order", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun ScheduledOrderCard(
    order: OrderItem,
    onModify: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(OrderCardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ORDER ID",
                        color = Color(0xFF64748B),
                        fontSize = OrderCardLabelFontSize,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatOrderDisplayId(order.id, order.orderCode),
                        color = Color(0xFF0F172A),
                        fontSize = OrderCardHeadingFontSize,
                        fontWeight = FontWeight.Medium
                    )
                }
                StatusPill(text = "AWAITING DRIVER", color = PrimaryBlue)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFDCE6F1), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📅", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PICKUP SCHEDULED",
                            color = Color(0xFF64748B),
                            fontSize = OrderCardLabelFontSize,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.6.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = order.date.ifBlank { "Pending schedule" },
                            color = Color(0xFF0F172A),
                            fontSize = OrderCardValueFontSize,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(120.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2f
                        val dash = PathEffect.dashPathEffect(floatArrayOf(7f, 7f), 0f)
                        drawLine(
                            color = Color(0xFF6B9BD4).copy(alpha = 0.55f),
                            start = Offset(cx, 10.dp.toPx()),
                            end = Offset(cx, size.height - 10.dp.toPx()),
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = dash
                        )
                    }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(PrimaryBlue, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(0xFF9EC5EB), CircleShape)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PICKUP",
                        color = Color(0xFF64748B),
                            fontSize = OrderCardLabelFontSize,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order.pickup,
                        color = Color(0xFF0F172A),
                            fontSize = OrderCardSubValueFontSize,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "DROP-OFF",
                        color = Color(0xFF64748B),
                            fontSize = OrderCardLabelFontSize,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order.delivery,
                        color = Color(0xFF0F172A),
                            fontSize = OrderCardSubValueFontSize,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onModify,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED)),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Text("Modify", color = Color.White, fontWeight = FontWeight.Medium)
                }
                OutlinedActionButton(modifier = Modifier.weight(1f), text = "Cancel", onClick = onCancel)
            }
        }
    }
}

@Composable
private fun CompletedOrderCard(
    card: CompletedOrderPreview,
    onViewDetails: () -> Unit,
    onRepeat: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(OrderCardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF2FC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text("ORDER ID", color = Color(0xFF555881), fontSize = OrderCardLabelFontSize, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                    Text(formatOrderDisplayId(card.order.id, card.order.orderCode), color = Color.Black, fontSize = OrderCardHeadingFontSize, fontWeight = FontWeight.Medium)
                }
                Surface(shape = RoundedCornerShape(999.dp), color = Color.White) {
                    Row(
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(13.dp).background(Color(0xFF2F80ED), CircleShape),
                            contentAlignment = Alignment.Center
                        ) { Text("✓", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Medium) }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(card.statusText, color = Color(0xFF2F80ED), fontSize = OrderCardStatusFontSize, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth().height(90.dp)) {
                Column(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(10.dp).background(Color(0xFF2F80ED), CircleShape)
                    )
                    Box(
                        modifier = Modifier.width(1.dp).height(40.dp).background(Color(0x4DA7AAD7))
                    )
                    Box(
                        modifier = Modifier.size(10.dp).background(Color(0xFF2F80ED), CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("PICKUP", color = Color(0xFF555881), fontSize = OrderCardLabelFontSize, fontWeight = FontWeight.Medium)
                        Text(card.order.pickup, color = Color.Black, fontSize = OrderCardSubValueFontSize, fontWeight = FontWeight.Normal)
                    }
                    Column {
                        Text("DROP", color = Color(0xFF555881), fontSize = OrderCardLabelFontSize, fontWeight = FontWeight.Medium)
                        Text(card.order.delivery, color = Color.Black, fontSize = OrderCardSubValueFontSize, fontWeight = FontWeight.Normal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1EFFF))
            Spacer(modifier = Modifier.height(17.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(card.dateTime, color = Color(0xFF555881), fontSize = OrderCardMetaFontSize, fontWeight = FontWeight.Medium)
                    Text(card.amountText, color = Color.Black, fontSize = OrderCardValueFontSize, fontWeight = FontWeight.Medium)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onViewDetails,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFF2F80ED)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("View Details", color = Color(0xFF2F80ED), fontSize = OrderCardActionFontSize, fontWeight = FontWeight.Medium)
                    }
                    Button(
                        onClick = onRepeat,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.shadow(4.dp, RoundedCornerShape(16.dp))
                    ) {
                        Text("Re-book", color = Color.White, fontSize = OrderCardActionFontSize, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
private fun CancelledOrderCard(
    card: CancelledOrderPreview,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(OrderCardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color(0x33A6D2F3)),
        border = BorderStroke(1.dp, Color(0x1AA7AAD7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(25.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = formatOrderDisplayId(card.order.id, card.order.orderCode),
                        color = Color.Black,
                        fontSize = OrderCardHeadingFontSize,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📅", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(card.dateTime, color = Color.Black, fontSize = OrderCardMetaFontSize, fontWeight = FontWeight.Medium)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = card.statusBgColor
                ) {
                    Text(
                        text = "CANCELLED",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = card.statusColor,
                        fontSize = OrderCardLabelFontSize,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(56.dp)
                        .align(Alignment.CenterStart)
                        .padding(start = 11.dp)
                        .background(Color(0xFFE7E6FF))
                )
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFEFF6FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.location_pin),
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                colorFilter = ColorFilter.tint(Color(0xFF2F80ED))
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("PICKUP", color = Color.Black, fontSize = OrderCardLabelFontSize, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
                            Text(card.order.pickup, color = Color.Black, fontSize = OrderCardSubValueFontSize, fontWeight = FontWeight.Normal)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(card.dropIconBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.to_pin),
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                colorFilter = ColorFilter.tint(card.dropIconTint)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("DROP-OFF", color = Color.Black, fontSize = OrderCardLabelFontSize, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
                            Text(card.order.delivery, color = Color.Black, fontSize = OrderCardSubValueFontSize, fontWeight = FontWeight.Normal)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                if (card.reasonLeftBorder) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF2F80ED))
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (card.reasonLeftBorder) {
                        Text("⊗", color = Color(0xFF2F80ED), fontSize = 20.sp, fontWeight = FontWeight.Medium)
                    } else {
                        Image(
                            painter = painterResource(Res.drawable.icon_profile),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            colorFilter = ColorFilter.tint(Color(0xFF2F80ED))
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            card.reasonTitle,
                            color = if (card.reasonTitleBlue) Color(0xFF2F80ED) else Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(card.reasonText, color = Color.Black, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFE7E6FF))
            Spacer(modifier = Modifier.height(9.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (card.footerText.isBlank()) {
                    Image(
                        painter = painterResource(Res.drawable.ellipse_4),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFE2E8F0), CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(card.footerText, color = Color.Black, fontSize = 12.sp, lineHeight = 16.sp)
                }

                Button(
                    onClick = onAction,
                    modifier = Modifier
                        .shadow(10.dp, RoundedCornerShape(999.dp)),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED)),
                    contentPadding = PaddingValues(horizontal = 30.dp, vertical = 10.dp)
                ) {
                    Text(
                        card.actionLabel,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = OrderCardActionFontSize
                    )
                }
            }
        }
    }
}

@Composable
private fun ProMembershipBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF3D7FD4)),
        contentAlignment = Alignment.BottomStart
    ) {
        // Faded truck icon at top
        Image(
            painter = painterResource(Res.drawable.vector_truck),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
                .size(100.dp),
            alpha = 0.14f,
            colorFilter = ColorFilter.tint(Color(0xFF1D4ED8))
        )
        // Bottom-up gradient overlay (transparent top → dark blue bottom)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color(0x000050D4),
                            0.5f to Color(0x660050D4),
                            1f to Color(0xFF0050D4)
                        )
                    )
                )
        )
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Scale your delivery fleet",
                color = Color.White,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Integrate CarryOn into your business workflow\nfor bulk rates.",
                color = Color(0xFFDBEAFE),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { },
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text("CONTACT SALES", color = Color(0xFF2F80ED), fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.6.sp)
            }
        }
    }
}

@Composable
private fun UtilitySectionCards() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(OrderCardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = Color(0x33A6D2F3)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Image(
                    painter = painterResource(Res.drawable.icon_help),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    colorFilter = ColorFilter.tint(Color(0xFF2F80ED))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("24/7 Support", color = Color.Black, fontSize = OrderCardSubValueFontSize, fontWeight = FontWeight.Medium)
                Text("Chat with us now", color = Color.Black, fontSize = 10.sp)
            }
        }
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(OrderCardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = Color(0x33A6D2F3)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Image(
                    painter = painterResource(Res.drawable.icon_spark),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(Color(0xFF2F80ED))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Loyalty Perks", color = Color.Black, fontSize = OrderCardSubValueFontSize, fontWeight = FontWeight.Medium)
                Text("Points: 1,450", color = Color.Black, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun OutlinedActionButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
        border = ButtonDefaults.outlinedButtonBorder
    ) {
        Text(text, color = PrimaryBlue, fontWeight = FontWeight.Normal, fontSize = OrderCardActionFontSize)
    }
}

@Composable
private fun StatusPill(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.White
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            color = color,
            fontWeight = FontWeight.Medium,
            fontSize = OrderCardStatusFontSize
        )
    }
}

@Composable
private fun RetryBlock(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(OrderCardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3F3))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(message, color = ErrorRed, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onRetry) { Text("Retry", color = PrimaryBlue) }
        }
    }
}

@Composable
private fun EmptyOrdersState(message: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFEAF2FC),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            color = Color(0xFF64748B),
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
        )
    }
}
