package com.company.carryon.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.ScreenHorizontalPadding
import com.company.carryon.ui.theme.ScreenVerticalPadding

@Composable
fun SupportChatScreen(onBack: () -> Unit) {
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
                            text = "←",
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
                                        text = "ONLINE",
                                        color = Color(0xB3555881),
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    Text(text = "⋮", color = Color(0xFF667085), fontSize = 22.sp)
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
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0x33A6D2F3), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📷", color = PrimaryBlue, fontSize = 20.sp)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .background(Color(0x33A6D2F3), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Type your message...",
                                color = PrimaryBlue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(text = "☺", color = PrimaryBlue, fontSize = 18.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(PrimaryBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "➤", color = Color.White, fontSize = 20.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = ScreenHorizontalPadding, vertical = ScreenVerticalPadding)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
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

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .background(PrimaryBlue, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 4.dp))
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Hi, I need help with my current\nshipment.",
                        color = Color(0xFFF1F2FF),
                        fontSize = 15.sp,
                        lineHeight = 24.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("10:42 AM", color = Color(0x99555881), fontSize = 10.sp, modifier = Modifier.padding(end = 8.dp))
            }

            Spacer(modifier = Modifier.height(26.dp))

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
                    Text(text = "🎧", fontSize = 14.sp, color = PrimaryBlue)
                }

                Column {
                    Box(
                        modifier = Modifier
                            .background(Color(0x33A6D2F3), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 24.dp))
                            .padding(horizontal = 20.dp, vertical = 13.dp)
                    ) {
                        Text(
                            text = "Hello! I am happy to help.\nWhich shipment are you\nreferring to?",
                            color = Color.Black,
                            fontSize = 15.sp,
                            lineHeight = 24.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("10:43 AM", color = Color(0x99555881), fontSize = 10.sp, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(58.dp))

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
                    Text(text = "🎧", fontSize = 14.sp, color = PrimaryBlue)
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

@Composable
private fun TypingDot() {
    Box(
        modifier = Modifier
            .size(6.dp)
            .background(Color(0x66555881), CircleShape)
    )
}
