package com.example.carryon.ui.screens.tracking

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
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.icon_timer
import carryon.composeapp.generated.resources.icon_people
import carryon.composeapp.generated.resources.payment_icon
import carryon.composeapp.generated.resources.map_background
import carryon.composeapp.generated.resources.image_3
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDetailsScreen(
    orderId: String = "560023",
    onBack: () -> Unit = {},
    onDelivered: () -> Unit = {},
    onUnsuccessful: () -> Unit = {},
    onChatWithDriver: () -> Unit = {},
    onViewInvoice: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onNavigateToWallet: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val strings = LocalStrings.current
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlueDark)
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Text("☰", fontSize = 22.sp, color = TextPrimary) } },
                actions = { IconButton(onClick = { }) { Image(painter = painterResource(Res.drawable.bell_icon), contentDescription = "Notifications", modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            DeliveryDetailsBottomNav(
                onNavigateToHome = onNavigateToHome,
                onNavigateToOrders = onNavigateToOrders,
                onNavigateToWallet = onNavigateToWallet,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color.White).verticalScroll(rememberScrollState())
        ) {
            // Delivery / Ride Tabs
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                Column(modifier = Modifier.weight(1f).clickable { selectedTab = 0 }, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(strings.delivery, fontSize = 15.sp, fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal, color = if (selectedTab == 0) TextPrimary else TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(if (selectedTab == 0) PrimaryBlue else Color.Transparent, RoundedCornerShape(2.dp)))
                }
                Column(modifier = Modifier.weight(1f).clickable { selectedTab = 1 }, horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text(strings.ride, fontSize = 15.sp, fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal, color = if (selectedTab == 1) TextPrimary else TextSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier.background(PrimaryBlue, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) { Text(strings.newLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(if (selectedTab == 1) PrimaryBlue else Color.Transparent, RoundedCornerShape(2.dp)))
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

            // Track your shipment header
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(strings.trackYourShipment, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                // Select dropdown
                OutlinedTextField(
                    value = strings.select,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { Text("▼", fontSize = 12.sp, color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.LightGray, focusedContainerColor = Color(0xFFF8F8F8), unfocusedContainerColor = Color(0xFFF8F8F8))
                )
            }

            // Map Section
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Image(painter = painterResource(Res.drawable.map_background), contentDescription = "Route Map", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)) {
                    Box(modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("13 min  ●●●", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    }
                }
            }

            // "Your Package" section
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(Res.drawable.image_3),
                        contentDescription = "Package",
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(strings.yourPackage, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("Order #$orderId", fontSize = 12.sp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Transit and Package Timeline
                DeliveryTimelineItem(icon = "📊", label = strings.transit, location = "3nE.Bandung", date = "22Dec,2021", time = "12:30pm")
                Spacer(modifier = Modifier.height(12.dp))
                DeliveryTimelineItem(icon = "📦", label = strings.sentPackage, location = "JnE.north BekoPf", date = "22Dec,2021", time = "12:30pm")

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // Sender / Receiver Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(strings.sendersName, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Phoebe", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(strings.sendersNumber, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("028607329", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(strings.receiversName, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Paul", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(strings.receiversNumber, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("028607329", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Delivery Method & Fee
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(strings.deliveryMethod, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Text("COD", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(strings.deliveryFee, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Text("150", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Chat & Invoice Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onChatWithDriver,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
                    ) { Text(strings.chatWithDriver, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue) }

                    OutlinedButton(
                        onClick = onViewInvoice,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
                    ) { Text(strings.viewInvoice, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue) }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onUnsuccessful,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCCCCCC))
                    ) { Text(strings.unsuccessful, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary) }

                    Button(
                        onClick = onDelivered,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) { Text(strings.delivered, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White) }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DeliveryTimelineItem(icon: String, label: String, location: String, date: String, time: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(44.dp).background(Color(0xFFF5F5F5), CircleShape), contentAlignment = Alignment.Center) { Text(icon, fontSize = 20.sp) }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(location, fontSize = 12.sp, color = TextSecondary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(date, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(time, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun DeliveryDetailsBottomNav(
    onNavigateToHome: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onNavigateToWallet: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val strings = LocalStrings.current
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        val items = listOf(Pair(Res.drawable.icon_home, strings.navHome), Pair(Res.drawable.icon_timer, strings.navOrders), Pair(Res.drawable.payment_icon, strings.navPayments), Pair(Res.drawable.icon_people, strings.navAccount))
        items.forEachIndexed { index, (iconRes, label) ->
            NavigationBarItem(
                icon = { Image(painter = painterResource(iconRes), contentDescription = label, modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit) },
                selected = index == 0,
                onClick = {
                    when (index) {
                        0 -> onNavigateToHome()
                        1 -> onNavigateToOrders()
                        2 -> onNavigateToWallet()
                        3 -> onNavigateToProfile()
                    }
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = if (index == 0) PrimaryBlueSurface else Color.Transparent)
            )
        }
    }
}