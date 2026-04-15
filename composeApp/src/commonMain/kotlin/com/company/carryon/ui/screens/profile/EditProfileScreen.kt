package com.company.carryon.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.ellipse_4
import com.company.carryon.data.network.UserApi
import com.company.carryon.ui.theme.*
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("Marcus Holloway") }
    var email by remember { mutableStateOf("m.holloway@carryon.logistics") }
    var phone by remember { mutableStateOf("+1 (555) 0123-4567") }
    var city by remember { mutableStateOf("Chicago, IL") }
    var language by remember { mutableStateOf("English") }
    var isLoading by remember { mutableStateOf(false) }
    var isFetching by remember { mutableStateOf(true) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        UserApi.getProfile()
            .onSuccess { user ->
                name = user.name.ifBlank { name }
                email = user.email.ifBlank { email }
                phone = user.phone.ifBlank { phone }
                isFetching = false
            }
            .onFailure {
                isFetching = false
            }
    }

    Scaffold(
        containerColor = Color(0xFFF5F6F8)
    ) { paddingValues ->
        if (isFetching) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F6F8))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 130.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "←",
                            color = PrimaryBlue,
                            fontSize = 24.sp,
                            modifier = Modifier.clickable { onBack() }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Personal Info", color = PrimaryBlue, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE0E0E0)))

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.size(112.dp)) {
                            Image(
                                painter = painterResource(Res.drawable.ellipse_4),
                                contentDescription = "Driver profile",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(4.dp, Color(0x4DA6D2F3), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(34.dp)
                                    .background(PrimaryBlue, CircleShape)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✎", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = name,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.Black,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "CARRYON USER",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color(0xFF666666),
                        fontSize = 18.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ProfileFieldLabel("FULL NAME")
                        EditableField(value = name, onValueChange = { name = it })

                        ProfileFieldLabel("EMAIL ADDRESS")
                        LockedField(value = email)
                        Text("Contact support to change email.", color = Color(0xB3666666), fontSize = 13.sp)

                        ProfileFieldLabel("PHONE NUMBER")
                        LockedField(value = phone)
                        Text("Contact support to change your verified phone number.", color = Color(0xB3666666), fontSize = 13.sp)

                        ProfileFieldLabel("CITY")
                        SelectField(value = city)

                        ProfileFieldLabel("LANGUAGE")
                        SelectField(value = language)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.White)
                        .border(1.dp, Color(0x4DE0E0E0), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .padding(top = 14.dp, start = 16.dp, end = 16.dp, bottom = 14.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE0E0E0)))
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            saveError = null
                            isLoading = true
                            scope.launch {
                                UserApi.updateProfile(name = name, email = email)
                                    .onSuccess {
                                        isLoading = false
                                        showSuccessDialog = true
                                    }
                                    .onFailure { err ->
                                        isLoading = false
                                        saveError = err.message ?: "Save failed"
                                    }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(12.dp), clip = false),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                        } else {
                            Text("Save Changes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (saveError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(saveError ?: "", color = ErrorRed, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false; onBack() },
            title = { Text("Success") },
            text = { Text("Profile updated successfully") },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false; onBack() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) { Text("OK") }
            }
        )
    }
}

@Composable
private fun ProfileFieldLabel(text: String) {
    Text(
        text = text,
        color = Color.Black,
        fontSize = 10.sp,
        letterSpacing = 0.5.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun EditableField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0x33A6D2F3),
            unfocusedContainerColor = Color(0x33A6D2F3),
            focusedBorderColor = Color(0x33A6D2F3),
            unfocusedBorderColor = Color(0x33A6D2F3),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )
}

@Composable
private fun LockedField(value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        enabled = false,
        singleLine = true,
        trailingIcon = { Text("🔒", color = PrimaryBlue) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledContainerColor = Color(0x33A6D2F3),
            disabledBorderColor = Color(0x33A6D2F3),
            disabledTextColor = Color.Black,
            disabledTrailingIconColor = PrimaryBlue
        )
    )
}

@Composable
private fun SelectField(value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        trailingIcon = { Text("⌄", color = Color.Black, fontSize = 20.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0x33A6D2F3),
            unfocusedContainerColor = Color(0x33A6D2F3),
            focusedBorderColor = Color(0x33A6D2F3),
            unfocusedBorderColor = Color(0x33A6D2F3),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )
}

