package com.company.carryon.ui.screens.profile

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.bike
import carryon.composeapp.generated.resources.car_4_seater
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.icon_profile
import carryon.composeapp.generated.resources.icon_timer
import carryon.composeapp.generated.resources.payment_icon
import carryon.composeapp.generated.resources.van_vehicle
import com.company.carryon.ui.theme.PrimaryBlue
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private enum class VehicleType { BIKE, CAR, VAN }

@Composable
fun DefaultVehicleScreen(
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    var selectedVehicle by remember { mutableStateOf(VehicleType.BIKE) }

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF2F3F6))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(Res.drawable.icon_home, "Home", false)
                BottomNavItem(Res.drawable.icon_timer, "Deliveries", false)
                BottomNavItem(Res.drawable.payment_icon, "Wallet", false)
                BottomNavItem(Res.drawable.icon_profile, "Profile", true)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "←",
                    color = PrimaryBlue,
                    fontSize = 28.sp,
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text("Settings", color = Color(0xFF282B51), fontSize = 36.sp / 2, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Default Vehicle",
                color = Color.Black,
                fontSize = 72.sp / 2,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select your most frequent delivery type",
                color = Color.Black,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(26.dp))

            VehicleCard(
                title = "Bike",
                description = "Small packages up to\n5 kg. Ideal for\ndocuments, food, and\nurgent city deliveries.",
                imageRes = Res.drawable.bike,
                selected = selectedVehicle == VehicleType.BIKE,
                onClick = { selectedVehicle = VehicleType.BIKE }
            )
            Spacer(modifier = Modifier.height(16.dp))
            VehicleCard(
                title = "Car",
                description = "Medium loads 5-20\nkg. Best for grocery\nhauls and multiple\nmedium-sized boxes.",
                imageRes = Res.drawable.car_4_seater,
                selected = selectedVehicle == VehicleType.CAR,
                onClick = { selectedVehicle = VehicleType.CAR }
            )
            Spacer(modifier = Modifier.height(16.dp))
            VehicleCard(
                title = "Van",
                description = "Bulk shipments.\nPerfect for furniture,\nlarge appliances, or\nbusiness inventory.",
                imageRes = Res.drawable.van_vehicle,
                selected = selectedVehicle == VehicleType.VAN,
                onClick = { selectedVehicle = VehicleType.VAN }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    onSave(
                        when (selectedVehicle) {
                            VehicleType.BIKE -> "Bike"
                            VehicleType.CAR -> "Car"
                            VehicleType.VAN -> "Van"
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E7FE1))
            ) {
                Text("Save Preferences", color = Color(0xFFF1F2FF), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun VehicleCard(
    title: String,
    description: String,
    imageRes: DrawableResource,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) Color.White else Color(0x33A6D2F3), RoundedCornerShape(12.dp))
            .border(2.dp, if (selected) Color(0xFF2F80ED) else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(if (selected) Color(0x33A6D2F3) else Color.White, RoundedCornerShape(12.dp))
                .border(if (selected) 2.dp else 0.dp, if (selected) Color(0xFF2F80ED) else Color.Transparent, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = title,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                if (selected) {
                    Box(
                        modifier = Modifier
                            .background(Color(0x33A6D2F3), RoundedCornerShape(999.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("SELECTED", color = Color(0xFF2F80ED), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = Color.Black,
                fontSize = 14.sp,
                lineHeight = 23.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(if (selected) Color(0xFF4E7FE1) else Color.Transparent, CircleShape)
                .border(if (selected) 0.dp else 2.dp, Color(0xFF4E7FE1), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Text("✓", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    iconRes: DrawableResource,
    label: String,
    selected: Boolean
) {
    Column(
        modifier = Modifier
            .background(if (selected) Color(0xFFDCE9FF) else Color.Transparent, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = label,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (selected) Color(0xFF2F55D4) else Color(0xFF8A94AA),
            fontWeight = FontWeight.Medium
        )
    }
}
