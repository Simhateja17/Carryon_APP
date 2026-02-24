package com.example.carryon.ui.screens.booking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.bell_icon
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SenderReceiverScreen(
    onBack: () -> Unit,
    onNext: (senderName: String, senderPhone: String, receiverName: String, receiverPhone: String, notes: String) -> Unit
) {
    var locationType by remember { mutableStateOf("Current Location") }
    var whatSending by remember { mutableStateOf("") }
    var sampleType by remember { mutableStateOf("") }
    var request by remember { mutableStateOf("") }
    var paymentType by remember { mutableStateOf("") }
    var senderName by remember { mutableStateOf("") }
    var overallTrack by remember { mutableStateOf("") }
    var receiverName by remember { mutableStateOf("") }
    var recipientContact by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val strings = LocalStrings.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlueDark)
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Text("←", fontSize = 22.sp, color = TextPrimary) } },
                actions = { IconButton(onClick = {}) { Image(painter = painterResource(Res.drawable.bell_icon), contentDescription = "Notifications", modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(strings.requestForRide, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(20.dp))

            // Location type toggle (Current Location / Office)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(strings.currentLocation to "Current Location", strings.office to "Office").forEach { (label, key) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (locationType == key) PrimaryBlue else Color(0xFFF5F5F5),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { locationType = key }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (locationType == key) Color.White else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Form fields matching design
            BookingInputField(label = strings.whatAreYouSendingQuestion, value = whatSending, placeholder = "e.g. Electronics, Clothes", onValueChange = { whatSending = it })
            Spacer(modifier = Modifier.height(14.dp))
            BookingInputField(label = strings.sampleType, value = sampleType, placeholder = "e.g. Fragile, Standard", onValueChange = { sampleType = it })
            Spacer(modifier = Modifier.height(14.dp))
            BookingInputField(label = strings.request, value = request, placeholder = "e.g. Handle with care", onValueChange = { request = it })
            Spacer(modifier = Modifier.height(14.dp))
            BookingInputField(label = strings.paymentType, value = paymentType, placeholder = "e.g. Cash, Card", onValueChange = { paymentType = it })
            Spacer(modifier = Modifier.height(14.dp))
            BookingInputField(label = strings.senderName, value = senderName, placeholder = "e.g. Phoebe", onValueChange = { senderName = it })
            Spacer(modifier = Modifier.height(14.dp))
            BookingInputField(label = strings.overallTrack, value = overallTrack, placeholder = "Track ID", onValueChange = { overallTrack = it })
            Spacer(modifier = Modifier.height(14.dp))
            BookingInputField(label = strings.receiverName, value = receiverName, placeholder = "e.g. Paul", onValueChange = { receiverName = it })
            Spacer(modifier = Modifier.height(14.dp))
            PhoneInputField(label = strings.recipientContactNumber, value = recipientContact, onValueChange = { recipientContact = it })
            Spacer(modifier = Modifier.height(14.dp))
            BookingInputField(label = strings.address, value = address, placeholder = "e.g. Vill.Chalishin", onValueChange = { address = it })

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    onNext(
                        senderName.ifBlank { "Phoebe" },
                        recipientContact.ifBlank { "028607329" },
                        receiverName.ifBlank { "Paul" },
                        recipientContact.ifBlank { "028607329" },
                        request
                    )
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(strings.continueText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BookingInputField(label: String, value: String, placeholder: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray, fontSize = 13.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color(0xFFF8F8F8),
                unfocusedContainerColor = Color(0xFFF8F8F8),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            singleLine = true
        )
    }
}

@Composable
private fun PhoneInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("1X-XXXXXXXX", color = Color.LightGray, fontSize = 13.sp) },
            prefix = { Text("+60 ", color = androidx.compose.ui.graphics.Color.Black, fontSize = 13.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color(0xFFF8F8F8),
                unfocusedContainerColor = Color(0xFFF8F8F8),
                focusedTextColor = androidx.compose.ui.graphics.Color.Black,
                unfocusedTextColor = androidx.compose.ui.graphics.Color.Black
            ),
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone)
        )
    }
}