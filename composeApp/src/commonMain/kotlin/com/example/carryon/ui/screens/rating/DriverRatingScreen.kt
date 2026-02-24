package com.example.carryon.ui.screens.rating

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.carryon_logo
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.icon_profile
import carryon.composeapp.generated.resources.icon_messages
import carryon.composeapp.generated.resources.icon_search
import carryon.composeapp.generated.resources.bell_icon
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverRatingScreen(
    driverName: String = "Josh Knight",
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalStrings.current
    var rating by remember { mutableStateOf(5) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var comment by remember { mutableStateOf("") }
    var selectedTip by remember { mutableStateOf<Int?>(null) }
    var customTip by remember { mutableStateOf("") }

    val feedbackTags = listOf(strings.goodCommunication, strings.excellentService, strings.cleanAndComfy)
    val tipAmounts = listOf(10, 20, 50, 80, 100)
    
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
            BottomNavigationBar(selectedIndex = 2)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title
            Text(
                text = strings.giveRatingForDriver,
                fontSize = 16.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // How was the driver?
            Text(
                text = strings.howWasTheDriver,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Driver Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue)
            ) {
                Image(
                    painter = painterResource(Res.drawable.carryon_logo),
                    contentDescription = "Driver",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Driver Name with Rating
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = driverName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Box(
                    modifier = Modifier
                        .border(1.dp, StarYellow, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 12.sp)
                        Text(
                            text = "5.0",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Star Rating
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                (1..5).forEach { star ->
                    Text(
                        text = "⭐",
                        fontSize = 36.sp,
                        modifier = Modifier
                            .clickable { rating = star }
                            .padding(horizontal = 4.dp),
                        color = if (star <= rating) StarYellow else Color.LightGray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Yay! What impressed you?
            Text(
                text = strings.whatImpressedYou,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Feedback Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                feedbackTags.take(2).forEach { tag ->
                    FeedbackChip(
                        text = tag,
                        isSelected = selectedTags.contains(tag),
                        onClick = {
                            selectedTags = if (selectedTags.contains(tag)) {
                                selectedTags - tag
                            } else {
                                selectedTags + tag
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Third tag centered
            FeedbackChip(
                text = feedbackTags[2],
                isSelected = selectedTags.contains(feedbackTags[2]),
                onClick = {
                    val tag = feedbackTags[2]
                    selectedTags = if (selectedTags.contains(tag)) {
                        selectedTags - tag
                    } else {
                        selectedTags + tag
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Comment Field
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                placeholder = {
                    Text(
                        strings.sayNiceToDriver,
                        color = Color.LightGray
                    )
                },
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

            Spacer(modifier = Modifier.height(24.dp))

            // Tips Section
            Text(
                text = strings.tipsForDriver,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Tip Amount Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tipAmounts.forEach { amount ->
                    TipChip(
                        amount = amount,
                        isSelected = selectedTip == amount,
                        onClick = { selectedTip = amount }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Custom Tip Field
            OutlinedTextField(
                value = customTip,
                onValueChange = { customTip = it },
                placeholder = { 
                    Text(
                        strings.enterYourTips,
                        color = Color.LightGray
                    )
                },
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

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Text(
                    text = strings.submit,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FeedbackChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.dp,
                if (isSelected) PrimaryBlue else Color.LightGray,
                RoundedCornerShape(20.dp)
            )
            .background(
                if (isSelected) PrimaryBlueSurface else Color.White
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = if (isSelected) PrimaryBlue else TextPrimary
        )
    }
}

@Composable
private fun TipChip(
    amount: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                if (isSelected) PrimaryBlue else Color.LightGray,
                RoundedCornerShape(8.dp)
            )
            .background(
                if (isSelected) PrimaryBlueSurface else Color.White
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = "RM $amount",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) PrimaryBlue else TextPrimary
        )
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
            Pair(Res.drawable.icon_search, strings.navSearch),
            Pair(Res.drawable.icon_messages, strings.navMessages),
            Pair(Res.drawable.icon_home, strings.navHome),
            Pair(Res.drawable.icon_profile, strings.navProfile)
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
