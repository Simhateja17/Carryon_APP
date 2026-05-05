package com.company.carryon.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.model.WalletTransaction
import com.company.carryon.data.network.TransactionsPage
import com.company.carryon.data.network.WalletApi
import com.company.carryon.ui.components.CarryOnHeader
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.TextSecondary
import com.company.carryon.util.formatDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TransactionsScreen(
    onBack: () -> Unit
) {
    var page by remember { mutableStateOf<TransactionsPage?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }

    LaunchedEffect(Unit) {
        val result = withContext(Dispatchers.Default) { WalletApi.getTransactions(page = 1, limit = 20) }
        result.onSuccess { response ->
            page = response.data
        }
        isLoading = false
    }

    Scaffold(
        containerColor = Color(0xFFF5F6F8),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
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

        val transactions = page?.transactions ?: emptyList()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CarryOnHeader(
                    title = "Transactions",
                    titleColor = Color(0xFF1E293B),
                    onBack = onBack,
                    contentPadding = PaddingValues(0.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transactions yet", color = TextSecondary, fontSize = 16.sp)
                    }
                }
            } else {
                items(transactions, key = { it.id }) { txn ->
                    TransactionCard(txn = txn)
                }

                // Load more if there are more transactions
                val total = page?.total ?: 0
                if (transactions.size < total && !isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Trigger load more when this item is composed
                            LaunchedEffect(currentPage) {
                                isLoadingMore = true
                                val nextPage = currentPage + 1
                                val result = withContext(Dispatchers.Default) {
                                    WalletApi.getTransactions(page = nextPage, limit = 20)
                                }
                                result.onSuccess { response ->
                                    response.data?.let { newData ->
                                        page = page?.copy(
                                            transactions = transactions + newData.transactions,
                                            page = newData.page
                                        )
                                        currentPage = nextPage
                                    }
                                }
                                isLoadingMore = false
                            }
                            CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionCard(txn: WalletTransaction) {
    val isCredit = txn.amount >= 0
    val amountText = "${if (isCredit) "+" else "-"}RM ${kotlin.math.abs(txn.amount).formatDecimal(0)}"
    val title = txn.description.ifBlank {
        if (txn.type == "TOP_UP") "Wallet top-up" else "Wallet transaction"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33A6D2F3), RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val deliveryLike = title.startsWith("Delivery #CN-")
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFE7EEF9), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (deliveryLike) {
                Icon(
                    imageVector = Icons.Outlined.LocalShipping,
                    contentDescription = title,
                    modifier = Modifier.size(22.dp),
                    tint = PrimaryBlue
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.NorthEast,
                    contentDescription = if (isCredit) "Credit" else "Debit",
                    modifier = Modifier.size(18.dp),
                    tint = PrimaryBlue
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            if (!txn.referenceId.isNullOrBlank()) {
                Text(txn.referenceId, color = Color(0xFF475569), fontSize = 13.sp)
            }
            Text("${formatTransactionDate(txn.createdAt)} \u2022 Success", color = Color(0xFF64748B), fontSize = 12.sp)
        }
        Text(
            amountText,
            color = if (isCredit) Color(0xFF3B82F6) else Color(0xFF3B82F6),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatTransactionDate(raw: String): String {
    val datePart = raw.take(10)
    val pieces = datePart.split("-")
    if (pieces.size != 3) return raw
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
        else -> pieces[1]
    }
    return "$month ${pieces[2]}, $year"
}
