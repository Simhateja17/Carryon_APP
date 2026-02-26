package com.example.carryon.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.ellipse_4
import carryon.composeapp.generated.resources.icon_people
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.icon_help
import carryon.composeapp.generated.resources.icon_messages_menu
import carryon.composeapp.generated.resources.icon_settings_menu
import carryon.composeapp.generated.resources.icon_logout_shield
import carryon.composeapp.generated.resources.icon_logout_hammer
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.data.network.UserApi
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
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
    val strings = LocalStrings.current
    var userName by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        UserApi.getProfile().onSuccess { user -> userName = user.name }
    }
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = strings.appName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            fontStyle = FontStyle.Italic
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Image(
                            painter = painterResource(Res.drawable.bell_icon),
                            contentDescription = "Notifications",
                            modifier = Modifier.size(24.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.hiThere,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = userName,
                        fontSize = 18.sp,
                        color = TextSecondary
                    )
                }

                // Circular Avatar
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD))
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ellipse_4),
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Saved Addresses Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clickable { onNavigateToSavedAddresses() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(Res.drawable.icon_help),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.CenterEnd),
                            contentScale = ContentScale.Crop,
                            alpha = 0.35f
                        )
                        Text(
                            text = strings.savedAddresses,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        )
                    }
                }

                // Rewards Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clickable { onNavigateToPromo() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(Res.drawable.icon_logout_shield),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.CenterEnd),
                            contentScale = ContentScale.Crop,
                            alpha = 0.35f
                        )
                        Text(
                            text = strings.rewards,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Menu Items
            ProfileMenuItem(iconRes = Res.drawable.icon_help, title = strings.helpAndSupport, onClick = onNavigateToHelp)
            HorizontalDivider(color = Color(0xFFF0F0F0))
            ProfileMenuItem(iconRes = Res.drawable.icon_messages_menu, title = strings.termsAndConditions, onClick = { })
            HorizontalDivider(color = Color(0xFFF0F0F0))
            ProfileMenuItem(iconRes = Res.drawable.icon_settings_menu, title = strings.settings, onClick = onNavigateToSettings)
            HorizontalDivider(color = Color(0xFFF0F0F0))
            ProfileMenuItem(iconRes = Res.drawable.icon_people, title = strings.referYourFriend, onClick = onNavigateToPromo)
            HorizontalDivider(color = Color(0xFFF0F0F0))
            ProfileMenuItem(iconRes = Res.drawable.icon_logout_shield, title = strings.walletTitle, onClick = onNavigateToWallet)
            HorizontalDivider(color = Color(0xFFF0F0F0))
            ProfileMenuItem(
                iconRes = Res.drawable.icon_logout_hammer,
                title = strings.logout,
                titleColor = Color(0xFFE53935),
                onClick = onLogout
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileMenuItem(
    iconRes: DrawableResource,
    title: String,
    titleColor: Color = TextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = title,
            modifier = Modifier.size(28.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFF0F0F0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ">",
                fontSize = 16.sp,
                color = Color(0xFF9E9E9E)
            )
        }
    }
}

