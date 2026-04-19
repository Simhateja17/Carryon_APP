package com.company.carryon.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.ui.theme.PrimaryBlue

@Composable
fun ClearCacheScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
            .verticalScroll(rememberScrollState())
    ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    color = PrimaryBlue,
                    fontSize = 24.sp,
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Storage Management",
                    color = Color(0xFF282B51),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "CarryOn",
                    color = PrimaryBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                StorageOverviewCard()

                Spacer(modifier = Modifier.height(16.dp))

                OptimizedCleaningCard()

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "DATA UTILITIES",
                    color = PrimaryBlue,
                    fontSize = 16.sp,
                    letterSpacing = 1.6.sp,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                DataUtilitiesCard()

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .border(2.dp, PrimaryBlue, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = " Clear All Cache",
                        color = PrimaryBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            StorageBottomStrip()
    }
}

@Composable
private fun StorageOverviewCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33A6D2F3), RoundedCornerShape(28.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "CURRENT FOOTPRINT",
            color = Color.Black,
            fontSize = 14.sp,
            letterSpacing = 1.6.sp
        )

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "245",
                color = PrimaryBlue,
                fontSize = 48.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MB",
                color = Color(0x992F80ED),
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(Color(0xFFD9DAFF), RoundedCornerShape(999.dp))
        ) {
            Box(
                modifier = Modifier
                    .weight(0.48f)
                    .fillMaxHeight()
                    .background(PrimaryBlue)
            )
            Box(
                modifier = Modifier
                    .weight(0.33f)
                    .fillMaxHeight()
                    .background(Color(0xFF7B9CFF))
            )
            Box(
                modifier = Modifier
                    .weight(0.13f)
                    .fillMaxHeight()
                    .background(Color(0xFFCAD5ED))
            )
            Box(modifier = Modifier.weight(0.06f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        FootprintItem("Map Data", "120 MB")
        Spacer(modifier = Modifier.height(12.dp))
        FootprintItem("App Assets", "85 MB")
        Spacer(modifier = Modifier.height(12.dp))
        FootprintItem("Cached Images", "40 MB")
    }
}

@Composable
private fun FootprintItem(title: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(PrimaryBlue, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, color = Color.Black, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = PrimaryBlue, fontSize = 22.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun OptimizedCleaningCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33A6D2F3), RoundedCornerShape(28.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "Optimized Cleaning",
            color = Color.Black,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Clearing your cache will free up space.\nThis will not delete your saved addresses\nor order history.",
            color = Color.Black,
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(10.dp, RoundedCornerShape(999.dp), clip = false)
                .background(PrimaryBlue, RoundedCornerShape(999.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Clear Cache",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DataUtilitiesCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33A6D2F3), RoundedCornerShape(24.dp))
            .padding(vertical = 4.dp)
    ) {
        UtilityRow("", "Manage Offline Maps")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(1.dp)
                .background(Color(0x20A7AAD7))
        )
        UtilityRow("", "Clear Search History")
    }
}

@Composable
private fun UtilityRow(icon: String, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = PrimaryBlue)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "›",
            color = Color.Black,
            fontSize = 24.sp
        )
    }
}

@Composable
private fun StorageBottomStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomItem("◫", "DASHBOARD", false)
        BottomItem("", "DELIVERIES", false)
        BottomItem("◉", "STORAGE", true)
        BottomItem("◌", "PROFILE", false)
    }
}

@Composable
private fun BottomItem(icon: String, label: String, active: Boolean) {
    Column(
        modifier = Modifier
            .background(
                if (active) Color(0xFFEFF6FF) else Color.Transparent,
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            color = if (active) PrimaryBlue else Color(0xFF94A3B8),
            fontSize = 18.sp
        )
        Text(
            text = label,
            color = if (active) PrimaryBlue else Color(0xFF94A3B8),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
