package com.company.carryon.ui.screens.booking

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.ScreenHorizontalPadding
import com.company.carryon.util.formatOrderDisplayId

@Composable
fun CancellationUnavailableScreen(
    bookingId: String,
    onBack: () -> Unit,
    onChatSupport: () -> Unit,
    onGoBack: () -> Unit
) {
    Scaffold(containerColor = Color(0xFFF5F6F8)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = ScreenHorizontalPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "‹",
                        color = Color.Black,
                        fontSize = 20.sp,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Text(
                        text = "  Cancellation Unavailable",
                        color = Color(0xFF282B51),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                    Text("On", color = Color(0xFF282B51), fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .background(Color(0xFFC9CBE1), CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⊘", color = Color(0xFFC62828), fontSize = 38.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Cannot Cancel Delivery",
                color = Color(0xFF1E2345),
                fontSize = 52.sp,
                lineHeight = 58.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "We're sorry, but this delivery can no longer\nbe cancelled because the driver has already\nreached the pickup location.",
                color = Color(0xFF111827),
                fontSize = 34.sp,
                lineHeight = 48.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFDCE6F1), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFD2E5F9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("", fontSize = 18.sp)
                    }
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(10.dp))
                    Column {
                        Text(
                            "DRIVER ARRIVED",
                            color = Color(0xFF4B8AC8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .background(Color(0xFFC8DCF1), RoundedCornerShape(999.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Order ID: ${formatOrderDisplayId(bookingId)}",
                            color = Color(0xFF1E2345),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = onChatSupport,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("◉ Chat with Support", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onGoBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6EBF2)),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Go Back", color = Color(0xFF111827), fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "View our refund policy for more details.",
                color = Color(0xFF4B5563),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
