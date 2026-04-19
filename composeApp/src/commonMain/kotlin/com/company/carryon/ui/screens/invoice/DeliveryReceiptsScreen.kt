package com.company.carryon.ui.screens.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.PrimaryBlueDark

@Composable
fun DeliveryReceiptsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF5F6F8),
        topBar = { PaymentsTopBar(onBack = onBack) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            Text(
                text = "FINANCE HISTORY",
                color = PrimaryBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Delivery Receipts",
                color = Color.Black,
                fontSize = 36.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))
            SpendingCard()
            Spacer(modifier = Modifier.height(16.dp))
            SearchCard()
            Spacer(modifier = Modifier.height(16.dp))

            ReceiptRow(
                orderId = "#CR-99210",
                subtitle = "Delivered Oct 24, 2023 •\nExpress",
                amount = "RM 245.00",
                selected = false
            )
            Spacer(modifier = Modifier.height(12.dp))
            ReceiptRow(
                orderId = "#CR-99188",
                subtitle = "Delivered Oct 22, 2023 •\nStandard",
                amount = "RM 1,120.50",
                selected = true
            )

            Spacer(modifier = Modifier.height(12.dp))
            DetailedReceiptCard()
            Spacer(modifier = Modifier.height(12.dp))

            ReceiptRow(
                orderId = "#CR-98772",
                subtitle = "Delivered Oct 15, 2023 •\nEconomy",
                amount = "RM 56.20",
                selected = false
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun PaymentsTopBar(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "←",
                    color = PrimaryBlue,
                    fontSize = 22.sp,
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Payments", color = Color(0xFF1F2937), fontSize = 28.sp, fontWeight = FontWeight.Medium)
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                Text("On", color = PrimaryBlueDark, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
            }
        }
        HorizontalDivider(color = Color(0xFFE9ECF2))
    }
}

@Composable
private fun SpendingCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(listOf(Color(0xFF2F80ED), Color(0xFF4D8BEA))),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(24.dp)
    ) {
        Text("Total Spending", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "RM 12,450.00",
            color = Color(0xFFF1F2FF),
            fontSize = 40.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.14f), RoundedCornerShape(999.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("↗", color = Color.White, fontSize = 10.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text("+12.5% vs last month", color = Color(0xFFF1F2FF), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SearchCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33A6D2F3), RoundedCornerShape(32.dp))
            .padding(20.dp)
    ) {
        Text("Quick Search", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFDCE6F1), RoundedCornerShape(22.dp))
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF3F6FB), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text("⌕  Order ID or date...", color = PrimaryBlue, fontSize = 16.sp)
            }
        }

    }
}

@Composable
private fun ReceiptRow(
    orderId: String,
    subtitle: String,
    amount: String,
    selected: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33A6D2F3), RoundedCornerShape(16.dp))
            .border(
                width = if (selected) 0.dp else 0.dp,
                color = Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .let {
                if (selected) {
                    it.border(4.dp, Color.Transparent, RoundedCornerShape(16.dp))
                        .padding(start = 0.dp)
                } else {
                    it
                }
            }
            .padding(start = if (selected) 0.dp else 0.dp)
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(86.dp)
                    .background(PrimaryBlue, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Order $orderId",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = Color.Black,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(amount, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Box(
                    modifier = Modifier
                        .background(Color(0x33A6D2F3), RoundedCornerShape(999.dp))
                        .padding(horizontal = 8.dp, vertical = 1.dp)
                ) {
                    Text("PAID", color = PrimaryBlue, fontSize = 10.sp, letterSpacing = 0.5.sp, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text("›", color = PrimaryBlue, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DetailedReceiptCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33A6D2F3), RoundedCornerShape(36.dp))
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x33A6D2F3), RoundedCornerShape(32.dp))
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Column {
                        Text(
                            "DETAILED VIEW",
                            color = PrimaryBlue,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Order #CR-\n99054",
                            color = Color.Black,
                            fontSize = 36.sp,
                            lineHeight = 40.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Text("⇩", color = PrimaryBlue, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("DATE", color = PrimaryBlue, fontSize = 10.sp, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("October 18, 2023", color = Color.Black, fontSize = 14.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("PAYMENT METHOD", color = PrimaryBlue, fontSize = 10.sp, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Visa ending in\n••44", color = Color.Black, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color(0xFFA7AAD7).copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(16.dp))

            LineAmountRow("Logistics Service (International)", "RM 850.00")
            Spacer(modifier = Modifier.height(12.dp))
            LineAmountRow("Insurance Premium", "RM 45.00")
            Spacer(modifier = Modifier.height(12.dp))
            LineAmountRow("Priority Handling Fee", "RM 25.00")
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Amount", color = PrimaryBlue, fontSize = 28.sp, fontWeight = FontWeight.Medium)
                Text("RM 920.00", color = PrimaryBlue, fontSize = 28.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun LineAmountRow(title: String, amount: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = title,
            color = Color.Black,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(text = amount, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
