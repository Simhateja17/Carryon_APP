package com.company.carryon.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.HeadsetMic
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.ScreenHorizontalPadding

@Composable
fun SupportCallScreen(
    onBack: () -> Unit,
    onEndCall: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF7F7F8),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("‹", color = PrimaryBlue, fontSize = 28.sp, modifier = Modifier.clickable { onBack() })
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Contact Support", color = Color(0xFF1D4ED8), fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                        Text("On", color = Color(0xFF282B51), fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x1A000000)))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenHorizontalPadding, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(88.dp)
                    .background(Color(0xFFE4EFFC), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.HeadsetMic, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(42.dp))
            }
            Text("Useful support contacts", color = Color(0xFF111827), fontSize = 24.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(
                "For order-specific problems, using Support Chat is best because it attaches your order and conversation to a ticket.",
                color = Color(0xFF475569),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            ContactCard(Icons.Outlined.Phone, "Operations support", "+60 3-9212 7740", "8:00 AM - 10:00 PM MYT. Use for active delivery issues.")
            ContactCard(Icons.Outlined.Email, "Email support", "support@carryon.my", "Use for account, receipt, refund, or document follow-up.")
            ContactCard(Icons.Outlined.Schedule, "Expected response", "Tickets: usually within 2 hours", "Urgent and active delivery tickets are prioritized first.")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue, RoundedCornerShape(999.dp))
                    .clickable { onEndCall() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Back to Support", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ContactCard(icon: ImageVector, title: String, value: String, detail: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(42.dp).background(Color(0xFFE4EFFC), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color(0xFF64748B), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(value, color = Color(0xFF111827), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(detail, color = Color(0xFF475569), fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}
