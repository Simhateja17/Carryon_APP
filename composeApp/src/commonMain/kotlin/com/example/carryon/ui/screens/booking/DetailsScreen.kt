package com.example.carryon.ui.screens.booking

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carryon.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    var itemType by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("5") }
    var payer by remember { mutableStateOf("me") }
    var recipientName by remember { mutableStateOf("Donald Duck") }
    var recipientPhone by remember { mutableStateOf("08123456789") }

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
                actions = { IconButton(onClick = {}) { Text("🔔", fontSize = 20.sp) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            // Back + Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("<", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue, modifier = Modifier.clickable { onBack() }.padding(end = 8.dp))
                Text("Details", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(20.dp))

            // What are you sending
            Text("What are you sending", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text("Select type of item (e.g gadget, document)", fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = itemType, onValueChange = { itemType = it }, placeholder = { Text("Select", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF5F5F5), focusedContainerColor = Color(0xFFF5F5F5), unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue), singleLine = true)

            // Warning
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(Color(0xFFFFF8F8)).padding(10.dp)) {
                Text("⚠", fontSize = 14.sp, color = Color.Red)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Our Prohibited Items include: blah, blah, blah, blah, blah, blah, blah, blah, blah, blah, blah, blah, blah, blah", fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
            }

            // Quantity
            Spacer(modifier = Modifier.height(16.dp))
            Text("Quantity", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(value = quantity, onValueChange = { quantity = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF5F5F5), focusedContainerColor = Color(0xFFF5F5F5), unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue), singleLine = true)

            // Select who pays
            Spacer(modifier = Modifier.height(16.dp))
            Text("Select who pays", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { payer = "me" }) {
                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(if (payer == "me") SuccessGreen else Color.Transparent).padding(2.dp), contentAlignment = Alignment.Center) {
                        if (payer == "me") Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(SuccessGreen))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Me", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { payer = "recipient" }) {
                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(if (payer == "recipient") SuccessGreen else Color.Transparent).padding(2.dp), contentAlignment = Alignment.Center) {
                        if (payer == "recipient") Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(SuccessGreen))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Recipient", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                }
            }

            // Payment type dropdown
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("Payment type", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF5F5F5), focusedContainerColor = Color(0xFFF5F5F5), unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue), singleLine = true, readOnly = true, trailingIcon = { Text("▼", fontSize = 14.sp, color = TextSecondary) })

            // Recipient Names
            Spacer(modifier = Modifier.height(16.dp))
            Text("Recipient Names", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(value = recipientName, onValueChange = { recipientName = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF5F5F5), focusedContainerColor = Color(0xFFF5F5F5), unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue), singleLine = true)

            // Recipient contact number
            Spacer(modifier = Modifier.height(14.dp))
            Text("Recipient contact number", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(value = recipientPhone, onValueChange = { recipientPhone = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF5F5F5), focusedContainerColor = Color(0xFFF5F5F5), unfocusedBorderColor = Color.Transparent, focusedBorderColor = PrimaryBlue), singleLine = true)

            // Camera box
            Spacer(modifier = Modifier.height(18.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📷", fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Take a picture of the package", fontSize = 13.sp, color = TextSecondary)
                }
            }

            // Continue button
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) { Text("Continue", fontSize = 15.sp, fontWeight = FontWeight.SemiBold) }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
