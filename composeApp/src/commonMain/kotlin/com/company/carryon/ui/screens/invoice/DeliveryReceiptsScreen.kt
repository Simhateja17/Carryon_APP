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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

private const val DeliveryModePooling = "Pooling"
private const val DeliveryModePriority = "Priority"
private const val DeliveryModeRegular = "Regular"

@Composable
fun DeliveryReceiptsScreen(
    onBack: () -> Unit
) {
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
            Spacer(modifier = Modifier.width(12.dp))
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
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE2E8F0)))

        Spacer(modifier = Modifier.height(16.dp))
        Text("FINANCE HISTORY", color = PrimaryBlue, fontSize = 12.sp, letterSpacing = 1.2.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Delivery Receipts", color = Color(0xFF0F172A), fontSize = 52.sp, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF3C86E8), Color(0xFF2E74D8))),
                    RoundedCornerShape(28.dp)
                )
                .padding(18.dp)
        ) {
            Text("Total Spending", color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("RM 12,450.00", color = Color.White, fontSize = 52.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("↗ +12.5% vs last month", color = Color.White, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFDCE6F1), RoundedCornerShape(22.dp))
                .padding(14.dp)
        ) {
            Text("Quick Search", color = Color(0xFF111827), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF3F6FB), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text("⌕  Order ID or date...", color = PrimaryBlue, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ReceiptRow("#CR-99210", "Delivered Oct 24, 2023 •\n$DeliveryModePriority", "RM 245.00")
        Spacer(modifier = Modifier.height(10.dp))
        ReceiptRow("#CR-99188", "Delivered Oct 22, 2023 •\n$DeliveryModeRegular", "RM 1,120.50", highlighted = true)

        Spacer(modifier = Modifier.height(12.dp))

        DetailedReceiptCard()

        Spacer(modifier = Modifier.height(12.dp))

        ReceiptRow("#CR-98772", "Delivered Oct 15, 2023 •\n$DeliveryModePooling", "RM 56.20")

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFF2F7), RoundedCornerShape(16.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MiniTab("◷", "Payments", false)
            MiniTab("▭", "Methods", false)
            MiniTab("▤", "Invoices", true)
            MiniTab("⚙", "Settings", false)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ReceiptRow(
    orderId: String,
    subtitle: String,
    amount: String,
    highlighted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDCE6F1), RoundedCornerShape(18.dp))
            .border(
                width = if (highlighted) 2.dp else 0.dp,
                color = if (highlighted) Color(0xFF3B82F6) else Color.Transparent,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFEFF4FB), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) { Text(if (highlighted) "◈" else "🚚", color = PrimaryBlue) }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text("Order $orderId", color = Color(0xFF111827), fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Color(0xFF111827), fontSize = 14.sp, lineHeight = 18.sp)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(amount, color = Color(0xFF111827), fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            Box(
                modifier = Modifier
                    .background(Color(0xFFE7F0FF), RoundedCornerShape(999.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("PAID", color = PrimaryBlue, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text("›", color = PrimaryBlue, fontSize = 24.sp)
    }
}

@Composable
private fun DetailedReceiptCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD1E1F0), RoundedCornerShape(28.dp))
            .border(1.dp, Color(0xFFC0D3E6), RoundedCornerShape(28.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color(0xFFEAF2FB), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) { Text("📦", color = Color(0xFFC0882D), fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("DETAILED VIEW", color = PrimaryBlue, fontSize = 11.sp, letterSpacing = 1.sp)
                Text("Order #CR-\n99054", color = Color(0xFF0F172A), fontSize = 44.sp, lineHeight = 44.sp, fontWeight = FontWeight.SemiBold)
            }
            Text("⇩", color = PrimaryBlue, fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("DATE", color = PrimaryBlue, fontSize = 11.sp, letterSpacing = 0.8.sp)
                Text("October 18, 2023", color = Color(0xFF0F172A), fontSize = 15.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("PAYMENT METHOD", color = PrimaryBlue, fontSize = 11.sp, letterSpacing = 0.8.sp)
                Text("Visa ending in\n••44", color = Color(0xFF0F172A), fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFABC3DB)))
        Spacer(modifier = Modifier.height(12.dp))

        LineAmountRow("Logistics Service (International)", "RM 850.00")
        Spacer(modifier = Modifier.height(8.dp))
        LineAmountRow("Insurance Premium", "RM 45.00")
        Spacer(modifier = Modifier.height(8.dp))
        LineAmountRow("Priority Handling Fee", "RM 25.00")

        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total Amount", color = PrimaryBlue, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text("RM 920.00", color = PrimaryBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LineAmountRow(title: String, amount: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, color = Color(0xFF111827), fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(amount, color = Color(0xFF111827), fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
