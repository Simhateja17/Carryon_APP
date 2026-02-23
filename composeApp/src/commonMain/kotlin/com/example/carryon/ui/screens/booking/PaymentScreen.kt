package com.example.carryon.ui.screens.booking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import carryon.composeapp.generated.resources.payment_icon
import carryon.composeapp.generated.resources.payment_icon_1
import carryon.composeapp.generated.resources.payment_icon_2
import carryon.composeapp.generated.resources.payment_icon_3
import carryon.composeapp.generated.resources.rectangle_22
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    totalAmount: Int = 150,
    onBack: () -> Unit,
    onConfirmPayment: (paymentMethod: String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("VISA") }
    var email by remember { mutableStateOf("") }

    val paymentMethods = listOf(
        Triple("VISA", "Visa Card", Res.drawable.payment_icon_1),
        Triple("MASTERCARD", "Mastercard", Res.drawable.payment_icon_2),
        Triple("CASH", "Cash on Delivery", Res.drawable.payment_icon_3),
        Triple("WALLET", "Wallet", Res.drawable.payment_icon)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlueDark)
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Text("←", fontSize = 22.sp, color = TextPrimary) } },
                actions = { IconButton(onClick = {}) { Image(painter = painterResource(Res.drawable.bell_icon), contentDescription = "Notifications", modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text("Request for Ride", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Select payment method", fontSize = 14.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(20.dp))

            // Visa Card Image
            Image(
                painter = painterResource(Res.drawable.rectangle_22),
                contentDescription = "Payment Card",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            // "This is price of the package"
            Text("This is price of the package", fontSize = 14.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("$$totalAmount", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)

            Spacer(modifier = Modifier.height(20.dp))

            // Email field
            Text("Email", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter your email", color = Color.LightGray, fontSize = 13.sp) },
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

            Spacer(modifier = Modifier.height(20.dp))

            // Payment Method Selection
            Text("Payment Method", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                paymentMethods.forEach { (id, label, iconRes) ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(70.dp)
                            .clickable { selectedMethod = id },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedMethod == id) PrimaryBlueSurface else Color(0xFFF5F5F5)
                        ),
                        border = if (selectedMethod == id) androidx.compose.foundation.BorderStroke(2.dp, PrimaryBlue) else null,
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(iconRes),
                                contentDescription = label,
                                modifier = Modifier.size(36.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Continue Button (Green)
            Button(
                onClick = { onConfirmPayment(selectedMethod) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}