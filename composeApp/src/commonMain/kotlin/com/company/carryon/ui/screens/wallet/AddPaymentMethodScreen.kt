package com.company.carryon.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.icon_people
import carryon.composeapp.generated.resources.icon_timer
import carryon.composeapp.generated.resources.wallet_add_money_icon
import com.company.carryon.ui.components.CarryOnHeader
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.PrimaryBlueDark
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private enum class MethodType { CARDS, UPI, NET_BANKING }

@Composable
fun AddPaymentMethodScreen(
    onBack: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf(MethodType.CARDS) }
    var cardNumber by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }
    var upiName by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var ifscCode by remember { mutableStateOf("") }
    var saveCard by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        CarryOnHeader(
            title = "Payments",
            titleColor = Color(0xFF1F2937),
            onBack = onBack,
            contentPadding = PaddingValues(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .background(Color(0xFFE6F0FF), RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) { Text("SECURE CHECKOUT", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp) }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Add New\nPayment Method", color = Color(0xFF0F172A), fontSize = 38.sp, lineHeight = 40.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "Choose your preferred way to fund your logistics operations. All transactions are encrypted.",
            color = Color(0xFF1F2937),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        PaymentTypeButton(
            icon = "▭",
            title = "Cards",
            selected = selectedMethod == MethodType.CARDS,
            onClick = { selectedMethod = MethodType.CARDS }
        )
        Spacer(modifier = Modifier.height(10.dp))
        PaymentTypeButton(
            icon = "◎",
            title = "UPI ID",
            selected = selectedMethod == MethodType.UPI,
            onClick = { selectedMethod = MethodType.UPI }
        )
        Spacer(modifier = Modifier.height(10.dp))
        PaymentTypeButton(
            icon = "▦",
            title = "Net Banking",
            selected = selectedMethod == MethodType.NET_BANKING,
            onClick = { selectedMethod = MethodType.NET_BANKING }
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFDCE6F1), RoundedCornerShape(22.dp))
                    .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        when (selectedMethod) {
                            MethodType.CARDS -> "Card Details"
                            MethodType.UPI -> "UPI Details"
                            MethodType.NET_BANKING -> "Banking Details"
                        },
                        color = Color(0xFF111827),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text("Securely encrypted by CarryOn Pay", color = Color(0xFF334155), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedMethod) {
                MethodType.CARDS -> {
                    Label("CARD NUMBER")
                    AppField(value = cardNumber, onValueChange = { cardNumber = it }, placeholder = "0000 0000 0000 0000", trailing = "")

                    Spacer(modifier = Modifier.height(10.dp))
                    Label("CARDHOLDER NAME")
                    AppField(value = cardName, onValueChange = { cardName = it }, placeholder = "e.g. LOGISTICS PRO")

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Label("EXPIRY DATE")
                            AppField(value = expiry, onValueChange = { expiry = it }, placeholder = "MM / YY")
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Label("CVV")
                            AppField(value = cvv, onValueChange = { cvv = it }, placeholder = "***", trailing = "?")
                        }
                    }
                }
                MethodType.UPI -> {
                    Label("UPI ID")
                    AppField(value = upiId, onValueChange = { upiId = it }, placeholder = "name@bank")

                    Spacer(modifier = Modifier.height(10.dp))
                    Label("ACCOUNT HOLDER NAME")
                    AppField(value = upiName, onValueChange = { upiName = it }, placeholder = "e.g. LOGISTICS PRO")
                }
                MethodType.NET_BANKING -> {
                    Label("BANK NAME")
                    AppField(value = bankName, onValueChange = { bankName = it }, placeholder = "e.g. Maybank")

                    Spacer(modifier = Modifier.height(10.dp))
                    Label("ACCOUNT NUMBER")
                    AppField(value = accountNumber, onValueChange = { accountNumber = it }, placeholder = "Enter account number")

                    Spacer(modifier = Modifier.height(10.dp))
                    Label("IFSC / SWIFT CODE")
                    AppField(value = ifscCode, onValueChange = { ifscCode = it }, placeholder = "Enter code")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF4FB), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("", color = PrimaryBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save method for future\npayments", color = Color(0xFF111827), fontSize = 13.sp, lineHeight = 16.sp)
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = saveCard,
                    onCheckedChange = { saveCard = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryBlue,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFCBD5E1)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("  Securely Save Payment Method", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun PaymentTypeButton(icon: String, title: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable { onClick() }
            .background(if (selected) Color(0xFF4E8FE4) else Color(0x33A6D2F3), RoundedCornerShape(14.dp))
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = if (selected) Color.Transparent else Color(0x33A6D2F3),
                shape = RoundedCornerShape(14.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, color = if (selected) Color.White else Color(0xFF111827), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, color = if (selected) Color.White else Color(0xFF111827), fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(text, color = Color(0xFF111827), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.6.sp)
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun AppField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    trailing: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFF94A3B8)) },
        trailingIcon = if (trailing.isNotBlank()) ({ Text(trailing, color = PrimaryBlue, fontWeight = FontWeight.SemiBold) }) else null,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFEFF2F7),
            unfocusedContainerColor = Color(0xFFEFF2F7),
            focusedBorderColor = Color(0xFFD5DEEA),
            unfocusedBorderColor = Color(0xFFD5DEEA)
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun HomeStyleBottomBar(selectedTab: Int) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        val items = listOf(
            Res.drawable.icon_home to "HOME",
            Res.drawable.icon_timer to "ORDERS",
            Res.drawable.wallet_add_money_icon to "WALLET",
            Res.drawable.icon_people to "PROFILE"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedTab == index
                val (iconRes, label) = item

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isSelected) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryBlue,
                            shadowElevation = 6.dp,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                BottomBarIcon(iconRes = iconRes, label = label, tint = Color.White, size = 18.dp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(label, color = Color.White, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    } else {
                        BottomBarIcon(iconRes = iconRes, label = label, tint = PrimaryBlue, size = 21.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(label, color = PrimaryBlue, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBarIcon(iconRes: DrawableResource, label: String, tint: Color, size: androidx.compose.ui.unit.Dp) {
    Image(
        painter = painterResource(iconRes),
        contentDescription = label,
        modifier = Modifier.size(size),
        colorFilter = ColorFilter.tint(tint),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun BottomMiniTab(icon: String, label: String, selected: Boolean) {
    Column(
        modifier = Modifier
            .background(if (selected) Color(0xFFDCE9FF) else Color.Transparent, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, color = if (selected) PrimaryBlue else Color(0xFF94A3B8), fontSize = 16.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, color = if (selected) PrimaryBlue else Color(0xFF94A3B8), fontSize = 12.sp)
    }
}
