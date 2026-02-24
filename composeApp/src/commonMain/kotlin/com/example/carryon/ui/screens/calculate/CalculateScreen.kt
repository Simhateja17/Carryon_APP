package com.example.carryon.ui.screens.calculate

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.icon_products
import carryon.composeapp.generated.resources.icon_boxes
import carryon.composeapp.generated.resources.icon_documents
import carryon.composeapp.generated.resources.icon_map
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.icon_profile
import carryon.composeapp.generated.resources.icon_messages
import carryon.composeapp.generated.resources.icon_search
import carryon.composeapp.generated.resources.bell_icon
import carryon.composeapp.generated.resources.calc_products
import carryon.composeapp.generated.resources.calc_boxes
import carryon.composeapp.generated.resources.calc_documents
import carryon.composeapp.generated.resources.calc_map
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings

data class PackageType(
    val icon: String,
    val name: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculateScreen(
    onBack: () -> Unit,
    onFreeCheck: (String, String, String, String) -> Unit
) {
    val strings = LocalStrings.current
    val packageTypes = remember {
        listOf(
            PackageType("🛒", strings.products),
            PackageType("📦", strings.boxes),
            PackageType("📄", strings.documents)
        )
    }
    
    var selectedTab by remember { mutableStateOf(0) } // 0 = Domestic, 1 = International
    var selectedPackageType by remember { mutableStateOf(packageTypes[2]) }
    var fromAddress by remember { mutableStateOf("") }
    var destinationAddress by remember { mutableStateOf("") }
    var selectedDeliveryOption by remember { mutableStateOf("Jne Express") }
    var itemWeight by remember { mutableStateOf("") }
    var showDeliveryOptions by remember { mutableStateOf(false) }
    
    val deliveryOptions = listOf("Jne Express", "Standard", "Economy", "Same Day")
    
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
            Column {
                // Sticky Book Now button
                Button(
                    onClick = {
                        onFreeCheck(
                            fromAddress.ifBlank { "North Bekasi" },
                            destinationAddress.ifBlank { "Bandung" },
                            selectedDeliveryOption,
                            itemWeight.ifBlank { "1Kg" }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(strings.freeCheck, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                BottomNavigationBar(selectedIndex = 2)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Sub Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
                    text = strings.calculate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text("⋮", fontSize = 20.sp, color = TextSecondary)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Domestic / International Tabs
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(PrimaryBlueSurface, RoundedCornerShape(10.dp))
                    .padding(4.dp)
            ) {
                Row {
                    // Domestic Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (selectedTab == 0) PrimaryBlue else Color.Transparent
                            )
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.domestic,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedTab == 0) Color.White else TextSecondary
                        )
                    }

                    // International Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (selectedTab == 1) PrimaryBlue else Color.Transparent
                            )
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.international,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedTab == 1) Color.White else TextSecondary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // What are you sending?
            Text(
                text = strings.whatAreYouSendingCalc,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Package Type Selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                packageTypes.forEach { packageType ->
                    PackageTypeCard(
                        packageType = packageType,
                        isSelected = selectedPackageType == packageType,
                        onClick = { selectedPackageType = packageType },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Radio indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                packageTypes.forEach { packageType ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(
                                    2.dp,
                                    if (selectedPackageType == packageType) PrimaryBlue else Color.LightGray,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedPackageType == packageType) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(PrimaryBlue, CircleShape)
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // From Field
            Text(
                text = strings.from,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = fromAddress,
                onValueChange = { fromAddress = it },
                placeholder = { Text(strings.addAddressPlaceholder, color = Color.LightGray) },
                trailingIcon = {
                    Image(
                        painter = painterResource(Res.drawable.calc_map),
                        contentDescription = "Map",
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
            
            // Destination Field
            Text(
                text = strings.destination,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = destinationAddress,
                onValueChange = { destinationAddress = it },
                placeholder = { Text(strings.addAddressPlaceholder, color = Color.LightGray) },
                trailingIcon = {
                    Image(
                        painter = painterResource(Res.drawable.calc_map),
                        contentDescription = "Map",
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
            
            // Delivery Option
            Text(
                text = strings.deliveryOption,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ExposedDropdownMenuBox(
                expanded = showDeliveryOptions,
                onExpandedChange = { showDeliveryOptions = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = selectedDeliveryOption,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = {
                        Text(if (showDeliveryOptions) "▲" else "▼", fontSize = 12.sp, color = TextSecondary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color(0xFFF8F8F8),
                        unfocusedContainerColor = Color(0xFFF8F8F8),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                
                ExposedDropdownMenu(
                    expanded = showDeliveryOptions,
                    onDismissRequest = { showDeliveryOptions = false }
                ) {
                    deliveryOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedDeliveryOption = option
                                showDeliveryOptions = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Item Weight
            Text(
                text = strings.itemWeight,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = itemWeight,
                onValueChange = { itemWeight = it },
                placeholder = { Text(strings.kilogram, color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
        }
    }
}

@Composable
private fun PackageTypeCard(
    packageType: PackageType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconResource = when (packageType.name) {
        "Products" -> Res.drawable.calc_products
        "Boxes" -> Res.drawable.calc_boxes
        "Documents" -> Res.drawable.calc_documents
        else -> Res.drawable.calc_documents
    }
    
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF6F9FA)
        ),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCE8E9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(iconResource),
                contentDescription = packageType.name,
                modifier = Modifier.size(36.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = packageType.name,
                fontSize = 12.sp,
                color = TextPrimary
            )
        }
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
