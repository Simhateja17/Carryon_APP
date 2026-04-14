package com.company.carryon.ui.screens.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@Composable
fun ChangePasswordScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Color(0xFFF5F6F8)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F6F8))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "←",
                        color = PrimaryBlue,
                        fontSize = 24.sp,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Change Password",
                        color = Color(0xFF282B51),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "CARRYON",
                    color = Color(0xFF2563EB),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Update your password to keep\nyour account secure.",
                    color = Color.Black,
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ensure your account stays protected by using a\nstrong, unique password.",
                    color = Color.Black,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(PrimaryBlue, RoundedCornerShape(32.dp))
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(120.dp)
                            .background(Color(0x33A6D2F3), RoundedCornerShape(24.dp))
                    )
                    Column {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0x33FFFFFF), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔒", color = Color.White, fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Encryption Active", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text("Your data is secured with AES-256", color = Color(0xCCF1F2FF), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33A6D2F3), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PasswordFieldBlock("Current Password")
                    PasswordFieldBlock("New Password")
                    PasswordFieldBlock("Confirm New Password")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White)
                    )

                    Text(
                        text = "PASSWORD REQUIREMENTS",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp
                    )
                    RequirementRow("At least 8 characters")
                    RequirementRow("One uppercase letter")
                    RequirementRow("One number")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryBlue, RoundedCornerShape(999.dp))
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Update Password ⚡",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cancel",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBack() }
                        .padding(vertical = 12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .padding(horizontal = 28.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecurityTab("⌂", "Home", selected = false)
                SecurityTab("🚚", "Deliveries", selected = false)
                SecurityTab("🔒", "Security", selected = true)
                SecurityTab("◉", "Profile", selected = false)
            }
        }
    }
}

@Composable
private fun PasswordFieldBlock(label: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("••••••••", color = Color.Black, fontSize = 16.sp)
            Text("◉", color = Color.Black, fontSize = 14.sp)
        }
    }
}

@Composable
private fun RequirementRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(Color.White, CircleShape)
                .border(1.dp, Color(0xFFE2E8F0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("✓", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, color = Color.Black, fontSize = 14.sp)
    }
}

@Composable
private fun SecurityTab(icon: String, label: String, selected: Boolean) {
    Column(
        modifier = Modifier
            .background(if (selected) Color(0xFFEFF6FF) else Color.Transparent, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, color = if (selected) Color(0xFF1D4ED8) else Color(0xFF64748B), fontSize = 16.sp)
        Text(
            text = label,
            color = if (selected) Color(0xFF1D4ED8) else Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
