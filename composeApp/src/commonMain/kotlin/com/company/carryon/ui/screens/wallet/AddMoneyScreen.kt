package com.company.carryon.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.icon_people
import carryon.composeapp.generated.resources.icon_timer
import carryon.composeapp.generated.resources.wallet_add_money_icon
import com.company.carryon.data.network.WalletApi
import com.company.carryon.data.payment.StripePaymentLauncher
import com.company.carryon.data.payment.StripePaymentResult
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.PrimaryBlueDark
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun AddMoneyScreen(
    onBack: () -> Unit
) {
    var amount by remember { mutableStateOf(500) }
    var isProcessing by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Payment", color = Color(0xFF1F2937), fontSize = 28.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
            Text("On", color = PrimaryBlueDark, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(Color(0x33A6D2F3), RoundedCornerShape(999.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("WALLET REFILL", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Add Money",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color(0xFF0F172A),
            fontSize = 34.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "Securely fund your CarryOn wallet for instant payments",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color(0xFF111827),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(18.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x33A6D2F3), RoundedCornerShape(28.dp))
                .padding(18.dp)
        ) {
            Text(
                "ENTER AMOUNT",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF111827),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("RM", color = Color(0xFF8BB1E7), fontSize = 36.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(amount.toString(), color = PrimaryBlue, fontSize = 46.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAmountButton(
                    modifier = Modifier.weight(1f),
                    label = "+ RM 100",
                    selected = amount == 100,
                    onClick = { amount = 100 }
                )
                QuickAmountButton(
                    modifier = Modifier.weight(1f),
                    label = "+ RM 500",
                    selected = amount == 500,
                    onClick = { amount = 500 }
                )
                QuickAmountButton(
                    modifier = Modifier.weight(1f),
                    label = "+ RM 1000",
                    selected = amount == 1000,
                    onClick = { amount = 1000 }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x33A6D2F3), RoundedCornerShape(26.dp))
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Payment Method", color = Color(0xFF111827), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("CHANGE", color = PrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF2F7), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFDDEAFE), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalance,
                        contentDescription = "Bank",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Maybank Debit Card", color = Color(0xFF111827), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text("Ending in •••• 4290", color = Color(0xFF5B6380), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Text("›", color = Color(0xFF5B6380), fontSize = 26.sp)
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            "  PCI DSS COMPLIANT • 256-BIT ENCRYPTION",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color(0xFF111827),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        statusMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                it,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF334155),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(56.dp))

        Button(
            onClick = {
                scope.launch {
                    isProcessing = true
                    statusMessage = "Starting secure payment..."
                    val config = WalletApi.getPaymentConfig().getOrNull()?.data
                    val intent = WalletApi.createTopUpIntent(amount.toDouble()).getOrNull()?.data
                    if (config?.publishableKey.isNullOrBlank() || intent?.clientSecret.isNullOrBlank()) {
                        statusMessage = "Payment setup is unavailable. Please try again later."
                        isProcessing = false
                        return@launch
                    }

                    statusMessage = "Complete payment in Stripe."
                    val result = StripePaymentLauncher.presentWalletTopUp(
                        clientSecret = intent.clientSecret,
                        publishableKey = config.publishableKey
                    )
                    statusMessage = when (result) {
                        StripePaymentResult.COMPLETED -> "Payment completed. Wallet balance will update once confirmed."
                        StripePaymentResult.CANCELED -> "Payment canceled."
                        StripePaymentResult.FAILED -> "Payment failed. Please try another card."
                    }
                    isProcessing = false
                }
            },
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            if (isProcessing) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Proceed to Pay  →", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFF2F7), RoundedCornerShape(16.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MiniTab(Res.drawable.icon_home, "HOME", false)
            MiniTab(Res.drawable.icon_timer, "ORDERS", false)
            MiniTab(Res.drawable.wallet_add_money_icon, "WALLET", true)
            MiniTab(Res.drawable.icon_people, "PROFILE", false)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Back",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onBack() },
            color = PrimaryBlue,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun QuickAmountButton(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) PrimaryBlue else Color(0xFFF8FAFD),
            contentColor = if (selected) Color.White else Color(0xFF111827)
        )
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MiniTab(iconRes: DrawableResource, label: String, selected: Boolean) {
    Column(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(if (selected) PrimaryBlue else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = label,
                colorFilter = ColorFilter.tint(if (selected) Color.White else PrimaryBlue),
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
