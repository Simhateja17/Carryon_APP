package com.company.carryon.ui.screens.profile

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LaptopMac
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.ui.theme.PrimaryBlue

@Composable
fun LoggedInDevicesScreen(onBack: () -> Unit) {
    Scaffold(containerColor = Color(0xFFF5F6F8)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F6F8))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "←",
                        color = PrimaryBlue,
                        fontSize = 24.sp,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Security", color = Color(0xFF000000), fontSize = 28.sp, fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                    Text("On", color = Color(0xFF282B51), fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    "Logged in Devices",
                    color = Color.Black,
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "You are currently logged in on these devices.\nMonitor and manage your active CarryOn\nsessions.",
                    color = Color.Black,
                    fontSize = 16.sp,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                DeviceCard(
                    icon = Icons.Outlined.Smartphone,
                    title = "iPhone 15 Pro (Current\nDevice)",
                    location = "San Francisco,\nCA",
                    status = "Active\nnow",
                    showLogoutIcon = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                DeviceCard(
                    icon = Icons.Outlined.LaptopMac,
                    title = "MacBook Pro 14\"",
                    location = "San\nFrancisco,\nCA",
                    status = "Last active: 2\nhours ago",
                    showLogoutIcon = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                DeviceCard(
                    icon = Icons.Outlined.Smartphone,
                    title = "Samsung Galaxy S23",
                    location = "New York,\nNY",
                    status = "Last active: 3\ndays ago",
                    showLogoutIcon = true
                )

                Spacer(modifier = Modifier.height(30.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryBlue, RoundedCornerShape(999.dp))
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Log out of all other sessions",
                        color = Color(0xFFF1F2FF),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp)
                        .background(Color(0x33A6D2F3), RoundedCornerShape(16.dp))
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column {
                        Text(
                            "PRIVACY PROTECTION",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.4.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Ensure your account stays private by regularly reviewing these active sessions.",
                            color = Color.Black,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun DeviceCard(
    icon: ImageVector,
    title: String,
    location: String,
    status: String,
    showLogoutIcon: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33A6D2F3), RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(26.dp),
                    tint = PrimaryBlue
                )
            }
            Column {
                Text(title, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp)
                val isActiveNow = status.startsWith("Active")
                Spacer(modifier = Modifier.height(2.dp))
                if (isActiveNow) {
                    Text(
                        "Active now",
                        color = PrimaryBlue,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(location, color = Color.Black, fontSize = 14.sp, lineHeight = 20.sp)
                if (!isActiveNow) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        status,
                        color = Color.Black,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        if (showLogoutIcon) {
            Icon(
                imageVector = Icons.Outlined.Logout,
                contentDescription = "Log out session",
                modifier = Modifier.size(24.dp),
                tint = PrimaryBlue
            )
        } else {
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}
