package com.example.carryon.ui.screens.booking

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carryon.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestForRideScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    var selectedPayment by remember { mutableStateOf("visa") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlueDark)
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Text("☰", fontSize = 22.sp, color = TextPrimary) } },
                actions = { IconButton(onClick = {}) { Text("🔔", fontSize = 20.sp) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            // Back + Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("<", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue, modifier = Modifier.clickable { onBack() }.padding(end = 8.dp))
                Text("Request for Ride", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Route Card
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // From
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(SuccessGreen))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Current Location", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            Text("32 Samwell Sq, Chevron", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                    // Dotted line
                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        repeat(3) {
                            Box(modifier = Modifier.width(2.dp).height(4.dp).background(Color.Gray))
                            Spacer(modifier = Modifier.height(3.dp))
                        }
                    }
                    // To
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color.Red))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Office", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            Text("1, Emirates, Estate Rd, Chevron", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Car Card
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        // Car image placeholder
                        Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF5F5F5)), contentAlignment = Alignment.Center) {
                            Text("🚗", fontSize = 30.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mustang Shelby GT", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⭐", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("4.9 (531 reviews)", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Charge
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Charge", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Amount", fontSize = 14.sp, color = TextSecondary)
                        Text("$200.00", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tax", fontSize = 14.sp, color = TextSecondary)
                        Text("$20.00", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("$220.00", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Methods
            Text("Payment Methods", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            // VISA
            PaymentMethodRow(label = "VISA", subLabel = "****7890", isSelected = selectedPayment == "visa") { selectedPayment = "visa" }
            Spacer(modifier = Modifier.height(8.dp))
            PaymentMethodRow(label = "MasterCard", subLabel = "****3456", isSelected = selectedPayment == "mc") { selectedPayment = "mc" }
            Spacer(modifier = Modifier.height(8.dp))
            PaymentMethodRow(label = "PayPal", subLabel = "john@mail.com", isSelected = selectedPayment == "paypal") { selectedPayment = "paypal" }
            Spacer(modifier = Modifier.height(8.dp))
            PaymentMethodRow(label = "Cash", subLabel = "", isSelected = selectedPayment == "cash") { selectedPayment = "cash" }

            Spacer(modifier = Modifier.height(20.dp))

            // Continue button
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) { Text("Continue", fontSize = 15.sp, fontWeight = FontWeight.SemiBold) }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PaymentMethodRow(label: String, subLabel: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) PrimaryBlueSurface else Color(0xFFF5F5F5))
            .border(width = if (isSelected) 1.5.dp else 0.dp, color = if (isSelected) PrimaryBlue else Color.Transparent, shape = RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Radio dot
        Box(modifier = Modifier.size(20.dp).clip(CircleShape).border(2.dp, if (isSelected) PrimaryBlue else Color.Gray, CircleShape), contentAlignment = Alignment.Center) {
            if (isSelected) Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(PrimaryBlue))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.weight(1f))
        if (subLabel.isNotEmpty()) {
            Text(subLabel, fontSize = 13.sp, color = TextSecondary)
        }
    }
}
