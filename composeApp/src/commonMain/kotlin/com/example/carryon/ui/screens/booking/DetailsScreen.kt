package com.example.carryon.ui.screens.booking

import androidx.compose.foundation.BorderStroke
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
import carryon.composeapp.generated.resources.camera_icon
import com.example.carryon.ui.theme.*
import com.example.carryon.ui.components.rememberCameraCapture
import com.example.carryon.ui.components.decodeImageBytes
import com.example.carryon.data.network.UploadApi
import com.example.carryon.i18n.LocalStrings
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    vehicleType: String = "",
    pickup: String = "",
    delivery: String = "",
    onContinue: (vehicleType: String, pickup: String, delivery: String) -> Unit,
    onBack: () -> Unit
) {
    var itemType by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("5") }
    var payer by remember { mutableStateOf("me") }
    var paymentType by remember { mutableStateOf("") }
    var paymentDropdownExpanded by remember { mutableStateOf(false) }
    val paymentOptions = listOf("Cash", "DuitNow QR", "Touch 'n Go eWallet", "GrabPay", "FPX Online Banking", "Credit / Debit Card")
    var recipientName by remember { mutableStateOf("") }
    var recipientPhone by remember { mutableStateOf("") }
    val strings = LocalStrings.current

    // Camera capture state
    var capturedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var packageImageUrl by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val launchCamera = rememberCameraCapture(
        onImageCaptured = { bytes ->
            capturedImageBytes = bytes
            uploadError = null
            // Upload in background
            scope.launch {
                isUploading = true
                UploadApi.uploadPackageImage(bytes)
                    .onSuccess { url ->
                        packageImageUrl = url
                        isUploading = false
                    }
                    .onFailure { err ->
                        uploadError = err.message ?: "Upload failed"
                        isUploading = false
                    }
            }
        },
        onDenied = {
            uploadError = "Camera permission denied"
        }
    )

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 12.dp)) {
                Button(
                    onClick = { onContinue(vehicleType, pickup, delivery) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) { Text(strings.continueText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold) }
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text("Carry", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(" On", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlueDark)
                    }
                },
                actions = { IconButton(onClick = {}) { Text("🔔", fontSize = 20.sp) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            // Back + Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("<", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue, modifier = Modifier.clickable { onBack() }.padding(end = 8.dp))
                Text(strings.details, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(20.dp))

            // What are you sending
            Text(strings.whatAreYouSending, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(strings.selectTypeOfItem, fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = itemType,
                onValueChange = { itemType = it },
                placeholder = { Text(strings.select, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF6F9FA),
                    focusedContainerColor = Color(0xFFF6F9FA),
                    unfocusedBorderColor = Color(0xFFDCE8E9),
                    focusedBorderColor = PrimaryBlue,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )

            // Warning
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(Color.Transparent).padding(10.dp)) {
                Text("⚠", fontSize = 14.sp, color = Color.Red)
                Spacer(modifier = Modifier.width(6.dp))
                Text(strings.prohibitedItems, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
            }

            // Quantity
            Spacer(modifier = Modifier.height(16.dp))
            Text(strings.quantity, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF6F9FA),
                    focusedContainerColor = Color(0xFFF6F9FA),
                    unfocusedBorderColor = Color(0xFFDCE8E9),
                    focusedBorderColor = PrimaryBlue,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )

            // Select who pays
            Spacer(modifier = Modifier.height(16.dp))
            Text(strings.selectWhoPays, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { payer = "me" }) {
                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(if (payer == "me") PrimaryBlue else Color.Transparent).border(BorderStroke(1.5.dp, if (payer == "me") PrimaryBlue else Color(0xFFDCE8E9)), CircleShape), contentAlignment = Alignment.Center) {
                        if (payer == "me") Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.me, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { payer = "recipient" }) {
                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(if (payer == "recipient") PrimaryBlue else Color.Transparent).border(BorderStroke(1.5.dp, if (payer == "recipient") PrimaryBlue else Color(0xFFDCE8E9)), CircleShape), contentAlignment = Alignment.Center) {
                        if (payer == "recipient") Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.recipient, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                }
            }

            // Payment type dropdown
            Spacer(modifier = Modifier.height(12.dp))
            ExposedDropdownMenuBox(
                expanded = paymentDropdownExpanded,
                onExpandedChange = { paymentDropdownExpanded = !paymentDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = paymentType,
                    onValueChange = {},
                    placeholder = { Text(strings.paymentType, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF6F9FA),
                        focusedContainerColor = Color(0xFFF6F9FA),
                        unfocusedBorderColor = Color(0xFFDCE8E9),
                        focusedBorderColor = PrimaryBlue,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    singleLine = true,
                    readOnly = true,
                    trailingIcon = {
                        Text(
                            if (paymentDropdownExpanded) "▲" else "▼",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                )
                ExposedDropdownMenu(
                    expanded = paymentDropdownExpanded,
                    onDismissRequest = { paymentDropdownExpanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    paymentOptions.forEachIndexed { index, option ->
                        DropdownMenuItem(
                            text = { Text(option, fontSize = 14.sp, color = TextPrimary) },
                            onClick = {
                                paymentType = option
                                paymentDropdownExpanded = false
                            },
                            modifier = Modifier.background(Color.White)
                        )
                        if (index < paymentOptions.size - 1) {
                            HorizontalDivider(
                                color = Color(0xFFE0E0E0),
                                thickness = 0.8.dp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }

            // Recipient Names
            Spacer(modifier = Modifier.height(16.dp))
            Text(strings.recipientNames, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = recipientName,
                onValueChange = { recipientName = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF6F9FA),
                    focusedContainerColor = Color(0xFFF6F9FA),
                    unfocusedBorderColor = Color(0xFFDCE8E9),
                    focusedBorderColor = PrimaryBlue,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )

            // Recipient contact number
            Spacer(modifier = Modifier.height(14.dp))
            Text(strings.recipientContactNumber, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = recipientPhone,
                onValueChange = { recipientPhone = it },
                placeholder = { Text("1X-XXXXXXXX", color = Color.Gray) },
                prefix = { Text("+60 ", color = TextPrimary, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF6F9FA),
                    focusedContainerColor = Color(0xFFF6F9FA),
                    unfocusedBorderColor = Color(0xFFDCE8E9),
                    focusedBorderColor = PrimaryBlue,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone)
            )

            // Camera box
            Spacer(modifier = Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (capturedImageBytes != null) 200.dp else 100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF6F9FA))
                    .border(BorderStroke(1.dp, Color(0xFFDCE8E9)), RoundedCornerShape(10.dp))
                    .clickable { launchCamera() },
                contentAlignment = Alignment.Center
            ) {
                if (capturedImageBytes != null) {
                    // Show captured image preview
                    val imageBitmap = remember(capturedImageBytes) {
                        capturedImageBytes?.let { decodeImageBytes(it) }
                    }
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "Package photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    // Upload indicator overlay
                    if (isUploading) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                    // Re-take button overlay at bottom
                    Box(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Retake", fontSize = 12.sp, color = Color.White)
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(Res.drawable.camera_icon),
                            contentDescription = "Camera",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(strings.takePictureOfPackage, fontSize = 13.sp, color = TextSecondary)
                    }
                }
            }
            // Upload error message
            if (uploadError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(uploadError!!, fontSize = 12.sp, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
