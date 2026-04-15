package com.company.carryon.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
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

private data class SavedAddressUi(
    val icon: String,
    val title: String,
    val lines: List<String>
)

@Composable
fun SavedAddressesScreen(
    onAddNewAddress: () -> Unit,
    onBack: () -> Unit
) {
    val addresses = listOf(
        SavedAddressUi(
            icon = "🏠",
            title = "Home",
            lines = listOf("1248 North Sheridan Rd,", "Apt 4B", "Chicago, IL 60660")
        ),
        SavedAddressUi(
            icon = "🏢",
            title = "Main Warehouse",
            lines = listOf("Global Logistics Center, Bay", "12", "8800 West Bryn Mawr Ave,", "IL 60631")
        ),
        SavedAddressUi(
            icon = "✣",
            title = "Downtown Hub",
            lines = listOf("Central Distribution Point", "200 East Randolph St,", "Chicago, IL 60601")
        )
    )

    Scaffold(containerColor = Color(0xFFF3F4F6)) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF3F4F6))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 178.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "←",
                        color = PrimaryBlue,
                        fontSize = 25.sp,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Saved Addresses",
                        color = Color(0xFF111111),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = "⌕", color = Color.Black, fontSize = 28.sp)
                }

                HorizontalDivider(color = Color(0x14000000), thickness = 1.dp)

                Text(
                    text = "Frequent Locations",
                    color = Color(0xFF111111),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 24.dp, top = 18.dp, bottom = 10.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(addresses) { address ->
                        SavedAddressCard(address = address)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0xFFF3F4F6))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Button(
                    onClick = onAddNewAddress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .shadow(10.dp, RoundedCornerShape(20.dp), clip = false),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED))
                ) {
                    Text(
                        text = "⌖  + Add New Address",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(22.dp))
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomMenuItem(icon = "🚗", label = "ROUTE", selected = false)
                    BottomMenuItem(icon = "💵", label = "EARNINGS", selected = false)
                    BottomMenuItem(icon = "📋", label = "TASKS", selected = false)
                    BottomMenuItem(icon = "👤", label = "PROFILE", selected = true)
                }
            }
        }
    }
}

@Composable
private fun SavedAddressCard(address: SavedAddressUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDCE6F1), RoundedCornerShape(26.dp))
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = address.icon, color = Color(0xFF2F80ED), fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = address.title,
                color = Color(0xFF111111),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            address.lines.forEach { line ->
                Text(
                    text = line,
                    color = Color(0xFF111111),
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(text = "✎", color = Color(0xFF111111), fontSize = 18.sp)
            Text(text = "🗑", color = Color(0xFF111111), fontSize = 18.sp)
        }
    }
}

@Composable
private fun BottomMenuItem(
    icon: String,
    label: String,
    selected: Boolean
) {
    Column(
        modifier = Modifier
            .background(
                if (selected) Color(0xFFCED4F7) else Color.Transparent,
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            color = if (selected) Color(0xFF1B3D94) else Color(0xFF75839B),
            fontSize = 16.sp
        )
        Text(
            text = label,
            color = if (selected) Color(0xFF1B3D94) else Color(0xFF75839B),
            fontSize = 10.sp,
            letterSpacing = 0.4.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
