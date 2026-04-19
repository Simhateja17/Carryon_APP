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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.ui.theme.PrimaryBlue

private val InvoiceCardBackground = Color(0x33A6D2F3)

@Composable
fun InvoiceHubScreen(
    onBack: () -> Unit
) {
    val invoiceItems = listOf(
        InvoiceItem("Logistics Express - Kuala Lumpur", "Sep 18, 2023 • 2 Heavy Packages", "RM 420.00"),
        InvoiceItem("Global Cargo - Penang", "Sep 23, 2023 • Standard Air", "RM 1,150.00"),
        InvoiceItem("Instant Ship - Johor Bahru", "Sep 22, 2023 • Files Delivery", "RM 290.00")
    )
    // TODO: Fetch invoice items from API
    val invoiceItems = emptyList<InvoiceItem>()

    Scaffold(containerColor = Color(0xFFF7F9FC)) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "←",
                            color = PrimaryBlue,
                            fontSize = 18.sp,
                            modifier = Modifier.clickable { onBack() }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Financial Hub", color = Color(0xFF1E3A5F), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color(0xFF111827), CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text("👤", fontSize = 10.sp) }
                }
            }

            item {
                Text(
                    "Invoices &\nStatements",
                    color = Color(0xFF3A7BC8),
                    fontSize = 36.sp,
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Text(
                    "Manage your logistics billing, download\ntax-ready monthly statements, or export\nspecific shipment receipts.",
                    color = Color(0xFF111827),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8EEF6), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text("Unpaid Balance", color = Color(0xFF334155), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("RM 1,240.50", color = Color(0xFF3A7BC8), fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF3B82F6), Color(0xFF2F80ED))),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("Next Statement", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                            Text("Oct 31", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    FilterChip("Month ▾")
                    FilterChip("2023 ▾")
                    Box(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Apply", fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEEF3F9), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    Text("⌕  Search by trip ID or recipient...", color = Color(0xFF7A8CA3), fontSize = 11.sp)
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(InvoiceCardBackground, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Monthly Summary",
                        color = Color(0xFF4C86C8),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .background(Color(0xFFD5E5F8), RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Text("September 2023\nStatement", color = Color(0xFF3A7BC8), fontSize = 28.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Full breakdown of 42 deliveries, including\nfuel surcharges and premium handling fees.",
                        color = Color(0xFF334155),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    Text("Total Amount", color = Color(0xFF334155), fontSize = 10.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("RM 8,450.00", color = Color(0xFF2F80ED), fontSize = 34.sp, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = { },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED))
                        ) {
                            Text("⇩ Download\nPDF", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(InvoiceCardBackground, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFFF1F5FA), CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text("📅", color = PrimaryBlue) }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Custom Range", color = Color(0xFF334155), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text("Select specific dates to generate a\nconsolidated PDF report.", color = Color(0xFF64748B), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Select Dates", color = Color(0xFF334155), fontSize = 13.sp)
                    }
                }
            }

            items(invoiceItems.size) { index ->
                val item = invoiceItems[index]
                InvoiceHistoryRow(item)
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .background(Color(0xFFE8EEF6), RoundedCornerShape(999.dp))
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) { Text("Load More History", color = Color(0xFF64748B), fontSize = 12.sp, fontWeight = FontWeight.Medium) }
            }
        }
    }
}

private data class InvoiceItem(
    val title: String,
    val subtitle: String,
    val amount: String
)

@Composable
private fun FilterChip(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFE8EEF6), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text, color = Color(0xFF64748B), fontSize = 10.sp)
    }
}

@Composable
private fun InvoiceHistoryRow(item: InvoiceItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(InvoiceCardBackground, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color(0xFFF4F8FC), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) { Text("◫", color = PrimaryBlue, fontSize = 11.sp) }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, color = Color(0xFF334155), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(item.subtitle, color = Color(0xFF64748B), fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.amount, color = Color(0xFF2F80ED), fontSize = 20.sp, fontWeight = FontWeight.Medium)
        }
        Text("⇩", color = Color(0xFF3B82F6), fontSize = 14.sp)
    }
}
