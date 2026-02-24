package com.example.carryon.ui.screens.tracking

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.carryon_logo
import carryon.composeapp.generated.resources.icon_documents
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.payment_icon
import carryon.composeapp.generated.resources.icon_people
import carryon.composeapp.generated.resources.icon_timer
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.track_sent
import carryon.composeapp.generated.resources.track_transit
import carryon.composeapp.generated.resources.track_journey
import carryon.composeapp.generated.resources.track_accepted
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackShipmentScreen(
    onSearch: (String) -> Unit,
    onViewDetails: (String) -> Unit,
    onNavigateToHistory: () -> Unit = {}
) {
    val strings = LocalStrings.current
    var trackingNumber by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Carry",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = " On",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlueDark
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Text("☰", fontSize = 20.sp)
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
            BottomNavigationBar(selectedIndex = 0, onNavigateToHistory = onNavigateToHistory)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Header with avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.trackYourShipment,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.carryon_logo),
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Tracking Number Input
            OutlinedTextField(
                value = trackingNumber,
                onValueChange = { trackingNumber = it },
                placeholder = { Text(strings.enterTrackingNumber, color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color(0xFFF8F8F8),
                    unfocusedContainerColor = Color(0xFFF8F8F8),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search Button
            Button(
                onClick = { onSearch(trackingNumber) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Text(
                    text = strings.search,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Your Package Section
            Text(
                text = strings.yourPackage,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Package Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Package Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.icon_documents),
                                contentDescription = "Documents",
                                modifier = Modifier.size(22.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = strings.documents,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = "menu.number",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    
                    // Details Grid
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.from,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "North Bekasi",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.destination,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Bandung",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.delivery,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "JnE Express",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.itemWeight,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "1Kg",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    // Pagination dots
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { i ->
                            Box(
                                modifier = Modifier
                                    .size(if (i == 0) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (i == 0) Color.White else Color.White.copy(alpha = 0.4f))
                            )
                            if (i < 2) Spacer(modifier = Modifier.width(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // View Details Button
                    Button(
                        onClick = { onViewDetails("ORDB1234") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF97CBF1),
                                        Color(0xFFB7DAF5)
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = strings.viewDetails,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Details Timeline
            Text(
                text = strings.details,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Timeline Items
            TrackingTimelineItem(
                iconRes = Res.drawable.track_sent,
                title = strings.sentPackage,
                location = "JnE.north Beko时",
                date = "22Dec,2021",
                time = "12:30pm",
                isCompleted = true,
                isLast = false
            )

            TrackingTimelineItem(
                iconRes = Res.drawable.track_transit,
                title = strings.transit,
                location = "3nE.Bandung",
                date = "22Dec,2021",
                time = "12:30pm",
                isCompleted = true,
                isLast = false
            )

            TrackingTimelineItem(
                iconRes = Res.drawable.track_journey,
                title = strings.onAJourney,
                location = "your destination",
                date = "22Dec,2021",
                time = "12:30pm",
                isCompleted = true,
                isLast = false
            )

            TrackingTimelineItem(
                iconRes = Res.drawable.track_accepted,
                title = strings.accepted,
                location = "By Fernando",
                date = "22Dec,2021",
                time = "12:30pm",
                isCompleted = true,
                isLast = true
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TrackingTimelineItem(
    iconRes: DrawableResource,
    title: String,
    location: String,
    date: String,
    time: String,
    isCompleted: Boolean,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(70.dp)
        ) {
            // Circle with icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFFB3D9F2),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    contentScale = ContentScale.Fit
                )
            }
            
            // Dotted line connector
            if (!isLast) {
                Canvas(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .padding(top = 4.dp)
                ) {
                    val dashPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width / 2, 0f)
                        lineTo(size.width / 2, size.height)
                    }
                    drawPath(
                        path = dashPath,
                        color = Color(0xFFB3D9F2),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(5f, 5f)
                            )
                        )
                    )
                }
            }
        }
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, top = 4.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = location,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
        
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = date,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = time,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(selectedIndex: Int, onNavigateToHistory: () -> Unit = {}) {
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
                selected = selectedIndex == index,
                onClick = { if (index == 1) onNavigateToHistory() }, // Orders tab
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
