package com.company.carryon.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.ui.theme.PrimaryBlue

@Composable
fun AddMoneyScreen(
    onBack: () -> Unit
) {
    var amount by remember { mutableStateOf(500) }

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
            Text("←", color = PrimaryBlue, fontSize = 22.sp, modifier = Modifier.clickable { onBack() })
            Spacer(modifier = Modifier.width(10.dp))
            Text("Financial Hub", color = PrimaryBlue, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(Color(0xFFDDEAFE), CircleShape),
                contentAlignment = Alignment.Center
            ) { Text("👨‍💼", fontSize = 14.sp) }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE1E6ED)))

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(Color(0xFFE6F0FF), RoundedCornerShape(999.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("WALLET REFILL", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Add Money",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color(0xFF0F172A),
            fontSize = 52.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "Securely fund your CarryOn wallet for instant payments",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color(0xFF111827),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFDCE6F1), RoundedCornerShape(28.dp))
                .padding(18.dp)
        ) {
            Text(
                "ENTER AMOUNT",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF111827),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("₹", color = Color(0xFF8BB1E7), fontSize = 52.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(amount.toString(), color = PrimaryBlue, fontSize = 66.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAmountButton(
                    modifier = Modifier.weight(1f),
                    label = "+ ₹100",
                    selected = amount == 100,
                    onClick = { amount = 100 }
                )
                QuickAmountButton(
                    modifier = Modifier.weight(1f),
                    label = "+ ₹500",
                    selected = amount == 500,
                    onClick = { amount = 500 }
                )
                QuickAmountButton(
                    modifier = Modifier.weight(1f),
                    label = "+ ₹1000",
                    selected = amount == 1000,
                    onClick = { amount = 1000 }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFDCE6F1), RoundedCornerShape(26.dp))
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Payment Method", color = Color(0xFF111827), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text("CHANGE", color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
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
                    Text("💳", color = PrimaryBlue)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("HDFC Bank Debit Card", color = Color(0xFF111827), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("Ending in •••• 4290", color = Color(0xFF5B6380), fontSize = 14.sp)
                }
                Text("›", color = Color(0xFF5B6380), fontSize = 26.sp)
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            "🛡  PCI DSS COMPLIANT • 256-BIT ENCRYPTION",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color(0xFF111827),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(56.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Proceed to Pay  →", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFF2F7), RoundedCornerShape(16.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MiniTab("◷", "Payments", true)
            MiniTab("▭", "Methods", false)
            MiniTab("▤", "Invoices", false)
            MiniTab("⚙", "Settings", false)
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
private fun MiniTab(icon: String, label: String, selected: Boolean) {
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
