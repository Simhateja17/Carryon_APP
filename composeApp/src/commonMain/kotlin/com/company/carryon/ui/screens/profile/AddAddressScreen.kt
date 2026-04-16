package com.company.carryon.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.ui.theme.PrimaryBlue

@Composable
fun AddAddressScreen(
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    var selectedType by remember { mutableStateOf("Home") }

    Scaffold(containerColor = Color(0xFFF3F4F6)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF3F4F6))
                .verticalScroll(rememberScrollState())
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
                Text("Add Address", color = Color(0xFF111111), fontSize = 36.sp, fontWeight = FontWeight.Medium)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF2A7FA8), Color(0xFF4FA2C8), Color(0x33FFFFFF))
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-34).dp)
                    .background(Color(0xFFEFF2F5), RoundedCornerShape(22.dp))
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⌕", fontSize = 28.sp, color = Color(0xFF111111))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Search for address or landmark",
                        color = Color(0xFF111111),
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF2F80ED), RoundedCornerShape(22.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⌖", color = Color.White, fontSize = 22.sp)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-18).dp)
            ) {
                Label("ADDRESS LABEL")
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AddressTypeChip("Home", selectedType == "Home") { selectedType = "Home" }
                    AddressTypeChip("Office", selectedType == "Office") { selectedType = "Office" }
                    AddressTypeChip("Warehouse", selectedType == "Warehouse") { selectedType = "Warehouse" }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Label("FULL ADDRESS")
                FilledField("")

                Spacer(modifier = Modifier.height(14.dp))
                Label("FLOOR / UNIT / LANDMARK")
                FilledField("")

                Spacer(modifier = Modifier.height(14.dp))
                Label("CITY")
                FilledField("")

                Spacer(modifier = Modifier.height(14.dp))
                Label("POSTAL CODE")
                FilledField("")

                Spacer(modifier = Modifier.height(18.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFDCE6F1), RoundedCornerShape(20.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF2F80ED), RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("↺", color = Color.White, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Recent Destinations", color = Color(0xFF111111), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text("Frequently used pickup locations", color = Color(0xFF2F80ED), fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF4F6F8), RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("➤", color = Color(0xFF111111), fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("890 Innovation Blvd, South Side", color = Color(0xFF111111), fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED))
                ) {
                    Text("💾  Save Address", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(
        text = text,
        color = Color(0xFF111111),
        fontSize = 10.sp,
        letterSpacing = 1.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun AddressTypeChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(52.dp)
            .background(
                if (selected) Color(0xFF2F80ED) else Color(0xFFF5F6F8),
                RoundedCornerShape(26.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 22.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) Color.White else Color(0xFF2F80ED),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun FilledField(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(Color(0xFFDCE6F1), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 16.dp)
    ) {
        Text(text, color = Color(0xFF111111), fontSize = 18.sp)
    }
}
