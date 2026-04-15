package com.company.carryon.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.logged_in_device_iphone
import carryon.composeapp.generated.resources.logged_in_device_pc
import com.company.carryon.ui.theme.PrimaryBlue
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

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
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "←",
                    color = PrimaryBlue,
                    fontSize = 24.sp,
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Security", color = PrimaryBlue, fontSize = 40.sp, fontWeight = FontWeight.SemiBold)
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
                    fontWeight = FontWeight.SemiBold
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
                    icon = Res.drawable.logged_in_device_iphone,
                    title = "iPhone 15 Pro (Current\nDevice)",
                    location = "San Francisco,\nCA",
                    status = "Active\nnow",
                    showLogoutIcon = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                DeviceCard(
                    icon = Res.drawable.logged_in_device_pc,
                    title = "MacBook Pro 14\"",
                    location = "San\nFrancisco,\nCA",
                    status = "Last active: 2\nhours ago",
                    showLogoutIcon = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                DeviceCard(
                    icon = Res.drawable.logged_in_device_iphone,
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
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                        .background(Color(0xFFE7E6FF), RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            "PRIVACY PROTECTION",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Ensure your account stays\nprivate by regularly reviewing\nthese active sessions.",
                            color = Color.Black,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 28.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecurityBottomTab("⌂", "Home", false)
                SecurityBottomTab("🚚", "Deliveries", false)
                SecurityBottomTab("🔒", "Security", true)
                SecurityBottomTab("◉", "Profile", false)
            }
        }
    }
}

@Composable
private fun DeviceCard(
    icon: DrawableResource,
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
                    .size(62.dp)
                    .background(Color(0xFFF3F4F6), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = title,
                    modifier = Modifier.size(36.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Column {
                Text(title, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(location, color = Color.Black, fontSize = 14.sp, lineHeight = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(4.dp).background(Color.White, RoundedCornerShape(99.dp)))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        status,
                        color = if (status.startsWith("Active")) PrimaryBlue else Color.Black,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = if (status.startsWith("Active")) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        if (showLogoutIcon) {
            Text("⇥", color = PrimaryBlue, fontSize = 30.sp)
        } else {
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

@Composable
private fun SecurityBottomTab(icon: String, label: String, selected: Boolean) {
    Column(
        modifier = Modifier
            .background(if (selected) Color(0xFFEFF6FF) else Color.Transparent, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, color = if (selected) PrimaryBlue else Color(0xFF64748B), fontSize = 16.sp)
        Text(
            label,
            color = if (selected) PrimaryBlue else Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
