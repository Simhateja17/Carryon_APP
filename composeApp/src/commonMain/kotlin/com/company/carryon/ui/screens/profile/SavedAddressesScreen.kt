package com.company.carryon.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.model.Address
import com.company.carryon.data.model.AddressType
import com.company.carryon.data.network.AddressApi
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.saved_address_delete
import carryon.composeapp.generated.resources.saved_address_edit
import carryon.composeapp.generated.resources.saved_address_hub
import carryon.composeapp.generated.resources.saved_address_primary
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.PrimaryBlueDark
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private data class SavedAddressUi(
    val icon: DrawableResource,
    val title: String,
    val lines: List<String>
)

@Composable
fun SavedAddressesScreen(
    onAddNewAddress: () -> Unit,
    onBack: () -> Unit
) {
    var addresses by remember { mutableStateOf<List<SavedAddressUi>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        AddressApi.getAddresses()
            .onSuccess { result -> addresses = result.map(::toSavedAddressUi) }
            .onFailure { addresses = emptyList() }
        isLoading = false
    }

    Scaffold(containerColor = Color(0xFFF3F4F6)) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF3F4F6))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp)
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
                            text = "←",
                            color = PrimaryBlue,
                            fontSize = 25.sp,
                            modifier = Modifier.clickable { onBack() }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Saved Addresses",
                            color = Color(0xFF1D254B),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                        Text("On", color = PrimaryBlueDark, fontWeight = FontWeight.SemiBold, fontSize = 21.sp)
                    }
                }

                Text(
                    text = "Frequent Locations",
                    color = Color(0xFF111111),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 24.dp, top = 18.dp, bottom = 10.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = PrimaryBlue)
                            }
                        }
                    } else if (addresses.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFDCE6F1), RoundedCornerShape(26.dp))
                                    .padding(horizontal = 18.dp, vertical = 24.dp)
                            ) {
                                Text(
                                    text = "No saved addresses yet.",
                                    color = Color(0xFF111111),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    } else items(addresses) { address ->
                        SavedAddressCard(address = address)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0xFFF3F4F6))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Button(
                    onClick = onAddNewAddress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .shadow(10.dp, RoundedCornerShape(20.dp), clip = false),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED))
                ) {
                    Text(
                        text = "+ Add New Address",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun toSavedAddressUi(address: Address): SavedAddressUi {
    val icon = when (address.type) {
        AddressType.HOME -> Res.drawable.saved_address_primary
        AddressType.OFFICE -> Res.drawable.saved_address_hub
        AddressType.OTHER -> Res.drawable.saved_address_hub
    }
    val subtitleLines = listOfNotNull(
        address.address.takeIf { it.isNotBlank() },
        address.landmark.takeIf { it.isNotBlank() }
    ).ifEmpty { listOf("Address unavailable") }

    return SavedAddressUi(
        icon = icon,
        title = address.label.ifBlank {
            when (address.type) {
                AddressType.HOME -> "Home"
                AddressType.OFFICE -> "Office"
                AddressType.OTHER -> "Saved Address"
            }
        },
        lines = subtitleLines
    )
}

@Composable
private fun SavedAddressCard(address: SavedAddressUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDCE6F1), RoundedCornerShape(26.dp))
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(address.icon),
                contentDescription = address.title,
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = address.title,
                color = Color(0xFF111111),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            address.lines.forEach { line ->
                Text(
                    text = line,
                    color = Color(0xFF111111),
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.saved_address_edit),
                contentDescription = "Edit ${address.title}",
                modifier = Modifier.size(16.dp),
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(Res.drawable.saved_address_delete),
                contentDescription = "Delete ${address.title}",
                modifier = Modifier.size(16.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}
