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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.ellipse_4
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.profile_help_support_icon
import carryon.composeapp.generated.resources.profile_logout_icon
import carryon.composeapp.generated.resources.profile_payments_wallet_icon
import carryon.composeapp.generated.resources.profile_personal_info_icon
import carryon.composeapp.generated.resources.profile_privacy_security_icon
import carryon.composeapp.generated.resources.profile_rating_icon
import carryon.composeapp.generated.resources.profile_saved_addresses_icon
import carryon.composeapp.generated.resources.profile_settings_icon
import carryon.composeapp.generated.resources.profile_shipments_icon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.DrawableResource
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
    var userName by remember { mutableStateOf("—") }
    var userPhone by remember { mutableStateOf("—") }
    var isLoading by remember { mutableStateOf(true) }
    var profileError by remember { mutableStateOf<String?>(null) }
    var totalShipments by remember { mutableStateOf(0) }
    var userRating by remember { mutableStateOf(0.0) }
    var statsError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        UserApi.getProfile()
            .onSuccess { user ->
                userName = user.name.ifBlank { "—" }
                userPhone = user.phone.ifBlank { "—" }
                profileError = null
            }
            .onFailure { profileError = it.message ?: "Failed to load profile" }

        UserApi.getUserStats()
            .onSuccess { stats ->
                totalShipments = stats.totalShipments
                userRating = stats.userRating
                statsError = null
            }
            .onFailure { statsError = it.message ?: "Failed to load stats" }

        isLoading = false
    }

    val displayName = if (isLoading) "Loading..." else userName

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
                text = if (isLoading) "Loading..." else userPhone,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF111827),
                fontSize = 16.sp
            )

            if (profileError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = profileError ?: "",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(142.dp),
                    iconRes = Res.drawable.profile_shipments_icon,
                    value = if (statsError != null) "—" else totalShipments.toString(),
                    label = "TOTAL SHIPMENTS"
                )
                StatCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(142.dp),
                    iconRes = Res.drawable.profile_rating_icon,
                    value = if (statsError != null) "—" else "${userRating.toInt()}.0",
                    label = "USER RATING"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            ProfileOptionCard(iconRes = Res.drawable.profile_personal_info_icon, title = "Personal Info", onClick = onNavigateToEditProfile)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileOptionCard(iconRes = Res.drawable.profile_saved_addresses_icon, title = "Saved Addresses", onClick = onNavigateToSavedAddresses)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileOptionCard(iconRes = Res.drawable.profile_payments_wallet_icon, title = "Payments & Wallet", subtitleBadge = "VERIFIED", onClick = onNavigateToWallet)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileOptionCard(iconRes = Res.drawable.profile_settings_icon, title = "Settings", onClick = onNavigateToSettings)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileOptionCard(iconRes = Res.drawable.profile_help_support_icon, title = "Help & Support", onClick = onNavigateToHelp)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileOptionCard(iconRes = Res.drawable.profile_privacy_security_icon, title = "Privacy & Security", onClick = onNavigateToPromo)

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x33A6D2F3), RoundedCornerShape(18.dp))
                    .clickable { onLogout() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(Res.drawable.profile_logout_icon),
                        contentDescription = "Logout",
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
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
    iconRes: DrawableResource,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0x0D000000),
                spotColor = Color(0x0D000000)
            )
            .background(Color(0x33A6D2F3), RoundedCornerShape(24.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
        }
        Text(value, color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Medium, lineHeight = 32.sp)
        Text(
            label,
            color = Color.Black,
            fontSize = 12.sp,
            letterSpacing = 0.6.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun ProfileOptionCard(
    iconRes: DrawableResource,
    title: String,
    subtitleBadge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33A6D2F3), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0x33A6D2F3), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, color = TextPrimary)
            if (subtitleBadge != null) {
                Box(
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .background(Color.White, RoundedCornerShape(999.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(subtitleBadge, color = PrimaryBlue, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Text(">", fontSize = 20.sp, color = Color(0xFF111827))
    }
}
