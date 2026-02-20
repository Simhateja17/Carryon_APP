package com.example.carryon.ui.screens.home

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.map_background
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.icon_profile
import carryon.composeapp.generated.resources.icon_messages
import carryon.composeapp.generated.resources.icon_search
import carryon.composeapp.generated.resources.bell_icon
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAddressScreen(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var selectedNavItem by remember { mutableStateOf(2) }

    val recentPlaces = listOf(
        Triple("Hospital", "Zydus Corporate Park, Scheme No. 63,Khoraj", "2.7km"),
        Triple("Coffee shop", "1901 Thornridge Cir. Shiloh, Hawaii 81063", "1.1km"),
        Triple("College", "Nirma Universit ,Sarkhej - Gandhinagar Hwy, Gota", "4.9km")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlueDark)
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Text("☰", fontSize = 22.sp, color = TextPrimary) } },
                actions = { IconButton(onClick = {}) { Image(painter = painterResource(Res.drawable.bell_icon), contentDescription = "Notifications", modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                val items = listOf(Pair(Res.drawable.icon_search, "Search"), Pair(Res.drawable.icon_messages, "Messages"), Pair(Res.drawable.icon_home, "Home"), Pair(Res.drawable.icon_profile, "Profile"))
                items.forEachIndexed { index, (iconRes, label) ->
                    NavigationBarItem(icon = { Image(painter = painterResource(iconRes), contentDescription = label, modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit) }, selected = selectedNavItem == index, onClick = { selectedNavItem = index }, colors = NavigationBarItemDefaults.colors(indicatorColor = if (selectedNavItem == index) PrimaryBlueSurface else Color.Transparent))
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Map Image
            Image(
                painter = painterResource(Res.drawable.map_background),
                contentDescription = "Map",
                modifier = Modifier.fillMaxWidth().height(220.dp),
                contentScale = ContentScale.Crop
            )

            // Bottom sheet
            Column(
                modifier = Modifier.fillMaxWidth().offset(y = (-16).dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color.White).padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Handle bar
                Box(modifier = Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color.LightGray).align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(16.dp))

                Text("Select address", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(16.dp))

                // From field
                OutlinedTextField(value = from, onValueChange = { from = it }, placeholder = { Text("Form", color = PrimaryBlue) }, leadingIcon = { Text("📍", fontSize = 16.sp) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = PrimaryBlue), singleLine = true)
                Spacer(modifier = Modifier.height(10.dp))

                // To field
                OutlinedTextField(value = to, onValueChange = { to = it }, placeholder = { Text("To", color = SuccessGreen) }, leadingIcon = { Text("○", fontSize = 16.sp, color = SuccessGreen) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = PrimaryBlue), singleLine = true)
                Spacer(modifier = Modifier.height(20.dp))

                // Recent places
                Text("Recent places", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                recentPlaces.forEach { (name, address, distance) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onNext() }.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("📍", fontSize = 16.sp, color = PrimaryBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(address, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                        }
                        Text(distance, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    Divider(color = Color(0xFFF0F0F0))
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
