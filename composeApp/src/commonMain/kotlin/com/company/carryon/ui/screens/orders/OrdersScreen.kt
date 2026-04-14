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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

data class OrderItem(
    val id: String,
    val date: String,
    val pickup: String,
    val delivery: String,
    val vehicleType: String,
    val price: Double,
    val status: BookingStatus
)

private fun Booking.toOrderItem() = OrderItem(
    id          = id,
    date        = createdAt.take(10),
    pickup      = pickupAddress.label.ifEmpty { pickupAddress.address },
    delivery    = deliveryAddress.label.ifEmpty { deliveryAddress.address },
    vehicleType = vehicleType,
    price       = if (finalPrice > 0) finalPrice else estimatedPrice,
    status      = status
)

private enum class OrdersTab(val label: String) {
    ALL("All"),
    ONGOING("Ongoing"),
    SCHEDULED("Scheduled"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

@Composable
fun OrdersScreen(
    onBack: () -> Unit,
    onOrderClick: (orderId: String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(OrdersTab.ALL) }

    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        BookingApi.getBookings()
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

    val orders = bookings.map { it.toOrderItem() }

    val ongoingOrder = orders.firstOrNull {
        it.status in setOf(
            BookingStatus.SEARCHING_DRIVER,
            BookingStatus.DRIVER_ASSIGNED,
            BookingStatus.DRIVER_ARRIVED,
            BookingStatus.PICKUP_DONE,
            BookingStatus.IN_TRANSIT
        )
    } ?: OrderItem(
        id = "CO-9842",
        date = "",
        pickup = "Sector 15, Urban Complex",
        delivery = "Building 10C, Phase II",
        vehicleType = "Bike",
        price = 0.0,
        status = BookingStatus.IN_TRANSIT
    )

    val scheduledFromApi = orders.filter {
        it.status == BookingStatus.PENDING || it.status == BookingStatus.SEARCHING_DRIVER
    }
    val placeholderScheduled = listOf(
        OrderItem(
            id = "VL-8829-01",
            date = "",
            pickup = "32nd Avenue, Sector 15",
            delivery = "DLF Cyber City, Phase 2",
            vehicleType = "Bike",
            price = 0.0,
            status = BookingStatus.PENDING
        ),
        OrderItem(
            id = "VL-8830-02",
            date = "",
            pickup = "Connaught Place, Block A",
            delivery = "Indirapuram Hub, Tower 3",
            vehicleType = "Bike",
            price = 0.0,
            status = BookingStatus.PENDING
        )
    )
    val scheduledOrders = if (scheduledFromApi.isNotEmpty()) scheduledFromApi else placeholderScheduled

    val completedFromApi = orders.filter { it.status == BookingStatus.DELIVERED }
    val completedCards = if (completedFromApi.isNotEmpty()) {
        completedFromApi.take(2).mapIndexed { index, order ->
            CompletedOrderPreview(
                order = order,
                dateTime = if (order.date.isBlank()) if (index == 0) "Oct 24, 2:30 PM" else "Oct 22, 11:45 AM" else order.date,
                amountText = "₹${order.price.toInt()}",
                statusText = "DELIVERED"
            )
        }
    } else {
        listOf(
            CompletedOrderPreview(
                order = OrderItem(
                    id = "VL-882910",
                    date = "",
                    pickup = "Warehouse",
                    delivery = "Retail Store",
                    vehicleType = "Car",
                    price = 249.0,
                    status = BookingStatus.DELIVERED
                ),
                dateTime = "Oct 24, 2:30 PM",
                amountText = "₹249",
                statusText = "DELIVERED"
            ),
            CompletedOrderPreview(
                order = OrderItem(
                    id = "VL-882890",
                    date = "",
                    pickup = "Home",
                    delivery = "Office",
                    vehicleType = "Car",
                    price = 150.0,
                    status = BookingStatus.DELIVERED
                ),
                dateTime = "Oct 22, 11:45 AM",
                amountText = "₹150",
                statusText = "DELIVERED"
            )
        )
    }

    val cancelledFromApi = orders.filter { it.status == BookingStatus.CANCELLED }
    val cancelledCards = if (cancelledFromApi.isNotEmpty()) {
        cancelledFromApi.take(2).mapIndexed { index, order ->
            CancelledOrderPreview(
                order = order,
                dateTime = if (order.date.isBlank()) if (index == 0) "Oct 24, 2023 • 14:30 PM" else "Oct 22, 2023 • 09:15 AM" else order.date,
                reasonTitle = "Cancellation Reason",
                reasonText = if (index == 0) "Driver unavailable in your area" else "Cancelled by user",
                statusColor = if (index == 0) Color(0xFF2F80ED) else Color(0xFF64748B),
                statusBgColor = if (index == 0) Color.White else Color(0xFFF1F5F9),
                reasonLeftBorder = index == 0,
                reasonTitleBlue = index != 0,
                actionLabel = if (index == 0) "Re-book" else "Repeat\nOrder",
                footerText = if (index == 0) "" else "Refund processed to\nWallet",
                dropIconBg = if (index == 0) Color(0x1AFB5151) else Color(0xFFA6D2F3),
                dropIconTint = if (index == 0) Color(0xFF2F80ED) else Color(0xFF2F80ED)
            )
        }
    } else {
        listOf(
            CancelledOrderPreview(
                order = OrderItem(
                    id = "VL-8829-X9",
                    date = "",
                    pickup = "Harrington Fashion Hub, London",
                    delivery = "Baker Street Residences, NW1 6XE",
                    vehicleType = "Bike",
                    price = 0.0,
                    status = BookingStatus.CANCELLED
                ),
                dateTime = "Oct 24, 2023 • 14:30 PM",
                reasonTitle = "Cancellation Reason",
                reasonText = "Driver unavailable in your area",
                statusColor = Color(0xFF2F80ED),
                statusBgColor = Color.White,
                reasonLeftBorder = true,
                reasonTitleBlue = false,
                actionLabel = "Re-book",
                footerText = "",
                dropIconBg = Color(0x1AFB5151),
                dropIconTint = Color(0xFF2F80ED)
            ),
            CancelledOrderPreview(
                order = OrderItem(
                    id = "VL-9102-M1",
                    date = "",
                    pickup = "Westfield Shopping Centre",
                    delivery = "Kensington High Street, W8 4NS",
                    vehicleType = "Bike",
                    price = 0.0,
                    status = BookingStatus.CANCELLED
                ),
                dateTime = "Oct 22, 2023 • 09:15 AM",
                reasonTitle = "Cancellation Reason",
                reasonText = "Cancelled by user",
                statusColor = Color(0xFF64748B),
                statusBgColor = Color(0xFFF1F5F9),
                reasonLeftBorder = false,
                reasonTitleBlue = true,
                actionLabel = "Repeat\nOrder",
                footerText = "Refund processed to\nWallet",
                dropIconBg = Color(0xFFA6D2F3),
                dropIconTint = Color(0xFF2F80ED)
            )
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
        OrdersHeader(onMenuClick = onBack)

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
                        fontWeight = FontWeight.SemiBold,
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
                        BookingApi.getBookings()
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
                }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        val showOngoing = selectedTab == OrdersTab.ALL || selectedTab == OrdersTab.ONGOING
        val showScheduled = selectedTab == OrdersTab.ALL || selectedTab == OrdersTab.SCHEDULED
        val showCompleted = selectedTab == OrdersTab.ALL || selectedTab == OrdersTab.COMPLETED
        val showCancelled = selectedTab == OrdersTab.ALL || selectedTab == OrdersTab.CANCELLED

        if (showOngoing) {
            OngoingOrderCard(order = ongoingOrder, onTrack = { onOrderClick(ongoingOrder.id) })
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (showScheduled) {
            scheduledOrders.forEach { scheduled ->
                ScheduledOrderCard(
                    order = scheduled,
                    onModify = { onOrderClick(scheduled.id) },
                    onCancel = { onOrderClick(scheduled.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (showCompleted) {
            completedCards.forEach { completed ->
                CompletedOrderCard(
                    card = completed,
                    onViewDetails = { onOrderClick(completed.order.id) },
                    onRepeat = { onOrderClick(completed.order.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (showCancelled) {
            cancelledCards.forEach { cancelled ->
                CancelledOrderCard(
                    card = cancelled,
                    onAction = { onOrderClick(cancelled.order.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
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
                fontWeight = FontWeight.Bold
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
                    fontWeight = FontWeight.SemiBold,
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
                    fontWeight = FontWeight.SemiBold,
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
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Order History",
            color = Color(0xFF28345E),
            fontWeight = FontWeight.ExtraBold,
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
    val cards = if (ongoingOrders.isNotEmpty()) {
        ongoingOrders.take(2).mapIndexed { idx, order ->
            OngoingDeliveryPreview(
                orderId = if (order.id.isBlank()) "VL-8829${idx + 40}" else order.id,
                statusLabel = if (idx == 0) "IN TRANSIT" else "OUT FOR PICKUP",
                etaMinutes = if (idx == 0) 12 else 5,
                courierName = if (idx == 0) "Marcus Jensen" else "Elena Rodriguez",
                primaryValue = if (idx == 0) order.pickup else "Arriving at Merchant: Artisan Bakes",
                secondaryValue = if (idx == 0) order.delivery else "",
                buttonText = if (idx == 0) "Track Order" else "View Details"
            )
        }
    } else {
        listOf(
            OngoingDeliveryPreview(
                orderId = "VL-882941",
                statusLabel = "IN TRANSIT",
                etaMinutes = 12,
                courierName = "Marcus Jensen",
                primaryValue = "Global Logistics Hub, Bld 4",
                secondaryValue = "242 West 11th Street, Unit 4B",
                buttonText = "Track Order"
            ),
            OngoingDeliveryPreview(
                orderId = "VL-882949",
                statusLabel = "OUT FOR PICKUP",
                etaMinutes = 5,
                courierName = "Elena Rodriguez",
                primaryValue = "Arriving at Merchant: Artisan Bakes",
                secondaryValue = "",
                buttonText = "View Details"
            )
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
                fontWeight = FontWeight.Bold,
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
                fontWeight = FontWeight.SemiBold,
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
                    fontWeight = FontWeight.Bold
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
        shape = RoundedCornerShape(20.dp),
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
                        text = formatOrderDisplayId(card.orderId),
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
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
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
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
                        Text("COURIER", fontSize = 9.sp, color = Color(0xFF71749E), fontWeight = FontWeight.SemiBold)
                        Text(card.courierName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("ETA", fontSize = 9.sp, color = Color(0xFF71749E), fontWeight = FontWeight.SemiBold)
                        Text("${card.etaMinutes} MINS", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
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
                            Text("PICKUP", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(card.primaryValue, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("DROPOFF", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(card.secondaryValue, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        } else {
                            Text("CURRENT GOAL", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(card.primaryValue, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
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
                    Text(card.buttonText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
@Composable
private fun OrdersHeader(
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "≡",
            modifier = Modifier
                .size(30.dp)
                .clickable { onMenuClick() },
            color = PrimaryBlue,
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Orders",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D254B)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text("On", color = PrimaryBlueDark, fontWeight = FontWeight.Bold, fontSize = 22.sp)
    }
    HorizontalDivider(color = Color(0xFFE9ECF2))
}

@Composable
private fun OngoingOrderCard(order: OrderItem, onTrack: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
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
                    Text("ID: #${formatOrderId(order.id)}", color = Color(0xFF8F9BB3), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("Ongoing Delivery", color = Color(0xFF28345E), fontWeight = FontWeight.Bold, fontSize = 22.sp, maxLines = 1)
                }

                StatusPill(text = "IN TRANSIT", color = PrimaryBlue)
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
                    Text("15 mins", color = PrimaryBlue, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onTrack,
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text("Track Order", color = Color.White, fontWeight = FontWeight.SemiBold)
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
        shape = RoundedCornerShape(20.dp),
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
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatOrderDisplayId(order.id),
                        color = Color(0xFF0F172A),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
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
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.6.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Today, 5:00 PM",
                            color = Color(0xFF0F172A),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
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
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order.pickup,
                        color = Color(0xFF0F172A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "DROP-OFF",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order.delivery,
                        color = Color(0xFF0F172A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
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
                    Text("Modify", color = Color.White, fontWeight = FontWeight.SemiBold)
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
        shape = RoundedCornerShape(20.dp),
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
                    Text("ORDER ID", color = Color(0xFF555881), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(formatOrderDisplayId(card.order.id), color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Surface(shape = RoundedCornerShape(999.dp), color = Color.White) {
                    Row(
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(13.dp).background(Color(0xFF2F80ED), CircleShape),
                            contentAlignment = Alignment.Center
                        ) { Text("✓", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(card.statusText, color = Color(0xFF2F80ED), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                        Text("PICKUP", color = Color(0xFF555881), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(card.order.pickup, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column {
                        Text("DROP", color = Color(0xFF555881), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(card.order.delivery, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
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
                    Text(card.dateTime, color = Color(0xFF555881), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(card.amountText, color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onViewDetails,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFF2F80ED)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("View Details", color = Color(0xFF2F80ED), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onRepeat,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.shadow(4.dp, RoundedCornerShape(16.dp))
                    ) {
                        Text("Repeat", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFF2F80ED), RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)))
}

@Composable
private fun CancelledOrderCard(
    card: CancelledOrderPreview,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
                        text = formatOrderDisplayId(card.order.id),
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📅", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(card.dateTime, color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
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
                            Text("PICKUP", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            Text(card.order.pickup, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
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
                            Text("DROP-OFF", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            Text(card.order.delivery, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
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
                        Text("⊗", color = Color(0xFF2F80ED), fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                            fontWeight = FontWeight.Bold
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
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
                fontWeight = FontWeight.ExtraBold
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
                Text("CONTACT SALES", color = Color(0xFF2F80ED), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.6.sp)
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
            shape = RoundedCornerShape(20.dp),
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
                Text("24/7 Support", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Chat with us now", color = Color.Black, fontSize = 10.sp)
            }
        }
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
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
                Text("Loyalty Perks", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
        Text(text, color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
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
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun RetryBlock(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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

private fun formatOrderId(id: String): String {
    if (id.isBlank()) return "CO-0000"
    val cleaned = id.removePrefix("#").uppercase()
    return if (cleaned.startsWith("CO-")) cleaned else "CO-$cleaned"
}

private fun formatOrderDisplayId(id: String): String {
    if (id.isBlank()) return "#CO-0000"
    val trimmed = id.trim().removePrefix("#").uppercase()
    return if (trimmed.startsWith("VL-") || trimmed.contains("VL-")) "#$trimmed" else "#${formatOrderId(id)}"
}
