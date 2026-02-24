package com.example.carryon.ui.screens.tracking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.icon_timer
import carryon.composeapp.generated.resources.icon_people
import carryon.composeapp.generated.resources.payment_icon
import carryon.composeapp.generated.resources.map_background
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveShipmentScreen(
    onTrackShipments: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToOrders: () -> Unit = {},
    onNavigateToWallet: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onChatWithDriver: (String, String) -> Unit = { _, _ -> }
) {
    val strings = LocalStrings.current
    var shareWithNeighbors by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Carry",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = " On",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlueDark
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Text("☰", fontSize = 22.sp, color = TextPrimary)
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
        bottomBar = {
            ActiveShipmentBottomNav(
                onNavigateToHome = onNavigateToHome,
                onNavigateToOrders = onNavigateToOrders,
                onNavigateToWallet = onNavigateToWallet,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Map Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.map_background),
                    contentDescription = "Route Map",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Route overlay line (visual indicator)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x33000000))
                )

                // From/To pin labels
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "13 min  ●●●",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Share with neighbors checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = shareWithNeighbors,
                        onCheckedChange = { shareWithNeighbors = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryBlue,
                            uncheckedColor = TextSecondary
                        ),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = strings.shareWithNeighbors,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = strings.forExtraDiscount,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider
                Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))

                // Customer Name
                Text(
                    text = "Sara",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Order Info
                Text(
                    text = "order: 560023 | #33, Brook-field, 560013",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Dispatched / Deliver by Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = strings.dispatched,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "4:30 pm | 23 Jan 24",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = strings.deliverBy,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "8:30 pm | 24 Jan 24",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Chat with Driver Button
                OutlinedButton(
                    onClick = { onChatWithDriver("560023", "Sara") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
                ) {
                    Text(
                        text = strings.chatWithDriver,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Track Shipments Button
                Button(
                    onClick = onTrackShipments,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    )
                ) {
                    Text(
                        text = strings.trackShipments,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ActiveShipmentBottomNav(
    onNavigateToHome: () -> Unit,
    onNavigateToOrders: () -> Unit = {},
    onNavigateToWallet: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val strings = LocalStrings.current
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Pair(Res.drawable.icon_home, strings.navHome),
            Pair(Res.drawable.icon_timer, strings.navOrders),
            Pair(Res.drawable.payment_icon, strings.navPayments),
            Pair(Res.drawable.icon_people, strings.navAccount)
        )

        items.forEachIndexed { index, (iconRes, label) ->
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = label,
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                selected = index == 0,
                onClick = {
                    when (index) {
                        0 -> onNavigateToHome()
                        1 -> onNavigateToOrders()
                        2 -> onNavigateToWallet()
                        3 -> onNavigateToProfile()
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = if (index == 0) PrimaryBlueSurface else Color.Transparent
                )
            )
        }
    }
}
