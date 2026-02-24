package com.example.carryon.ui.screens.booking

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.*
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestForRideScreen(
    vehicleType: String = "",
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    var selectedPayment by remember { mutableStateOf("visa") }
    val strings = LocalStrings.current

    val vehicleImageRes = when (vehicleType) {
        "Bike" -> Res.drawable.bike
        "Car (2-Seat)" -> Res.drawable.car_two_seater
        "Car (4-Seat)" -> Res.drawable.car_4_seater
        "Mini Van" -> Res.drawable.mini_van
        "Truck" -> Res.drawable.truck
        "Open Truck" -> Res.drawable.open_truck
        else -> Res.drawable.car_mustang
    }
    val vehicleDisplayName = vehicleType.ifBlank { "Vehicle" }

    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 12.dp)) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) { Text(strings.continueText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold) }
            }
        },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Back + Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "<",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    modifier = Modifier.clickable { onBack() }.padding(end = 8.dp)
                )
                Text(strings.requestForRide, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Route section
            Row(modifier = Modifier.fillMaxWidth()) {
                // Left: icons + dotted line
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(36.dp)) {
                    // Red pin
                    Box(
                        modifier = Modifier.size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(Color(0xFFE53935)))
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                    }
                    // Dotted line
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        repeat(5) {
                            Box(modifier = Modifier.width(2.dp).height(5.dp).background(Color(0xFFB0BEC5)))
                            Spacer(modifier = Modifier.height(3.dp))
                        }
                    }
                    // Green pin
                    Box(
                        modifier = Modifier.size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(Color(0xFF43A047)))
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right: location labels
                Column(modifier = Modifier.weight(1f)) {
                    // Pickup location
                    Column {
                        Text(strings.pickup, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(strings.selectPickupLocation, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    // Delivery location
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(strings.delivery, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(strings.selectDeliveryLocation, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Car Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F9FA)),
                border = BorderStroke(1.dp, Color(0xFFDCE8E9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(vehicleDisplayName, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("4.9 (531 reviews)", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                    Image(
                        painter = painterResource(vehicleImageRes),
                        contentDescription = vehicleDisplayName,
                        modifier = Modifier.width(110.dp).height(70.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Charge section
            Text(strings.charge, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(strings.fairPrice, fontSize = 14.sp, color = TextSecondary)
                Text("RM 200", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(strings.taxPercent, fontSize = 14.sp, color = TextSecondary)
                Text("RM 20", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Select payment method
            Text(strings.selectPaymentMethod, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            PaymentMethodRow(
                iconRes = Res.drawable.payment_visa,
                title = "**** **** **** 8970",
                subtitle = "Expires: 12/26",
                isSelected = selectedPayment == "visa"
            ) { selectedPayment = "visa" }
            Spacer(modifier = Modifier.height(10.dp))
            PaymentMethodRow(
                iconRes = Res.drawable.payment_mc,
                title = "**** **** **** 8970",
                subtitle = "Expires: 12/26",
                isSelected = selectedPayment == "mc"
            ) { selectedPayment = "mc" }
            Spacer(modifier = Modifier.height(10.dp))
            PaymentMethodRow(
                iconRes = Res.drawable.payment_paypal,
                title = "mailaddress@mail.com",
                subtitle = "Expires: 12/26",
                isSelected = selectedPayment == "paypal"
            ) { selectedPayment = "paypal" }
            Spacer(modifier = Modifier.height(10.dp))
            PaymentMethodRow(
                iconRes = Res.drawable.payment_cash,
                title = strings.cash,
                subtitle = "",
                isSelected = selectedPayment == "cash"
            ) { selectedPayment = "cash" }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PaymentMethodRow(
    iconRes: org.jetbrains.compose.resources.DrawableResource,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(BorderStroke(1.dp, Color(0xFFDCE8E9)), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = title,
            modifier = Modifier.size(42.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}
