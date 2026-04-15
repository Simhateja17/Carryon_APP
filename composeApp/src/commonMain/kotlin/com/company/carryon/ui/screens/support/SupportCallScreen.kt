package com.company.carryon.ui.screens.support

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.customer_support_representative
import carryon.composeapp.generated.resources.icon_help
import carryon.composeapp.generated.resources.icon_map
import carryon.composeapp.generated.resources.icon_profile
import carryon.composeapp.generated.resources.icon_timer
import com.company.carryon.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
fun SupportCallScreen(
    onBack: () -> Unit,
    onEndCall: () -> Unit
) {
    var elapsedSeconds by remember { mutableStateOf(45) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            elapsedSeconds++
        }
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val callDuration = "In Call: ${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

    Scaffold(
        containerColor = Color(0xFFF7F7F8),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "←",
                        color = PrimaryBlue,
                        fontSize = 28.sp,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Text(
                        text = "Support",
                        color = Color(0xFF1D4ED8),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("⋮", color = PrimaryBlue, fontSize = 24.sp)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0x1A000000))
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xF2FFFFFF))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(iconRes = Res.drawable.icon_timer, label = "Deliveries", selected = false)
                BottomNavItem(iconRes = Res.drawable.icon_map, label = "Map", selected = false)
                BottomNavItem(iconRes = Res.drawable.icon_help, label = "Support", selected = true)
                BottomNavItem(iconRes = Res.drawable.icon_profile, label = "Profile", selected = false)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .background(Color(0xFFD0D7E5), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(192.dp)
                            .background(Color(0xFF2F80ED), CircleShape)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.customer_support_representative),
                            contentDescription = "Support representative",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Support Team",
                    color = Color.Black,
                    fontSize = 56.sp / 2,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .background(Color(0x33A6D2F3), RoundedCornerShape(999.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF2F80ED), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(callDuration, color = Color(0xFF2F80ED), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionButton(icon = "🔇", label = "Mute", selected = false)
                    ActionButton(icon = "⠿", label = "Keypad", selected = false)
                    ActionButton(icon = "🔊", label = "Speaker", selected = true)
                }

                Spacer(modifier = Modifier.height(54.dp))

                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .background(Color(0xFFFB5151), CircleShape)
                        .clickable { onEndCall() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("📞", fontSize = 30.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ActionButton(
    icon: String,
    label: String,
    selected: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(if (selected) Color(0xFFA6D2F3) else Color(0x33A6D2F3), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = PrimaryBlue, fontSize = 26.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = if (selected) Color(0xFF0050D4) else PrimaryBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BottomNavItem(
    iconRes: org.jetbrains.compose.resources.DrawableResource,
    label: String,
    selected: Boolean
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFFDCE9FF) else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 6.dp),
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
            color = if (selected) Color(0xFF1D4ED8) else Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
