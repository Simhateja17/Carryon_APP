package com.company.carryon.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.carryon.data.network.AuthStateManager
import com.company.carryon.data.network.AuthApi
import com.company.carryon.ui.theme.*
import com.company.carryon.i18n.LocalStrings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    phoneNumber: String,
    mode: String = "login",
    name: String = "",
    phone: String = "",
    onVerifySuccess: () -> Unit,
    onBack: () -> Unit
) {
    var otpValue by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resendTimer by remember { mutableStateOf(30) }
    var canResend by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val strings = LocalStrings.current
    val otpPhone = phone.ifBlank { phoneNumber }

    fun verifyOtp() {
        if (otpValue.length == 6) {
            isLoading = true
            errorMessage = null
            scope.launch {
                AuthApi.verifyOtp(otp = otpValue, mode = mode, name = name, phone = otpPhone).fold(
                    onSuccess = { authResponse ->
                        AuthStateManager.onOtpAuthenticated(authResponse)
                        onVerifySuccess()
                    },
                    onFailure = { e ->
                        isLoading = false
                        errorMessage = e.message ?: strings.verificationFailed
                    }
                )
            }
        } else {
            errorMessage = strings.pleaseEnter6DigitCode
        }
    }

    // Countdown timer
    LaunchedEffect(resendTimer) {
        if (resendTimer > 0) {
            delay(1000)
            resendTimer--
        } else {
            canResend = true
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val compactHeight = maxHeight < 700.dp
        val compactWidth = maxWidth < 360.dp
        val horizontalPadding = if (compactWidth) 16.dp else 24.dp

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Scrollable top content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding)
            ) {
                Spacer(modifier = Modifier.height(if (compactHeight) 8.dp else 16.dp))

                // Top bar: Back Arrow + Continue button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "‹",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(8.dp)
                    )
                    Text(
                        text = strings.next,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (otpValue.length == 6 && !isLoading) PrimaryBlue else Color.LightGray,
                        modifier = Modifier
                            .clickable(enabled = otpValue.length == 6 && !isLoading) { verifyOtp() }
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(if (compactHeight) 20.dp else 40.dp))

                // Enter the Code
                Text(
                    text = strings.enterTheCode,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Subtitle
                Text(
                    text = strings.verificationCodeSentTo,
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = otpPhone,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(if (compactHeight) 24.dp else 40.dp))

                // OTP Input - 6 digit boxes
                OtpCodeBoxes(
                    otpValue = otpValue,
                    hasError = errorMessage != null
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
                        text = strings.dontReceiveCode,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )

                    if (canResend) {
                        Text(
                            text = strings.resendAgain,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                scope.launch {
                                    AuthApi.sendOtp(mode = mode, phone = otpPhone).fold(
                                        onSuccess = {
                                            resendTimer = 30
                                            canResend = false
                                        },
                                        onFailure = { e ->
                                            errorMessage = e.message ?: strings.failedToResendCode
                                        }
                                    )
                                }
                            }
                        )
                    } else {
                        Text(
                            text = strings.resendAgainTimer(resendTimer),
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (compactHeight) 16.dp else 24.dp))

                // Next Button — directly below resend row
                Button(
                    onClick = { verifyOtp() },
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
                        Text(strings.next, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(if (compactHeight) 16.dp else 20.dp))
            }

            // Keypad pinned at the bottom
            CarryOnNumberPad(
                compact = compactHeight,
                modifier = Modifier.padding(horizontal = horizontalPadding).padding(bottom = if (compactHeight) 16.dp else 24.dp),
                onNumberClick = { number ->
                    if (otpValue.length < 6) {
                        otpValue += number
                        errorMessage = null
                    }
                },
                onBackspaceClick = {
                    if (otpValue.isNotEmpty()) {
                        otpValue = otpValue.dropLast(1)
                        errorMessage = null
                    }
                }
            )
        }
    }
}

@Composable
private fun OtpCodeBoxes(
    otpValue: String,
    hasError: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(6) { index ->
            val char = otpValue.getOrNull(index)
            val isNext = otpValue.length == index

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .border(
                        width = if (isNext) 2.dp else 1.5.dp,
                        color = when {
                            hasError -> Color.Red
                            isNext || char != null -> PrimaryBlue
                            else -> Color(0xFFD4DCE8)
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(
                        color = when {
                            hasError -> Color(0xFFFFF0F0)
                            char != null -> PrimaryBlueSurface
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
                    color = TextPrimary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun CarryOnNumberPad(
    compact: Boolean,
    modifier: Modifier = Modifier,
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit
) {
    val keyHeight = if (compact) 52.dp else 58.dp
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0x40000000),
                spotColor = Color(0x40000000)
            )
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { number ->
                    NumberPadKey(
                        modifier = Modifier.weight(1f).height(keyHeight),
                        onClick = { onNumberClick(number) }
                    ) {
                        Text(
                            text = number,
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f).height(keyHeight))
            NumberPadKey(
                modifier = Modifier.weight(1f).height(keyHeight),
                onClick = { onNumberClick("0") }
            ) {
                Text(
                    text = "0",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
            NumberPadKey(
                modifier = Modifier.weight(1f).height(keyHeight),
                onClick = onBackspaceClick,
                containerColor = PrimaryBlue
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Delete last digit",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}

@Composable
private fun NumberPadKey(
    modifier: Modifier,
    onClick: () -> Unit,
    containerColor: Color = Color.White,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = if (containerColor == Color.White) {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCE6F2))
        } else {
            null
        },
        shadowElevation = if (containerColor == Color.White) 1.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}
