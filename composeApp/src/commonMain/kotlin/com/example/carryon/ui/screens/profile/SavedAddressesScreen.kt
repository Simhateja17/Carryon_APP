package com.example.carryon.ui.screens.profile

import kotlin.random.Random
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carryon.data.model.Address
import com.example.carryon.data.model.AddressType
import com.example.carryon.data.model.MapConfig
import com.example.carryon.data.model.PlaceResult
import com.example.carryon.data.network.LocationApi
import com.example.carryon.ui.components.MapViewComposable
import com.example.carryon.ui.components.MapMarker
import com.example.carryon.ui.components.MarkerColor
import com.example.carryon.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedAddressesScreen(
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    
    // TODO: Load saved addresses from API
    val addresses = remember { mutableStateListOf<Address>() }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Addresses") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Back", color = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryOrange,
                contentColor = Color.White
            ) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        if (addresses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📍", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No saved addresses",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Add addresses for quick booking",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                    ) {
                        Text("+ Add Address")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BackgroundLight),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(addresses) { address ->
                    AddressCard(
                        address = address,
                        onEdit = { },
                        onDelete = { showDeleteDialog = address.id }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }
    
    // Add Address Dialog
    if (showAddDialog) {
        AddAddressDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newAddress ->
                addresses.add(newAddress)
                showAddDialog = false
            }
        )
    }
    
    // Delete Confirmation Dialog
    showDeleteDialog?.let { addressId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Address") },
            text = { Text("Are you sure you want to delete this address?") },
            confirmButton = {
                Button(
                    onClick = {
                        addresses.removeAll { it.id == addressId }
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AddressCard(
    address: Address,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val typeIcon = when (address.type) {
        AddressType.HOME -> "🏠"
        AddressType.OFFICE -> "🏢"
        AddressType.OTHER -> "📍"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(typeIcon, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = address.label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                
                TextButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
                    Text("✏️", fontSize = 16.sp)
                }
                TextButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                    Text("🗑️", fontSize = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = address.address,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            
            if (address.landmark.isNotEmpty()) {
                Text(
                    text = "Landmark: ${address.landmark}",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray.copy(alpha = 0.5f)
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("👤 ${address.contactName}", fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(16.dp))
                Text("📱 ${address.contactPhone}", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAddressDialog(
    onDismiss: () -> Unit,
    onSave: (Address) -> Unit
) {
    var label by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AddressType.HOME) }
    var showMapPicker by remember { mutableStateOf(false) }
    var pickedLat by remember { mutableStateOf(0.0) }
    var pickedLng by remember { mutableStateOf(0.0) }
    var mapConfig by remember { mutableStateOf(MapConfig()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        LocationApi.getMapConfig().onSuccess { config ->
            mapConfig = config
        }
    }

    if (showMapPicker) {
        // Map picker overlay
        AlertDialog(
            onDismissRequest = { showMapPicker = false },
            title = { Text("Pick Location on Map") },
            text = {
                Column {
                    Text("Tap on the map to select a location", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    MapViewComposable(
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        styleUrl = mapConfig.styleUrl,
                        centerLat = if (pickedLat != 0.0) pickedLat else 17.385,
                        centerLng = if (pickedLng != 0.0) pickedLng else 78.4867,
                        zoom = 13.0,
                        markers = if (pickedLat != 0.0) listOf(
                            MapMarker("picked", pickedLat, pickedLng, "Selected", MarkerColor.RED)
                        ) else emptyList(),
                        onMapClick = { lat, lng ->
                            pickedLat = lat
                            pickedLng = lng
                            scope.launch {
                                LocationApi.reverseGeocode(lat, lng).onSuccess { place ->
                                    if (place != null) {
                                        address = place.label
                                    }
                                }
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showMapPicker = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    enabled = pickedLat != 0.0
                ) {
                    Text("Confirm Location")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMapPicker = false }) {
                    Text("Cancel")
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add New Address") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Address Type
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AddressType.entries.forEach { type ->
                            val icon = when (type) {
                                AddressType.HOME -> "🏠"
                                AddressType.OFFICE -> "🏢"
                                AddressType.OTHER -> "📍"
                            }
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = {
                                    Text("$icon ${type.name}")
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryOrange.copy(alpha = 0.2f)
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        label = { Text("Label (e.g., Mom's House)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    // Pick on Map button
                    OutlinedButton(
                        onClick = { showMapPicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Pick on Map", color = PrimaryBlue)
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Full Address") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    OutlinedTextField(
                        value = landmark,
                        onValueChange = { landmark = it },
                        label = { Text("Landmark (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    OutlinedTextField(
                        value = contactName,
                        onValueChange = { contactName = it },
                        label = { Text("Contact Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = { contactPhone = it },
                        label = { Text("Contact Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (label.isNotBlank() && address.isNotBlank()) {
                            onSave(
                                Address(
                                    id = Random.nextInt(100000, 999999).toString(),
                                    label = label,
                                    address = address,
                                    landmark = landmark,
                                    latitude = pickedLat,
                                    longitude = pickedLng,
                                    contactName = contactName,
                                    contactPhone = contactPhone,
                                    type = selectedType
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    enabled = label.isNotBlank() && address.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}
