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
import carryon.composeapp.generated.resources.mini_van
import carryon.composeapp.generated.resources.open_truck
import carryon.composeapp.generated.resources.truck
import carryon.composeapp.generated.resources.van_vehicle
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.ScreenHorizontalPadding
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private enum class VehicleType(val apiValue: String) {
    BIKE("BIKE"),
    CAR("CAR"),
    PICKUP("PICKUP"),
    VAN_7FT("VAN_7FT"),
    VAN_9FT("VAN_9FT"),
    LORRY_10FT("LORRY_10FT"),
    LORRY_14FT("LORRY_14FT"),
    LORRY_17FT("LORRY_17FT"),
}

@Composable
fun DefaultVehicleScreen(
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    var selectedVehicle by remember { mutableStateOf(VehicleType.BIKE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ScreenHorizontalPadding)
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
                title = "Motorcycle",
                description = "Small packages up to 10 kg.\n0.3 × 0.3 × 0.3 m. Ideal for\ndocuments, food, and urgent\ncity deliveries.",
                imageRes = Res.drawable.bike,
                selected = selectedVehicle == VehicleType.BIKE,
                onClick = { selectedVehicle = VehicleType.BIKE }
            )
            Spacer(modifier = Modifier.height(16.dp))
            VehicleCard(
                title = "Car",
                description = "Up to 40 kg. 0.5 × 0.5 × 0.5 m.\nGrocery hauls, flowers,\nfragile parcels.",
                imageRes = Res.drawable.car_4_seater,
                selected = selectedVehicle == VehicleType.CAR,
                onClick = { selectedVehicle = VehicleType.CAR }
            )
            Spacer(modifier = Modifier.height(16.dp))
            VehicleCard(
                title = "Pickup (4x4)",
                description = "Up to 250 kg.\n1.2 × 0.9 × 0.9 m.\nSmall boxes, furniture,\nbicycles.",
                imageRes = Res.drawable.open_truck,
                selected = selectedVehicle == VehicleType.PICKUP,
                onClick = { selectedVehicle = VehicleType.PICKUP }
            )
            Spacer(modifier = Modifier.height(16.dp))
            VehicleCard(
                title = "Van 7-ft",
                description = "Up to 500 kg.\n1.7 × 1 × 1.2 m.\nSmall fridge, bike,\nwashing machine.",
                imageRes = Res.drawable.van_vehicle,
                selected = selectedVehicle == VehicleType.VAN_7FT,
                onClick = { selectedVehicle = VehicleType.VAN_7FT }
            )
            Spacer(modifier = Modifier.height(16.dp))
            VehicleCard(
                title = "Large Van 9-ft",
                description = "Up to 800 kg.\n2.7 × 1.3 × 1.2 m.\nSmall fridge, washer,\n1-seater sofa.",
                imageRes = Res.drawable.mini_van,
                selected = selectedVehicle == VehicleType.VAN_9FT,
                onClick = { selectedVehicle = VehicleType.VAN_9FT }
            )
            Spacer(modifier = Modifier.height(16.dp))
            VehicleCard(
                title = "Small Lorry 10-ft",
                description = "Up to 1000 kg.\n2.9 × 1.5 × 1.5 m.\nSmall bed, 2-seater sofa,\nfridge.",
                imageRes = Res.drawable.truck,
                selected = selectedVehicle == VehicleType.LORRY_10FT,
                onClick = { selectedVehicle = VehicleType.LORRY_10FT }
            )
            Spacer(modifier = Modifier.height(16.dp))
            VehicleCard(
                title = "Medium Lorry 14-ft",
                description = "Up to 2500 kg.\n4.2 × 2 × 2 m.\nQueen bed, wardrobe,\nvases.",
                imageRes = Res.drawable.truck,
                selected = selectedVehicle == VehicleType.LORRY_14FT,
                onClick = { selectedVehicle = VehicleType.LORRY_14FT }
            )
            Spacer(modifier = Modifier.height(16.dp))
            VehicleCard(
                title = "Large Lorry 17-ft",
                description = "Up to 4000 kg.\n5.1 × 2 × 2.1 m.\nKing bed, washer,\n3-seater sofa.",
                imageRes = Res.drawable.truck,
                selected = selectedVehicle == VehicleType.LORRY_17FT,
                onClick = { selectedVehicle = VehicleType.LORRY_17FT }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onSave(selectedVehicle.apiValue) },
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
