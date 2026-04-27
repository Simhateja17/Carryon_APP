package com.company.carryon.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.support_call_icon
import carryon.composeapp.generated.resources.support_chat_icon
import com.company.carryon.data.model.SupportTicket
import com.company.carryon.data.network.SupportApi
import com.company.carryon.ui.components.CarryOnHeader
import com.company.carryon.ui.theme.*
import com.company.carryon.i18n.LocalStrings
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun SupportScreen(
    onBack: () -> Unit,
    onTicketClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        CarryOnHeader(
            title = "Help & Support",
            onBack = onBack,
            contentPadding = PaddingValues(0.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text("Quick Support", color = Color(0xFF0F172A), fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickSupportCard(
                modifier = Modifier.weight(1f),
                icon = Res.drawable.support_chat_icon,
                title = "Chat with us",
                subtitle = "Instant response",
                onClick = { onTicketClick("chat") }
            )
            QuickSupportCard(
                modifier = Modifier.weight(1f),
                icon = Res.drawable.support_call_icon,
                title = "Call Support",
                subtitle = "Direct line",
                onClick = { onTicketClick("call") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFDCE6F1), RoundedCornerShape(26.dp))
                .padding(16.dp)
        ) {
            Text("Need help with an order?", color = Color(0xFF111827), fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Select a recent shipment to report delays or damage.",
                color = Color(0xFF1F2937),
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .background(Color(0xFFF4F7FC), RoundedCornerShape(999.dp))
                    .clickable { onTicketClick("select_order") }
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text("Select Order  →", color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("FAQs", color = Color(0xFF0F172A), fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            Text("See All", color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        FaqRow("How do I track my order?", onClick = { onTicketClick("faq_track") })
        Spacer(modifier = Modifier.height(10.dp))
        FaqRow("How to change delivery address?", onClick = { onTicketClick("faq_address") })
        Spacer(modifier = Modifier.height(10.dp))
        FaqRow("What are the delivery charges?", onClick = { onTicketClick("faq_charges") })

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = { onTicketClick("report_issue") },
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("  Report an Issue", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(Color(0xFFE7EEF8), RoundedCornerShape(999.dp))
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text("•  AVAILABLE 24/7", color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFF2F7), RoundedCornerShape(18.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MiniTab("⌂", "Home", false)
            MiniTab("", "Orders", false)
            MiniTab("?", "Support", true)
            MiniTab("◉", "Profile", false)
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun QuickSupportCard(
    modifier: Modifier = Modifier,
    icon: DrawableResource,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(170.dp)
            .background(Color(0xFFDCE6F1), RoundedCornerShape(22.dp))
            .border(1.dp, Color(0xFFD2DDEB), RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFFF4F7FC), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = title,
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(title, color = Color(0xFF111827), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = Color(0xFF1F2937), fontSize = 16.sp)
    }
}

@Composable
private fun FaqRow(question: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDCE6F1), RoundedCornerShape(18.dp))
            .border(1.dp, Color(0xFFD2DDEB), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("•", color = PrimaryBlue, fontSize = 26.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(question, color = Color(0xFF111827), fontSize = 18.sp, modifier = Modifier.weight(1f))
        Text("›", color = Color(0xFF111827), fontSize = 28.sp)
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
        Box(
            modifier = if (selected) Modifier
                .size(26.dp)
                .background(PrimaryBlue, CircleShape)
                .padding(4.dp) else Modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = if (selected) Color.White else Color(0xFF6B7B93), fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, color = if (selected) PrimaryBlue else Color(0xFF6B7B93), fontSize = 12.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LegacySupportScreen(
    onBack: () -> Unit,
    onTicketClick: (String) -> Unit
) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    var tickets by remember { mutableStateOf<List<SupportTicket>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        SupportApi.getTickets().onSuccess { resp ->
            tickets = resp.data ?: emptyList()
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.supportTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("‹ ${strings.back}", color = Color.Black) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = PrimaryBlue,
                contentColor = Color.White
            ) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (tickets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(strings.noTickets, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text(strings.noTicketsSubtitle, fontSize = 13.sp, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tickets) { ticket ->
                    TicketCard(ticket = ticket, onClick = { onTicketClick(ticket.id) })
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateTicketDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { subject, category, message ->
                scope.launch {
                    SupportApi.createTicket(subject, category, message).onSuccess { resp ->
                        resp.data?.let { newTicket ->
                            tickets = listOf(newTicket) + tickets
                        }
                    }
                    showCreateDialog = false
                }
            }
        )
    }
}

@Composable
private fun TicketCard(ticket: SupportTicket, onClick: () -> Unit) {
    val statusColor = when (ticket.status) {
        "OPEN" -> PrimaryBlue
        "IN_PROGRESS" -> Color(0xFFFFA000)
        "RESOLVED" -> SuccessGreen
        "CLOSED" -> TextSecondary
        else -> TextSecondary
    }

    val categoryIcon = when (ticket.category) {
        "DELIVERY_ISSUE" -> "D"
        "PAYMENT_ISSUE" -> "P"
        "DRIVER_COMPLAINT" -> "C"
        "APP_BUG" -> "B"
        "REFUND_REQUEST" -> "R"
        else -> "?"
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryBlueSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(categoryIcon, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(ticket.subject, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(ticket.status.replace("_", " "), fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                val lastMessage = ticket.messages.firstOrNull()?.message ?: ""
                if (lastMessage.isNotEmpty()) {
                    Text(
                        lastMessage.take(80) + if (lastMessage.length > 80) "..." else "",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(ticket.createdAt.take(10), fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun CreateTicketDialog(
    onDismiss: () -> Unit,
    onCreate: (subject: String, category: String, message: String) -> Unit
) {
    val strings = LocalStrings.current
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("OTHER") }

    val categories = listOf(
        "DELIVERY_ISSUE" to strings.deliveryIssue,
        "PAYMENT_ISSUE" to strings.paymentIssueCat,
        "DRIVER_COMPLAINT" to strings.driverComplaint,
        "REFUND_REQUEST" to strings.refundRequest,
        "APP_BUG" to strings.appBug,
        "OTHER" to strings.otherCategory
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.createTicket, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Category selection
                Text(strings.categoryLabel, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { (key, label) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedCategory == key) PrimaryBlueSurface else Color(0xFFF5F5F5))
                                .clickable { selectedCategory = key }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(label, fontSize = 11.sp, color = if (selectedCategory == key) PrimaryBlue else TextPrimary)
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.drop(3).forEach { (key, label) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedCategory == key) PrimaryBlueSurface else Color(0xFFF5F5F5))
                                .clickable { selectedCategory = key }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(label, fontSize = 11.sp, color = if (selectedCategory == key) PrimaryBlue else TextPrimary)
                        }
                    }
                }

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text(strings.subjectLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text(strings.describeIssue) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank() && message.isNotBlank()) {
                        onCreate(subject, selectedCategory, message)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) { Text(strings.submit) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}
