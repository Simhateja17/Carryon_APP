package com.example.carryon.ui.screens.tracking

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.payment_icon
import carryon.composeapp.generated.resources.icon_people
import carryon.composeapp.generated.resources.icon_timer
import carryon.composeapp.generated.resources.bell_icon
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings

data class TrackingStep(
    val icon: String,
    val title: String,
    val subtitle: String,
    val date: String,
    val time: String,
    val isCompleted: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageDetailsScreen(
    orderId: String,
    onBack: () -> Unit,
    onRateDriver: () -> Unit
) {
    val strings = LocalStrings.current
    val trackingSteps = remember(strings) {
        listOf(
            TrackingStep("📦", strings.sentPackage, "JnE.north Bekasi", "22Dec,2021", "12:30pm"),
            TrackingStep("📊", strings.transit, "3nE.Bandung", "22Dec,2021", "12:30pm"),
            TrackingStep("🚚", strings.onAJourney, "your destination", "22Dec,2021", "12:30pm"),
            TrackingStep("📍", strings.accepted, "By Fernando", "22Dec,2021", "12:30pm")
        )
    }
    
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
                            color = PrimaryBlue,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                        Text(
                            text = " On",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
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
            BottomNavigationBar(selectedIndex = 1)
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
            // Sub Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "<",
                    fontSize = 20.sp,
                    color = TextSecondary,
                    modifier = Modifier.clickable { onBack() }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = strings.packageLabel,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text("⋮", fontSize = 20.sp, color = TextSecondary)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Package Card with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2F80ED),
                                Color(0xFF64B5F6)
                            )
                        )
                    )
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
                            Text("📦", fontSize = 18.sp)
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
                    
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = 16.dp)
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
                    
                    // View Details Button
                    Button(
                        onClick = onRateDriver,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlueLight.copy(alpha = 0.5f)
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Details Timeline
            Text(
                text = strings.details,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Timeline
            trackingSteps.forEachIndexed { index, step ->
                TimelineItem(
                    step = step,
                    isLast = index == trackingSteps.lastIndex
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TimelineItem(
    step: TrackingStep,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Timeline indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(PrimaryBlueSurface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(step.icon, fontSize = 20.sp)
            }
            
            if (!isLast) {
                // Dotted line
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(PrimaryBlue, CircleShape)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = step.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = step.subtitle,
                fontSize = 13.sp,
                color = TextSecondary
            )
        }
        
        // Date & Time
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = step.date,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = step.time,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
    
    if (!isLast) {
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun BottomNavigationBar(selectedIndex: Int) {
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
                onClick = { },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
