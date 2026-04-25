package com.company.carryon.ui.screens.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.model.Invoice
import com.company.carryon.data.model.InvoiceDetail
import com.company.carryon.data.network.InvoiceApi
import com.company.carryon.ui.components.CarryOnHeader
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.PrimaryBlueDark
import com.company.carryon.util.formatDecimal
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun DeliveryReceiptsScreen(
    onBack: () -> Unit
) {
    var invoices by remember { mutableStateOf<List<Invoice>>(emptyList()) }
    var selectedBookingId by remember { mutableStateOf<String?>(null) }
    var selectedDetail by remember { mutableStateOf<InvoiceDetail?>(null) }

    LaunchedEffect(Unit) {
        InvoiceApi.getInvoices()
            .onSuccess { response ->
                invoices = response.data.orEmpty()
                selectedBookingId = invoices.firstOrNull()?.bookingId
            }
            .onFailure { invoices = emptyList() }
    }

    LaunchedEffect(selectedBookingId) {
        val bookingId = selectedBookingId ?: return@LaunchedEffect
        InvoiceApi.getInvoiceDetail(bookingId)
            .onSuccess { response -> selectedDetail = response.data }
            .onFailure { selectedDetail = null }
    }

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
            SpendingCard(totalAmount = invoices.sumOf { it.total })
            Spacer(modifier = Modifier.height(16.dp))
            SearchCard()
            Spacer(modifier = Modifier.height(16.dp))

            invoices.forEach { invoice ->
                ReceiptRow(
                    orderId = invoice.invoiceNumber.ifBlank { invoice.bookingId },
                    subtitle = invoice.issuedAt.takeIf { it.isNotBlank() }?.let(::formatReceiptDate) ?: "Invoice date unavailable",
                    amount = "RM ${invoice.total.formatDecimal(2)}",
                    selected = invoice.bookingId == selectedBookingId,
                    onClick = { selectedBookingId = invoice.bookingId }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            selectedDetail?.let {
                DetailedReceiptCard(it)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (invoices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33A6D2F3), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("No receipts available yet.", color = Color.Black, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun PaymentsTopBar(onBack: () -> Unit) {
    CarryOnHeader(
        title = "Payments",
        titleColor = Color(0xFF1F2937),
        onBack = onBack,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp)
    )
}

@Composable
private fun SpendingCard(totalAmount: Double) {
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
            text = "RM ${totalAmount.formatDecimal(2)}",
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
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
private fun DetailedReceiptCard(detail: InvoiceDetail) {
    val invoice = detail.invoice
    val booking = detail.booking
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
                            "Order ${invoice.invoiceNumber.ifBlank { booking.id }}",
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
                    Text(invoice.issuedAt.takeIf { it.isNotBlank() }?.let(::formatReceiptDate) ?: "Unavailable", color = Color.Black, fontSize = 14.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("PAYMENT METHOD", color = PrimaryBlue, fontSize = 10.sp, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(booking.paymentMethod.ifBlank { "Unavailable" }, color = Color.Black, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color(0xFFA7AAD7).copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(16.dp))

            LineAmountRow("Subtotal", "RM ${invoice.subtotal.formatDecimal(2)}")
            Spacer(modifier = Modifier.height(12.dp))
            LineAmountRow("Tax", "RM ${invoice.tax.formatDecimal(2)}")
            Spacer(modifier = Modifier.height(12.dp))
            LineAmountRow("Discount", "RM ${invoice.discount.formatDecimal(2)}")
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Amount", color = PrimaryBlue, fontSize = 28.sp, fontWeight = FontWeight.Medium)
                Text("RM ${invoice.total.formatDecimal(2)}", color = PrimaryBlue, fontSize = 28.sp, fontWeight = FontWeight.Medium)
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

private fun formatReceiptDate(value: String): String {
    return runCatching {
        val dateTime = Instant.parse(value).toLocalDateTime(TimeZone.currentSystemDefault())
        val month = dateTime.month.name.lowercase().replaceFirstChar { it.titlecase() }
        "$month ${dateTime.dayOfMonth}, ${dateTime.year}"
    }.getOrDefault(value)
}
