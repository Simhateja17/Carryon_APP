package com.company.carryon.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.network.AuthApi
import com.company.carryon.ui.theme.*
import com.company.carryon.i18n.LocalStrings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    phone: String,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit = {},
    onNavigateToOtp: (phone: String, name: String) -> Unit = { _, _ -> }
) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember(phone) { mutableStateOf(phone) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val strings = LocalStrings.current
    val focusManager = LocalFocusManager.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val compactHeight = maxHeight < 700.dp
        val compactWidth = maxWidth < 360.dp
        val horizontalPadding = if (compactWidth) 16.dp else 24.dp
        val titleFontSize = if (compactWidth) 22.sp else 26.sp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (compactHeight) 40.dp else 96.dp))

            // Welcome Text
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "‹",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextPrimary,
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(end = 8.dp)
                )
                Text(strings.welcomeTo, fontSize = titleFontSize, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                com.company.carryon.ui.components.CarryOnWordmark(fontSize = titleFontSize)
                Text("!", fontSize = titleFontSize, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(strings.registerSubtitle, fontSize = 14.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(if (compactHeight) 20.dp else 32.dp))

            // Name
            Text(strings.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(strings.enterYourName, color = Color.LightGray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE8E8E8),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF8F8F8),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number
            Text(strings.phoneNumber, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = { Text(strings.enterYourPhone, color = Color.LightGray) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE8E8E8),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF8F8F8),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = strings.otpHint,
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(if (compactHeight) 20.dp else 28.dp))

            // Error message
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )
            }

            // Sign Up Button
            Button(
                onClick = {
                    if (name.isNotBlank() && phoneNumber.isNotBlank()) {
                        focusManager.clearFocus()
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            try {
                                AuthApi.sendOtp(mode = "signup", phone = phoneNumber).fold(
                                    onSuccess = {
                                        isLoading = false
                                        onNavigateToOtp(phoneNumber, name)
                                    },
                                    onFailure = { e ->
                                        isLoading = false
                                        val msg = e.message ?: ""
                                        errorMessage = if (msg.contains("Chain validation failed", ignoreCase = true) ||
                                            msg.contains("SSL", ignoreCase = true) ||
                                            msg.contains("certificate", ignoreCase = true) ||
                                            msg.contains("trust anchor", ignoreCase = true)) {
                                            "Connection error. Please check your internet connection and try again."
                                        } else {
                                            msg.ifBlank { strings.failedToSendCode }
                                        }
                                    }
                                )
                            } catch (e: Exception) {
                                isLoading = false
                                val msg = e.message ?: ""
                                errorMessage = if (msg.contains("Chain validation failed", ignoreCase = true) ||
                                    msg.contains("SSL", ignoreCase = true) ||
                                    msg.contains("certificate", ignoreCase = true) ||
                                    msg.contains("trust anchor", ignoreCase = true)) {
                                    "Connection error. Please check your internet connection and try again."
                                } else {
                                    msg.ifBlank { strings.unexpectedError }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(strings.signUp, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(if (compactHeight) 20.dp else 40.dp))
        }
    }
}
