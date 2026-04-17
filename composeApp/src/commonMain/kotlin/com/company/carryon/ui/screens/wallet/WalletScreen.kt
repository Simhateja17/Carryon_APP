package com.company.carryon.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.wallet_add_money_icon
import carryon.composeapp.generated.resources.wallet_delivery_icon
import carryon.composeapp.generated.resources.wallet_download_invoice_icon
import carryon.composeapp.generated.resources.wallet_google_pay_upi_icon
import carryon.composeapp.generated.resources.wallet_hdfc_bank_card_icon
import carryon.composeapp.generated.resources.wallet_send_money_icon
import carryon.composeapp.generated.resources.wallet_view_receipts_icon
import com.company.carryon.data.model.Wallet
import com.company.carryon.data.model.WalletTransaction
import com.company.carryon.data.network.WalletApi
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.TextSecondary
import com.company.carryon.util.formatDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun WalletScreen(
    onBack: () -> Unit,
    onAddMoney: () -> Unit = {},
    onSendMoney: () -> Unit = {},
    onAddNewMethod: () -> Unit = {},
    onDownloadInvoices: () -> Unit = {},
    onViewReceipts: () -> Unit = {}
) {
    var wallet by remember { mutableStateOf<Wallet?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val result = withContext(Dispatchers.Default) { WalletApi.getWallet() }
        result.onSuccess { response ->
            wallet = response.data
        }
        isLoading = false
    }

    Scaffold(containerColor = Color(0xFFF5F6F8)) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
            return@Scaffold
        }

        val transactions = (wallet?.transactions ?: emptyList()).take(3)
        val balance = wallet?.balance ?: 500.0

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("☰", color = Color(0xFF64748B), fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Payments", color = Color(0xFF1E293B), fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color(0xFFDDEAFE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 14.sp)
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF3B82F6), Color(0xFF2F80ED))
                            ),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .padding(18.dp)
                ) {
                    Text("Wallet Balance", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("RM ${balance.toInt()}", color = Color.White, fontSize = 44.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onAddMoney,
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(Res.drawable.wallet_add_money_icon),
                                contentDescription = "Add Money",
                                modifier = Modifier.size(18.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Money", color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionItem(Res.drawable.wallet_add_money_icon, "Add Money", onClick = onAddMoney)
                    ActionItem(Res.drawable.wallet_send_money_icon, "Send Money", onClick = onSendMoney)
                    ActionItem(Res.drawable.wallet_google_pay_upi_icon, "Withdraw")
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Transactions", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text("See All", color = Color(0xFF3B82F6), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFDCE6F1), RoundedCornerShape(18.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (transactions.isEmpty()) {
                        Text("No transactions yet", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(8.dp))
                    } else {
                        transactions.forEach { txn ->
                            TransactionRow(txn)
                        }
                    }
                }
            }

            item {
                Text("Saved Methods", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            item {
                PaymentMethodItem(
                    iconRes = Res.drawable.wallet_google_pay_upi_icon,
                    title = "Google Pay (UPI)",
                    subtitle = "linked: user@okaxis"
                )
            }

            item {
                PaymentMethodItem(
                    iconRes = Res.drawable.wallet_hdfc_bank_card_icon,
                    title = "HDFC Bank Card",
                    subtitle = "Ending in •••• 4421"
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAddNewMethod() }
                        .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(14.dp))
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+ Add New Method", color = Color(0xFF3B82F6), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UtilityCard(
                        modifier = Modifier.weight(1f),
                        title = "Download Invoice",
                        subtitle = "Monthly statement",
                        iconRes = Res.drawable.wallet_download_invoice_icon,
                        onClick = onDownloadInvoices
                    )
                    UtilityCard(
                        modifier = Modifier.weight(1f),
                        title = "View Receipts",
                        subtitle = "Individual orders",
                        iconRes = Res.drawable.wallet_view_receipts_icon,
                        onClick = onViewReceipts
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionItem(iconRes: DrawableResource, label: String, onClick: (() -> Unit)? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFFE8F1FC), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            label,
            color = Color.Black,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TransactionRow(txn: WalletTransaction) {
    val isCredit = txn.amount >= 0
    val amountText = "${if (isCredit) "+" else "-"}RM ${kotlin.math.abs(txn.amount).formatDecimal(0)}"
    val title = txn.description.ifBlank {
        if (txn.type == "TOP_UP") "Wallet Top-up" else "Delivery #CN-0000"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val deliveryLike = title == "Delivery #CN-9281" || title == "Delivery #CN-8822" || title.startsWith("Delivery #CN-")
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFE7EEF9), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (deliveryLike) {
                Image(
                    painter = painterResource(Res.drawable.wallet_delivery_icon),
                    contentDescription = title,
                    modifier = Modifier.size(20.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(if (isCredit) "↥" else "◫", color = PrimaryBlue, fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text("${formatDate(txn.createdAt)} • Success", color = Color(0xFF64748B), fontSize = 12.sp)
        }
        Text(
            amountText,
            color = if (isCredit) Color(0xFF3B82F6) else Color(0xFF3B82F6),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PaymentMethodItem(iconRes: DrawableResource, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFDCE6F1))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(Color(0xFFE7EEF9), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(18.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Color(0xFF334155), fontSize = 13.sp)
        }
        Text("›", color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun UtilityCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    iconRes: DrawableResource,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFDCE6F1))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(Color(0xFFE7EEF9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(18.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(title, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = Color(0xFF475569), fontSize = 12.sp)
    }
}

private fun formatDate(raw: String): String {
    val datePart = raw.take(10)
    val pieces = datePart.split("-")
    if (pieces.size != 3) return "Oct 24, 2023"
    val year = pieces[0]
    val month = when (pieces[1]) {
        "01" -> "Jan"
        "02" -> "Feb"
        "03" -> "Mar"
        "04" -> "Apr"
        "05" -> "May"
        "06" -> "Jun"
        "07" -> "Jul"
        "08" -> "Aug"
        "09" -> "Sep"
        "10" -> "Oct"
        "11" -> "Nov"
        "12" -> "Dec"
        else -> "Oct"
    }
    return "$month ${pieces[2]}, $year"
}
