package com.example.carryon.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
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
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.icon_profile
import carryon.composeapp.generated.resources.icon_messages
import carryon.composeapp.generated.resources.icon_search
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.clip_path_group
import carryon.composeapp.generated.resources.clip_path_group_1
import carryon.composeapp.generated.resources.mask_group
import carryon.composeapp.generated.resources.rectangle_22
import carryon.composeapp.generated.resources.ellipse_4
import androidx.compose.ui.graphics.Brush
import carryon.composeapp.generated.resources.group_vehicle
import carryon.composeapp.generated.resources.van_vehicle
import carryon.composeapp.generated.resources.vector_truck
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToBooking: (String, String, String) -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToTracking: (String) -> Unit,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToCalculate: () -> Unit = {}
) {
    var pickupLocation by remember { mutableStateOf("") }
    var deliveryLocation by remember { mutableStateOf("") }
    var selectedNavItem by remember { mutableStateOf(2) }
    var selectedVehicle by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Carry", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlueDark)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { }) { Text("☰", fontSize = 22.sp, color = TextPrimary) }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Image(painter = painterResource(Res.drawable.bell_icon), contentDescription = "Notifications", modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = Color.White) {
                Column {
                    Button(
                        onClick = { onNavigateToBooking(pickupLocation.ifBlank { "32 Samwell Sq, Chevron" }, deliveryLocation.ifBlank { "21b, Karimu Kotun Street, Victoria Island" }, "Bike") },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) { Text("Next", fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
                    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                        val navItems = listOf(Pair(Res.drawable.icon_search, "Search"), Pair(Res.drawable.icon_messages, "Messages"), Pair(Res.drawable.icon_home, "Home"), Pair(Res.drawable.icon_profile, "Profile"))
                        navItems.forEachIndexed { index, (iconRes, label) ->
                            NavigationBarItem(
                                icon = { Image(painter = painterResource(iconRes), contentDescription = label, modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit) },
                                selected = selectedNavItem == index,
                                onClick = {
                                    selectedNavItem = index
                                    when (index) {
                                        0 -> onNavigateToCalculate()
                                        1 -> onNavigateToHistory()
                                        3 -> onNavigateToProfile()
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(indicatorColor = if (selectedNavItem == index) PrimaryBlueSurface else Color.Transparent)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
                .background(Brush.verticalGradient(listOf(Color(0x332F80ED), Color.White)))
                .verticalScroll(rememberScrollState())
        ) {
            // Banner: "We are Ready to Serve" with background image
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(Res.drawable.rectangle_22),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(130.dp),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier.fillMaxWidth().height(130.dp).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Welcome, Devansh", fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("We are Ready to\nServe", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, lineHeight = 26.sp)
                    }
                    Image(
                        painter = painterResource(Res.drawable.ellipse_4),
                        contentDescription = "Profile",
                        modifier = Modifier.size(64.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pickup Location
            Text("Pickup Location", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(value = pickupLocation, onValueChange = { pickupLocation = it }, placeholder = { Text("32 Samwell Sq, Chevron", color = Color.Gray) }, leadingIcon = { Text("📍", fontSize = 14.sp) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Color.LightGray, focusedContainerColor = Color.White, unfocusedContainerColor = Color(0xFFF8F8F8)), singleLine = true)

            Spacer(modifier = Modifier.height(12.dp))

            // Delivery Location
            Text("Delivery Location", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(value = deliveryLocation, onValueChange = { deliveryLocation = it }, placeholder = { Text("21b, Karimu Kotun Street, Victoria Island", color = Color.Gray) }, leadingIcon = { Text("○", fontSize = 14.sp, color = SuccessGreen) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Color.LightGray, focusedContainerColor = Color.White, unfocusedContainerColor = Color(0xFFF8F8F8)), singleLine = true)

            Spacer(modifier = Modifier.height(20.dp))

            // Vehicle Type
            Text("Vehicle Type", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val vehicles = listOf(
                    Pair(Res.drawable.group_vehicle, "Bike"),
                    Pair(Res.drawable.van_vehicle, "Van"),
                    Pair(Res.drawable.vector_truck, "Truck")
                )
                vehicles.forEachIndexed { index, (iconRes, label) ->
                    Card(
                        modifier = Modifier.weight(1f).height(64.dp).clickable { selectedVehicle = index },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedVehicle == index) PrimaryBlueSurface else Color.White
                        ),
                        border = if (selectedVehicle == index) androidx.compose.foundation.BorderStroke(2.dp, PrimaryBlue) else null,
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(iconRes),
                                contentDescription = label,
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Our services
            Text("Our services", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ServiceCard(imageRes = Res.drawable.clip_path_group_1, title = "Same day\ndelivery", modifier = Modifier.weight(1f))
                ServiceCard(imageRes = Res.drawable.clip_path_group, title = "Overnight\ndelivery", modifier = Modifier.weight(1f))
                ServiceCard(imageRes = Res.drawable.mask_group, title = "Express\ndelivery", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ServiceCard(imageRes: org.jetbrains.compose.resources.DrawableResource, title: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x332F80ED)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = title,
                modifier = Modifier.size(90.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary, lineHeight = 15.sp, modifier = Modifier.padding(horizontal = 2.dp))
    }
}

@Composable
private fun BottomNavigationBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        val items = listOf(Pair(Res.drawable.icon_search, "Search"), Pair(Res.drawable.icon_messages, "Messages"), Pair(Res.drawable.icon_home, "Home"), Pair(Res.drawable.icon_profile, "Profile"))
        items.forEachIndexed { index, (iconRes, label) ->
            NavigationBarItem(icon = { Image(painter = painterResource(iconRes), contentDescription = label, modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit) }, selected = selectedIndex == index, onClick = { onItemSelected(index) }, colors = NavigationBarItemDefaults.colors(indicatorColor = if (selectedIndex == index) PrimaryBlueSurface else Color.Transparent))
        }
    }
}