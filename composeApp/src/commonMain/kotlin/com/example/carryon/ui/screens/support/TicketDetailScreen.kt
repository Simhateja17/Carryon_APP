package com.example.carryon.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.carryon.data.model.TicketMessage
import com.example.carryon.data.network.SupportApi
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    ticketId: String,
    onBack: () -> Unit
) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    var ticket by remember { mutableStateOf<SupportTicket?>(null) }
    var replyText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()

    LaunchedEffect(ticketId) {
        SupportApi.getTicket(ticketId).onSuccess { resp ->
            ticket = resp.data
        }
        isLoading = false
    }

    LaunchedEffect(ticket?.messages?.size) {
        val msgs = ticket?.messages ?: emptyList()
        if (msgs.isNotEmpty()) {
            listState.animateScrollToItem(msgs.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(ticket?.subject ?: strings.supportTitle, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        ticket?.let {
                            Text("${it.category.replace("_", " ")} - ${it.status.replace("_", " ")}", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("< ${strings.back}", color = Color.Black) }
                },
                actions = {
                    if (ticket?.status != "CLOSED") {
                        TextButton(onClick = {
                            scope.launch {
                                SupportApi.closeTicket(ticketId).onSuccess {
                                    ticket = ticket?.copy(status = "CLOSED")
                                }
                            }
                        }) {
                            Text(strings.closeTicket, color = ErrorRed, fontSize = 13.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (ticket?.status != "CLOSED") {
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color.White).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text(strings.typeReply, fontSize = 14.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (replyText.isNotBlank()) {
                                val msg = replyText.trim()
                                replyText = ""
                                scope.launch {
                                    SupportApi.replyToTicket(ticketId, msg).onSuccess { resp ->
                                        resp.data?.let { newMsg ->
                                            ticket = ticket?.copy(
                                                messages = ticket!!.messages + newMsg,
                                                status = "OPEN"
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text(strings.send, fontSize = 14.sp)
                    }
                }
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            val messages = ticket?.messages ?: emptyList()
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    TicketMessageBubble(msg)
                }
            }
        }
    }
}

@Composable
private fun TicketMessageBubble(msg: TicketMessage) {
    val isStaff = msg.isStaff

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isStaff) Arrangement.Start else Arrangement.End
    ) {
        Column(horizontalAlignment = if (isStaff) Alignment.Start else Alignment.End) {
            if (isStaff) {
                Text("Support", fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(2.dp))
            }
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp, topEnd = 16.dp,
                            bottomStart = if (!isStaff) 16.dp else 4.dp,
                            bottomEnd = if (!isStaff) 4.dp else 16.dp
                        )
                    )
                    .background(if (isStaff) Color.White else PrimaryBlue)
                    .padding(12.dp)
            ) {
                Column {
                    Text(msg.message, fontSize = 14.sp, color = if (isStaff) TextPrimary else Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        msg.createdAt.take(16).replace("T", " "),
                        fontSize = 10.sp,
                        color = if (isStaff) TextSecondary else Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
