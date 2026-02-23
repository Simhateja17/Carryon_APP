package com.example.carryon.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carryon.data.network.AuthApi
import com.example.carryon.data.network.saveToken
import com.example.carryon.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    email: String,
    mode: String = "login",
    name: String = "",
    onVerifySuccess: () -> Unit,
    onBack: () -> Unit
) {
    var otpValue by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resendTimer by remember { mutableStateOf(30) }
    var canResend by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Countdown timer
    LaunchedEffect(resendTimer) {
        if (resendTimer > 0) {
            delay(1000)
            resendTimer--
        } else {
            canResend = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Back Arrow
            Text(
                text = "<",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Enter the Code
            Text(
                text = "Enter the Code",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "A verification code has been sent to",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = email,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(40.dp))

            // OTP Input - 6 digit boxes
            BasicTextField(
                value = otpValue,
                onValueChange = {
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        otpValue = it
                        errorMessage = null
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                decorationBox = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(6) { index ->
                            val char = otpValue.getOrNull(index)
                            val isFocused = otpValue.length == index

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .border(
                                        width = if (isFocused) 2.dp else 1.5.dp,
                                        color = when {
                                            errorMessage != null -> Color.Red
                                            isFocused -> PrimaryBlue
                                            char != null -> PrimaryBlue
                                            else -> Color(0xFFBDBDBD)
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .background(
                                        color = when {
                                            errorMessage != null -> Color(0xFFFFF0F0)
                                            char != null -> Color(0x332F80ED)
                                            else -> Color.White
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char?.toString() ?: "",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Resend Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't receive code?  ",
                    color = TextSecondary,
                    fontSize = 14.sp
                )

                if (canResend) {
                    Text(
                        text = "Resend again",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            scope.launch {
                                AuthApi.sendOtp(email, mode).fold(
                                    onSuccess = {
                                        resendTimer = 30
                                        canResend = false
                                    },
                                    onFailure = { e ->
                                        errorMessage = e.message ?: "Failed to resend code"
                                    }
                                )
                            }
                        }
                    )
                } else {
                    Text(
                        text = "Resend again (${resendTimer}s)",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Next Button
            Button(
                onClick = {
                    if (otpValue.length == 6) {
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            AuthApi.verifyOtp(email, otpValue, mode, name).fold(
                                onSuccess = { authResponse ->
                                    saveToken(authResponse.token)
                                    onVerifySuccess()
                                },
                                onFailure = { e ->
                                    isLoading = false
                                    errorMessage = e.message ?: "Verification failed"
                                }
                            )
                        }
                    } else {
                        errorMessage = "Please enter 6-digit code"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = otpValue.length == 6 && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Next", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
