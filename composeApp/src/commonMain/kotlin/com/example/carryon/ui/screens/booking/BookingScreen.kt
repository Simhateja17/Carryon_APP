package com.example.carryon.ui.screens.booking

import kotlin.random.Random
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.map_background
import carryon.composeapp.generated.resources.group_6476
import carryon.composeapp.generated.resources.mask_group
import carryon.composeapp.generated.resources.ellipse_4
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*

data class VehicleOption(
    val icon: String,
    val name: String,
    val description: String,
    val price: String,
    val eta: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    pickupAddress: String,
    deliveryAddress: String,
    packageType: String,
    onConfirmBooking: (String) -> Unit,
    onBack: () -> Unit
) {
    val vehicles = remember {
        listOf(
            VehicleOption("🏍️", "Bike", "Up to 10 kg", "₹49", "15 min"),
            VehicleOption("🛺", "Auto", "Up to 50 kg", "₹99", "20 min"),
            VehicleOption("🚚", "Mini Truck", "Up to 500 kg", "₹299", "30 min"),
            VehicleOption("🚛", "Truck", "Up to 2000 kg", "₹599", "45 min")
        )
    }

    var selectedVehicle by remember { mutableStateOf(vehicles[2]) }
    var paymentType by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("←", fontSize = 24.sp, color = TextPrimary) }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 12.dp).background(SuccessGreen.copy(alpha = 0.15f), RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(SuccessGreen, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ride 🔔", fontSize = 12.sp, color = SuccessGreen)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(BackgroundLight)
        ) {
            // Map Area with route
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(PrimaryBlueSurface)) {
                Image(
                    painter = painterResource(Res.drawable.map_background),
                    contentDescription = "Route Map",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Route markers
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).align(Alignment.Center), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(36.dp).background(PrimaryBlue, CircleShape), contentAlignment = Alignment.Center) { Text("🔵", fontSize = 16.sp) }
                        Text("Pickup", fontSize = 11.sp, color = TextSecondary)
                    }
                    Text("• • • • • • •", color = PrimaryBlue, modifier = Modifier.align(Alignment.CenterVertically))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(36.dp).background(SuccessGreen, CircleShape), contentAlignment = Alignment.Center) { Text("📍", fontSize = 16.sp) }
                        Text("Delivery", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }

            // Vehicle Info Card
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Vehicle image and name
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(Res.drawable.mask_group),
                                contentDescription = "Vehicle",
                                modifier = Modifier.size(64.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Mustang Dafoy 01", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(selectedVehicle.name, fontSize = 13.sp, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Charge
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Charge", fontSize = 14.sp, color = TextSecondary)
                            Text("$198", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                    }
                }

                // Address Summary
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(PrimaryBlue, CircleShape))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("52 Sanvard Sq, Clayton", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(pickupAddress.ifBlank { "Pickup Location" }, fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                        Box(modifier = Modifier.padding(start = 4.dp).width(2.dp).height(20.dp).background(Color.LightGray))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(SuccessGreen, CircleShape))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("216, Karna Kunjar Street", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(deliveryAddress.ifBlank { "Delivery Location" }, fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Type field
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Payment Type", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = paymentType,
                        onValueChange = { paymentType = it },
                        placeholder = { Text("Select payment type", color = Color.LightGray, fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color(0xFFF8F8F8),
                            unfocusedContainerColor = Color(0xFFF8F8F8),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vehicle Selection
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Select Vehicle", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            vehicles.forEach { vehicle ->
                                VehicleChip(vehicle = vehicle, isSelected = selectedVehicle == vehicle, onClick = { selectedVehicle = vehicle })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onConfirmBooking("BK${Random.nextInt(100000, 999999)}") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) { Text("Next", fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun VehicleChip(vehicle: VehicleOption, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable { onClick() }.then(if (isSelected) Modifier.background(PrimaryBlueSurface, RoundedCornerShape(12.dp)) else Modifier).padding(8.dp)) {
        Box(modifier = Modifier.size(48.dp).background(if (isSelected) PrimaryBlue else Color(0xFFF5F5F5), CircleShape), contentAlignment = Alignment.Center) {
            Text(vehicle.icon, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(vehicle.name, fontSize = 11.sp, color = if (isSelected) PrimaryBlue else TextSecondary)
        Text(vehicle.price, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (isSelected) PrimaryBlue else TextPrimary)
    }
}