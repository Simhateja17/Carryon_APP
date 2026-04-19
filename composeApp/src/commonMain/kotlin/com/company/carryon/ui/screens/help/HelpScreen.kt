package com.company.carryon.ui.screens.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.ui.theme.*
import com.company.carryon.i18n.LocalStrings

data class FaqItem(
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBack: () -> Unit,
    onNavigateToOrders: () -> Unit = {},
    onNavigateToTracking: () -> Unit = {},
    onNavigateToSupport: () -> Unit = {}
) {
    val strings = LocalStrings.current
    val allFaqs = remember {
        listOf(
            FaqItem(strings.faqBookDelivery, strings.faqBookDeliveryAnswer),
            FaqItem(strings.faqPriceCalculated, strings.faqPriceCalculatedAnswer),
            FaqItem(strings.faqScheduleDelivery, strings.faqScheduleDeliveryAnswer),
            FaqItem(strings.faqTrackDelivery, strings.faqTrackDeliveryAnswer),
            FaqItem(strings.faqPaymentMethods, strings.faqPaymentMethodsAnswer),
            FaqItem(strings.faqCancelBooking, strings.faqCancelBookingAnswer),
            FaqItem(strings.faqDamagedPackage, strings.faqDamagedPackageAnswer),
            FaqItem(strings.faqContactDriver, strings.faqContactDriverAnswer)
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var expandedFaq by remember { mutableStateOf<Int?>(null) }

    val filteredFaqs = remember(searchQuery, allFaqs) {
        if (searchQuery.isBlank()) allFaqs
        else allFaqs.filter {
            it.question.contains(searchQuery, ignoreCase = true) ||
            it.answer.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.helpAndSupport) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← ${strings.back}", color = Color.Black)
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                        Text("On", color = Color(0xFF282B51), fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundLight),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        expandedFaq = null
                    },
                    placeholder = { Text("Search help topics…", color = Color.Gray) },
                    leadingIcon = { Text("", fontSize = 18.sp) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            TextButton(onClick = { searchQuery = "" }) {
                                Text("", color = Color.Gray)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Only show Contact + Quick Help when not searching
            if (searchQuery.isBlank()) {
                // Contact Options
                item {
                    Text(
                        text = strings.contactUs,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column {
                            ContactOption(
                                icon = "",
                                title = strings.callUs,
                                subtitle = strings.available247,
                                onClick = { /* phone dialer — platform specific */ }
                            )
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            ContactOption(
                                icon = "",
                                title = strings.chatSupport,
                                subtitle = strings.typicallyReplies,
                                onClick = onNavigateToSupport
                            )
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            ContactOption(
                                icon = "",
                                title = strings.emailUs,
                                subtitle = "support@carryon.com",
                                onClick = { /* email intent — platform specific */ }
                            )
                        }
                    }
                }

                // Quick Help Topics
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.quickHelp,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickHelpCard(
                            icon = "",
                            title = strings.trackOrder,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToTracking
                        )
                        QuickHelpCard(
                            icon = "",
                            title = strings.paymentIssue,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToSupport
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickHelpCard(
                            icon = "",
                            title = strings.cancelOrder,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToOrders
                        )
                        QuickHelpCard(
                            icon = "",
                            title = strings.refundStatus,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToSupport
                        )
                    }
                }

                // My Tickets shortcut
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToSupport() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryBlueSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "My Support Tickets",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryBlue
                                )
                                Text(
                                    "View or create support requests",
                                    fontSize = 13.sp,
                                    color = PrimaryBlue.copy(alpha = 0.7f)
                                )
                            }
                            Text("→", fontSize = 18.sp, color = PrimaryBlue)
                        }
                    }
                }
            }

            // FAQ Header
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = strings.faq,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (searchQuery.isNotBlank()) {
                        Text(
                            "${filteredFaqs.size} result${if (filteredFaqs.size == 1) "" else "s"}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            if (filteredFaqs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No results for \"$searchQuery\"",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(onClick = onNavigateToSupport) {
                                Text("Submit a support ticket", color = PrimaryBlue)
                            }
                        }
                    }
                }
            } else {
                items(filteredFaqs.indices.toList()) { index ->
                    FaqCard(
                        faq = filteredFaqs[index],
                        isExpanded = expandedFaq == index,
                        searchQuery = searchQuery,
                        onClick = {
                            expandedFaq = if (expandedFaq == index) null else index
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun ContactOption(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 28.sp)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, fontSize = 13.sp, color = Color.Gray)
        }
        Text("→", fontSize = 18.sp, color = Color.Gray)
    }
}

@Composable
private fun QuickHelpCard(
    icon: String,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun FaqCard(
    faq: FaqItem,
    isExpanded: Boolean,
    searchQuery: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (isExpanded) "▲" else "▼",
                    fontSize = 16.sp,
                    color = PrimaryOrange
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = faq.answer,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }
            }

            // Show match snippet when searching and not expanded
            if (searchQuery.isNotBlank() && !isExpanded) {
                val answerLower = faq.answer.lowercase()
                val queryLower = searchQuery.lowercase()
                val matchIdx = answerLower.indexOf(queryLower)
                if (matchIdx >= 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val start = maxOf(0, matchIdx - 20)
                    val end = minOf(faq.answer.length, matchIdx + searchQuery.length + 40)
                    val snippet = (if (start > 0) "…" else "") +
                        faq.answer.substring(start, end) +
                        (if (end < faq.answer.length) "…" else "")
                    Text(snippet, fontSize = 12.sp, color = Color.Gray, maxLines = 2)
                }
            }
        }
    }
}
