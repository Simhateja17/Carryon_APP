package com.company.carryon.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.network.AiChatMessage
import com.company.carryon.data.network.SupportApi
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.ScreenHorizontalPadding
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private data class SupportChatMessage(
    val text: String,
    val isUser: Boolean,
    val time: String
)

private fun formatCurrentTime(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val h = now.hour
    val m = now.minute.toString().padStart(2, '0')
    val ap = if (h < 12) "AM" else "PM"
    val h12 = if (h == 0) 12 else if (h > 12) h - 12 else h
    return "$h12:$m $ap"
}

@Composable
fun SupportChatScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember {
        mutableStateOf(
            listOf(
                SupportChatMessage(
                    text = "Hi! I'm CarryOn's AI assistant. How can I help you today? Ask me anything about your orders, delivery charges, or how to use the app.",
                    isUser = false,
                    time = formatCurrentTime()
                )
            )
        )
    }
    var conversationHistory by remember { mutableStateOf(listOf<AiChatMessage>()) }
    var isTyping by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }

    // Scroll to bottom when messages change or typing indicator appears
    LaunchedEffect(messages.size, isTyping) {
        val targetIndex = messages.size + (if (isTyping) 1 else 0)
        if (targetIndex > 0) listState.animateScrollToItem(targetIndex)
    }

    Scaffold(
        containerColor = Color(0xFFF5F6F8),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "‹",
                            color = PrimaryBlue,
                            fontSize = 26.sp,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clickable { onBack() }
                        )
                        Box(modifier = Modifier.padding(top = 2.dp)) {
                            Column {
                                Text(
                                    text = "Support Chat",
                                    fontSize = 18.sp,
                                    lineHeight = 22.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111111)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(0xFF10B981), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AI ASSISTANT",
                                        color = Color(0xB3555881),
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                        Text("On", color = Color(0xFF282B51), fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE7E7EE))
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xF2FFFFFF))
                    .border(1.dp, Color(0xFFE0E0FF))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "Type your message...",
                                color = PrimaryBlue.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFB0C4DE),
                            focusedContainerColor = Color(0x33A6D2F3),
                            unfocusedContainerColor = Color(0x33A6D2F3)
                        ),
                        singleLine = true,
                        maxLines = 1,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFF111827),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (inputText.isBlank()) Color(0xFFB0C4DE) else PrimaryBlue,
                                CircleShape
                            )
                            .clickable(enabled = inputText.isNotBlank() && !isTyping) {
                                val userMsg = inputText.trim()
                                inputText = ""

                                val sentHistory = conversationHistory.toList()
                                messages = messages + SupportChatMessage(userMsg, isUser = true, time = formatCurrentTime())
                                isTyping = true

                                scope.launch {
                                    SupportApi.sendAiMessage(userMsg, sentHistory)
                                        .onSuccess { response ->
                                            val reply = response.data?.reply
                                                ?: "Sorry, I couldn't understand that. Please try again."
                                            isTyping = false
                                            messages = messages + SupportChatMessage(reply, isUser = false, time = formatCurrentTime())
                                            conversationHistory = sentHistory +
                                                AiChatMessage("user", userMsg) +
                                                AiChatMessage("model", reply)
                                        }
                                        .onFailure {
                                            isTyping = false
                                            messages = messages + SupportChatMessage(
                                                "Something went wrong. Please try again.",
                                                isUser = false,
                                                time = formatCurrentTime()
                                            )
                                        }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "➤", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = ScreenHorizontalPadding),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0x33A6D2F3), RoundedCornerShape(999.dp))
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "TODAY",
                            color = PrimaryBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            items(messages) { msg ->
                if (msg.isUser) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .background(
                                    PrimaryBlue,
                                    RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 4.dp)
                                )
                                .padding(horizontal = 20.dp, vertical = 14.dp)
                        ) {
                            Text(text = msg.text, color = Color(0xFFF1F2FF), fontSize = 15.sp, lineHeight = 24.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(msg.time, color = Color(0x99555881), fontSize = 10.sp, modifier = Modifier.padding(end = 8.dp))
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0x33A6D2F3), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🤖", fontSize = 14.sp)
                        }
                        Column {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color(0x33A6D2F3),
                                        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 24.dp)
                                    )
                                    .padding(horizontal = 20.dp, vertical = 13.dp)
                            ) {
                                Text(text = msg.text, color = Color.Black, fontSize = 15.sp, lineHeight = 24.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(msg.time, color = Color(0x99555881), fontSize = 10.sp, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }

            if (isTyping) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0x33A6D2F3), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🤖", fontSize = 14.sp)
                        }
                        Row(
                            modifier = Modifier
                                .background(Color(0x33A6D2F3), RoundedCornerShape(999.dp))
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            TypingDot()
                            TypingDot()
                            TypingDot()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TypingDot() {
    Box(
        modifier = Modifier
            .size(6.dp)
            .background(Color(0x66555881), CircleShape)
    )
}
