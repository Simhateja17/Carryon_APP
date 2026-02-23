package com.example.carryon.ui.screens.profile

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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carryon.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 22.sp, color = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F8F8))
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            SettingsSection(title = "Account") {
                SettingsItem(title = "Edit Profile", onClick = {})
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                SettingsItem(title = "Change Password", onClick = {})
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                SettingsItem(title = "Saved Addresses", onClick = {})
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingsSection(title = "Notifications") {
                SettingsToggleItem(title = "Push Notifications", initialValue = true)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                SettingsToggleItem(title = "Email Notifications", initialValue = false)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                SettingsToggleItem(title = "SMS Notifications", initialValue = true)
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingsSection(title = "Preferences") {
                SettingsItem(title = "Language", subtitle = "English", onClick = {})
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                SettingsItem(title = "Currency", subtitle = "MYR", onClick = {})
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingsSection(title = "About") {
                SettingsItem(title = "App Version", subtitle = "1.0.0", onClick = {})
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                SettingsItem(title = "Privacy Policy", onClick = {})
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color(0xFFF0F0F0))
                SettingsItem(title = "Terms of Service", onClick = {})
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextSecondary,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsItem(title: String, subtitle: String = "", onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Text(text = ">", fontSize = 16.sp, color = Color(0xFFBDBDBD))
    }
}

@Composable
private fun SettingsToggleItem(title: String, initialValue: Boolean) {
    var checked by remember { mutableStateOf(initialValue) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue)
        )
    }
}
