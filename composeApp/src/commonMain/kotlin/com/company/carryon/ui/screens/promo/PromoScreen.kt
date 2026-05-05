package com.company.carryon.ui.screens.promo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.model.Coupon
import com.company.carryon.data.model.ReferralInfo
import com.company.carryon.data.network.PromoApi
import com.company.carryon.ui.theme.*
import com.company.carryon.i18n.LocalStrings
import com.company.carryon.util.formatDecimal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoScreen(
    onBack: () -> Unit,
    initialReferralCode: String = "",
    onApplyCoupon: ((String) -> Unit)? = null
) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    var coupons by remember { mutableStateOf<List<Coupon>>(emptyList()) }
    var referralInfo by remember { mutableStateOf<ReferralInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(if (initialReferralCode.isBlank()) 0 else 1) }
    var referralCodeInput by remember { mutableStateOf(initialReferralCode.uppercase()) }
    var referralMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        PromoApi.getAvailableCoupons().onSuccess { resp ->
            coupons = resp.data ?: emptyList()
        }
        PromoApi.getReferralInfo().onSuccess { resp ->
            referralInfo = resp.data
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.promoAndReferrals, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("‹ ${strings.back}", color = Color.Black) }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                        Text("On", color = Color(0xFF282B51), fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Tab bar
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PrimaryBlue
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text(strings.promoCodes, modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Medium)
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text(strings.referrals, modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Medium)
                }
            }

            when (selectedTab) {
                0 -> {
                    // Promo codes tab
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (coupons.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                                    Text(strings.noCoupons, color = TextSecondary)
                                }
                            }
                        }
                        items(coupons) { coupon ->
                            CouponCard(coupon = coupon, onApply = {
                                onApplyCoupon?.invoke(coupon.code)
                            })
                        }
                    }
                }
                1 -> {
                    // Referral tab
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Your referral code
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(strings.yourReferralCode, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        referralInfo?.referralCode ?: "...",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = 4.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(strings.shareAndEarn, fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                                }
                            }
                        }

                        // Referral stats
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                StatCard(
                                    title = strings.totalReferrals,
                                    value = "${referralInfo?.totalReferrals ?: 0}",
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    title = strings.totalEarned,
                                    value = "RM ${(referralInfo?.totalEarned ?: 0.0).formatDecimal(0)}",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Apply referral code
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(strings.haveReferralCode, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        OutlinedTextField(
                                            value = referralCodeInput,
                                            onValueChange = { referralCodeInput = it.uppercase() },
                                            placeholder = { Text(strings.enterReferralCode) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = {
                                                if (referralCodeInput.isNotBlank()) {
                                                    scope.launch {
                                                        PromoApi.applyReferralCode(referralCodeInput)
                                                            .onSuccess { resp ->
                                                                referralMessage = resp.message
                                                                referralCodeInput = ""
                                                            }
                                                            .onFailure { referralMessage = it.message }
                                                    }
                                                }
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                        ) {
                                            Text(strings.apply)
                                        }
                                    }
                                    referralMessage?.let {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(it, fontSize = 13.sp, color = if (it.contains("RM")) SuccessGreen else ErrorRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CouponCard(coupon: Coupon, onApply: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left accent
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(PrimaryBlue)
            )

            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        if (coupon.discountType == "PERCENTAGE") "${coupon.discountValue.toInt()}% OFF"
                        else "RM ${coupon.discountValue.formatDecimal(0)} OFF",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Box(
                        modifier = Modifier
                            .border(1.dp, PrimaryBlue, RoundedCornerShape(8.dp))
                            .clickable { onApply() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("APPLY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(coupon.code, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                if (coupon.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(coupon.description, fontSize = 13.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    if (coupon.minOrderValue > 0) {
                        Text("Min. RM ${coupon.minOrderValue.formatDecimal(0)}", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    coupon.maxDiscount?.let {
                        Text("Max discount RM ${it.formatDecimal(0)}", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 13.sp, color = TextSecondary)
        }
    }
}
