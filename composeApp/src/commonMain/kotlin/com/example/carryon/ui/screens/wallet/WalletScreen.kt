package com.example.carryon.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carryon.data.model.Wallet
import com.example.carryon.data.model.WalletTransaction
import com.example.carryon.data.network.WalletApi
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    onBack: () -> Unit
) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    var wallet by remember { mutableStateOf<Wallet?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showTopUp by remember { mutableStateOf(false) }
    var topUpAmount by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val quickAmounts = listOf(10, 20, 50, 100, 200, 500)

    LaunchedEffect(Unit) {
        WalletApi.getWallet().onSuccess { response ->
            wallet = response.data
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.walletTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("< ${strings.back}", color = Color.Black) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Balance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(strings.walletBalance, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "RM ${String.format("%.2f", wallet?.balance ?: 0.0)}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { showTopUp = !showTopUp },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text(strings.topUp, color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Top-up section
            if (showTopUp) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(strings.topUpWallet, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Quick amounts
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                quickAmounts.take(3).forEach { amount ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(
                                                1.dp,
                                                if (topUpAmount == amount.toString()) PrimaryBlue else Color.LightGray,
                                                RoundedCornerShape(10.dp)
                                            )
                                            .background(if (topUpAmount == amount.toString()) PrimaryBlueSurface else Color.White)
                                            .clickable { topUpAmount = amount.toString() }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("RM $amount", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                quickAmounts.drop(3).forEach { amount ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(
                                                1.dp,
                                                if (topUpAmount == amount.toString()) PrimaryBlue else Color.LightGray,
                                                RoundedCornerShape(10.dp)
                                            )
                                            .background(if (topUpAmount == amount.toString()) PrimaryBlueSurface else Color.White)
                                            .clickable { topUpAmount = amount.toString() }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("RM $amount", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = topUpAmount,
                                onValueChange = { topUpAmount = it.filter { c -> c.isDigit() || c == '.' } },
                                label = { Text(strings.enterAmount) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            errorMessage?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(it, color = ErrorRed, fontSize = 13.sp)
                            }
                            successMessage?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(it, color = SuccessGreen, fontSize = 13.sp)
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    val amt = topUpAmount.toDoubleOrNull()
                                    if (amt == null || amt <= 0) {
                                        errorMessage = "Please enter a valid amount"
                                        return@Button
                                    }
                                    errorMessage = null
                                    scope.launch {
                                        WalletApi.topUp(amt).onSuccess { resp ->
                                            wallet = wallet?.copy(balance = resp.data?.balance ?: wallet!!.balance)
                                            successMessage = "RM ${String.format("%.2f", amt)} added!"
                                            topUpAmount = ""
                                            showTopUp = false
                                            // Refresh wallet
                                            WalletApi.getWallet().onSuccess { w -> wallet = w.data }
                                        }.onFailure {
                                            errorMessage = it.message
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                            ) {
                                Text(strings.topUp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Transaction History
            item {
                Text(strings.transactionHistory, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            val transactions = wallet?.transactions ?: emptyList()
            if (transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(strings.noTransactions, color = TextSecondary, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                items(transactions) { txn ->
                    TransactionItem(txn)
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun TransactionItem(txn: WalletTransaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (txn.type) {
                "TOP_UP" -> "+"
                "PAYMENT" -> "-"
                "REFUND" -> "+"
                "CASHBACK" -> "+"
                "REFERRAL_BONUS" -> "+"
                "WITHDRAWAL" -> "-"
                else -> ""
            }
            val iconBg = if (txn.amount >= 0) SuccessGreen else ErrorRed

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = iconBg)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(txn.description.ifEmpty { txn.type }, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(
                    txn.createdAt.take(10),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Text(
                text = "${if (txn.amount >= 0) "+" else ""}RM ${String.format("%.2f", kotlin.math.abs(txn.amount))}",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (txn.amount >= 0) SuccessGreen else ErrorRed
            )
        }
    }
}
