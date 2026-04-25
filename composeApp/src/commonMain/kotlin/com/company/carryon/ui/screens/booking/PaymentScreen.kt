package com.company.carryon.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.PrimaryBlueDark
import com.company.carryon.ui.theme.ScreenHorizontalPadding
import com.company.carryon.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    totalAmount: Int = 150,
    initialMethod: String = "VISA",
    onBack: () -> Unit,
    onConfirmPayment: (paymentMethod: String) -> Unit
) {
    val method = initialMethod.uppercase()
    val isCard = method == "VISA" || method == "MASTERCARD" || method == "CARD"

    var cardNumber by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var walletId by remember { mutableStateOf("") }

    val canContinue = if (isCard) {
        cardNumber.filter { it.isDigit() }.length >= 12 &&
            cardName.trim().isNotBlank() &&
            expiry.trim().length >= 4 &&
            cvv.filter { it.isDigit() }.length >= 3
    } else {
        walletId.trim().isNotBlank()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = if (isCard) "Card Details" else "Wallet Details",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row {
                            Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                            Text("On", color = PrimaryBlueDark, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                        }
                    }
                },
                navigationIcon = {
                    Text(
                        "‹",
                        fontSize = 22.sp,
                        color = TextPrimary,
                        modifier = Modifier
                            .padding(start = 12.dp, top = 4.dp)
                            .clickable { onBack() }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenHorizontalPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Amount", color = Color(0xFF6B7280), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text("RM $totalAmount", color = PrimaryBlue, fontSize = 36.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(22.dp))

            if (isCard) {
                Text("Card Number", color = Color(0xFF374151), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                InputField(cardNumber, { cardNumber = it }, "0000 0000 0000 0000")

                Spacer(modifier = Modifier.height(12.dp))
                Text("Name on Card", color = Color(0xFF374151), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                InputField(cardName, { cardName = it }, "Enter cardholder name")

                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Expiry", color = Color(0xFF374151), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        InputField(expiry, { expiry = it }, "MM/YY")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("CVV", color = Color(0xFF374151), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        InputField(cvv, { cvv = it }, "***")
                    }
                }
            } else {
                Text("Wallet ID", color = Color(0xFF374151), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                InputField(walletId, { walletId = it }, "Enter wallet / UPI ID")
            }

            Spacer(modifier = Modifier.height(28.dp))
            Button(
                onClick = { onConfirmPayment(method) },
                enabled = canContinue,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Pay Now", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InputField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.LightGray, fontSize = 13.sp) },
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
