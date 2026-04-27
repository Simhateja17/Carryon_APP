package com.company.carryon.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.model.Address
import com.company.carryon.data.model.AddressType
import com.company.carryon.data.network.AddressApi
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.PrimaryBlueDark
import kotlinx.coroutines.launch

private val AddAddressFieldBackground = Color(0x33A6D2F3)

@Composable
fun AddAddressScreen(
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedType by remember { mutableStateOf("Home") }
    var fullAddress by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var recentAddresses by remember { mutableStateOf<List<Address>>(emptyList()) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        AddressApi.getAddresses()
            .onSuccess { recentAddresses = it.take(3) }
            .onFailure { recentAddresses = emptyList() }
    }

    fun selectedAddressType(): AddressType = when (selectedType) {
        "Home" -> AddressType.HOME
        "Office" -> AddressType.OFFICE
        else -> AddressType.OTHER
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
            .verticalScroll(rememberScrollState())
    ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "‹",
                        color = PrimaryBlue,
                        fontSize = 25.sp,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Add Address", color = Color(0xFF1D254B), fontSize = 28.sp, fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                    Text("On", color = PrimaryBlueDark, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 14.dp)
                    .background(Color(0xFFEFF2F5), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⌕", fontSize = 28.sp, color = Color(0xFF111111))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Search for address or landmark",
                        color = Color(0xFF111111),
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF2F80ED), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⌖", color = Color.White, fontSize = 22.sp)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp)
            ) {
                Label("ADDRESS LABEL")
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AddressTypeChip("Home", selectedType == "Home") { selectedType = "Home" }
                    AddressTypeChip("Office", selectedType == "Office") { selectedType = "Office" }
                    AddressTypeChip("Warehouse", selectedType == "Warehouse") { selectedType = "Warehouse" }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Label("FULL ADDRESS")
                FilledInputField(
                    value = fullAddress,
                    onValueChange = { fullAddress = it },
                    placeholder = "Enter full address"
                )

                Spacer(modifier = Modifier.height(14.dp))
                Label("FLOOR / UNIT / LANDMARK")
                FilledInputField(
                    value = landmark,
                    onValueChange = { landmark = it },
                    placeholder = "Optional landmark"
                )

                Spacer(modifier = Modifier.height(14.dp))
                Label("CITY")
                FilledInputField(
                    value = city,
                    onValueChange = { city = it },
                    placeholder = "Enter city"
                )

                Spacer(modifier = Modifier.height(14.dp))
                Label("POSTAL CODE")
                FilledInputField(
                    value = postalCode,
                    onValueChange = { postalCode = it },
                    placeholder = "Enter postal code"
                )

                Spacer(modifier = Modifier.height(18.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFDCE6F1), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF2F80ED), RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("↺", color = Color.White, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Recent Destinations", color = Color(0xFF111111), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text("Frequently used pickup locations", color = Color(0xFF2F80ED), fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (recentAddresses.isEmpty()) {
                        Text(
                            "No recent destinations yet",
                            color = Color(0xFF111111),
                            fontSize = 14.sp
                        )
                    } else {
                        recentAddresses.forEach { address ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF4F6F8), RoundedCornerShape(12.dp))
                                    .clickable {
                                        fullAddress = address.address
                                        landmark = address.landmark
                                        city = address.address.substringAfterLast(',', "").trim().ifBlank { city }
                                        postalCode = postalCode.ifBlank { "" }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("", color = Color(0xFF111111), fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(address.address.ifBlank { "Unnamed address" }, color = Color(0xFF111111), fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                errorMessage?.let { message ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(message, color = Color(0xFFB3261E), fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = {
                        if (fullAddress.isBlank()) {
                            errorMessage = "Address is required."
                            return@Button
                        }
                        isSaving = true
                        errorMessage = null
                        scope.launch {
                            AddressApi.createAddress(
                                Address(
                                    label = selectedType,
                                    address = listOf(fullAddress, postalCode.takeIf { it.isNotBlank() })
                                        .filterNotNull()
                                        .joinToString(", "),
                                    landmark = landmark,
                                    contactName = "",
                                    contactPhone = "",
                                    latitude = 0.0,
                                    longitude = 0.0,
                                    type = selectedAddressType()
                                )
                            ).onSuccess {
                                onSave()
                            }.onFailure {
                                errorMessage = it.message ?: "Failed to save address."
                            }
                            isSaving = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED))
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("  Save Address", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
    }
}

@Composable
private fun Label(text: String) {
    Text(
        text = text,
        color = Color(0xFF111111),
        fontSize = 10.sp,
        letterSpacing = 1.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun AddressTypeChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(52.dp)
            .background(
                if (selected) Color(0xFF2F80ED) else Color(0xFFF5F6F8),
                RoundedCornerShape(26.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 22.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) Color.White else Color(0xFF2F80ED),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun FilledInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AddAddressFieldBackground,
            unfocusedContainerColor = AddAddressFieldBackground,
            focusedBorderColor = Color(0xFFD5E1EF),
            unfocusedBorderColor = Color(0xFFD5E1EF)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    )
}
