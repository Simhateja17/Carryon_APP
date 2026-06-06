package com.company.carryon.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
fun LoginScreen(
    onNavigateToOtp: (String) -> Unit,
    onNavigateToRegister: () -> Unit = {}
) {
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val strings = LocalStrings.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Welcome Text
            Row {
                Text(strings.welcomeTo, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(strings.appName, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(strings.loginSubtitle, fontSize = 14.sp, color = TextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.height(40.dp))

            // Phone Number
            Text(strings.phoneNumber, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = { Text(strings.enterYourPhone, color = Color.LightGray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

            Spacer(modifier = Modifier.height(20.dp))

            // Error message
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )
            }

            // Log In Button
            Button(
                onClick = {
                    if (phoneNumber.isNotBlank()) {
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            try {
                                AuthApi.sendOtp(mode = "login", phone = phoneNumber).fold(
                                    onSuccess = {
                                        isLoading = false
                                        onNavigateToOtp(phoneNumber)
                                    },
                                    onFailure = { e ->
                                        isLoading = false
                                        errorMessage = e.message ?: strings.noAccountFound
                                    }
                                )
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = e.message ?: strings.unexpectedError
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
                    Text(strings.sendVerificationCode, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sign Up Link
            Row(
                modifier = Modifier.padding(bottom = 40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(strings.dontHaveAccount, color = TextSecondary, fontSize = 14.sp)
                TextButton(onClick = { onNavigateToRegister() }) {
                    Text(strings.signUp, color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
}
