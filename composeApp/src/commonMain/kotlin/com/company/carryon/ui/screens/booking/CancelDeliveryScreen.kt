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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.ui.theme.PrimaryBlue

@Composable
fun CancelDeliveryScreen(
    onBack: () -> Unit,
    onConfirmCancel: () -> Unit,
    onKeepBooking: () -> Unit
) {
    var selectedReason by remember { mutableStateOf("Change of plans") }
    val reasons = listOf("Change of plans", "Found another service", "Incorrect details", "Other")

    Scaffold(containerColor = Color(0xFFF5F6F8)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        text = "←",
                        color = PrimaryBlue,
                        fontSize = 20.sp,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Delivery Manager", color = Color(0xFF282B51), fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                }
                Text("⋮", color = PrimaryBlue, fontSize = 20.sp)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF0A1A2E), Color(0xFF1A355A), Color(0xFFFFFFFF))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("CANCEL\nSABOTAGE", color = Color.White.copy(alpha = 0.35f), fontSize = 22.sp, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Cancel Delivery?", color = Color.Black, fontSize = 40.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Are you sure you want to cancel this\nscheduled delivery? This action cannot be\nundone.",
                color = Color(0xFF1F2937),
                fontSize = 24.sp,
                lineHeight = 34.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFDCE6F1), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("SELECT A REASON", color = Color(0xFF111827), fontSize = 12.sp, letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold)
                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .clickable { selectedReason = reason }
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(reason, color = Color.Black, fontSize = 18.sp)
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color(0xFFE6F0FA), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedReason == reason) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(PrimaryBlue, CircleShape)
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFCEE0F1), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFFD9EAFA), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("◫", color = PrimaryBlue, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("REFUND POLICY", color = Color(0xFF4B7FB5), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                "A refund of RM 150 will be credited\nback to your wallet.",
                                color = Color(0xFF111827),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onConfirmCancel,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("⊗  Yes, Cancel Delivery", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onKeepBooking,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6EBF2)),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("No, Keep Booking", color = Color(0xFF111827), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text(
                "CARRYON LOGISTICS PROTOCOL V2.4 • TERMS\nAPPLY",
                color = Color(0xFF111827),
                fontSize = 10.sp,
                letterSpacing = 1.6.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
