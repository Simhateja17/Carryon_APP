package com.example.carryon.ui.screens.help

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings

data class FaqItem(
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBack: () -> Unit
) {
    val strings = LocalStrings.current
    val faqs = remember {
        listOf(
            FaqItem(
                strings.faqBookDelivery,
                strings.faqBookDeliveryAnswer
            ),
            FaqItem(
                strings.faqPriceCalculated,
                strings.faqPriceCalculatedAnswer
            ),
            FaqItem(
                strings.faqScheduleDelivery,
                strings.faqScheduleDeliveryAnswer
            ),
            FaqItem(
                strings.faqTrackDelivery,
                strings.faqTrackDeliveryAnswer
            ),
            FaqItem(
                strings.faqPaymentMethods,
                strings.faqPaymentMethodsAnswer
            ),
            FaqItem(
                strings.faqCancelBooking,
                strings.faqCancelBookingAnswer
            ),
            FaqItem(
                strings.faqDamagedPackage,
                strings.faqDamagedPackageAnswer
            ),
            FaqItem(
                strings.faqContactDriver,
                strings.faqContactDriverAnswer
            )
        )
    }
    
    var expandedFaq by remember { mutableStateOf<Int?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.helpAndSupport) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← ${strings.back}", color = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
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
                            icon = "📞",
                            title = strings.callUs,
                            subtitle = strings.available247,
                            onClick = { }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        ContactOption(
                            icon = "💬",
                            title = strings.chatSupport,
                            subtitle = strings.typicallyReplies,
                            onClick = { }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        ContactOption(
                            icon = "📧",
                            title = strings.emailUs,
                            subtitle = "support@carryon.com",
                            onClick = { }
                        )
                    }
                }
            }
            
            // Quick Help Topics
            item {
                Spacer(modifier = Modifier.height(8.dp))
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
                        icon = "📦",
                        title = strings.trackOrder,
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )
                    QuickHelpCard(
                        icon = "💳",
                        title = strings.paymentIssue,
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickHelpCard(
                        icon = "🚫",
                        title = strings.cancelOrder,
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )
                    QuickHelpCard(
                        icon = "💰",
                        title = strings.refundStatus,
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )
                }
            }
            
            // FAQs
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = strings.faq,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            items(faqs.indices.toList()) { index ->
                FaqCard(
                    faq = faqs[index],
                    isExpanded = expandedFaq == index,
                    onClick = {
                        expandedFaq = if (expandedFaq == index) null else index
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
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
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color.Gray
            )
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
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun FaqCard(
    faq: FaqItem,
    isExpanded: Boolean,
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
            
            if (isExpanded) {
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
    }
}
