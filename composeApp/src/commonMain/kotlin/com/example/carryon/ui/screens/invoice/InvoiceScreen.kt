package com.example.carryon.ui.screens.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carryon.data.model.InvoiceDetail
import com.example.carryon.data.network.InvoiceApi
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    bookingId: String,
    onBack: () -> Unit
) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    var invoiceDetail by remember { mutableStateOf<InvoiceDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(bookingId) {
        // First generate invoice if it doesn't exist
        InvoiceApi.generateInvoice(bookingId)
        // Then fetch full detail
        InvoiceApi.getInvoiceDetail(bookingId).onSuccess { resp ->
            invoiceDetail = resp.data
        }.onFailure {
            errorMessage = it.message
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.invoiceTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("< ${strings.back}", color = Color.Black) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(errorMessage ?: "", color = ErrorRed)
            }
        } else {
            invoiceDetail?.let { detail ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Invoice header
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("CarryOn", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                        Text(detail.company.name, fontSize = 12.sp, color = TextSecondary)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(strings.taxInvoice, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(detail.invoice.invoiceNumber, fontSize = 13.sp, color = PrimaryBlue)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(16.dp))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(strings.billTo, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(detail.customer.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        Text(detail.customer.email, fontSize = 12.sp, color = TextSecondary)
                                        if (detail.customer.phone.isNotEmpty()) {
                                            Text(detail.customer.phone, fontSize = 12.sp, color = TextSecondary)
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(strings.invoiceDate, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(detail.invoice.issuedAt.take(10), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }

                    // Trip details
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(strings.tripDetails, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(12.dp))

                                InvoiceRow(strings.pickup, detail.booking.pickupAddress.address)
                                Spacer(modifier = Modifier.height(8.dp))
                                InvoiceRow(strings.delivery, detail.booking.deliveryAddress.address)
                                Spacer(modifier = Modifier.height(8.dp))
                                InvoiceRow(strings.vehicleType, detail.booking.vehicleType)
                                Spacer(modifier = Modifier.height(8.dp))
                                InvoiceRow(strings.distance, "${String.format("%.1f", detail.booking.distance)} km")
                                Spacer(modifier = Modifier.height(8.dp))
                                InvoiceRow(strings.paymentMethod, detail.booking.paymentMethod)

                                detail.booking.driver?.let { driver ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    InvoiceRow(strings.driverLabel, "${driver.name} (${driver.vehicleNumber})")
                                }
                            }
                        }
                    }

                    // Price breakdown
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(strings.priceBreakdown, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(12.dp))

                                PriceRow(strings.subtotal, "RM ${String.format("%.2f", detail.invoice.subtotal)}")
                                Spacer(modifier = Modifier.height(8.dp))
                                PriceRow("SST (${(detail.invoice.taxRate * 100).toInt()}%)", "RM ${String.format("%.2f", detail.invoice.tax)}")

                                if (detail.invoice.discount > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    PriceRow(strings.discountLabel, "- RM ${String.format("%.2f", detail.invoice.discount)}", color = SuccessGreen)
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(strings.totalAmount, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        "${detail.invoice.currency} ${String.format("%.2f", detail.invoice.total)}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                }
                            }
                        }
                    }

                    // Company details (footer)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(detail.company.name, fontSize = 13.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                                Text("Reg: ${detail.company.registration}", fontSize = 11.sp, color = TextSecondary)
                                Text("SST No: ${detail.company.sstNo}", fontSize = 11.sp, color = TextSecondary)
                                Text(detail.company.address.replace("\n", ", "), fontSize = 11.sp, color = TextSecondary, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(detail.company.email, fontSize = 11.sp, color = PrimaryBlue)
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun InvoiceRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.width(100.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PriceRow(label: String, value: String, color: Color = TextPrimary) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp, color = TextSecondary)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = color)
    }
}
