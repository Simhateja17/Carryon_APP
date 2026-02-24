package com.example.carryon.ui.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carryon.data.model.BookingStatus
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings

data class OrderItem(
    val id: String,
    val date: String,
    val pickup: String,
    val delivery: String,
    val vehicleType: String,
    val price: Double,
    val status: BookingStatus
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onBack: () -> Unit,
    onOrderClick: (orderId: String) -> Unit
) {
    val strings = LocalStrings.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(strings.all, strings.active, strings.completed, strings.cancelled)
    
    // TODO: Fetch real orders from API
    val orders = remember { listOf<OrderItem>() }
    
    val filteredOrders = when (selectedTab) {
        1 -> orders.filter { it.status == BookingStatus.IN_TRANSIT || it.status == BookingStatus.SEARCHING_DRIVER }
        2 -> orders.filter { it.status == BookingStatus.DELIVERED }
        3 -> orders.filter { it.status == BookingStatus.CANCELLED }
        else -> orders
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.myOrders) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 24.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundLight)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PrimaryOrange
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    )
                }
            }
            
            if (filteredOrders.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📦", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = strings.noOrdersFound,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = strings.ordersWillAppearHere,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders) { order ->
                        OrderCard(
                            order = order,
                            onClick = { onOrderClick(order.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: OrderItem,
    onClick: () -> Unit
) {
    val strings = LocalStrings.current
    val (statusColor, statusText, statusIcon) = when (order.status) {
        BookingStatus.DELIVERED -> Triple(SuccessGreen, strings.delivered, "✅")
        BookingStatus.CANCELLED -> Triple(ErrorRed, strings.cancelled, "❌")
        BookingStatus.IN_TRANSIT -> Triple(InfoBlue, strings.inTransit, "🚚")
        BookingStatus.SEARCHING_DRIVER -> Triple(WarningYellow, strings.findingDriver, "🔍")
        else -> Triple(Color.Gray, strings.processing, "⏳")
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.id,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(statusIcon, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Route
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📍", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = order.pickup,
                    fontSize = 14.sp
                )
                Text(
                    text = " → ",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = order.delivery,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.vehicleType,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = " • ${order.date}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                
                Text(
                    text = "RM ${order.price.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryOrange
                )
            }
        }
    }
}
