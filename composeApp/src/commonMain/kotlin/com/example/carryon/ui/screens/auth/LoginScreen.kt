package com.example.carryon.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.sign_in_google
import carryon.composeapp.generated.resources.sign_in_apple
import carryon.composeapp.generated.resources.sign_in_facebook
import org.jetbrains.compose.resources.painterResource
import com.example.carryon.data.network.AuthApi
import com.example.carryon.ui.theme.*
import com.example.carryon.i18n.LocalStrings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToOtp: (String) -> Unit,
    onNavigateToRegister: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                Text(strings.welcomeTo, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(strings.appName, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                Text("!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(strings.loginSubtitle, fontSize = 14.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(40.dp))

            // Email Address
            Text(strings.emailAddress, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(strings.enterYourEmail, color = Color.LightGray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

            Spacer(modifier = Modifier.height(20.dp))

            // Password
            Text(strings.password, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text(strings.password, color = Color.LightGray) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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

            // Forgot Password
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { }) {
                    Text(strings.forgotPassword, fontSize = 13.sp, color = PrimaryBlue, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                    if (email.isNotBlank()) {
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            try {
                                AuthApi.sendOtp(email, mode = "login").fold(
                                    onSuccess = {
                                        isLoading = false
                                        onNavigateToOtp(email)
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
                    Text(strings.logIn, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Or Divider
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE8E8E8))
                Text("  ${strings.or}  ", color = TextSecondary, fontSize = 14.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE8E8E8))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social Login Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.sign_in_apple),
                    contentDescription = "Apple",
                    modifier = Modifier.height(64.dp).clickable { },
                    contentScale = ContentScale.FillHeight
                )
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    painter = painterResource(Res.drawable.sign_in_google),
                    contentDescription = "Google",
                    modifier = Modifier.height(64.dp).clickable { },
                    contentScale = ContentScale.FillHeight
                )
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    painter = painterResource(Res.drawable.sign_in_facebook),
                    contentDescription = "Facebook",
                    modifier = Modifier.height(64.dp).clickable { },
                    contentScale = ContentScale.FillHeight
                )
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
