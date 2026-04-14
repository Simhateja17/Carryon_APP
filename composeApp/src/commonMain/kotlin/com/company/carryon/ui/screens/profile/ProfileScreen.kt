package com.company.carryon.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.ellipse_4
import carryon.composeapp.generated.resources.bell_icon
import org.jetbrains.compose.resources.painterResource
import com.company.carryon.data.network.UserApi
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.TextPrimary
import com.company.carryon.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToSavedAddresses: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToCalculate: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToTrackShipment: () -> Unit,
    onNavigateToDriverRating: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToWallet: () -> Unit = {},
    onNavigateToPromo: () -> Unit = {},
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    var userName by remember { mutableStateOf("") }
    var profileError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        UserApi.getProfile()
            .onSuccess { user -> userName = user.name; profileError = false }
            .onFailure { profileError = true }
    }

    val displayName = if (profileError) "Guest User" else userName.ifBlank { "Devansh" }

    Scaffold(
        containerColor = Color(0xFFF5F6F8)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F6F8))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("←", color = PrimaryBlue, fontSize = 22.sp, modifier = Modifier.clickable { onBack() })
                Spacer(modifier = Modifier.width(8.dp))
                Text("Profile", color = Color(0xFF1F2937), fontSize = 32.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDDEAFE)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.bell_icon),
                        contentDescription = "Notification",
                        modifier = Modifier.size(18.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(130.dp)) {
                    Image(
                        painter = painterResource(Res.drawable.ellipse_4),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(34.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .align(Alignment.BottomEnd)
                            .background(PrimaryBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✎", color = Color.White, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = displayName,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF111827),
                fontSize = 44.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "+1 (555) 0123-4567",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF111827),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(108.dp),
                    icon = "🚚",
                    value = "24",
                    label = "TOTAL SHIPMENTS"
                )
                StatCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(108.dp),
                    icon = "⭐",
                    value = "4.9",
                    label = "USER RATING"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            ProfileOptionCard(icon = "◉", title = "Personal Info", onClick = onNavigateToEditProfile)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileOptionCard(icon = "🗺", title = "Saved Addresses", onClick = onNavigateToSavedAddresses)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileOptionCard(icon = "▣", title = "Payments & Wallet", subtitleBadge = "VERIFIED", onClick = onNavigateToWallet)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileOptionCard(icon = "⚙", title = "Settings", onClick = onNavigateToSettings)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileOptionCard(icon = "🎧", title = "Help & Support", onClick = onNavigateToHelp)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileOptionCard(icon = "🛡", title = "Privacy & Security", onClick = onNavigateToPromo)

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3EDF8), RoundedCornerShape(18.dp))
                    .clickable { onLogout() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("↪  Logout", color = PrimaryBlue, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "CARRYON V.2.4.0",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF6B7280),
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .background(Color(0xFFDCE6F1), RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(Color(0xFFCCE0F6), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = PrimaryBlue)
        }
        Text(value, color = Color(0xFF111827), fontSize = 38.sp, fontWeight = FontWeight.SemiBold)
        Text(label, color = Color(0xFF1F2937), fontSize = 12.sp)
    }
}

@Composable
private fun ProfileOptionCard(
    icon: String,
    title: String,
    subtitleBadge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDCE6F1), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFD5E1EF), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color(0xFFCFE1F4), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) { Text(icon, color = PrimaryBlue) }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, color = TextPrimary)
            if (subtitleBadge != null) {
                Box(
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .background(Color(0xFFD7E9FF), RoundedCornerShape(999.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(subtitleBadge, color = PrimaryBlue, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Text(">", fontSize = 20.sp, color = Color(0xFF111827))
    }
}

