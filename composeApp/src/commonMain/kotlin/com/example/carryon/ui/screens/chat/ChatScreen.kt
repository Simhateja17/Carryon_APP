package com.example.carryon.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.carryon.data.model.ChatMessage
import com.example.carryon.data.network.ChatApi
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    bookingId: String,
    driverName: String = "Driver",
    currentUserId: String = "",
    onBack: () -> Unit
) {
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var messageText by remember { mutableStateOf("") }
    var quickMessages by remember { mutableStateOf<List<String>>(emptyList()) }
    var showQuickMessages by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Load messages and poll for new ones
    LaunchedEffect(bookingId) {
        // Load quick messages
        ChatApi.getQuickMessages(bookingId).onSuccess { resp ->
            quickMessages = resp.data ?: emptyList()
        }

        // Poll for messages
        while (true) {
            ChatApi.getMessages(bookingId).onSuccess { resp ->
                val newMessages = resp.data ?: emptyList()
                if (newMessages.size != messages.size) {
                    messages = newMessages
                }
            }
            delay(3000)
        }
    }

    // Scroll to bottom when messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(driverName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(strings.chatWithDriver, fontSize = 12.sp, color = TextSecondary)
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("< ${strings.back}", color = Color.Black) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(Color.White)) {
                // Quick messages
                if (showQuickMessages && quickMessages.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(quickMessages) { msg ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PrimaryBlueSurface)
                                    .clickable {
                                        messageText = msg
                                        showQuickMessages = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(msg, fontSize = 12.sp, color = PrimaryBlue)
                            }
                        }
                    }
                }

                // Input row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick message toggle
                    IconButton(onClick = { showQuickMessages = !showQuickMessages }) {
                        Text("...", fontSize = 20.sp, color = PrimaryBlue)
                    }

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text(strings.typeMessage, fontSize = 14.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = Color(0xFFF8F8F8),
                            unfocusedContainerColor = Color(0xFFF8F8F8)
                        ),
                        singleLine = true,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                val msg = messageText.trim()
                                messageText = ""
                                scope.launch {
                                    ChatApi.sendMessage(bookingId, msg).onSuccess { resp ->
                                        resp.data?.let { newMsg ->
                                            messages = messages + newMsg
                                        }
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text(strings.send, fontSize = 14.sp)
                    }
                }
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        if (messages.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(strings.noMessagesYet, color = TextSecondary, fontSize = 14.sp)
                    Text(strings.startConversation, color = TextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    MessageBubble(
                        message = msg,
                        isOwnMessage = msg.senderType == "USER"
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    isOwnMessage: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isOwnMessage) 16.dp else 4.dp,
                        bottomEnd = if (isOwnMessage) 4.dp else 16.dp
                    )
                )
                .background(if (isOwnMessage) PrimaryBlue else Color.White)
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = message.message,
                    fontSize = 14.sp,
                    color = if (isOwnMessage) Color.White else TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.createdAt.takeLast(8).take(5),
                    fontSize = 10.sp,
                    color = if (isOwnMessage) Color.White.copy(alpha = 0.7f) else TextSecondary
                )
            }
        }
    }
}
