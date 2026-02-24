package com.example.carryon.ui.screens.support

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
import com.example.carryon.data.model.SupportTicket
import com.example.carryon.data.network.SupportApi
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
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
                    TextButton(onClick = onBack) { Text("< ${strings.back}", color = Color.Black) }
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
